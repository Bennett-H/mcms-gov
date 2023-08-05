/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.action;

import java.util.MissingResourceException;

/**
 * progress基础控制层
 * @author 铭飞科技
 * 创建日期：2021-3-18 11:50:14<br/>
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
			str = getLocaleString(key, net.mingsoft.progress.constant.Const.RESOURCES);
		}

		return str;
	}

}
