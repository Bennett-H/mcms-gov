/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.bean;

import net.mingsoft.people.entity.PeopleUserEntity;

/**
 * 
 * 会员扩展数据，用于后台数据查询
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
public class PeopleBean extends PeopleUserEntity{

	private String  peopleDateTimes;
	
	private String startTime;
	
	private String endTime;

	public String getPeopleDateTimes() {
		return peopleDateTimes;
	}

	public void setPeopleDateTimes(String peopleDateTimes) {
		this.peopleDateTimes = peopleDateTimes;
	}

	public String getStartTime() {
		if(peopleDateTimes != null && peopleDateTimes != "" ){
			return peopleDateTimes.split("至")[0];
		}
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		if(peopleDateTimes != null && peopleDateTimes != "" ){
			return peopleDateTimes.split("至")[1];
		}
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	
}
