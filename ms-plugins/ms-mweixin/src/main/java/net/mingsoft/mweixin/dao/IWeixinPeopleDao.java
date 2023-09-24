/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.mweixin.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mweixin.entity.WeixinPeopleEntity;
/**
 * 
 * 微信用户持久层层接口
 * @author 付琛  QQ1658879747
 * @version 
 * 版本号：100-000-000<br/>
 * 创建日期：2015年11月17日
 * 历史修订：<br/>
 */
public interface IWeixinPeopleDao extends IBaseDao{
	
	/**
	 * 根据应用ID和微信ID查询微信用户列表（带分页）
	 * @param weixinId 微信ID
	 * @param pageNo 当前页
	 * @param pageSize 一页显示的条数
	 * @param orderBy 排序字段
	 * @param order 顺序or倒叙
	 * @return 微信用户列表
	 */
	public List<WeixinPeopleEntity> queryList(@Param("weixinId") int weixinId,@Param("pageNo")int pageNo,@Param("pageSize")int pageSize,@Param("orderBy")String orderBy,@Param("order") boolean order);


	/**
	 * 根据应用ID和微信id查找用户总数
	 * @param weixinId 微信id
	 * @return 用户总数
	 */
	public int queryCount(@Param("weixinId") int weixinId);
	
	/**
	 * 根据自定义字段查找微信用户实体
	 * @param whereMap 查询条件
	 * @return 用户实体
	 */
	public WeixinPeopleEntity getEntity(@Param("whereMap")Map<String,Object> whereMap);
	
	/**
	 * 根据具体字段查询微信用户实体</br>
	 * @param weixinId 关联的微信Id
	 * @param openId 微信对用户的唯一标识
	 * @return 微信用户实体
	 */
	public WeixinPeopleEntity getWeixinPeopleEntity(@Param("weixinId") Integer weixinId,@Param("openId") String openId);
	
	/**
	 * 根据微信openId查询用户实体
	 * @param openId
	 * @return
	 */
	public WeixinPeopleEntity getByOpenId(@Param("openId") String openId);
}
