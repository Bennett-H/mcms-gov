/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.quartz.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
/**
* 任务实体表实体
* @author 铭飞开源团队
* 创建日期：2019-11-21 15:06:56<br/>
* 历史修订：<br/>
*/
@TableName("quartz_job")
public class JobEntity extends BaseEntity {

private static final long serialVersionUID = 1574320016685L;

	/**
	* 任务名称
	*/
	private String qjName;
	/**
	* 任务组
	*/
	private String qjGroup;
	/**
	* 调用目标
	*/
	private String qjTarget;
	/**
	* 执行表达式
	*/
	private String qjCron;
	/**
	* 开启并发
	*/
	private Boolean qjAsync;
	/**
	* 状态
	*/
	private Boolean qjStatus;

	/**
	* 设置任务名称
	*/
	public void setQjName(String qjName) {
	this.qjName = qjName.trim();
	}

	/**
	* 获取任务名称
	*/
	public String getQjName() {
	return this.qjName;
	}
	/**
	* 设置任务组
	*/
	public void setQjGroup(String qjGroup) {
	this.qjGroup = qjGroup.trim();
	}

	/**
	* 获取任务组
	*/
	public String getQjGroup() {
	return this.qjGroup;
	}
	/**
	* 设置调用目标
	*/
	public void setQjTarget(String qjTarget) {
	this.qjTarget = qjTarget;
	}

	/**
	* 获取调用目标
	*/
	public String getQjTarget() {
	return this.qjTarget;
	}
	/**
	* 设置执行表达式
	*/
	public void setQjCron(String qjCron) {
	this.qjCron = qjCron;
	}

	/**
	* 获取执行表达式
	*/
	public String getQjCron() {
	return this.qjCron;
	}
	/**
	* 设置开启并发
	*/
	public void setQjAsync(Boolean qjAsync) {
	this.qjAsync = qjAsync;
	}

	/**
	* 获取开启并发
	*/
	public Boolean getQjAsync() {
	return this.qjAsync;
	}
	/**
	* 设置状态
	*/
	public void setQjStatus(Boolean qjStatus) {
	this.qjStatus = qjStatus;
	}

	/**
	* 获取状态
	*/
	public Boolean getQjStatus() {
	return this.qjStatus;
	}
}
