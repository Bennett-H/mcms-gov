/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.ad.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.ad.entity.PositionEntity;
import net.mingsoft.ad.biz.IPositionBiz;
import net.mingsoft.ad.dao.IPositionDao;

/**
 * 广告位管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2019-11-23 8:49:39<br/>
 * 历史修订：<br/>
 */
 @Service("adpositionBizImpl")
public class PositionBizImpl extends BaseBizImpl<IPositionDao,PositionEntity> implements IPositionBiz {

	
	@Autowired
	private IPositionDao positionDao;
	
	
	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return positionDao;
	} 
}