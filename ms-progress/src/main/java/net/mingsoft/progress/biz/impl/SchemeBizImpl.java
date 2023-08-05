/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.biz.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.progress.dao.IProgressDao;
import net.mingsoft.progress.entity.ProgressEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.progress.entity.SchemeEntity;
import net.mingsoft.progress.biz.ISchemeBiz;
import net.mingsoft.progress.dao.ISchemeDao;
import org.springframework.transaction.annotation.Transactional;
/**
 * 进度方案管理持久化层
 * @author 铭飞科技
 * 创建日期：2020-5-28 15:12:02<br/>
 * 历史修订：<br/>
 */
 @Service("progressschemeBizImpl")
@Transactional
public class SchemeBizImpl extends BaseBizImpl<ISchemeDao,SchemeEntity> implements ISchemeBiz {


	@Autowired
	private ISchemeDao schemeDao;

	@Autowired
	private IProgressDao progressDao;

	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return schemeDao;
	}

	@Override
	public boolean updateBusinessTableStatus(String schemeName, String status, String dataId) {
		SchemeEntity schemeEntity = new SchemeEntity();
		schemeEntity.setSchemeName(schemeName);
		SchemeEntity scheme = schemeDao.selectOne(new QueryWrapper<>(schemeEntity));
		if(scheme==null){
			return false;
		}
		// 更新业务表的状态
		schemeDao.updateSchemeTableStatus(scheme.getSchemeTable(), status, DateUtil.now(), dataId);
		return true;
	}

	/**
	 * 批量删除进度方案及其所有节点
	 *
	 * @param schemeIds 进度方案id集合
	 * @return 删除标记
	 */
	@Override
	@Transactional
	public boolean deleteBySchemeIds(List<String> schemeIds) {
		LambdaQueryWrapper<ProgressEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.in(ProgressEntity::getSchemeId,schemeIds);
		if (progressDao.delete(wrapper)>0) {
			return schemeDao.deleteBatchIds(schemeIds)>0;
		}else {
			return false;
		}
	}
}
