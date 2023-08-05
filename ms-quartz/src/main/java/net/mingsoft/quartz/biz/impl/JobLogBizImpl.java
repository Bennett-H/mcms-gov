/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.quartz.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.quartz.entity.JobLogEntity;
import net.mingsoft.quartz.biz.IJobLogBiz;
import net.mingsoft.quartz.dao.IJobLogDao;

/**
 * 任务调度日志管理持久化层
 * @author 铭飞开源团队
 * 创建日期：2019-11-21 10:09:26<br/>
 * 历史修订：<br/>
 */
 @Service("jobLogBizImpl")
public class JobLogBizImpl extends BaseBizImpl<IJobLogDao,JobLogEntity> implements IJobLogBiz {

	
	@Autowired
	private IJobLogDao jobLogDao;
	
	
		@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return jobLogDao;
	} 
}
