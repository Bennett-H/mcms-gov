/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.quartz.job;

import net.mingsoft.quartz.entity.JobEntity;
import net.mingsoft.quartz.utils.JobInvokeUtil;
import org.quartz.JobExecutionContext;

/**
 * 定时任务处理（允许并发执行）
 * 
 * @author 铭飞开源团队
 *
 */
public class MultiThreadingJob extends BaseQuartzJob
{
    @Override
    protected void doExecute(JobExecutionContext context, JobEntity jobEntity) throws Exception
    {
        JobInvokeUtil.invokeMethod(jobEntity);
    }
}
