/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.quartz.service;

import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.quartz.biz.IJobBiz;
import net.mingsoft.quartz.entity.JobEntity;
import net.mingsoft.quartz.utils.ScheduleUtil;
import org.quartz.Scheduler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务启动器
 */
@Service
public class RunService implements ApplicationRunner {

    /**
     * 项目启动时，初始化定时器
     * 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        IJobBiz jobBiz = SpringUtil.getBean(IJobBiz.class);
        Scheduler scheduler = SpringUtil.getBean(Scheduler.class);
        List<JobEntity> jobList = jobBiz.list();
        for (JobEntity job : jobList) {
            ScheduleUtil.createJob(scheduler, job);
        }
    }


}
