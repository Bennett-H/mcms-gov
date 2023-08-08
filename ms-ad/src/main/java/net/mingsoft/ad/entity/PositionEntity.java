/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.ad.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
/**
* 广告位实体
* @author 铭飞开发团队
* 创建日期：2019-11-23 8:49:39<br/>
* 历史修订：<br/>
*/
@TableName("ad_position")
public class PositionEntity extends BaseEntity {

private static final long serialVersionUID = 1574470179817L;

	/**
	* 广告位名称
	*/
	private String positionName;
	/**
	* 广告位宽度
	*/
	private Integer positionWidth;
	/**
	* 广告位高度
	*/
	private Integer positionHeight;
	/**
	* 广告位描述
	*/
	private String positionDesc;


	/**
	* 设置广告位名称
	*/
	public void setPositionName(String positionName) {
	this.positionName = positionName;
	}

	/**
	* 获取广告位名称
	*/
	public String getPositionName() {
	return this.positionName;
	}
	/**
	* 设置广告位宽度
	*/
	public void setPositionWidth(Integer positionWidth) {
	this.positionWidth = positionWidth;
	}

	/**
	* 获取广告位宽度
	*/
	public Integer getPositionWidth() {
	return this.positionWidth;
	}
	/**
	* 设置广告位高度
	*/
	public void setPositionHeight(Integer positionHeight) {
	this.positionHeight = positionHeight;
	}

	/**
	* 获取广告位高度
	*/
	public Integer getPositionHeight() {
	return this.positionHeight;
	}
	/**
	* 设置广告位描述
	*/
	public void setPositionDesc(String positionDesc) {
	this.positionDesc = positionDesc;
	}

	/**
	* 获取广告位描述
	*/
	public String getPositionDesc() {
	return this.positionDesc;
	}
}
