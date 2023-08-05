/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.approval.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.mingsoft.approval.biz.IConfigBiz;
import net.mingsoft.approval.constant.e.ProgressStatusEnum;
import net.mingsoft.approval.dao.IConfigDao;
import net.mingsoft.approval.entity.ConfigEntity;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.progress.bean.ProgressLogBean;
import net.mingsoft.progress.biz.IProgressBiz;
import net.mingsoft.progress.biz.IProgressLogBiz;
import net.mingsoft.progress.biz.ISchemeBiz;
import net.mingsoft.progress.entity.ProgressEntity;
import net.mingsoft.progress.entity.ProgressLogEntity;
import net.mingsoft.progress.entity.SchemeEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审批配置管理持久化层
 * @author 铭软科技
 * 创建日期：2021-3-19 16:46:27<br/>
 * 历史修订：<br/>
 */
 @Service("approvalconfigBizImpl")
public class ConfigBizImpl extends BaseBizImpl<IConfigDao,ConfigEntity> implements IConfigBiz {


	@Autowired
	private IConfigDao configDao;
	@Autowired
	private IProgressLogBiz progressLogBiz;

	@Autowired
	private IProgressBiz progressBiz;

	@Autowired
	private ISchemeBiz schemeBiz;

	@Autowired
	private IManagerBiz managerBiz;

	@Autowired
	private IContentBiz contentBiz;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return configDao;
	}

	@Override
	public boolean auditPermissionsVerify(String schemeName, String managerId, String progressNodeName) {
		List<ConfigEntity> configList = this.queryList(schemeName, managerId, progressNodeName);
		return CollUtil.isNotEmpty(configList);
	}

	@Override
	public boolean auditPermissionsVerifyForRole(String schemeName, String categoryId, int roleId, String configName) {
		List<ConfigEntity> configList = this.queryListForRole(schemeName, categoryId, roleId, configName);
		return CollUtil.isNotEmpty(configList);
	}

	@Override
	public boolean auditPermissionsVerifyForRoles(String schemeName, String categoryId, String roleIds, String configName) {
		List<ConfigEntity> configList = new ArrayList<>();
		for (String roleId : roleIds.split(",")) {
			configList.addAll(this.queryListForRole(schemeName, categoryId, Integer.parseInt(roleId), configName));
		}
		return CollUtil.isNotEmpty(configList);
	}

	@Override
	public List<String> auditList(String schemeName, String managerId,String plStatus) {
		List<ConfigEntity> configList = this.queryList(schemeName, managerId, "");
		List<String> progressIds = configList.stream().map(ConfigEntity::getProgressId).collect(Collectors.toList());
		if(progressIds.size()==0) {
			return CollUtil.newArrayList();
		} else {
			return progressLogBiz.queryDataIdBySchemeNameAndNodeNames(schemeName, progressIds,plStatus);
		}
	}

	@Override
	public List<ProgressLogBean> auditListForCategoryIds(List<String> categoryIds, String plStatus) {
		return progressLogBiz.queryDataByCategoryId(categoryIds, plStatus);
	}

	@Override
	public void deleteByCategoryIds(List<String> categoryIds) {
		LambdaQueryWrapper<ConfigEntity> queryWrapper = new QueryWrapper<ConfigEntity>().lambda().in(ConfigEntity::getCategoryId, categoryIds);
		configDao.delete(queryWrapper);
	}

	/**
	 * 方案、管理员、等级编号查询管理员拥有的权限
	 * @param schemeName
	 * @param managerId
	 * @param progressNodeName
	 * @return
	 */
	@Deprecated
	private List<ConfigEntity> queryList(String schemeName, String managerId,String progressNodeName) {
		ConfigEntity configEntity = new ConfigEntity();
		SchemeEntity schemeEntity = new SchemeEntity();
		schemeEntity.setSchemeName(schemeName);
		SchemeEntity _schemeEntity = schemeBiz.getOne(new QueryWrapper<>(schemeEntity));
		configEntity.setSchemeId(_schemeEntity.getId());
		if(StringUtils.isNotEmpty(progressNodeName)){
			ProgressEntity progressEntity = new ProgressEntity();
			progressEntity.setProgressNodeName(progressNodeName);
			progressEntity.setSchemeId(_schemeEntity.getIntId());
			ProgressEntity _progressEntity = progressBiz.getOne(new QueryWrapper<>(progressEntity));
			configEntity.setProgressId(_progressEntity.getId());
		}
		QueryWrapper<ConfigEntity> queryWrapper = new QueryWrapper<>(configEntity);
		queryWrapper.apply( managerId != "" ,"FIND_IN_SET (" + managerId + ",config_manager_ids) > 0");
		List<ConfigEntity> configList = configDao.selectList(queryWrapper);
		return configList;
	}

	@Override
	public List<ConfigEntity> queryListForRole(String schemeName, String categoryId, int roleId, String progressNodeName) {
		ConfigEntity configEntity = new ConfigEntity();
		SchemeEntity schemeEntity = new SchemeEntity();
		schemeEntity.setSchemeName(schemeName);
		SchemeEntity _schemeEntity = schemeBiz.getOne(new QueryWrapper<>(schemeEntity));
		configEntity.setSchemeId(_schemeEntity.getId());
		// progressNodeName为空则查询所有节点
		if(StringUtils.isNotEmpty(progressNodeName)){
			ProgressEntity progressEntity = new ProgressEntity();
			progressEntity.setProgressNodeName(progressNodeName);
			progressEntity.setSchemeId(_schemeEntity.getIntId());
			ProgressEntity _progressEntity = progressBiz.getOne(new QueryWrapper<>(progressEntity));
			configEntity.setProgressId(_progressEntity.getId());
		}
		LambdaQueryWrapper<ConfigEntity> queryWrapper = new QueryWrapper<>(configEntity).lambda();
		// categoryId 为空则查询所有的栏目权限
		queryWrapper.eq(StringUtils.isNotBlank(categoryId), ConfigEntity::getCategoryId, categoryId);
		queryWrapper.apply(StringUtils.isNotEmpty(Integer.toString(roleId)) ,"FIND_IN_SET ({0},CONFIG_ROLE_IDS) > 0", roleId);
		return configDao.selectList(queryWrapper);
	}

	/**
	 *
	 * @param schemeEntity  方案
	 * @param progressLogEntity 进度
	 * @param progressId 栏目的审批级数
	 */
	@Override
	public void approval(SchemeEntity schemeEntity,ProgressLogEntity progressLogEntity,int progressId) {
		// 修改当前日志的状态和备注
		progressLogBiz.updateById(progressLogEntity);
		// 获取当前进度节点
		ProgressEntity progressEntity = progressBiz.getById(progressLogEntity.getProgressId());
		// 获取当前节点所对应的状态
		String status = progressEntity.getProgressStatus(progressLogEntity.getPlStatus());
		// 回写业务表的状态
		ProgressLogEntity nextProgressLog = new ProgressLogEntity();
		boolean approvalFlag = false;
		// 1.审核通过与否
		if("adopt".equals(progressLogEntity.getPlStatus())){
			// 如果栏目的审批级数==当前审批级数
			if(progressLogEntity.getProgressId() == progressId) {
				// 取最后一个进度配置节点
				ProgressEntity _progressEntity = progressBiz.getOne(Wrappers.<ProgressEntity>lambdaQuery().orderByDesc(ProgressEntity::getId), false);
				String _status = _progressEntity.getProgressStatus(progressLogEntity.getPlStatus());
				// 设置状态为终审完成
				schemeBiz.updateBusinessTableStatus(schemeEntity.getSchemeName(), _status, progressLogEntity.getDataId());
				return;
			}
			ProgressEntity nextProgress = progressBiz.nextProgress(progressLogEntity.getSchemeId(), progressLogEntity.getPlNodeName());
			//有下一节点，有则新增一条进度日志
			if(nextProgress != null){
				nextProgressLog.setCreateBy(progressLogEntity.getUpdateBy());
				nextProgressLog.setDataId(progressLogEntity.getDataId());
				// 设置进度数
				nextProgressLog.setPlNumber(nextProgress.getProgressNumber());
				nextProgressLog.setCreateDate(new Date());
				nextProgressLog.setPlNodeName(nextProgress.getProgressNodeName());
				nextProgressLog.setSchemeId(nextProgress.getSchemeId());
				nextProgressLog.setProgressId(nextProgress.getIntegerId());
				progressLogBiz.save(nextProgressLog);
				String categoryId = contentBiz.getById(progressLogEntity.getDataId()).getCategoryId();
				String roleIds = managerBiz.getById(progressLogEntity.getUpdateBy()).getRoleIds();
				approvalFlag = auditPermissionsVerifyForRoles(schemeEntity.getSchemeName(), categoryId, roleIds, nextProgress.getProgressNodeName());
			}
		}
		schemeBiz.updateBusinessTableStatus(schemeEntity.getSchemeName(),status,progressLogEntity.getDataId());
		// 当前管理员下一个节点还可以继续审核
		if(approvalFlag){
			nextProgressLog.setPlStatus(progressLogEntity.getPlStatus());
			nextProgressLog.setPlContent(progressLogEntity.getPlContent());
			nextProgressLog.setUpdateBy(progressLogEntity.getUpdateBy());
			nextProgressLog.setPlOperator(progressLogEntity.getPlOperator());
			nextProgressLog.setUpdateDate(new Date());
			approval(schemeEntity,nextProgressLog,progressId);
		}
	}


	@Override
	public boolean isCheck(String schemeName, String dataId) {
		SchemeEntity schemeEntity = new SchemeEntity();
		schemeEntity.setSchemeName(schemeName);
		SchemeEntity scheme = schemeBiz.getOne(new QueryWrapper<>(schemeEntity));
		if(scheme == null){
			LOG.debug(schemeName,"方案不存在");
			return false;
		}
		ProgressLogEntity progressLogEntity = new ProgressLogEntity();
		progressLogEntity.setSchemeId(scheme.getIntegerId());
		progressLogEntity.setDataId(dataId);
		// 审核状态和更新时间都为空，表示存在审核中的数据
		List<ProgressLogEntity> list = progressLogBiz.list(new QueryWrapper<>(progressLogEntity).isNull("pl_status").isNull("update_date"));
		return CollUtil.isNotEmpty(list);
	}

	@Override
	public boolean submit(String schemeName, String dataId, String createBy) {
		// 方案和进度是否都存在
		if (this.isCheck(schemeName,dataId)) {
			return false;
		}
		boolean flag = progressLogBiz.insertProgressLog(schemeName, dataId,createBy);
		if(flag){
			return schemeBiz.updateBusinessTableStatus(schemeName, ProgressStatusEnum.REVIEW.toString(), dataId);
		}
		return false;
	}


}
