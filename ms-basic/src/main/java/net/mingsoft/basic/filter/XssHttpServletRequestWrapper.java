/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.basic.filter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.JsoupUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * XSS 过滤器 用于请求参数的脚本数据
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private HttpServletRequest request = null;



    /**
     * 接收排除的字段名
     */
    private List<String> excludesFiled = new ArrayList<>();


	public XssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		this.request = request;
	}

	public XssHttpServletRequestWrapper(HttpServletRequest request,List<String> excludesFiled){
		super(request);
		this.request = request;
		if (CollectionUtil.isNotEmpty(excludesFiled)){
			this.excludesFiled.addAll(excludesFiled);
		}
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		FastByteArrayOutputStream read = IoUtil.read(request.getInputStream());
		// TODO: 2023/1/4 增加上传文件时，json对象的xss处理
		String tmp = read.toString();
		if(JSONUtil.isTypeJSON(tmp)) {
			if(JSONUtil.isTypeJSONArray(tmp)) {
				List<Map> jsonList = JSONUtil.toList(tmp, Map.class);
				jsonList.forEach(map -> {
					Iterator iterator = map.keySet().iterator();
					while (iterator.hasNext()) {
						String key = iterator.next().toString();
						JsoupUtil.cleanOrSqlInjection(key);
						if(map.get(key) instanceof  String && !excludesFiled.contains(key)) {
							JsoupUtil.cleanOrSqlInjection(String.valueOf(map.get(key)));
						}

					}

				});
			}  else {
				Map jsonMap = JSONUtil.toBean(tmp, Map.class);
				Iterator iterator = jsonMap.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next().toString();
					JsoupUtil.cleanOrSqlInjection(key);
					if(jsonMap.get(key) instanceof  String && !excludesFiled.contains(key)) {
						JsoupUtil.cleanOrSqlInjection(String.valueOf(jsonMap.get(key)));
					}
				}

			}
		}
		if(JsoupUtil.hasXSS(tmp)) {
			throw  new BusinessException("上传文件存在xss攻击");
		} else {
			return new WrappedServletInputStream(new ByteArrayInputStream(read.toByteArray()));
		}
	}

	/**
	 * 覆盖getParameter方法，将参数名和参数值都做xss过滤。
	 *
	 * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取
	 *
	 * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
	 */
	@Override
	public String getParameter(String name) {
		name = JsoupUtil.cleanOrSqlInjection(name);
		String value = super.getParameter(name);
		if (StringUtils.isNotBlank(value)) {
			// 如果参数名是排除的，就不通过clean过滤
			if (!excludesFiled.contains(name)) {
				value = JsoupUtil.cleanOrSqlInjection(value);
			}
		}
		return value;
	}

	@Override
	public Map getParameterMap() {
		Map map = super.getParameterMap();
		// 返回值Map
		Map<String, String> returnMap = new HashMap<String, String>();
		Iterator entries = map.entrySet().iterator();
		Map.Entry entry;
		String name = "";
		String value = "";
		while (entries.hasNext()) {
			entry = (Map.Entry) entries.next();
			name = (String) entry.getKey();
			Object valueObj = entry.getValue();
			if (null == valueObj) {
				value = "";
			} else if (valueObj instanceof String[]) {
				String[] values = (String[]) valueObj;
				for (int i = 0; i < values.length; i++) {
					value = values[i] + ",";
				}
				value = value.substring(0, value.length() - 1);
			} else {
				value = valueObj.toString();
			}
			// 如果参数名是排除的，就不通过clean过滤
			if (excludesFiled.contains(name)){
				returnMap.put(name, value.trim());
			} else {
				returnMap.put(JsoupUtil.cleanOrSqlInjection(name), JsoupUtil.cleanOrSqlInjection(value).trim());
			}
		}
		return returnMap;
	}

    @Override
    public String[] getParameterValues(String name) {
        String[] arr = super.getParameterValues(name);
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                // 如果参数名是排除的，就不通过clean过滤
                if (!excludesFiled.contains(name)) {
                    arr[i] = JsoupUtil.cleanOrSqlInjection(arr[i]);
                }
            }
        }
        return arr;
    }

    /**
     * 覆盖getHeader方法，将参数名和参数值都做xss过滤。
	 *
     * 如果需要获得原始的值，则通过super.getHeaders(name)来获取
	 *
     * getHeaderNames 也可能需要覆盖
     */
    @Override
    public String getHeader(String name) {
        name = JsoupUtil.cleanOrSqlInjection(name);
        String value = super.getHeader(name);
        if (StringUtils.isNotBlank(value)) {
            // 如果参数名是排除的，就不通过clean过滤
            if (!excludesFiled.contains(name)) {
                value = JsoupUtil.cleanOrSqlInjection(value);
            }
        }
        return value;
    }

	/**
	 * 获取最原始的request
	 *
	 * @return
	 */
	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * 获取最原始的request的静态方法
	 *
	 * @return
	 */
	public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
		if (req instanceof XssHttpServletRequestWrapper) {
			return ((XssHttpServletRequestWrapper) req).getRequest();
		}

		return req;
	}


	private class WrappedServletInputStream extends ServletInputStream {
		public void setStream(InputStream stream) {
			this.stream = stream;
		}

		private InputStream stream;

		public WrappedServletInputStream(InputStream stream) {
			this.stream = stream;
		}

		@Override
		public int read() throws IOException {
			return stream.read();
		}

		@Override
		public boolean isFinished() {
			return true;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setReadListener(ReadListener readListener) {

		}
	}
}
