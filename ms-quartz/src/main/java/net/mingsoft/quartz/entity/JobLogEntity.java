/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.quartz.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
/**
* 任务调度日志实体
* @author 铭飞开源团队
* 创建日期：2019-11-21 15:06:56<br/>
* 历史修订：<br/>
*/
@TableName("quartz_job_log")
public class JobLogEntity extends BaseEntity {

private static final long serialVersionUID = 1574320016792L;

	/**
	* 任务名称
	*/
	private String qjlName;
	/**
	* 任务组
	*/
	private String qjlGroup;
	/**
	* 调用目标
	*/
	private String qjlTarget;
	/**
	* 执行状态
	*/
	private Boolean qjlStatus;
	/**
	* 日志信息
	*/
	private String qjlMsg;
	/**
	* 错误信息
	*/
	private String qjlErrorMsg;

	/**
	* 设置任务名称
	*/
	public void setQjlName(String qjlName) {
	this.qjlName = qjlName;
	}

	/**
	* 获取任务名称
	*/
	public String getQjlName() {
	return this.qjlName;
	}
	/**
	* 设置任务组
	*/
	public void setQjlGroup(String qjlGroup) {
	this.qjlGroup = qjlGroup;
	}

	/**
	* 获取任务组
	*/
	public String getQjlGroup() {
	return this.qjlGroup;
	}
	/**
	* 设置调用目标
	*/
	public void setQjlTarget(String qjlTarget) {
	this.qjlTarget = qjlTarget;
	}

	/**
	* 获取调用目标
	*/
	public String getQjlTarget() {
	return this.qjlTarget;
	}
	/**
	* 设置执行状态
	*/
	public void setQjlStatus(Boolean qjlStatus) {
	this.qjlStatus = qjlStatus;
	}

	/**
	* 获取执行状态
	*/
	public Boolean getQjlStatus() {
	return this.qjlStatus;
	}
	/**
	* 设置日志信息
	*/
	public void setQjlMsg(String qjlMsg) {
	this.qjlMsg = qjlMsg;
	}

	/**
	* 获取日志信息
	*/
	public String getQjlMsg() {
	return this.qjlMsg;
	}
	/**
	* 设置错误信息
	*/
	public void setQjlErrorMsg(String qjlErrorMsg) {
	this.qjlErrorMsg = qjlErrorMsg;
	}

	/**
	* 获取错误信息
	*/
	public String getQjlErrorMsg() {
	return this.qjlErrorMsg;
	}
}
