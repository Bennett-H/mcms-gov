/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import net.mingsoft.base.constant.e.BaseEnum;
import net.mingsoft.base.entity.BaseEntity;

/**
 * 
 * 用户收货地址实体类
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
public class PeopleAddressEntity extends BaseEntity{

	/**
	 * 对应用户基础信息拓展表的id
	 */
	int peopleId;
	
	/**
	 * 用户收货人姓名
	 */
	String peopleAddressConsigneeName;
	
	/**
	 * 收货人所在的省
	 */
	String peopleAddressProvince;
	
	/**
	 * 收货人所在的省
	 */
	long peopleAddressProvinceId;
	
	/**
	 * 收货人所在的市
	 */
	String peopleAddressCity;
	
	long peopleAddressCityId;
	
	/**
	 * 收货人所在区
	 */
	String peopleAddressDistrict;
	long peopleAddressDistrictId;
	
	/**
	 * 街道
	 */
	String peopleAddressStreet;
	
	long peopleAddressStreetId;
	
	/**
	 * 收货人的详细收货地址
	 */
	String peopleAddressAddress;
	
	/**
	 * 收货人邮箱
	 */
	String peopleAddressMail;
	
	/**
	 * 收货人手机
	 */
	String peopleAddressPhone;
	
	/**
	 * 是否是收货人最终收货地址。0代表是，1代表不是，默认为0
	 */
	int peopleAddressDefault;

	public int getPeopleId() {
		return peopleId;
	}

	public void setPeopleId(int peopleId) {
		this.peopleId = peopleId;
	}

	public String getPeopleAddressConsigneeName() {
		return peopleAddressConsigneeName;
	}

	public void setPeopleAddressConsigneeName(String peopleAddressConsigneeName) {
		this.peopleAddressConsigneeName = peopleAddressConsigneeName;
	}

	public String getPeopleAddressProvince() {
		return peopleAddressProvince;
	}

	public void setPeopleAddressProvince(String peopleAddressProvince) {
		this.peopleAddressProvince = peopleAddressProvince;
	}

	public String getPeopleAddressCity() {
		return peopleAddressCity;
	}

	public void setPeopleAddressCity(String peopleAddressCity) {
		this.peopleAddressCity = peopleAddressCity;
	}

	public String getPeopleAddressDistrict() {
		return peopleAddressDistrict;
	}

	public void setPeopleAddressDistrict(String peopleAddressDistrict) {
		this.peopleAddressDistrict = peopleAddressDistrict;
	}

	public String getPeopleAddressAddress() {
		return peopleAddressAddress;
	}

	public void setPeopleAddressAddress(String peopleAddressAddress) {
		this.peopleAddressAddress = peopleAddressAddress;
	}

	public String getPeopleAddressMail() {
		return peopleAddressMail;
	}

	public void setPeopleAddressMail(String peopleAddressMail) {
		this.peopleAddressMail = peopleAddressMail;
	}

	public String getPeopleAddressPhone() {
		return peopleAddressPhone;
	}

	public void setPeopleAddressPhone(String peopleAddressPhone) {
		this.peopleAddressPhone = peopleAddressPhone;
	}

	public int getPeopleAddressDefault() {
		return peopleAddressDefault;
	}
	
	/**
	 * 推荐使用枚举类形参方法，此方法过时
	 * @param peopleAddressDefault
	 */
	@Deprecated
	public void setPeopleAddressDefault(int peopleAddressDefault) {
		this.peopleAddressDefault = peopleAddressDefault;
	}
	/**
	 * 枚举类形参方法
	 * @param peopleAddressDefault
	 */
	public void setPeopleAddressDefault(BaseEnum peopleAddressDefault) {
		this.peopleAddressDefault = peopleAddressDefault.toInt();
	}

	public String getPeopleAddressStreet() {
		return peopleAddressStreet;
	}

	public void setPeopleAddressStreet(String peopleAddressStreet) {
		this.peopleAddressStreet = peopleAddressStreet;
	}

	public long getPeopleAddressProvinceId() {
		return peopleAddressProvinceId;
	}

	public void setPeopleAddressProvinceId(long peopleAddressProvinceId) {
		this.peopleAddressProvinceId = peopleAddressProvinceId;
	}

	public long getPeopleAddressCityId() {
		return peopleAddressCityId;
	}

	public void setPeopleAddressCityId(long peopleAddressCityId) {
		this.peopleAddressCityId = peopleAddressCityId;
	}

	public long getPeopleAddressDistrictId() {
		return peopleAddressDistrictId;
	}

	public void setPeopleAddressDistrictId(long peopleAddressDistrictId) {
		this.peopleAddressDistrictId = peopleAddressDistrictId;
	}

	public long getPeopleAddressStreetId() {
		return peopleAddressStreetId;
	}

	public void setPeopleAddressStreetId(long peopleAddressStreetId) {
		this.peopleAddressStreetId = peopleAddressStreetId;
	}
	
	

}
