/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import net.mingsoft.mdiy.entity.PageEntity;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;

import net.mingsoft.mdiy.biz.IPageBiz;
import net.mingsoft.mdiy.dao.IPageDao;

/**
 * 自定义页面表管理持久化层
 * @author 蓝精灵
 * @version
 * 版本号：1<br/>
 * 创建日期：2017-8-11 14:01:54<br/>
 * 历史修订：<br/>
 */
 @Service("pageBizImpl")
public class PageBizImpl extends BaseBizImpl<IPageDao, PageEntity> implements IPageBiz {


	@Autowired
	private IPageDao pageDao;


		@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return pageDao;
	}
}
