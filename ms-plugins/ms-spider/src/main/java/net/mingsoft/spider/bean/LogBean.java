/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.bean;

import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.spider.entity.LogEntity;

/**
* 日志表实体
* @author 铭软科技
* 创建日期：2020-9-12 10:51:17<br/>
* 历史修订：<br/>
*/
public class LogBean extends LogEntity {

	/**
	 * 规则名称
	 */
	private String regularName;
	/**
	 * 任务名称
	 */
	private String taskName;
	/**
	 * 导入的表
	 */
	private String importTable;

	public String getImportTable() {
		return importTable;
	}

	public void setImportTable(String importTable) {
		this.importTable = importTable;
	}

	public String getRegularName() {
		return regularName;
	}

	public void setRegularName(String regularName) {
		this.regularName = regularName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
}
