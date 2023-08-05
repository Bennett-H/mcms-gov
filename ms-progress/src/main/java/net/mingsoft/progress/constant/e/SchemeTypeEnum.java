/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * 方案进度类型枚举
 * @author 铭飞开发团队
 * 创建日期：2018-10-18 10:19:59<br/>
 * 历史修订：<br/>
 */
public enum SchemeTypeEnum implements BaseEnum {
	/**
	 * 累加 所有进度值总和应不超过100%
	 */
	ADD("add"),
	/**
	 * 固定值
	 */
	SET("set");
	private final String code;
	SchemeTypeEnum(String code){
		this.code =code;
	}

	@Override
	public int toInt() {
	  return 0;
	}
	@Override
	public String toString(){
		return this.code;
	}
}
