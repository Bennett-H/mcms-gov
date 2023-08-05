/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * 文章操作类型
 * @author 铭软开发团队
 * 创建日期：2018-10-18 10:19:59<br/>
 * 历史修订：<br/>
 */
public enum ArticleOperationTypeEnum implements BaseEnum {
	/**
	 * 移动
	 */
	REMOVE("remove"),
	/**
	 * 复制
	 */
	COPY("copy");
	private String code;
	ArticleOperationTypeEnum(String code){
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
