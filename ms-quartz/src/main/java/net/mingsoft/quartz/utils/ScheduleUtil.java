/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.quartz.utils;

import net.mingsoft.quartz.constant.Const;
import net.mingsoft.quartz.entity.JobEntity;
import net.mingsoft.quartz.job.SingleThreadJob;
import net.mingsoft.quartz.job.MultiThreadingJob;
import org.quartz.*;

/**
 * 定时任务工具类
 *
 * @author 铭飞开源团队
 */
public class ScheduleUtil {
    /**
     * 得到quartz任务类
     *
     * @param job 执行计划
     * @return 具体执行任务类
     */
    private static Class<? extends Job> getQuartzJobClass(JobEntity job) {
        return getQuartzJobClass(job.getQjAsync());
    }

    /**
     * 得到quartz任务类
     *
     * @param async 执行计划
     * @return 具体执行任务类
     */
    public static Class<? extends Job> getQuartzJobClass(boolean async) {
        return async ? MultiThreadingJob.class : SingleThreadJob.class;
    }

    /**
     * 构建任务触发对象
     */
    public static TriggerKey getTriggerKey(String jobId, String jobGroup) {
        return TriggerKey.triggerKey(Const.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 构建任务键对象
     */
    public static JobKey getJobKey(String jobId, String jobGroup) {
        return JobKey.jobKey(Const.TASK_CLASS_NAME + jobId, jobGroup);
    }


    /**
     * 创建定时任务
     */
    public static void createJob(Scheduler scheduler, JobEntity job) {
        try {

            Class<? extends Job> jobClass = getQuartzJobClass(job);
            // 构建job信息
            String jobId = job.getId();
            String jobGroup = job.getQjGroup();
            TriggerKey triggerKey = ScheduleUtil.getTriggerKey(jobId, jobGroup);
            // 判断是否存在
            if (scheduler.checkExists(getJobKey(jobId, jobGroup))) {
                // 防止创建时存在数据问题 先移除，然后在执行创建操作
                scheduler.deleteJob(getJobKey(jobId, jobGroup));
            }
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(jobId, jobGroup)).build();
            // 表达式调度构建器
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getQjCron());
            // 按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
            // 放入参数，运行时的方法可以获取
            jobDetail.getJobDataMap().put(Const.TASK_PROPERTIES, job);
            // 判断是否存在
            if (scheduler.checkExists(getJobKey(jobId, jobGroup))) {
                // 防止创建时存在数据问题 先移除，然后在执行创建操作
                scheduler.deleteJob(getJobKey(jobId, jobGroup));
            }
            scheduler.scheduleJob(jobDetail, trigger);
            //暂停没有开启的任务
            if(!job.getQjStatus()) {
                scheduler.pauseTrigger(triggerKey);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建定时任务
     */
    public static void deleteJob(Scheduler scheduler, JobEntity job) {
        try {
            // 构建job信息
            String jobId = job.getId();
            String jobGroup = job.getQjGroup();
            // 判断是否存在
            if (scheduler.checkExists(getJobKey(jobId, jobGroup))) {
                // 防止创建时存在数据问题 先移除，然后在执行创建操作
                scheduler.deleteJob(getJobKey(jobId, jobGroup));
            }

        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 立即执行任务
     */
    public static void triggerJob(Scheduler scheduler, JobEntity job) {

        try {

            // 构建job信息
            String jobId = job.getId();
            String jobGroup = job.getQjGroup();
            JobKey key = ScheduleUtil.getJobKey(jobId, jobGroup);
            TriggerKey triggerKey = ScheduleUtil.getTriggerKey(jobId, jobGroup);
            //恢复任务
            scheduler.resumeTrigger(triggerKey);
            //触发任务
            scheduler.triggerJob(key);
            //如果任务本身不是没启动，执行完任务就暂停任务
            if(!job.getQjStatus()) {
                scheduler.pauseTrigger(triggerKey);
            }

        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
