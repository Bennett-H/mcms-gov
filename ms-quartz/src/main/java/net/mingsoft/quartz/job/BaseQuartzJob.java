/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.quartz.job;

import cn.hutool.core.exceptions.ExceptionUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.quartz.biz.IJobLogBiz;
import net.mingsoft.quartz.constant.Const;
import net.mingsoft.quartz.entity.JobEntity;
import net.mingsoft.quartz.entity.JobLogEntity;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * quartz任务基础类
 *
 * @author 铭飞开源团队
 */
public abstract class BaseQuartzJob implements Job
{
    private static final Logger LOG = LoggerFactory.getLogger(BaseQuartzJob.class);

    /**
     * 线程本地变量
     */
    private static ThreadLocal<Date> threadLocal = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        JobEntity jobEntity =(JobEntity)context.getMergedJobDataMap().get(Const.TASK_PROPERTIES);
        try
        {
            before(context, jobEntity);
            if (jobEntity != null)
            {
                doExecute(context, jobEntity);
            }
            after(context, jobEntity, null);
        }
        catch (Exception e)
        {
            LOG.error("任务执行异常：", e);
            after(context, jobEntity, e);
        }
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param jobEntity 系统计划任务
     */
    protected void before(JobExecutionContext context, JobEntity jobEntity)
    {
        threadLocal.set(new Date());
    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param jobEntity 系统计划任务
     */
    protected void after(JobExecutionContext context,JobEntity jobEntity, Exception e)
    {
        Date startTime = threadLocal.get();
        threadLocal.remove();
        final JobLogEntity jobLog = new JobLogEntity();
        jobLog.setQjlName(jobEntity.getQjName());
        jobLog.setQjlGroup(jobEntity.getQjGroup());
        jobLog.setQjlTarget(jobEntity.getQjTarget());
        jobLog.setCreateDate(startTime);
        Date endTime = new Date();
        jobLog.setUpdateDate(endTime);
        long runMs = endTime.getTime() - startTime.getTime();
        jobLog.setQjlMsg(jobLog.getQjlName() + " 总共耗时：" + runMs + "毫秒");
        if (e != null)
        {
            jobLog.setQjlStatus(false);
            String errorMsg = StringUtils.substring(ExceptionUtil.getMessage(e), 0, 2000);
            jobLog.setQjlErrorMsg(errorMsg);
        }
        else
        {
            jobLog.setQjlStatus(true);
        }

        // 写入数据库当中
        SpringUtil.getBean(IJobLogBiz.class).saveEntity(jobLog);
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param jobEntity 系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, JobEntity jobEntity) throws Exception;
}
