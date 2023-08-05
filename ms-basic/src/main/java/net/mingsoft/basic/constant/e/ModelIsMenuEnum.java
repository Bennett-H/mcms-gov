/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * 菜单类型枚举
 * @Package net.mingsoft.basic.constant.e 
 * @author 李书宇
 * @version 
 * 版本号：<br/>
 * 创建日期：@date 2015年10月21日<br/>
 * 历史修订：<br/>
 */
public enum ModelIsMenuEnum implements BaseEnum{
	/**
	 * 是否是菜单0非菜单
	 */
	MODEL_NOTMENU(0,"否"),
	
	/**
	 * 是否是菜单1菜单
	 */
	MODEL_MEUN(1,"是");
	
	private String code;
	
	private int id;

	/**
	 * 构造方法
	 * @param id 默认ID
	 * @param code 传入的枚举类型
	 */
	ModelIsMenuEnum(int id,String code) {
		this.code = code;
		this.id = id;
	}

	@Override
	public int toInt() {
		return this.id;
	}

	@Override
	public String toString() {
		return this.code.toString();
	}

}
