/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.entity;

import net.mingsoft.base.entity.BaseEntity;

/**
 * 
 * 开发平台用户 
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
public class PeopleOpenEntity extends PeopleUserEntity{
 
	private int peopleOpenPeopleId;
	
	private String peopleOpenId;
	
	private int peopleOpenPlatform;

	public int getPeopleOpenPeopleId() {
		return peopleOpenPeopleId;
	}

	public void setPeopleOpenPeopleId(int peopleOpenPeopleId) {
		this.peopleOpenPeopleId = peopleOpenPeopleId;
	}

	public String getPeopleOpenId() {
		return peopleOpenId;
	}

	public void setPeopleOpenId(String peopleOpenId) {
		this.peopleOpenId = peopleOpenId;
	}

	public int getPeopleOpenPlatform() {
		return peopleOpenPlatform;
	}

	public void setPeopleOpenPlatform(int peopleOpenPlatform) {
		this.peopleOpenPlatform = peopleOpenPlatform;
	}
	
}
