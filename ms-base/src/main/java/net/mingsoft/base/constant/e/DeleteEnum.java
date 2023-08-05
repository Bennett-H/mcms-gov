/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.base.constant.e;

/**
 * 
 * @ClassName:  DeleteEnum   
 * @Description:TODO(删除枚举)
 * @author: 铭飞开发团队
 * @date:   2018年3月19日 下午3:34:02   
 *     
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 */
public enum DeleteEnum implements BaseEnum{
	/**
	 * 伪删除（NOTDEL正常,值为0）
	 */
	NOTDEL(0,"正常"),

	/**
	 * 伪删除（DEL已删除,值为1）
	 */
	DEL(1,"已删除");
	

	
	private String code;
	
	private int id;

	/**
	 * 构造方法
	 * @param id 默认ID
	 * @param code 传入的枚举类型
	 */
	DeleteEnum(int id,String code) {
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
