/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.spider.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.spider.entity.TaskRegularEntity;
import net.mingsoft.spider.biz.ITaskRegularBiz;
import net.mingsoft.spider.dao.ITaskRegularDao;
import org.springframework.transaction.annotation.Transactional;
/**
 * 采集规则管理持久化层
 * @author 铭软科技
 * 创建日期：2020-9-10 14:12:40<br/>
 * 历史修订：<br/>
 */
 @Service("spidertaskRegularBizImpl")
@Transactional
public class TaskRegularBizImpl extends BaseBizImpl<ITaskRegularDao,TaskRegularEntity> implements ITaskRegularBiz {


	@Autowired
	private ITaskRegularDao taskRegularDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return taskRegularDao;
	}
}
