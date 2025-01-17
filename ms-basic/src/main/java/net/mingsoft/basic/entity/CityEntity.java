/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.base.entity.BaseEntity;

 /**
 * 省市县镇村数据实体
 * @author 伍晶晶
 * @version 
 * 版本号：100<br/>
 * 创建日期：2017-7-27 14:47:29<br/>
 * 历史修订：<br/>
 */
@TableName("city")
public class CityEntity extends BaseEntity {

	private static final long serialVersionUID = 1501138049606L;
	
	/**
	 * 省／直辖市／自治区级id
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long provinceId; 
	/**
	 * 省／直辖市／自治区级名称
	 */
	private String provinceName; 
	/**
	 * 市级id 
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long cityId; 
	/**
	 * 市级名称
	 */
	private String cityName; 
	/**
	 * 城市拼音首字母
	 */
	@TableField(exist = false)
	private String cityPy; 
	/**
	 * 县／区级id
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long countyId; 
	/**
	 * 县／区级名称
	 */
	private String countyName; 
	/**
	 * 街道／镇级id
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long townId; 
	/**
	 * 街道／镇级名称
	 */
	private String townName; 
	/**
	 * 村委会id
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long villageId; 
	/**
	 * 村委会名称
	 */
	private String villageName; 
	
	public CityEntity(){}
	
	public CityEntity(Long provinceId) {
		this.provinceId = provinceId;
	
	}
	
	public CityEntity(Long provinceId,String provinceName) {
		this.provinceId = provinceId;
		this.provinceName = provinceName;
	
	}
	
	public CityEntity(Long provinceId,String provinceName,Long cityId) {
		this.provinceId = provinceId;
		this.provinceName = provinceName;
		this.cityId = cityId;
	
	}
	
	public CityEntity(Long provinceId,String provinceName,Long cityId,String cityName) {
		this.provinceId = provinceId;
		this.provinceName = provinceName;
		this.cityId = cityId;
		this.cityName = cityName;
	
	}
	
	public CityEntity(Long provinceId,String provinceName,Long cityId,String cityName,Long countyId) {
		this.provinceId = provinceId;
		this.provinceName = provinceName;
		this.cityId = cityId;
		this.cityName = cityName;
		this.countyId = countyId;
	
	}
	
	public CityEntity(Long provinceId,String provinceName,Long cityId,String cityName,Long countyId,String countyName) {
		this.provinceId = provinceId;
		this.provinceName = provinceName;
		this.cityId = cityId;
		this.cityName = cityName;
		this.countyId = countyId;
		this.countyName = countyName;
	
	}
	
	public CityEntity(Long provinceId,String provinceName,Long cityId,String cityName,Long countyId,String countyName,Long townId) {
		this.provinceId = provinceId;
		this.provinceName = provinceName;
		this.cityId = cityId;
		this.cityName = cityName;
		this.countyId = countyId;
		this.countyName = countyName;
		this.townId = townId;
	
	}
	
	public CityEntity(Long provinceId,String provinceName,Long cityId,String cityName,Long countyId,String countyName,Long townId,String townName) {
		this.provinceId = provinceId;
		this.provinceName = provinceName;
		this.cityId = cityId;
		this.cityName = cityName;
		this.countyId = countyId;
		this.countyName = countyName;
		this.townId = townId;
		this.townName = townName;
	
	}
	
	public CityEntity(Long provinceId,String provinceName,Long cityId,String cityName,Long countyId,String countyName,Long townId,String townName,Long villageId) {
		this.provinceId = provinceId;
		this.provinceName = provinceName;
		this.cityId = cityId;
		this.cityName = cityName;
		this.countyId = countyId;
		this.countyName = countyName;
		this.townId = townId;
		this.townName = townName;
		this.villageId = villageId;
	
	}
	
	public CityEntity(Long provinceId,String provinceName,Long cityId,String cityName,Long countyId,String countyName,Long townId,String townName,Long villageId,String villageName) {
		this.provinceId = provinceId;
		this.provinceName = provinceName;
		this.cityId = cityId;
		this.cityName = cityName;
		this.countyId = countyId;
		this.countyName = countyName;
		this.townId = townId;
		this.townName = townName;
		this.villageId = villageId;
		this.villageName = villageName;
	
	}
	
		
	
	/**
	 * 设置省／直辖市／自治区级id
	 */
	public void setProvinceId(Long provinceId) {
		this.provinceId = provinceId;
	}

	/**
	 * 获取省／直辖市／自治区级id
	 */
	public Long getProvinceId() {
		return this.provinceId;
	}
	
	/**
	 * 设置省／直辖市／自治区级名称
	 */
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	/**
	 * 获取省／直辖市／自治区级名称
	 */
	public String getProvinceName() {
		return this.provinceName;
	}
	
	/**
	 * 设置市级id 
	 */
	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	/**
	 * 获取市级id 
	 */
	public Long getCityId() {
		return this.cityId;
	}
	
	/**
	 * 设置市级名称
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	/**
	 * 获取市级名称
	 */
	public String getCityName() {
		return this.cityName;
	}
	
	/**
	 * 设置县／区级id
	 */
	public void setCountyId(Long countyId) {
		this.countyId = countyId;
	}

	/**
	 * 获取县／区级id
	 */
	public Long getCountyId() {
		return this.countyId;
	}
	
	/**
	 * 设置县／区级名称
	 */
	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	/**
	 * 获取县／区级名称
	 */
	public String getCountyName() {
		return this.countyName;
	}
	
	/**
	 * 设置街道／镇级id
	 */
	public void setTownId(Long townId) {
		this.townId = townId;
	}

	/**
	 * 获取街道／镇级id
	 */
	public Long getTownId() {
		return this.townId;
	}
	
	/**
	 * 设置街道／镇级名称
	 */
	public void setTownName(String townName) {
		this.townName = townName;
	}

	/**
	 * 获取街道／镇级名称
	 */
	public String getTownName() {
		return this.townName;
	}
	
	/**
	 * 设置村委会id
	 */
	public void setVillageId(Long villageId) {
		this.villageId = villageId;
	}

	/**
	 * 获取村委会id
	 */
	public Long getVillageId() {
		return this.villageId;
	}
	
	/**
	 * 设置村委会名称
	 */
	public void setVillageName(String villageName) {
		this.villageName = villageName;
	}

	/**
	 * 获取村委会名称
	 */
	public String getVillageName() {
		return this.villageName;
	}
	/**
	 * 获取城市拼音首字母
	 */
	public String getCityPy() {
		return cityPy;
	}
	/**
	 * 设置城市拼音首字母
	 */
	public void setCityPy(String cityPy) {
		this.cityPy = cityPy;
	}
}
