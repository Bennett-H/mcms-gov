/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */











package net.mingsoft.basic.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * XSS 过滤器 用于请求参数的脚本数据
 * 历史修订: 2022-1-24 把includes删除,改成excludes
 */
public class XSSEscapeFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(XSSEscapeFilter.class);

	private static boolean IS_INCLUDE_RICH_TEXT = false;// 是否过滤富文本内容
	public List<String> includes = new ArrayList<>();//拦截路径
	public List<String> excludes = new ArrayList<>();//排除路径
	public static List<String> excludesFiled = new ArrayList<>();//排除参数名

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		if (handleExcludeURL(req,resp)){//如果排除路径匹配
			filterChain.doFilter(request, response);
			return;
		}else if (!handleIncludeURL(req, resp)) {
			filterChain.doFilter(request, response);
			return;
		}
		XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request,excludesFiled);
		filterChain.doFilter(xssRequest, response);
	}

	/**
	 * 处理拦截路径
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean handleIncludeURL(HttpServletRequest request, HttpServletResponse response) {
		if (includes == null || includes.isEmpty()) {
			return false;
		}
		String url = request.getServletPath();
		AntPathMatcher antPathMatcher = new AntPathMatcher();
		for (String pattern : includes) {
			if(antPathMatcher.match(pattern,url)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 处理排除路径
	 * @param request
	 * @param response
	 * @return 是否匹配成功
	 */
	private boolean handleExcludeURL(HttpServletRequest request, HttpServletResponse response) {
		if (excludes == null || excludes.isEmpty()) {
			return false;
		}
		String url = request.getServletPath();
		AntPathMatcher antPathMatcher = new AntPathMatcher();
		for (String pattern : excludes) {
			if(antPathMatcher.match(pattern,url)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 过滤json类型的
	 * @param builder
	 * @return
	 */
	@Bean
	@Primary
	public ObjectMapper xssObjectMapper(Jackson2ObjectMapperBuilder builder) {
		//解析器
		ObjectMapper objectMapper = builder.createXmlMapper(false).build();
		//注册xss解析器
		SimpleModule xssModule = new SimpleModule("XssStringJsonSerializer");
		xssModule.addSerializer(new XssStringJsonSerializer());
		objectMapper.registerModule(xssModule);
		//返回
		return objectMapper;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("xss filter init");
		}
		String isIncludeRichText = filterConfig.getInitParameter("isIncludeRichText");
		if (StringUtils.isNotBlank(isIncludeRichText)) {
			IS_INCLUDE_RICH_TEXT = BooleanUtils.toBoolean(isIncludeRichText);
		}
		String temp = filterConfig.getInitParameter("excludes");
		if (temp != null) {
			String[] url = temp.split(",");
			for (int i = 0; url != null && i < url.length; i++) {
				includes.add(url[i]);
			}
		}
	}

	@Override
	public void destroy() {
	}

}
