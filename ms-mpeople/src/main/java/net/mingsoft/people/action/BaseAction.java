/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.action;

import cn.hutool.json.JSONUtil;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.people.constant.Const;
import net.mingsoft.people.constant.e.SessionConstEnum;
import net.mingsoft.people.entity.PeopleEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.MissingResourceException;

/**
 *
 * 基础类
 * @author 铭飞开发团队
 * @version
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
public class BaseAction extends net.mingsoft.mdiy.action.BaseAction {

	@Override
	protected String getResString(String key) {
		// TODO Auto-generated method stub
		String str = "";
		try {
			str = super.getResString(key);
		} catch (MissingResourceException e) {
			str = getLocaleString(key, Const.RESOURCES);
		}

		return str;
	}
	
	/**
	 * 获取用户session.没有返回null
	 */
	protected PeopleEntity getPeopleBySession() {
		Object obj = BasicUtil.getSession(SessionConstEnum.PEOPLE_SESSION);
		if (obj != null && obj instanceof PeopleEntity) {
			return (PeopleEntity) obj;
		} else if(obj instanceof String) {
			return JSONUtil.toBean(obj.toString(), PeopleEntity.class) ;
		}
		return null;
	}

	/**
	 * 设置用户session
	 * 
	 * @param request
	 * @param people
	 *            用户实体
	 */
	protected void setPeopleBySession(HttpServletRequest request, PeopleEntity people) {
		BasicUtil.setSession( SessionConstEnum.PEOPLE_SESSION, people);
	}

	/**
	 * 移除用户session
	 * 
	 * @param request
	 */
	protected void removePeopleBySession(HttpServletRequest request) {
		BasicUtil.removeSession(SessionConstEnum.PEOPLE_SESSION);
	}

}
