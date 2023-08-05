/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.gov.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;

/**
* 密码修改记录实体
* @author 铭软科技
* 创建日期：2021-3-12 15:06:18<br/>
* 历史修订：<br/>
*/
@TableName("SECURITY_PASSWORD")
public class PasswordEntity extends BaseEntity {

private static final long serialVersionUID = 1615532778594L;

	/**
	* 业务类型
	*/
	private String passwordType;
	/**
	* 关联用户
	*/
	private String passwordOwnerId;
	/**
	* 密钥
	*/
	private String passwordOwnerPwd;


	/**
	* 设置业务类型
	*/
	public void setPasswordType(String passwordType) {
	this.passwordType = passwordType;
	}

	/**
	* 获取业务类型
	*/
	public String getPasswordType() {
	return this.passwordType;
	}
	/**
	* 设置关联用户
	*/
	public void setPasswordOwnerId(String passwordOwnerId) {
	this.passwordOwnerId = passwordOwnerId;
	}

	/**
	* 获取关联用户
	*/
	public String getPasswordOwnerId() {
	return this.passwordOwnerId;
	}
	/**
	* 设置密钥
	*/
	public void setPasswordOwnerPwd(String passwordOwnerPwd) {
	this.passwordOwnerPwd = passwordOwnerPwd;
	}

	/**
	* 获取密钥
	*/
	public String getPasswordOwnerPwd() {
	return this.passwordOwnerPwd;
	}
}
