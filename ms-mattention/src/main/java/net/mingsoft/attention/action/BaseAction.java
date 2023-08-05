/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.attention.action;


import net.mingsoft.attention.constant.Const;

import java.util.MissingResourceException;


/**
 * 商城基础action类
 * @author 铭飞开源团队
 * @date 2019年7月16日
 */
public class BaseAction extends net.mingsoft.people.action.BaseAction {

	@Override
	protected String getResString(String key) {
		// TODO Auto-generated method stub
		String str = "";
		try {
			str = super.getResString(key);
		} catch (MissingResourceException e) {
			str = getLocaleString(key, Const.RESOURCES);
		}
		return str;
	}

}
