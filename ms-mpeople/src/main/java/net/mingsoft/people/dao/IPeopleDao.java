/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.dao;


import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.people.entity.PeopleEntity;
import org.apache.ibatis.annotations.Param;


/**
 * 
 * 用户持久化层
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
public interface IPeopleDao extends IBaseDao<PeopleEntity>{
	
	/**
	 * 根据用户用户名查询用户实体</br>
	 * @param userName 用户名(注:手机号,邮箱,用户名称都可作为用户名登录)
	 * @return 查询到的用户实体
	 */
	PeopleEntity getEntityByUserName(@Param("userName")String userName);
	
	
	/**
	 * 根据用户用户名查询用户实体</br>
	 * @param userName 用户名(注:手机号,邮箱)
	 * @return 查询到的用户实体
	 */
	PeopleEntity getEntityByMailOrPhone(@Param("userName")String userName);
	
	/**
	 * 根据用户信息查询对应用户
	 * @param people 用户信息
	 * @return  返回用户
	 */
	PeopleEntity getByPeople(@Param("people")PeopleEntity people);
	

	
	/**
	 * 根据用户的验证码和用户名（:手机号,邮箱,用户名称都可作为用户名登录）
	 * @param userName
	 * @param peopleCode
	 * @return
	 */
	PeopleEntity getEntityByCode(@Param("userName")String userName,@Param("peopleCode")String peopleCode);


}
