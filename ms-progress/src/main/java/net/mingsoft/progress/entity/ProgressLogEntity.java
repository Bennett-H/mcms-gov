/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
/**
* 进度日志实体
* @author 铭飞科技
* 创建日期：2021-3-5 14:29:00<br/>
* 历史修订：<br/>
*/
@TableName("PROGRESS_PROGRESS_LOG")
public class ProgressLogEntity extends BaseEntity {

private static final long serialVersionUID = 1614925740898L;

	/**
	* 进度方案关联id
	*/
	private Integer schemeId;
	/**
	* 进度关联id
	*/
	private Integer progressId;
	/**
	* 进度名称
	*/
	private String plNodeName;
	/**
	* 业务编号
	*/
	private String dataId;
	/**
	* 操作人
	*/
	private String plOperator;
	/**
	* 进度数
	*/
	private Integer plNumber;
	/**
	* 状态
	*/
	private String plStatus;
	/**
	* 是否处于完成状态
	*/
	private String plFinished;
	/**
	* 操作内容
	*/
	private String plContent;


	/**
	* 设置进度方案关联id
	*/
	public void setSchemeId(Integer schemeId) {
	this.schemeId = schemeId;
	}

	/**
	* 获取进度方案关联id
	*/
	public Integer getSchemeId() {
	return this.schemeId;
	}
	/**
	* 设置进度关联id
	*/
	public void setProgressId(Integer progressId) {
	this.progressId = progressId;
	}

	/**
	* 获取进度关联id
	*/
	public Integer getProgressId() {
	return this.progressId;
	}
	/**
	* 设置进度名称
	*/
	public void setPlNodeName(String plNodeName) {
	this.plNodeName = plNodeName;
	}

	/**
	* 获取进度名称
	*/
	public String getPlNodeName() {
	return this.plNodeName;
	}
	/**
	* 设置业务编号
	*/
	public void setDataId(String dataId) {
	this.dataId = dataId;
	}

	/**
	* 获取业务编号
	*/
	public String getDataId() {
	return this.dataId;
	}
	/**
	* 设置操作人
	*/
	public void setPlOperator(String plOperator) {
	this.plOperator = plOperator;
	}

	/**
	* 获取操作人
	*/
	public String getPlOperator() {
	return this.plOperator;
	}
	/**
	* 设置进度数
	*/
	public void setPlNumber(Integer plNumber) {
	this.plNumber = plNumber;
	}

	/**
	* 获取进度数
	*/
	public Integer getPlNumber() {
	return this.plNumber;
	}
	/**
	* 设置状态
	*/
	public void setPlStatus(String plStatus) {
	this.plStatus = plStatus;
	}

	/**
	* 获取状态
	*/
	public String getPlStatus() {
	return this.plStatus;
	}
	/**
	* 设置是否处于完成状态
	*/
	public void setPlFinished(String plFinished) {
	this.plFinished = plFinished;
	}

	/**
	* 获取是否处于完成状态
	*/
	public String getPlFinished() {
	return this.plFinished;
	}
	/**
	* 设置操作内容
	*/
	public void setPlContent(String plContent) {
	this.plContent = plContent;
	}

	/**
	* 获取操作内容
	*/
	public String getPlContent() {
	return this.plContent;
	}
}
