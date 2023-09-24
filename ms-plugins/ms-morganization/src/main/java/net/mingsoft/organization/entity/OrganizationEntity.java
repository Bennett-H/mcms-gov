/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
/**
* 部门管理实体
* @author 铭飞开源团队
* 创建日期：2020-1-16 9:46:15<br/>
* 历史修订：<br/>
*/
@TableName("ORGANIZATION")
public class OrganizationEntity extends BaseEntity {

private static final long serialVersionUID = 1579139175685L;

	/**
	* 部门名称
	*/
	private String organizationTitle;
	/**
	* 所属部门
	*/
	private String organizationId;
	/**
	* 部门状态
	*/
	private String organizationStatus;
	/**
	* 部门编号
	*/
	private String organizationCode;
	/**
	* 分管领导
	*/
	private Integer organizationLeaders;
	/**
	* 负责人
	*/
	private Integer organizationLeader;
	/**
	* 父级ids
	*/
	private String organizationParentId;
	/**
	* 机构类型
	*/
	private String organizationType;
	/**
	* 部门描述
	*/
	@TableField(exist = false)
	private String organizationDescription;

	public String getOrganizationDescription() {
		return organizationDescription;
	}

	public void setOrganizationDescription(String organizationDescription) {
		this.organizationDescription = organizationDescription;
	}

	/**
	* 设置部门名称
	*/
	public void setOrganizationTitle(String organizationTitle) {
	this.organizationTitle = organizationTitle;
	}

	/**
	* 获取部门名称
	*/
	public String getOrganizationTitle() {
	return this.organizationTitle;
	}
	/**
	* 设置所属部门
	*/
	public void setOrganizationId(String organizationId) {
	this.organizationId = organizationId;
	}

	/**
	* 获取所属部门
	*/
	public String getOrganizationId() {
	return this.organizationId;
	}
	/**
	* 设置部门状态
	*/
	public void setOrganizationStatus(String organizationStatus) {
	this.organizationStatus = organizationStatus;
	}

	/**
	* 获取部门状态
	*/
	public String getOrganizationStatus() {
	return this.organizationStatus;
	}
	/**
	* 设置部门编号
	*/
	public void setOrganizationCode(String organizationCode) {
	this.organizationCode = organizationCode;
	}

	/**
	* 获取部门编号
	*/
	public String getOrganizationCode() {
	return this.organizationCode;
	}
	/**
	* 设置分管领导
	*/
	public void setOrganizationLeaders(Integer organizationLeaders) {
	this.organizationLeaders = organizationLeaders;
	}

	/**
	* 获取分管领导
	*/
	public Integer getOrganizationLeaders() {
	return this.organizationLeaders;
	}
	/**
	* 设置负责人
	*/
	public void setOrganizationLeader(Integer organizationLeader) {
	this.organizationLeader = organizationLeader;
	}

	/**
	* 获取负责人
	*/
	public Integer getOrganizationLeader() {
	return this.organizationLeader;
	}
	/**
	* 设置父级ids
	*/
	public void setOrganizationParentId(String organizationParentId) {
	this.organizationParentId = organizationParentId;
	}

	/**
	* 获取父级ids
	*/
	public String getOrganizationParentId() {
	return this.organizationParentId;
	}
	/**
	* 设置机构类型
	*/
	public void setOrganizationType(String organizationType) {
	this.organizationType = organizationType;
	}

	/**
	* 获取机构类型
	*/
	public String getOrganizationType() {
	return this.organizationType;
	}
}
