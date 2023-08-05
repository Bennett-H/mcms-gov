/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.approval.action;

import java.util.MissingResourceException;

/**
 * approval基础控制层
 * @author 铭软科技
 * 创建日期：2021-3-19 16:46:27<br/>
 * 历史修订：<br/>
 */
public class BaseAction extends net.mingsoft.basic.action.BaseAction{

	@Override
	protected String getResString(String key) {
		// TODO Auto-generated method stub
		String str = "";
		try {
			str = super.getResString(key);
		} catch (MissingResourceException e) {
			str = getLocaleString(key, net.mingsoft.approval.constant.Const.RESOURCES);
		}

		return str;
	}

}
