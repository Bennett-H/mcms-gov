/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.co.biz.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.biz.IModelBiz;
import net.mingsoft.basic.entity.ModelEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.co.bean.CategoryDataBean;
import net.mingsoft.co.biz.IDataBiz;
import net.mingsoft.datascope.biz.IDataConfigBiz;
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
@Service("lhDataBizImpl")
@Transactional(rollbackFor = RuntimeException.class)
public class DataBizImpl extends BaseBizImpl implements IDataBiz {


	@Autowired
	private IDataDao dataDao;

	@Autowired
	private net.mingsoft.datascope.biz.IDataBiz dataBiz;

	@Autowired
	private ICategoryBiz categoryBiz;
	@Autowired
	private IModelBiz modelBiz;
	@Autowired
	private IDataConfigBiz DataConfigBiz;

	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return dataDao;
	}


	@Override
	public List<CategoryDataBean> categoryDataList(DataEntity data) {
		// 组织栏目的数据权限
		// [{id:1,category：栏目名称,check:false,model:[{modelTitle：新增，check:false,id:1}]}]
		ModelEntity modelEntity = new ModelEntity();
		modelEntity.setModelUrl("cms/content/index.do");
		// 栏目功能
		List<ModelEntity> modelList  = modelBiz.queryChildList(modelEntity);

		if (CollectionUtil.isEmpty(modelList)){
			throw new BusinessException("文章菜单不存在");
		}
		// 菜单功能跟栏目数据组合
		List<CategoryDataBean> categoryDataBeanList = new ArrayList<>();
		// 查询所有栏目
		List<CategoryEntity> categoryList = categoryBiz.queryCategoryIgnoreSite();

		for(CategoryEntity category : categoryList){
			List<Map<String,Object>> modelMaplist = new ArrayList<>();
			for(ModelEntity model : modelList){
				// 组织功能选中属性
				Map<String,Object> modelMap = new HashMap();
				modelMap.put("id",model.getId());
				modelMap.put("check",false);
				modelMap.put("modelTitle",model.getModelTitle());
				modelMap.put("modelUrl",model.getModelUrl());
				modelMaplist.add(modelMap);
			}
			CategoryDataBean categoryDataBean = new CategoryDataBean();
			categoryDataBean.setId(category.getId());
			categoryDataBean.setCategoryTitle(category.getCategoryTitle());
			categoryDataBean.setCategoryId(category.getCategoryId());
			categoryDataBean.setCategoryCategoryId(category.getCategoryId());
			categoryDataBean.setDataId(category.getId());
			categoryDataBean.setCategoryType(category.getCategoryType());
			categoryDataBean.setCheck(false);
			categoryDataBean.setLeaf(category.getLeaf());
			categoryDataBean.setDataIdModelList(modelMaplist);
			categoryDataBeanList.add(categoryDataBean);
		}
		// 查询当前targetId的权限
		List<DataEntity> dataList = dataDao.selectList(new QueryWrapper<>(data));
		if(CollUtil.isNotEmpty(dataList)) {
			for(DataEntity dataEntity : dataList){
				for(CategoryDataBean categoryDataBean : categoryDataBeanList){
					if(dataEntity.getDataId().equals(categoryDataBean.getDataId())){
						// 栏目是否选中
						categoryDataBean.setCheck(true);
						//fix:修复数据权限树结构问题
//						categoryDataBean.setId(dataEntity.getId());
						if (StringUtils.isNotEmpty(dataEntity.getDataIdModel())){
							List<Map> mapList = JSONUtil.toList(dataEntity.getDataIdModel(), Map.class);
							for(Map newModel : mapList){
								for(Map<String,Object> oldModel : categoryDataBean.getDataIdModelList()){
									// 菜单功能的选中
									if(ObjectUtil.isNotEmpty(oldModel.get("id")) && ObjectUtil.isNotEmpty(newModel.get("id"))){
										if(oldModel.get("id").toString().equals(newModel.get("id").toString())){
											if((Boolean)newModel.get("check")){
												oldModel.put("check",true);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return categoryDataBeanList;
	}

	@Override
	public void saveBatch(List<CategoryDataBean> datas, String targetId) {
		// 查询出对应的数据权限配置

		// 保存权限之前将当前类型的清空
		LambdaUpdateWrapper<DataEntity> wrapper = new UpdateWrapper<DataEntity>().lambda();
		List<String> targetIds = datas.stream().map(CategoryDataBean::getDataTargetId).distinct().collect(Collectors.toList());
		wrapper.in(CollUtil.isNotEmpty(targetIds), DataEntity::getDataTargetId, targetIds);
		wrapper.eq(CollUtil.isNotEmpty(datas), DataEntity::getDataType, datas.get(0).getDataType());
		dataDao.delete(wrapper);

		if(CollUtil.isNotEmpty(datas) && StringUtils.isNotBlank(datas.get(0).getId())) {
			List<DataEntity> dataBeanList = new ArrayList<>();
			for(CategoryDataBean data : datas){
				data.setDataIdModel(JSONUtil.toJsonStr(data.getDataIdModelList()));
				data.setCreateBy(targetId);
				// 将id为null防止id出错
				data.setId(null);
				data.setCreateDate(new Date());
				data.setDel(0);
				dataBeanList.add(data);
			}
			dataBiz.saveBatch(dataBeanList, dataBeanList.size());
		}
	}

	@Override
	public void saveToBatch(List<CategoryDataBean> datas, String targetId, String configId) {
		// 保存权限之前将当前类型的清空
		LambdaUpdateWrapper<DataEntity> wrapper = new UpdateWrapper<DataEntity>().lambda();
		List<String> targetIds = datas.stream().map(CategoryDataBean::getDataTargetId).distinct().collect(Collectors.toList());
		wrapper.in(CollUtil.isNotEmpty(targetIds), DataEntity::getDataTargetId, targetIds);
		wrapper.eq(CollUtil.isNotEmpty(datas), DataEntity::getDataType, datas.get(0).getDataType());
		dataDao.delete(wrapper);
		if(CollUtil.isNotEmpty(datas) && StringUtils.isNotBlank(datas.get(0).getId())) {
			List<DataEntity> dataBeanList = new ArrayList<>();
			for(CategoryDataBean data : datas){
				data.setDataIdModel(JSONUtil.toJsonStr(data.getDataIdModelList()));
				data.setCreateBy(targetId);
				data.setCreateDate(new Date());
				data.setDel(0);
				// 将id为null防止id出错
				data.setId(null);
				// 绑定配置id
				if (StringUtils.isNotBlank(configId)){
					data.setConfigId(configId);
				}
				dataBeanList.add(data);
			}
			dataBiz.saveBatch(dataBeanList, dataBeanList.size());
		}
	}
}
