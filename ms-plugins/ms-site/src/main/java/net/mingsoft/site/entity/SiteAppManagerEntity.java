/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.site.entity;

import net.mingsoft.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
/**
* 管理员站点关联表实体
* @author 铭飞科技
* 创建日期：2022-1-4 10:22:11<br/>
* 历史修订：<br/>
*/
@TableName("SITE_APP_MANAGER")
public class SiteAppManagerEntity extends BaseEntity {

private static final long serialVersionUID = 1641262931343L;


	/**
	* 管理员编号
	*/
	private String managerId;

	/**
	* 站点编号
	*/
	private Integer appId;


	/**
	* 设置管理员编号
	*/
	public void setManagerId(String managerId) {
	this.managerId = managerId;
	}

	/**
	* 获取管理员编号
	*/
	public String getManagerId() {
	return this.managerId;
	}
	/**
	* 设置站点编号
	*/
	public void setAppId(Integer appId) {
	this.appId = appId;
	}

	/**
	* 获取站点编号
	*/
	public Integer getAppId() {
	return this.appId;
	}


}
