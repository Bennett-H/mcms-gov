/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.quartz.aop;

import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.quartz.entity.JobEntity;
import net.mingsoft.quartz.utils.ScheduleUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Aspect
public class JobAop extends BaseAop {

    @Autowired
    Scheduler scheduler;
    /**
     * 切入点
     */
    @Pointcut("execution(* net.mingsoft.quartz.action.JobAction.save(..)) || " +
            "execution(* net.mingsoft.quartz.action.JobAction.update(..)) ")
    public void saveOrUpdate() {
    }

    /**
     * 切入点
     */
    @Pointcut("execution(* net.mingsoft.quartz.action.JobAction.delete(..))")
    public void delete() {
    }


    @Around("saveOrUpdate()")
    public Object saveOrUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        JobEntity job = this.getType(joinPoint, JobEntity.class);
        ScheduleUtil.createJob(scheduler,job);
        return result;
    }

    @Around("delete()")
    public Object delete(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        List<JobEntity> jobs = this.getType(joinPoint, ArrayList.class);
        jobs.forEach(job ->{
            ScheduleUtil.deleteJob(scheduler,job);
        });
        return result;
    }

}
