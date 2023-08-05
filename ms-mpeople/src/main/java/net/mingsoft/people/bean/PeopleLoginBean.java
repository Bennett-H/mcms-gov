/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.bean;

import net.mingsoft.people.entity.PeopleEntity;

/**
 * 
 * 会员扩展数据，用于登录返回cookie
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
public class PeopleLoginBean extends PeopleEntity{

	private String  cookie;

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	
	
	
}
