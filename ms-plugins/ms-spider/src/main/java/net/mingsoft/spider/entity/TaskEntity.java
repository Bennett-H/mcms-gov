/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.base.entity.BaseEntity;
import java.util.Date;
/**
* 采集任务实体
* @author 铭软科技
* 创建日期：2020-9-11 10:35:05<br/>
* 历史修订：<br/>
*/
@TableName("spider_task")
public class TaskEntity extends BaseEntity {

private static final long serialVersionUID = 1599791705262L;
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	* 采集名称
	*/
	private String taskName;
	/**
	* 导入表
	*/
	private String importTable;
	/**
	* 自动导入
	*/
	private String isAutoImport;
	/**
	* 是否去重
	*/
	private String isRepeat;


	/**
	* 设置采集名称
	*/
	public void setTaskName(String taskName) {
	this.taskName = taskName;
	}

	/**
	* 获取采集名称
	*/
	public String getTaskName() {
	return this.taskName;
	}
	/**
	* 设置导入表
	*/
	public void setImportTable(String importTable) {
	this.importTable = importTable;
	}

	/**
	* 获取导入表
	*/
	public String getImportTable() {
	return this.importTable;
	}
	/**
	* 设置自动导入
	*/
	public void setIsAutoImport(String isAutoImport) {
	this.isAutoImport = isAutoImport;
	}

	/**
	* 获取自动导入
	*/
	public String getIsAutoImport() {
	return this.isAutoImport;
	}
	/**
	* 设置是否去重
	*/
	public void setIsRepeat(String isRepeat) {
	this.isRepeat = isRepeat;
	}

	/**
	* 获取是否去重
	*/
	public String getIsRepeat() {
	return this.isRepeat;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
}
