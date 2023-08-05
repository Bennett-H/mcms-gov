/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.cms.entity;

import net.mingsoft.base.entity.BaseEntity;
/**
* 文章浏览记录实体
* @author 铭飞开发团队
* 创建日期：2019-12-23 9:24:03<br/>
* 历史修订：<br/>
*/
public class HistoryLogEntity extends BaseEntity {

private static final long serialVersionUID = 1577064243576L;

	/**
	* 文章编号
	*/
	private String contentId;
	/**
	* 浏览ip
	*/
	private String hlIp;
	/**
	* 用户idp
	*/
	private String peopleId;
	/**
	* 是否为移动端
	*/
	private Boolean hlIsMobile;


	/**
	* 设置文章编号
	*/
	public void setContentId(String contentId) {
	this.contentId = contentId;
	}

	/**
	* 获取文章编号
	*/
	public String getContentId() {
	return this.contentId;
	}
	/**
	* 设置浏览ip
	*/
	public void setHlIp(String hlIp) {
	this.hlIp = hlIp;
	}

	/**
	* 获取浏览ip
	*/
	public String getHlIp() {
	return this.hlIp;
	}

	public String getPeopleId() {
		return peopleId;
	}

	public void setPeopleId(String peopleId) {
		this.peopleId = peopleId;
	}

	/**
	* 设置是否为移动端
	*/
	public void setHlIsMobile(Boolean hlIsMobile) {
	this.hlIsMobile = hlIsMobile;
	}

	/**
	* 获取是否为移动端
	*/
	public Boolean getHlIsMobile() {
	return this.hlIsMobile;
	}
}
