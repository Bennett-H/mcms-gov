/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

public enum PeopleEnum implements BaseEnum {
	/**
	 * 用户为已审核状态
	 */
	STATE_CHECK(1),
	/**
	 * 用户为未审核状态
	 */
	STATE_NOT_CHECK(0),
	
	/**
	 * 手机验证通过
	 */
	PHONE_CHECK(1),
	
	/**
	 * 手机验证不通过
	 */
	PHONE_NO_CHECK(0),
	/**
	 * 邮箱验证通过
	 */
	MAIL_CHECK(1),
	/**
	 * 邮箱验证不通过
	 */
	MAIL_NO_CHECK(0),
	/**
	 *根据用户名注册
	 */
	REGISTER_NAME(1),
	/**
	 *根据手机号注册
	 */
	REGISTER_PHONE(2),
	/**
	 *根据邮箱注册
	 */
	REGISTER_EMAIL(3);
	
	PeopleEnum(Object code) {
		this.code = code;
	}

	private Object code;

	@Override
	public String toString() {
		return code.toString();
	}

	public int toInt() {
		return Integer.parseInt(code.toString());
	}
}
