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
* 采集规则实体
* @author 铭软科技
* 创建日期：2020-9-11 10:35:05<br/>
* 历史修订：<br/>
*/
@TableName("spider_task_regular")
public class TaskRegularEntity extends BaseEntity {

private static final long serialVersionUID = 1599791705338L;

	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	/**
	* 任务主键
	*/
	private String taskId;
	/**
	* 规则名称
	*/
	private String regularName;
	/**
	* 线程数
	*/
	private Integer threadNum;
	/**
	* 字符编码
	*/
	private String charset;
	/**
	* 被采集页面
	*/
	private String linkUrl;
	/**
	* 是否分页
	*/
	private String isPage;
	/**
	* 起始页
	*/
	private String startPage;
	/**
	* 结束页
	*/
	private String endPage;
	/**
	* 列表开始区域
	*/
	private String startArea;
	/**
	* 列表结束区域
	*/
	private String endArea;
	/**
	* 内容链接匹配
	*/
	private String articleUrl;

	/**
	 * 元数据
	 */
	private String metaData;

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

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
	* 设置规则名称
	*/
	public void setRegularName(String regularName) {
	this.regularName = regularName;
	}

	/**
	* 获取规则名称
	*/
	public String getRegularName() {
	return this.regularName;
	}
	/**
	* 设置线程数
	*/
	public void setThreadNum(Integer threadNum) {
	this.threadNum = threadNum;
	}

	/**
	* 获取线程数
	*/
	public Integer getThreadNum() {
	return this.threadNum;
	}
	/**
	* 设置字符编码
	*/
	public void setCharset(String charset) {
	this.charset = charset;
	}

	/**
	* 获取字符编码
	*/
	public String getCharset() {
	return this.charset;
	}
	/**
	* 设置被采集页面
	*/
	public void setLinkUrl(String linkUrl) {
	this.linkUrl = linkUrl;
	}

	/**
	* 获取被采集页面
	*/
	public String getLinkUrl() {
	return this.linkUrl;
	}
	/**
	* 设置是否分页
	*/
	public void setIsPage(String isPage) {
	this.isPage = isPage;
	}

	/**
	* 获取是否分页
	*/
	public String getIsPage() {
	return this.isPage;
	}
	/**
	* 设置起始页
	*/
	public void setStartPage(String startPage) {
	this.startPage = startPage;
	}

	/**
	* 获取起始页
	*/
	public String getStartPage() {
	return this.startPage;
	}
	/**
	* 设置结束页
	*/
	public void setEndPage(String endPage) {
	this.endPage = endPage;
	}

	/**
	* 获取结束页
	*/
	public String getEndPage() {
	return this.endPage;
	}
	/**
	* 设置列表开始区域
	*/
	public void setStartArea(String startArea) {
	this.startArea = startArea;
	}

	/**
	* 获取列表开始区域
	*/
	public String getStartArea() {
	return this.startArea;
	}
	/**
	* 设置列表结束区域
	*/
	public void setEndArea(String endArea) {
	this.endArea = endArea;
	}

	/**
	* 获取列表结束区域
	*/
	public String getEndArea() {
	return this.endArea;
	}
	/**
	* 设置内容链接匹配
	*/
	public void setArticleUrl(String articleUrl) {
	this.articleUrl = articleUrl;
	}

	/**
	* 获取内容链接匹配
	*/
	public String getArticleUrl() {
	return this.articleUrl;
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
