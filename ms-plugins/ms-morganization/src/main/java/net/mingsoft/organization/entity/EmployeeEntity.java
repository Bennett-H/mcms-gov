/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.base.entity.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
/**
* 员工实体
* @author 铭飞开源团队
* 创建日期：2020-1-6 18:25:28<br/>
* 历史修订：<br/>
*/

@TableName("ORGANIZATION_EMPLOYEE")
public class EmployeeEntity extends BaseEntity {

private static final long serialVersionUID = 1578306328591L;


	/**
	 * 管理员编号
	 */
	private Integer managerId;

	/**
	 * 员工姓名
	 */

	@TableField(condition = SqlCondition.ORACLE_LIKE)
	private String employeeNickName;

	/**
	 * 逻辑删除字段
	 */
	@TableLogic
	private Integer del;

	/**
	* 员工编号
	*/
	private String employeeCode;

	/**
	* 员工状态
	*/
	@TableField(condition = SqlCondition.ORACLE_LIKE)
	private String employeeStatus;

	/**
	* 性别
	*/
	private Integer employeeSex;

	/**
	* 所属角色
	*/
	private String employeeRoleIds;

	/**
	* 岗位
	*/
	@TableField(condition = SqlCondition.ORACLE_LIKE)
	private String postIds;

	/**
	* 所属部门
	*/
	private String organizationId;

	/**
	* 政治面貌
	*/
	private String employeePolitics;

	/**
	* 员工学历
	*/
	private String employeeEducation;

	/**
	* 出生日期
	*/
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")

	private Date employeeBirthDate;
	/**
	* 手机号
	*/
	@TableField(condition = SqlCondition.ORACLE_LIKE)
	private String employeePhone;
	/**
	 * 所属部门名称（不参与表结构）
	 */
	@TableField(exist = false)
	private String organizationTitle;


	/**
	 * 岗位名称（不参与表结构）
	 */
	@TableField(exist = false)
	private String postName;


	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public String getOrganizationTitle() {
		return organizationTitle;
	}

	public void setOrganizationTitle(String organizationTitle) {
		this.organizationTitle = organizationTitle;
	}

	/**
	* 设置员工编号
	*/
	public void setEmployeeCode(String employeeCode) {
	this.employeeCode = employeeCode;
	}

	/**
	* 获取员工编号
	*/
	public String getEmployeeCode() {
	return this.employeeCode;
	}
	/**
	* 设置员工状态
	*/
	public void setEmployeeStatus(String employeeStatus) {
	this.employeeStatus = employeeStatus;
	}

	/**
	* 获取员工状态
	*/
	public String getEmployeeStatus() {
	return this.employeeStatus;
	}
	/**
	* 设置性别
	*/
	public void setEmployeeSex(Integer employeeSex) {
	this.employeeSex = employeeSex;
	}

	/**
	* 获取性别
	*/
	public Integer getEmployeeSex() {
	return this.employeeSex;
	}

	/**
	 * 设置所属角色
	 */
	public String getEmployeeRoleIds() {
		return employeeRoleIds;
	}

	/**
	 * 获取所属角色
	 */
	public void setEmployeeRoleIds(String employeeRoleIds) {
		this.employeeRoleIds = employeeRoleIds;
	}

	/**
	* 设置岗位
	*/
	public void setPostIds(String postIds) {
	this.postIds = postIds;
	}

	/**
	* 获取岗位
	*/
	public String getPostIds() {
	return this.postIds;
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

	public Date getEmployeeBirthDate() {
		return employeeBirthDate;
	}

	public void setEmployeeBirthDate(Date employeeBirthDate) {
		this.employeeBirthDate = employeeBirthDate;
	}

	public String getEmployeePhone() {
		return employeePhone;
	}

	public void setEmployeePhone(String employeePhone) {
		this.employeePhone = employeePhone;
	}

	/**
	 * 设置管理员编号
	 */
	public Integer getManagerId() {
		return managerId;
	}
	/**
	 * 获取管理员编号
	 */
	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}



	public String getEmployeeNickName() {
		return employeeNickName;
	}

	public void setEmployeeNickName(String employeeNickName) {
		this.employeeNickName = employeeNickName;
	}



	public String getEmployeePolitics() {
		return employeePolitics;
	}

	public void setEmployeePolitics(String employeePolitics) {
		this.employeePolitics = employeePolitics;
	}

	public String getEmployeeEducation() {
		return employeeEducation;
	}

	public void setEmployeeEducation(String employeeEducation) {
		this.employeeEducation = employeeEducation;
	}
}
