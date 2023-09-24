/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.mweixin.biz.impl;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mweixin.biz.ICategoryBiz;
import net.mingsoft.mweixin.dao.IWXCategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分类管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2019-12-25 9:27:11<br/>
 * 历史修订：<br/>
 */
 @Service("mweixincategoryBizImpl")
public class CategoryBizImpl extends BaseBizImpl implements ICategoryBiz {

	
	@Autowired
	private IWXCategoryDao categoryDao;
	
	
	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return categoryDao;
	} 
}
