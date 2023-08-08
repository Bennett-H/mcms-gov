/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.ad.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.base.entity.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
/**
* 广告实体
* @author 铭飞开发团队
* 创建日期：2019-11-23 8:49:39<br/>
* 历史修订：<br/>
*/
@TableName("ad_ads")
public class AdsEntity extends BaseEntity {

private static final long serialVersionUID = 1574470179902L;

	/**
	* 广告位编号
	*/
	private Integer positionId;
	/**
	* 广告名称
	*/
	private String adsName;
	/**
	* 广告类型
	*/
	private String adsType;
	/**
	* 广告链接
	*/
	private String adsUrl;

	/**
	 * 广告图片
	 */
	private String adsImg;

	/**
	* 开始时间
	*/
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	private Date adsStartTime;
	/**
	* 结束时间
	*/
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	private Date adsEndTime;
	/**
	* 是否开启
	*/
	private String adsState;
	/**
	* 广告联系人
	*/
	private String adsPeopleName;
	/**
	* 广告联系人电话
	*/
	private String adsPeoplePhone;
	/**
	* 广告联系人邮箱
	*/
	private String adsPeopleEmail;


	/**
	* 设置广告位编号
	*/
	public void setPositionId(Integer positionId) {
	this.positionId = positionId;
	}

	/**
	* 获取广告位编号
	*/
	public Integer getPositionId() {
	return this.positionId;
	}
	/**
	* 设置广告名称
	*/
	public void setAdsName(String adsName) {
	this.adsName = adsName;
	}

	/**
	* 获取广告名称
	*/
	public String getAdsName() {
	return this.adsName;
	}
	/**
	* 设置广告类型
	*/
	public void setAdsType(String adsType) {
	this.adsType = adsType;
	}

	/**
	* 获取广告类型
	*/
	public String getAdsType() {
	return this.adsType;
	}
	/**
	* 设置广告链接
	*/
	public void setAdsUrl(String adsUrl) {
	this.adsUrl = adsUrl;
	}

	/**
	* 获取广告链接
	*/
	public String getAdsUrl() {
	return this.adsUrl;
	}
	/**
	* 设置开始时间
	*/
	public void setAdsStartTime(Date adsStartTime) {
	this.adsStartTime = adsStartTime;
	}

	/**
	* 获取开始时间
	*/
	public Date getAdsStartTime() {
	return this.adsStartTime;
	}
	/**
	* 设置结束时间
	*/
	public void setAdsEndTime(Date adsEndTime) {
	this.adsEndTime = adsEndTime;
	}

	/**
	* 获取结束时间
	*/
	public Date getAdsEndTime() {
	return this.adsEndTime;
	}

	/**
	 * 设置广告图片
	 */
	public void setAdsImg(String adsImg) {
		this.adsImg = adsImg;
	}

	/**
	 * 获取广告图片
	 */
	public String getAdsImg() {
		return this.adsImg;
	}

	/**
	* 设置是否开启
	*/
	public void setAdsState(String adsState) {
	this.adsState = adsState;
	}

	/**
	* 获取是否开启
	*/
	public String getAdsState() {
	return this.adsState;
	}
	/**
	* 设置广告联系人
	*/
	public void setAdsPeopleName(String adsPeopleName) {
	this.adsPeopleName = adsPeopleName;
	}

	/**
	* 获取广告联系人
	*/
	public String getAdsPeopleName() {
	return this.adsPeopleName;
	}
	/**
	* 设置广告联系人电话
	*/
	public void setAdsPeoplePhone(String adsPeoplePhone) {
	this.adsPeoplePhone = adsPeoplePhone;
	}

	/**
	* 获取广告联系人电话
	*/
	public String getAdsPeoplePhone() {
	return this.adsPeoplePhone;
	}
	/**
	* 设置广告联系人邮箱
	*/
	public void setAdsPeopleEmail(String adsPeopleEmail) {
	this.adsPeopleEmail = adsPeopleEmail;
	}

	/**
	* 获取广告联系人邮箱
	*/
	public String getAdsPeopleEmail() {
	return this.adsPeopleEmail;
	}
}
