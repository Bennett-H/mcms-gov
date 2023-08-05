/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.msend.util;

import cn.hutool.json.JSONUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * 铭飞MS平台-邮件模块
 *
 * @author 铭飞开发团队
 * @version 版本号：100-000-000<br/>
 *          创建日期：2016年5月8日<br/>
 *          历史修订：<br/>
 */
public class SendcloudUtil {

	/*
	 * log4j日志记录
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(SendcloudUtil.class);

	/**
	 * 发送邮件
	 *
	 * @param apiUser
	 *            平台分配
	 * @param apiKey
	 *            平台分配
	 * @param from
	 *            发送邮件
	 * @param fromName
	 *            发送名称
	 * @param to
	 *            接收者
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @throws IOException
	 */
	public static boolean sendMail(String apiUser, String apiKey, String from, String fromName, String to,
			String subject, String content) throws IOException {
		final String url = "http://api.sendcloud.net/webapi/mail.send.json";
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(url);

		List params = new ArrayList();
		// 不同于登录SendCloud站点的帐号，您需要登录后台创建发信子帐号，使用子帐号和密码才可以进行邮件的发送。
		params.add(new BasicNameValuePair("api_user", apiUser));
		params.add(new BasicNameValuePair("api_key", apiKey));
		params.add(new BasicNameValuePair("from", from));
		params.add(new BasicNameValuePair("fromname", fromName));
		params.add(new BasicNameValuePair("to", to));
		params.add(new BasicNameValuePair("subject", subject));
		params.add(new BasicNameValuePair("html", content));
		params.add(new BasicNameValuePair("resp_email_id", "true"));
		httpost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		// 请求
		HttpResponse response = httpclient.execute(httpost);
		// 处理响应
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 正常返回
			// 读取xml文档
			String result = EntityUtils.toString(response.getEntity());
			LOG.debug(result);
			return true;
		} else {
			LOG.debug("error");
		}
		httpost.releaseConnection();
		return false;
	}

	/**
	 * sendcloud发送
	 *
	 * @param smsUser
	 *            用户
	 * @param smsKey
	 *            key
	 * @param templateId
	 *            模板
	 * @param msgType
	 *            0短信 1彩信
	 * @param phone
	 *            接收手机号，多个用逗号分隔
	 * @param vars
	 *            需要替换的内容 json
	 * @throws IOException
	 */
	public static boolean sendSms(String smsUser, String smsKey, int templateId, String msgType, String phone,
			String vars)  {

		String url = "http://www.sendcloud.net/smsapi/send";

        // 填充参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("smsUser", smsUser);
        params.put("templateId", templateId+"");
        params.put("msgType", "0");
        params.put("phone", phone);
        params.put("vars", vars);
//        params.put("vars", "{\"appointmentPhone\":\"18979833333\",\"appointmentTime\":1504108800000,\"appointmentName\":\"xx\",\"appointmentCarNo\":\"asdas\",\"appointmentType\":0}");

        // 对参数进行排序
        Map<String, String> sortedMap = new TreeMap<String, String>(new Comparator<String>() {
            public int compare(String arg0, String arg1) {
                // 忽略大小写
                return arg0.compareToIgnoreCase(arg1);
            }
        });
        sortedMap.putAll(params);

        // 计算签名
        StringBuilder sb = new StringBuilder();
        sb.append(smsKey).append("&");
        for (String s : sortedMap.keySet()) {
            sb.append(String.format("%s=%s&", s, sortedMap.get(s)));
        }
        sb.append(smsKey);
        LOG.debug("sign_str:"+sb.toString());
        String sig = DigestUtils.md5Hex(sb.toString());
        LOG.debug("sign_md5"+sig);
        // 将所有参数和签名添加到post请求参数数组里
        List<NameValuePair> postparams = new ArrayList<NameValuePair>();
        for (String s : sortedMap.keySet()) {
            postparams.add(new BasicNameValuePair(s, sortedMap.get(s)));
        }
        postparams.add(new BasicNameValuePair("signature", sig));

        LOG.debug(JSONUtil.toJsonStr(postparams));
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postparams, "utf8"));
            CloseableHttpClient httpClient;
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000).setSocketTimeout(100000).build();
            httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            ResponseData rd = JSONUtil.toBean(EntityUtils.toString(entity), ResponseData.class);
			LOG.debug("sendcloud 返回："+rd);
			return rd.getResult();
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            httpPost.releaseConnection();
        }

		return false;
	}


}

class ResponseData {

	public boolean result;
	public int statusCode;
	public String message;
	public String info;

	@Override
	public String toString() {
		return JSONUtil.toJsonStr(this);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
