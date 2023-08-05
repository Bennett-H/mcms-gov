/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.biz.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.progress.bean.ProgressBean;
import net.mingsoft.progress.dao.ISchemeDao;
import net.mingsoft.progress.entity.SchemeEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import java.util.stream.Collectors;

import net.mingsoft.progress.entity.ProgressEntity;
import net.mingsoft.progress.biz.IProgressBiz;
import net.mingsoft.progress.dao.IProgressDao;
import org.springframework.transaction.annotation.Transactional;
/**
 * 进度表管理持久化层
 * @author 铭飞科技
 * 创建日期：2020-5-28 15:12:02<br/>
 * 历史修订：<br/>
 */
 @Service("progressprogressBizImpl")
@Transactional
public class ProgressBizImpl extends BaseBizImpl<IProgressDao, ProgressEntity> implements IProgressBiz {


	@Autowired
	private IProgressDao progressDao;

	@Autowired
	private ISchemeDao schemeDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return progressDao;
	}

	@Override
	public List<ProgressEntity> queryProgress(String schemeName) {
		SchemeEntity schemeEntity = new SchemeEntity();
		schemeEntity.setSchemeName(schemeName);
		SchemeEntity scheme =  schemeDao.selectOne(new QueryWrapper<>(schemeEntity));
		ProgressEntity progressEntity = new ProgressEntity();
		progressEntity.setSchemeId(scheme.getIntegerId());
		return progressDao.selectList(new QueryWrapper<>(progressEntity).orderByAsc("create_date"));
	}

	@Override
	public ProgressEntity nextProgress(int schemeId, String plNodeName){
		// 获取方案所有节点
		ProgressEntity progressEntity = new ProgressEntity();
		progressEntity.setSchemeId(schemeId);
		LambdaQueryWrapper<ProgressEntity> wrapper = new QueryWrapper<>(progressEntity).lambda();
		wrapper.orderByAsc(ProgressEntity::getProgressNumber);
		List<ProgressEntity> progressList = progressDao.selectList(wrapper);
		List<String> nodeNameList = progressList.stream().map(p -> p.getProgressNodeName()).collect(Collectors.toList());
		// 当前所处节点位置
		int index = nodeNameList.indexOf(plNodeName);
		// index < nodeNameList.size() 进行中的节点，还存在下一节点
		// index == -1 进度节点减少，审核降级,直接结束；
		if(index < nodeNameList.size()-1 && index != -1){
			return progressList.get(index+1);
		}else {
			return null;
		}
	}

	@Override
	public List<ProgressEntity> queryNotAddLog(ProgressBean progressBean) {
		return progressDao.queryNotAddLog(progressBean);
	}

	@Override
	public List<ProgressEntity> queryChildren(ProgressEntity progressEntity) {
		return progressDao.queryChildren(progressEntity);
	}

	@Override
	public void saveProgress(ProgressEntity progressEntity) {
		this.setParentProgressId(progressEntity);
		//更新新的父级
		if (StrUtil.isNotBlank(progressEntity.getProgressId()) && !"0".equals(progressEntity.getProgressId())) {
			ProgressEntity parent = getById(progressEntity.getProgressId());
			//如果之前是叶子节点就更新
			if (parent.getLeaf()) {
				parent.setLeaf(false);
				updateById(parent);
			}
		}
		progressEntity.setLeaf(false);
		//如果是新增栏目一定是叶子节点
		if (StrUtil.isEmpty(progressEntity.getId()) || progressEntity.getId().equals("0")) {
			progressEntity.setLeaf(true);
		}
		super.save(progressEntity);
	}

	private void setParentProgressId(ProgressEntity progressEntity) {
		if (StringUtils.isNotEmpty(progressEntity.getProgressId()) && Long.parseLong(progressEntity.getProgressId()) > 0) {
			ProgressEntity progress = getById(progressEntity.getProgressId());
			if (StringUtils.isEmpty(progress.getProgressParentIds())) {
				progressEntity.setProgressParentIds(progress.getId());
			} else {
				progressEntity.setProgressParentIds(progress.getProgressParentIds() + "," + progress.getId());
			}
		} else {
			progressEntity.setProgressParentIds(null);
		}

	}

	private void setChildParentId(ProgressEntity progressEntity) {
		ProgressEntity progress = new ProgressEntity();
		progress.setProgressId(progressEntity.getId());
		List<ProgressEntity> list = progressDao.query(progress);
		list.forEach(x -> {
			if (StringUtils.isEmpty(progressEntity.getProgressParentIds())) {
				x.setProgressParentIds(progressEntity.getId());
			} else {
				x.setProgressParentIds(progressEntity.getProgressParentIds() + "," + progressEntity.getId());
			}
			super.updateEntity(x);
			setChildParentId(x);
		});
	}

	@Override
	public void updateProgress(ProgressEntity progressEntity) {
		this.setParentProgressId(progressEntity);
		this.setProgressLeaf(progressEntity);
		//如果父级栏目和父级id为空字符串则转化成null
		if (StringUtils.isEmpty(progressEntity.getProgressId())) {
			progressEntity.setProgressId(null);
		}
		if (StringUtils.isEmpty(progressEntity.getProgressParentIds())) {
			progressEntity.setProgressParentIds(null);
		}
		progressDao.updateById(progressEntity);
		setChildParentId(progressEntity);
	}

	/**
	 * 设置父级叶子节点
	 * @param progressEntity
	 */
	private void setProgressLeaf(ProgressEntity progressEntity) {
		ProgressEntity progress = getById(progressEntity.getId());
		//如果父级不为空并且修改了父级则需要更新父级
		if (progressEntity.getProgressId() != null && !progressEntity.getProgressId().equals(progress.getProgressId())) {
			//更新旧的父级
			if (StrUtil.isNotBlank(progress.getProgressId()) && !"0".equals(progress.getProgressId())) {
				ProgressEntity parent = getById(progress.getProgressId());
				//如果修改了父级则需要判断父级是否还有子节点
				boolean leaf = parent.getLeaf();
				//查找不等于当前更新的分类子集，有则不是叶子节点
				LambdaQueryWrapper<ProgressEntity> queryWrapper = new QueryWrapper<ProgressEntity>().lambda();
				queryWrapper.eq(ProgressEntity::getProgressId, parent.getId());
				queryWrapper.ne(ProgressEntity::getId, progressEntity.getId());
				parent.setLeaf(count(queryWrapper) == 0);
				if (leaf != parent.getLeaf()) {
					updateById(parent);
				}
			}
			//更新新的父级
			if (StrUtil.isNotBlank(progressEntity.getProgressId()) && !"0".equals(progressEntity.getProgressId())) {
				ProgressEntity parent = getById(progressEntity.getProgressId());
				//如果之前是叶子节点就更新
				if (parent.getLeaf()) {
					parent.setLeaf(false);
					updateById(parent);
				}
			}
		}
	}



}
