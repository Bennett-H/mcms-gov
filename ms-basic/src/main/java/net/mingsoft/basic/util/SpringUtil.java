/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: SpringUtil
 * @Description: TODO(Spring工具类)
 * @author 铭软开发团队
 * @date 2020年7月2日
 *
 */
@Component
public class SpringUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	private static ThreadLocal<HttpServletRequest> requestThreadLocal=new ThreadLocal<>();
	/**
	 * 获取当前请求对象
	 *
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			return request;
		} catch (Exception e) {
			return requestThreadLocal.get();
		}
	}
	/**
	 * 设置当前请求对象
	 *
	 * @return
	 */
	public static void setRequest(HttpServletRequest request) {
		requestThreadLocal.set(request);
	}

	/**
	 * 通过spring的webapplicationcontext上下文对象读取bean对象
	 *
	 * @param sc
	 *            上下文servletConext对象
	 * @param beanName
	 *            要读取的bean的名称
	 * @return 返回获取到的对象。获取不到返回null
	 */
	@Deprecated
	public static Object getBean(ServletContext sc, String beanName) {
		return getApplicationContext().getBean(beanName);
	}

	/**
	 * 通过spring的webapplicationcontext上下文对象读取bean对象
	 *
	 * @param beanName
	 *            要读取的bean的名称
	 * @return 返回获取到的对象。获取不到返回null
	 */
	public static Object getBean(String beanName) {
		return getApplicationContext().getBean(beanName);
	}

	/**
	 * 通过spring的webapplicationcontext上下文对象读取bean对象
	 *
	 * @param cls
	 *            要读取的类名称
	 * @return 返回获取到的对象。获取不到返回null
	 */
	public static <T> T getBean(Class<T> cls) {
		return getApplicationContext().getBean(cls);
	}



	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (SpringUtil.applicationContext == null) {
			SpringUtil.applicationContext = applicationContext;
		}
	}
	//获取applicationContext
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
