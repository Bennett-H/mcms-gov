/**
 * 版权所有  铭软科技(mingsoft.net)
 * 未经授权，严禁通过任何介质未经授权复制此文件
 */
package net.mingsoft.impexp.action;

import net.mingsoft.base.util.BundleUtil;

import java.util.MissingResourceException;

/**
 * impexp基础控制层
 * @author 铭软科技
 * 创建日期：2021-2-2 17:35:57<br/>
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
			str = BundleUtil.getString(net.mingsoft.impexp.constant.Const.RESOURCES, key,params);
		}

		return str;
	}

}
