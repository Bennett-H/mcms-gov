/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.bean;

import net.mingsoft.basic.entity.RoleEntity;


/**
 * 角色Bean
 * @author qiu
 *
 */

public class RoleBean extends RoleEntity {
	/**
	 * 模块编号数组
	 */
	private String ids;

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
}
