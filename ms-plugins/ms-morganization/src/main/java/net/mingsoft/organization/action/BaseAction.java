/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.action;

import net.mingsoft.base.util.BundleUtil;

import java.util.MissingResourceException;

/**
 * organization基础控制层
 * @author 铭飞开源团队
 * 创建日期：2020-1-6 14:53:26<br/>
 * 历史修订：<br/>
 */
public class BaseAction extends net.mingsoft.basic.action.BaseAction{

	/**
	 * 读取国际化资源文件(没有占位符号的)，优先模块对应的资源文件，如果模块资源文件找不到就会优先基础层
	 * @param key 国际化文件key
	 * @return 国际化字符串
	 */
	protected String getResString(String key) {
		return this.getResString(key,"");
	}

	/**
	 * 读取国际化资源文件，优先模块对应的资源文件，如果模块资源文件找不到就会优先基础层
	 * @param key 国际化文件key
	 * @param params 拼接值
	 * @return 国际化字符串
	 */
	protected String getResString(String key,String... params) {
		// TODO Auto-generated method stub
		String str = "";
		try {
			str = super.getResString(key);
			//替换占位
			for (int i = 0; i < params.length; i++) {
				str = str.replace("{" + i + "}", params[i]);
			}
		} catch (MissingResourceException e) {
			str = BundleUtil.getString(net.mingsoft.organization.constant.Const.RESOURCES, key,params);
		}

		return str;
	}

}
