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
* 日志表实体
* @author 铭软科技
* 创建日期：2020-9-12 10:51:17<br/>
* 历史修订：<br/>
*/
@TableName("spider_log")
public class LogEntity extends BaseEntity {

private static final long serialVersionUID = 1599879077670L;

	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	* 任务主键
	*/
	private String taskId;
	/**
	* 规则主键
	*/
	private String regularId;
	/**
	* 是否已经导入过
	*/
	private String imported;
	/**
	* 内容链接
	*/
	private String sourceUrl;
	/**
	* 采集内容
	*/
	private String content;


	/**
	* 设置任务主键
	*/
	public void setTaskId(String taskId) {
	this.taskId = taskId;
	}

	/**
	* 获取任务主键
	*/
	public String getTaskId() {
	return this.taskId;
	}
	/**
	* 设置规则主键
	*/
	public void setRegularId(String regularId) {
	this.regularId = regularId;
	}

	/**
	* 获取规则主键
	*/
	public String getRegularId() {
	return this.regularId;
	}
	/**
	* 设置是否已经导入过
	*/
	public void setImported(String imported) {
	this.imported = imported;
	}

	/**
	* 获取是否已经导入过
	*/
	public String getImported() {
	return this.imported;
	}
	/**
	* 设置内容链接
	*/
	public void setSourceUrl(String sourceUrl) {
	this.sourceUrl = sourceUrl;
	}

	/**
	* 获取内容链接
	*/
	public String getSourceUrl() {
	return this.sourceUrl;
	}
	/**
	* 设置采集内容
	*/
	public void setContent(String content) {
	this.content = content;
	}

	/**
	* 获取采集内容
	*/
	public String getContent() {
	return this.content;
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
