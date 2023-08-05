/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.attention.biz.impl;

import cn.hutool.core.collection.CollectionUtil;
import net.mingsoft.attention.bean.CollectionBean;
import net.mingsoft.attention.biz.ICollectionLogBiz;
import net.mingsoft.attention.dao.ICollectionLogDao;
import net.mingsoft.attention.entity.CollectionLogEntity;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.base.util.BundleUtil;
import net.mingsoft.basic.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 关注管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2022-1-21 16:28:31<br/>
 * 历史修订：<br/>
 */
 @Service("collectionLogBizImpl")
public class CollectionLogBizImpl extends BaseBizImpl<ICollectionLogDao, CollectionLogEntity> implements ICollectionLogBiz {


	@Autowired
	private ICollectionLogDao collectionLogDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return collectionLogDao;
	}

	@Override
	public List<CollectionBean> queryCollectionCount(List<String> dataIds, String dataType, String peopleId) {
		// 判断dataIds是否为空
		if (CollectionUtil.isEmpty(dataIds)) {
			throw new BusinessException(BundleUtil.getBaseString("err.empty",
					BundleUtil.getString(net.mingsoft.attention.constant.Const.RESOURCES,"data.id")));
		}
		// 判断业务类型是否为空
		if (StringUtils.isEmpty(dataType)) {
			throw new BusinessException(
					BundleUtil.getBaseString("err.empty",
							BundleUtil.getString(net.mingsoft.attention.constant.Const.RESOURCES,"data.type")));
		}
		return collectionLogDao.queryCollectionCount(dataIds, dataType, peopleId);
	}
}
