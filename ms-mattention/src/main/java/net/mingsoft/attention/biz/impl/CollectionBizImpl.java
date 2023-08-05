/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.attention.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.attention.biz.ICollectionBiz;
import net.mingsoft.attention.dao.ICollectionDao;
import net.mingsoft.attention.dao.ICollectionLogDao;
import net.mingsoft.attention.entity.CollectionEntity;
import net.mingsoft.attention.entity.CollectionLogEntity;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.base.util.BundleUtil;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mdiy.util.DictUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 关注记录管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2019-11-22 14:34:49<br/>
 * 历史修订：<br/>
 */
 @Service("collectionBizImpl")
public class CollectionBizImpl extends BaseBizImpl<ICollectionDao, CollectionEntity> implements ICollectionBiz {

	
	@Autowired
	private ICollectionDao collectionDao;

	@Autowired
	private ICollectionLogDao collectionLogDao;
	
	
	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return collectionDao;
	}

	@Override
	@Transactional
	public boolean saveOrDelete(CollectionEntity collectionEntity) {
		// 判断评论类型
		String dataType = DictUtil.getDictValue("关注类型", collectionEntity.getDataType(), null);
		if (StringUtils.isBlank(dataType)){
			throw new BusinessException(
					BundleUtil.getBaseString("err.error",
							BundleUtil.getString(net.mingsoft.attention.constant.Const.RESOURCES,"data.type")));
		}
		// 设置dataType
		collectionEntity.setDataType(dataType);
		// 判断判断peopleId是否存在
		if (collectionEntity.getPeopleId() == null){
			throw new BusinessException(BundleUtil.getBaseString("err.empty",
					BundleUtil.getString(net.mingsoft.attention.constant.Const.RESOURCES,"people.id")));
		}
		// 判断判断dataId是否存在
		if (StringUtils.isBlank(collectionEntity.getDataId())){
			throw new BusinessException(BundleUtil.getBaseString("err.empty",
					BundleUtil.getString(net.mingsoft.attention.constant.Const.RESOURCES,"data.id")));
		}
		// 判断关注数据存在
		CollectionLogEntity collectionLog  = new CollectionLogEntity();
		collectionLog.setDataId(collectionEntity.getDataId());
		collectionLog.setDataType(collectionEntity.getDataType());
		// 重复点赞判断
		LambdaQueryWrapper<CollectionEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(CollectionEntity::getDataId, collectionEntity.getDataId());
		wrapper.eq(CollectionEntity::getPeopleId, collectionEntity.getPeopleId());
		wrapper.eq(CollectionEntity::getDataType, collectionEntity.getDataType());
		// 查询是否有数据
		CollectionEntity collection = collectionDao.selectOne(wrapper);
		if (collection != null) { // 不为空
			CollectionLogEntity collectionLogEntity = collectionLogDao.selectOne(new QueryWrapper<>(collectionLog));
			// 删除点赞记录里面的数据
			this.removeById(collection.getId());
			// 关注数-1
			collectionLogEntity.setDataCount(collectionLogEntity.getDataCount() - 1);
			// 修改时间
			collectionLogEntity.setUpdateDate(new Date());
			collectionLogDao.updateById(collectionLogEntity);
			//返回false表示取消关注
			return false;
		}
		// 设置当前Ip
		collectionEntity.setCollectionIp(BasicUtil.getIp());
		collectionEntity.setCreateDate(new Date());
		// 插入一条数据
		collectionDao.insert(collectionEntity);
		return true;
	}


}
