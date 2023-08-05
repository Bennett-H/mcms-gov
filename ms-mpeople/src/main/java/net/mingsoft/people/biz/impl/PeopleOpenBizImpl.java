/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.people.biz.IPeopleOpenBiz;
import net.mingsoft.people.dao.IPeopleDao;
import net.mingsoft.people.dao.IPeopleOpenDao;
import net.mingsoft.people.dao.IPeopleUserDao;
import net.mingsoft.people.entity.PeopleOpenEntity;

/**
 * 
 * 开发平台用户
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
@Service("peopleOpenBizImpl")
public class PeopleOpenBizImpl extends PeopleUserBizImpl implements IPeopleOpenBiz {

	@Autowired
	private IPeopleOpenDao peopleOpenDao;
	
	/**
	 * 用户信息持久化层注入
	 */
	@Autowired
	private IPeopleUserDao peopleUserDao; 
	
	
	@Autowired
	private IPeopleDao peopleDao; 

	@Override
	protected IBaseDao getDao() {
		return peopleOpenDao;
	}

	@Override
	public void savePeopleOpen(PeopleOpenEntity peopleOpen) {
		// TODO Auto-generated method stub
		peopleDao.saveEntity(peopleOpen);
		peopleUserDao.saveEntity(peopleOpen);
		peopleOpenDao.saveEntity(peopleOpen);
	}

	@Override
	public PeopleOpenEntity getByOpenId(String openId) {
		// TODO Auto-generated method stub
		return  peopleOpenDao.getByOpenId(openId);
	}

}
