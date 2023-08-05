/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.dao;

import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.entity.CityEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 省市县镇村数据持久层
 * @author 伍晶晶
 * @version 
 * 版本号：100<br/>
 * 创建日期：2017-7-27 14:47:29<br/>
 * 历史修订：<br/>
 */
public interface ICityDao extends IBaseDao<CityEntity> {
	
	/**
	 * 查询省／直辖市／自治区
	 * @return 省／直辖市／自治区列表
	 */
	public List<CityEntity> queryProvince();
	/**
	 * 查询市
	 * @param cityEntity 城市实体
	 * city_id
	 * city_name
	 * @return 市级列表
	 */
	public List<CityEntity> queryCity(CityEntity cityEntity);
	/**
	 * 查询区／县
	 * @param cityEntity
	 * county_id
	 * county_name
	 * @return 区／县列表

	 */
	public List<CityEntity> queryCounty(CityEntity cityEntity);
	/**
	 * 查询街道／镇
	 * @param cityEntity
	 * town_id
	 * town_name
	 * @return 街道／镇列表
	 */
	public List<CityEntity> queryTown(CityEntity cityEntity);
	/**
	 * 查询村委会
	 * @param cityEntity
	 * village_id
	 * village_name
	 * @return 村委会列表
	 */
	public List<CityEntity> queryVillage(CityEntity cityEntity);
	
	/**
	 * 通过层级过滤城市数据，提高性能
	 * @param level 默认3级获取到区数据
	 * @return 列表
	 */
	public List<CityEntity> queryByLevel(@Param("level")int level);

	public List<CityEntity> queryById(CityEntity cityEntity);

	CityEntity queryByEntity(CityEntity city);

	List<CityEntity> queryProvinceAndName(CityEntity city);
}

