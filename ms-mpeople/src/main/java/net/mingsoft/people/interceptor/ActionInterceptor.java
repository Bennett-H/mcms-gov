/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.interceptor;

import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ObjectUtil;

import org.apache.commons.lang3.StringUtils;

import net.mingsoft.base.constant.Const;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.interceptor.BaseInterceptor;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.people.constant.e.CookieConstEnum;
import net.mingsoft.people.entity.PeopleEntity;
import org.springframework.http.HttpStatus;

/**
 *
 * 铭飞MS平台－会员拦截
 * @author 铭飞开发团队
 * @version
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
public class ActionInterceptor extends BaseInterceptor {

	private String loginUrl;


	public ActionInterceptor(){

	}
	public ActionInterceptor(String loginUrl){
		this.loginUrl = loginUrl;

	}
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if (ObjectUtil.isNull(BasicUtil.getSession(net.mingsoft.people.constant.e.SessionConstEnum.PEOPLE_SESSION))) {
			//判断是否为ajax请求，默认不是
			if(StringUtils.isBlank(loginUrl) || !StringUtils.isBlank(request.getHeader("x-requested-with")) && request.getHeader("x-requested-with").equals("XMLHttpRequest")){
				throw new BusinessException(HttpStatus.UNAUTHORIZED,"登录失效");
			} else {
				String login = URLDecoder.decode(loginUrl,Const.UTF8);
				String backUrl = BasicUtil.getUrl()+request.getServletPath();
				if(request.getQueryString()!=null) {
					backUrl +="?"+request.getQueryString();
				}
				if(login.indexOf("?") > 0) {
					login = login+"&jump="+URLEncoder.encode(backUrl,Const.UTF8);
				} else {
					login = login+"?jump="+URLEncoder.encode(backUrl,Const.UTF8);
				}
				response.sendRedirect(login);
				return false;
			}

		} else {
			return true;
		}
	}
}
