/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.approval.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;

/**
* 审批配置实体
* @author 铭软科技
* 创建日期：2021-3-19 16:46:27<br/>
* 历史修订：<br/>
*/
@TableName("APPROVAL_CONFIG")
public class ConfigEntity extends BaseEntity {

private static final long serialVersionUID = 1616143587247L;

	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	* 方案名称
	*/
	private String schemeId;
	/**
	* 审批节点
	*/
	private String progressId;
	/**
	* 等级角色配置
	*/
	private String configRoleIds;
	/**
	 * 栏目ID
	 */
	private String categoryId;


	/**
	* 设置方案名称
	*/
	public void setSchemeId(String schemeId) {
	this.schemeId = schemeId;
	}

	/**
	* 获取方案名称
	*/
	public String getSchemeId() {
	return this.schemeId;
	}
	/**
	* 设置审批节点
	*/
	public void setProgressId(String progressId) {
	this.progressId = progressId;
	}

	/**
	* 获取审批节点
	*/
	public String getProgressId() {
	return this.progressId;
	}
	/**
	* 设置等级角色配置
	*/
	public void setConfigRoleIds(String configRoleIds) {
	this.configRoleIds = configRoleIds;
	}

	/**
	* 获取等级角色配置
	*/
	public String getConfigRoleIds() {
	return this.configRoleIds;
	}

	/**
	 * 获取栏目ID
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/**
	 * 设置栏目ID
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
}
