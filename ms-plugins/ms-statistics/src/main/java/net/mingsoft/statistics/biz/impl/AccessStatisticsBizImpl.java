/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.statistics.biz.impl;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.statistics.biz.IAccessStatisticsBiz;
import net.mingsoft.statistics.dao.IAccessStatisticsDao;
import net.mingsoft.statistics.entity.AccessStatisticsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 访问统计管理持久化层
 * @author 铭飞科技
 * 创建日期：2022-4-2 14:07:51<br/>
 * 历史修订：<br/>
 */
 @Service("siteaccessStatisticsBizImpl")
public class AccessStatisticsBizImpl extends BaseBizImpl<IAccessStatisticsDao,AccessStatisticsEntity> implements IAccessStatisticsBiz {


	@Autowired
	private IAccessStatisticsDao accessStatisticsDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return accessStatisticsDao;
	}



}
