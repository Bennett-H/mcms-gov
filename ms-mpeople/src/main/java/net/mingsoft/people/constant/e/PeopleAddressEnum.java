/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * 
 * 用户收货地址状态枚举类
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
public enum PeopleAddressEnum implements BaseEnum{
	/**
	 * 默认收货地址
	 */
	ADDRESS_DEFAULT(0),
	
	/**
	 * 非默认收货地址
	 */
	ADDRESS_NOT_DEFAULT(1);
	
	PeopleAddressEnum(Object code) {
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
