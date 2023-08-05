/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.gov.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.base.entity.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
/**
* 管理员扩展信息实体
* @author 铭软科技
* 创建日期：2022-5-25 16:03:06<br/>
* 历史修订：<br/>
*/
@TableName("MANAGER_INFO")
public class ManagerInfoEntity extends BaseEntity {

	private static final long serialVersionUID = 1653465786861L;

	/**
	* 雪花ID规则
	*/
	@TableId(type = IdType.ASSIGN_ID)
	private String id;


	/**
	* 验证码发送时间
	*/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	
	private Date sendTime;

	/**
	* 验证码
	*/
	@TableField(condition = SqlCondition.ORACLE_LIKE)
	private String managerCode;

	/**
	* 手机号
	*/
	@TableField(condition = SqlCondition.ORACLE_LIKE)
	private String managerPhone;

	/**
	* 管理员id
	*/
	@TableField(condition = SqlCondition.ORACLE_LIKE)
	private String managerId;


	/**
	* 设置验证码发送时间
	*/
	public void setSendTime(Date sendTime) {
	this.sendTime = sendTime;
	}

	/**
	* 获取验证码发送时间
	*/
	public Date getSendTime() {
	return this.sendTime;
	}
	/**
	* 设置验证码
	*/
	public void setManagerCode(String managerCode) {
	this.managerCode = managerCode;
	}

	/**
	* 获取验证码
	*/
	public String getManagerCode() {
	return this.managerCode;
	}
	/**
	* 设置手机号
	*/
	public void setManagerPhone(String managerPhone) {
	this.managerPhone = managerPhone;
	}

	/**
	* 获取手机号
	*/
	public String getManagerPhone() {
	return this.managerPhone;
	}
	/**
	* 设置管理员id
	*/
	public void setManagerId(String managerId) {
	this.managerId = managerId;
	}

	/**
	* 获取管理员id
	*/
	public String getManagerId() {
	return this.managerId;
	}


}
