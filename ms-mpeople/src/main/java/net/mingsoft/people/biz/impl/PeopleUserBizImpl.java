/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.biz.impl;


import net.mingsoft.base.exception.BusinessException;
import net.mingsoft.base.util.BundleUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.people.dao.IPeopleDao;
import net.mingsoft.people.entity.PeopleEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.people.biz.IPeopleUserBiz;
import net.mingsoft.people.dao.IPeopleUserDao;
import net.mingsoft.people.entity.PeopleUserEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 
 * 用户信息业务层层实现类
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
@Service("peopleUserBiz")
@Transactional
public class PeopleUserBizImpl extends PeopleBizImpl implements IPeopleUserBiz {

	/**
	 * 用户信息持久化层注入
	 */
	@Autowired
	private IPeopleUserDao peopleUserDao;

	@Autowired
	private IPeopleDao peopleDao;

	@Override
	protected IBaseDao getDao() {
		return peopleUserDao;
	}

	/**
	 * 用户信息实体保存</br>
	 * 只能在有子类继承时调用的</br>
	 */
	public int savePeopleUser(PeopleUserEntity peopleEntity){
		savePeople(peopleEntity);
		return peopleUserDao.saveEntity(peopleEntity);
	}	
	
	/**
	 * 更新用户信息</br>
	 * 只能在有子类时调用</br>
	 * @param peopleEntity 用户信息
	 */
	public void updatePeopleUser(PeopleUserEntity peopleEntity){
		updatePeople(peopleEntity);
		this.peopleUserDao.updateEntity(peopleEntity);
	}

	/**
	 * 删除用户信息</br>
	 * 只能在有子类时调用</br>
	 * @param peopleId 用户ID
	 */
	public void deletePeopleUser(int peopleId){
		deletePeople(peopleId);
		this.peopleUserDao.deleteEntity(peopleId);
	}

	@Override
	public void deletePeopleUsers(int[] peopleIds) {
		if(peopleIds==null){
			return;
		}
		this.deletePeople(peopleIds);
		this.peopleUserDao.deletePeopleUsers(peopleIds);
	}

	@Override
	public List<PeopleUserEntity> queryBatchIds(List<String> peopleIds) {
		return peopleUserDao.queryBatchIds(peopleIds);
	}

	@Override
	public PeopleUserEntity getByEntity(PeopleUserEntity peopleUser) {
		return (PeopleUserEntity) peopleUserDao.getByEntity(peopleUser);
	}




}
