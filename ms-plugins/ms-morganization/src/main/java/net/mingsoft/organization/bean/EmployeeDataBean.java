/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.bean;

import javax.print.DocFlavor;

/**
* 员工权限bean
* @author 铭飞开源团队
* 创建日期：2022-1-17 15:24:39<br/>
* 历史修订：<br/>
*/
public class EmployeeDataBean {
	/**
	* 员工Id
	*/
	private String EmployeeId;
	/**
	* 组织机构ids
	*/
	private String organizationIds;

	/**
	 * 权限类型
	 */
	private String dataType;

	/**
	 * 查询sql
	 */

	private String configName;

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	/**
	* 设置员工Id
	*/
	public void setEmployeeId(String employeeId) {
	this.EmployeeId = employeeId;
	}

	/**
	* 获取员工Id
	*/
	public String getEmployeeId() {
	return this.EmployeeId;
	}
	/**
	* 设置组织机构ids
	*/
	public void setOrganizationIds(String organizationIds) {
	this.organizationIds = organizationIds;
	}

	/**
	* 获取组织机构ids
	*/
	public String getOrganizationIds() {
	return this.organizationIds;
	}
}
