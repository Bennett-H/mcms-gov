/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.constant;

import net.mingsoft.base.constant.e.BaseSessionEnum;

/**
 * 微信系统中的session枚举类
 * @author 成卫雄(qq:330216230)
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2015年8月30日 下午4:44:20<br/>
 * 历史修订：<br/>
 */
public enum SessionConst  implements BaseSessionEnum {

	WEIXIN_SESSION("weixin_session"),
	WEIXIN_PEOPLE_SESSION("weixin_people_session");

	/**
	 * 设置session常量
	 * @paran attr 常量
	 */
	SessionConst(String attr){
		this.attr = attr;
	}

	private String attr;

	/**
	 *  返回SessionConst常量的字符串表示
	 *  @return 字符串
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return attr;
	}

}
