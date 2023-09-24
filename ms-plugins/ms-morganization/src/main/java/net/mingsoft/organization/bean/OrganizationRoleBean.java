/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.bean;

import net.mingsoft.organization.entity.EmployeeEntity;

import java.util.List;

/**
 * 角色关联成员bean
 */
public class OrganizationRoleBean extends net.mingsoft.basic.bean.RoleBean {
	/**
	 * 角色名称(用于级联显示)
	 */
	private String managerNickName;
	/**
	 * 角色id(用于级联显示)
	 */
	private int managerId;
	/**
	 * 级联的禁用
	 */
	private boolean disabled;
	/**
	 * 成员列表
	 */
	private List<EmployeeEntity> employeeList;

	public String getManagerNickName() {
		return managerNickName;
	}

	public void setManagerNickName(String managerNickName) {
		this.managerNickName = managerNickName;
	}

	public int getManagerId() {
		return managerId;
	}

	public void setManagerId(int managerId) {
		this.managerId = managerId;
	}

	public List<EmployeeEntity> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<EmployeeEntity> employeeList) {
		this.employeeList = employeeList;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
