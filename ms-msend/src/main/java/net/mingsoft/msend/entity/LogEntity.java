/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.msend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.base.entity.BaseEntity;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

 /**
 * 发送日志实体
 * @author 铭飞开发团队
 * @version
 * 版本号：0.0<br/>
 * 创建日期：2017-8-24 14:41:18<br/>
 * 历史修订：<br/>
 */
 @TableName("msend_log")
public class LogEntity extends BaseEntity {

	private static final long serialVersionUID = 1503556878953L;
	/**
	 * 时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date logDatetime;
	/**
	 * 接收内容
	 */
	private String logContent;
	/**
	 * 接收人
	 */
	private String logReceive;
	/**
	 * 日志类型0邮件1短信
	 */
	private String logType;

	 /**
	  * 模版id
	  */
	private int templateId;

	/**
	 * 设置时间
	 */
	public void setLogDatetime(Date logDatetime) {
		this.logDatetime = logDatetime;
	}

	/**
	 * 获取时间
	 */
	public Date getLogDatetime() {
		return this.logDatetime;
	}

	/**
	 * 设置接收内容
	 */
	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}

	/**
	 * 获取接收内容
	 */
	public String getLogContent() {
		return this.logContent;
	}

	/**
	 * 设置接收人
	 */
	public void setLogReceive(String logReceive) {
		this.logReceive = logReceive;
	}

	/**
	 * 获取接收人
	 */
	public String getLogReceive() {
		return this.logReceive;
	}

	/**
	 * 设置日志类型0邮件1短信
	 */
	public void setLogType(String logType) {
		this.logType = logType;
	}

	/**
	 * 获取日志类型0邮件1短信
	 */
	public String getLogType() {
		return this.logType;
	}

	 public int getTemplateId() {
		 return templateId;
	 }

	 public void setTemplateId(int templateId) {
		 this.templateId = templateId;
	 }
 }
