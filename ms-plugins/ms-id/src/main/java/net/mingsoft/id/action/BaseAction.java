/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.id.action;

import java.util.MissingResourceException;

/**
 * id基础控制层
 * @author  会 
 * 创建日期：2020-5-26 16:14:39<br/>
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
			str = net.mingsoft.id.constant.Const.RESOURCES.getString(key);
		}

		return str;
	}

}
