/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.interceptor;

import net.mingsoft.basic.constant.Const;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.util.BasicUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 所有action的拦截器，主要是设置base与basepath
 *
 * @author ms dev group
 * @version 版本号：100-000-000<br/>
 *          创建日期：2012-03-15<br/>
 *          历史修订：<br/>
 */
public class ActionInterceptor  extends HandlerInterceptorAdapter {

	@Value("${ms.manager.path}")
	private String managerPath;
	/**
	 * 所有action的拦截,主要拦截base与basepath
	 *
	 * @param request
	 *            HttpServletRequest对象
	 * @param response
	 *            HttpServletResponse 对象
	 * @param handler
	 *            处理器
	 * @throws Exception
	 *             异常处理
	 * @return 处理后返回true
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)  {

		String base = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort());
		String contextPath = request.getServletContext().getContextPath();
		//项目路径
		request.setAttribute(Const.BASE,contextPath);
		AppEntity app = BasicUtil.getWebsiteApp();
		if (app == null){
			request.setAttribute(Const.APP,BasicUtil.getApp());
		}else {
			request.setAttribute(Const.APP,app);
		}

		//设置后台路径
		request.setAttribute(Const.MANAGER_PATH, contextPath + managerPath);
		//设置当前地址参数，方便页面获取
		request.setAttribute(Const.PARAMS, BasicUtil.assemblyRequestUrlParams());
		return true;
	}

}
