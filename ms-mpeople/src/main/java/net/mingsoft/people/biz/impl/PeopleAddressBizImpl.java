/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.biz.impl;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.people.biz.IPeopleAddressBiz;
import net.mingsoft.people.dao.IPeopleAddressDao;
import net.mingsoft.people.entity.PeopleAddressEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * 用户收货地址业务处理层
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
@Service("peopleAddressBizImpl")
public class PeopleAddressBizImpl extends BaseBizImpl implements IPeopleAddressBiz{
	
	/**
	 * 注入用户收货地址持久层
	 */
	@Autowired
	private IPeopleAddressDao peopleAddressDao;
	
	/**
	 * 获取peopleAddressDao
	 */
	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return peopleAddressDao;
	}

	@Override
	public void setDefault(PeopleAddressEntity peopleAddress) {
		peopleAddressDao.setDefault(peopleAddress);
	}
}
