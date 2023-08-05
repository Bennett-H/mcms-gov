/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.quartz.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.quartz.entity.JobEntity;
import org.quartz.SchedulerException;


/**
 * 任务实体表业务
 * @author 铭飞开源团队
 * 创建日期：2019-11-21 10:09:26<br/>
 * 历史修订：<br/>
 */
public interface IJobBiz extends IBaseBiz<JobEntity> {

    /**
     * 直接运行任务
     * @param job
     * @throws SchedulerException
     */
    void run(JobEntity job) throws SchedulerException;

}
