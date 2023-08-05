/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.quartz.biz.impl;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.quartz.biz.IJobBiz;
import net.mingsoft.quartz.dao.IJobDao;
import net.mingsoft.quartz.entity.JobEntity;
import net.mingsoft.quartz.utils.ScheduleUtil;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 任务实体表管理持久化层
 * @author 铭飞开源团队
 * 创建日期：2019-11-21 10:09:26<br/>
 * 历史修订：<br/>
 */
 @Service("jobBizImpl")
public class JobBizImpl extends BaseBizImpl<IJobDao,JobEntity> implements IJobBiz {


	@Autowired
	private IJobDao jobDao;
	@Autowired
	private Scheduler scheduler;

		@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return jobDao;
	}

	/**
	 * 立即运行任务
	 *
	 * @param job 调度信息
	 */
	@Override
	public void run(JobEntity job) throws SchedulerException {
		ScheduleUtil.triggerJob(scheduler,job);
	}

}
