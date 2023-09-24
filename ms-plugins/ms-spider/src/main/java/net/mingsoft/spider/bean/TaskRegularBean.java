/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.bean;

import net.mingsoft.spider.entity.TaskRegularEntity;

/**
* 采集规则实体
* @author 铭软科技
* 创建日期：2020-9-11 10:35:05<br/>
* 历史修订：<br/>
*/
public class TaskRegularBean extends TaskRegularEntity {
	private String importTable;

	public String getImportTable() {
		return importTable;
	}

	public void setImportTable(String importTable) {
		this.importTable = importTable;
	}
}
