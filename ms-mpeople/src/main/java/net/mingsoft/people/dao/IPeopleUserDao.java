/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.dao;


import net.mingsoft.people.entity.PeopleUserEntity;
import org.apache.ibatis.annotations.Param;

import net.mingsoft.base.dao.IBaseDao;

import java.util.List;

/**
 * 
 * 用户信息持久化层
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
public interface IPeopleUserDao extends IBaseDao {
	/**
	 * 根据用户id集合批量删除用户
	 * @param peopleIds 用户id集合
	 */
	public void deletePeopleUsers(@Param("peopleIds")int[] peopleIds);

	/**
	 * 根据用户id集合批量查询用户
	 * @param peopleIds 用户id集合
	 */
	public List<PeopleUserEntity> queryBatchIds(@Param("peopleIds")List peopleIds);
}
