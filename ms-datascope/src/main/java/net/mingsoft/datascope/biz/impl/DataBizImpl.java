/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.datascope.biz.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.datascope.bean.DataBatchBean;
import net.mingsoft.datascope.biz.IDataBiz;
import net.mingsoft.datascope.dao.IDataDao;
import net.mingsoft.datascope.entity.DataEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据权限管理持久化层
 * @author 铭软开发团队
 * 创建日期：2020-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
@Service("datascopedataBizImpl")
@Transactional(rollbackFor = RuntimeException.class)
public class DataBizImpl extends BaseBizImpl<IDataDao,DataEntity> implements IDataBiz {


	@Autowired
	private IDataDao dataDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return dataDao;
	}

	@Override
	public List<String> queryProjectList(String id, String dataType) {
		DataEntity dataEntity = new DataEntity();
		dataEntity.setDataTargetId(id);
		dataEntity.setDataType(dataType);
		List<DataEntity> list = dataDao.selectList(new QueryWrapper<>(dataEntity));
		return list.stream().map(DataEntity::getDataId).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public void saveBatchByDataTargetIdAndDataType(String dataType, String dataTargetId, String createBy,
												   List<String> dataIdList) {
		if(StringUtils.isNotEmpty(dataType) && StringUtils.isNotEmpty(dataTargetId)
				 && StringUtils.isNotEmpty(createBy)) {
			DataEntity dataEntityDel = new DataEntity();
			dataEntityDel.setDataType(dataType);
			dataEntityDel.setDataTargetId(dataTargetId);
			//删除原先权限
			super.remove(new QueryWrapper<>(dataEntityDel));
			Date date = new Date();
			Collection<DataEntity> dataEntityList = new ArrayList<DataEntity>();
			for(String dataId : dataIdList){
				DataEntity dataEntity = new DataEntity();
				dataEntity.setDataType(dataType);
				dataEntity.setCreateBy(createBy);
				dataEntity.setCreateDate(date);
				dataEntity.setDataTargetId(dataTargetId);
				dataEntity.setDataId(dataId);
				dataEntityList.add(dataEntity);
			}
			//添加批处理权限
			super.saveBatch(dataEntityList);
		}
	}

	@Override
	public void saveDataBatch(List<DataBatchBean> dates) {
		DataEntity dataEntity = dates.get(0);
		LambdaQueryWrapper<DataEntity> deleteWrapper = new QueryWrapper<DataEntity>().lambda();
		deleteWrapper.eq(StringUtils.isNotEmpty(dataEntity.getDataType()), DataEntity::getDataType, dataEntity.getDataType());
		deleteWrapper.eq(StringUtils.isNotEmpty(dataEntity.getDataTargetId()), DataEntity::getDataTargetId, dataEntity.getDataTargetId());
		// 先删除防止数据已存在
		Collection<DataEntity> dataEntityList = new ArrayList<>();
		Date date = new Date();
		dataDao.delete(deleteWrapper);
		for (DataBatchBean dataBean : dates) {
			DataEntity data = new DataEntity();
			data.setDataType(dataBean.getDataType());
			data.setDataId(dataBean.getDataId());
			data.setDataTargetId(dataBean.getDataTargetId());
			data.setCreateDate(date);
			data.setDataIdModel(JSONUtil.toJsonStr(dataBean.getDataIdModelList()));
			dataEntityList.add(data);
		}
		super.saveBatch(dataEntityList);
	}

	@Override
	public DataEntity mergeDataBatch(List<DataEntity> dataList) {
		Assert.notEmpty(dataList);
		DataEntity dataEntity = dataList.get(0);
		List<String> modelList = dataList.stream().map(DataEntity::getDataIdModel).collect(Collectors.toList());
		List<Map> roleModels = new ArrayList<>();
		for (String models : modelList) {
			roleModels.addAll(JSONUtil.toList(models, Map.class));
		}
		// 过滤权限
		List<Map<String, Object>> _roleModels = new ArrayList<>();
		for (Map<String, Object> map : roleModels) {
			if (Boolean.parseBoolean(map.get("check").toString())) {
				_roleModels.add(map);
			}
		}
		dataEntity.setDataIdModel(JSONUtil.toJsonStr(_roleModels));
		return dataEntity;
	}
}
