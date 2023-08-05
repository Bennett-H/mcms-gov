/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.elasticsearch.aop;

import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.aop.BaseAop;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 拦截基础类
 * @author 铭软科技
 * 创建日期：2021-1-6 16:52:39<br/>
 * 历史修订：<br/>
 */
public abstract class ESBaseAop extends BaseAop {
    //保存和更新
    @Pointcut("@annotation(net.mingsoft.elasticsearch.annotation.ESSave)")
    public void save() {
    }

    @AfterReturning(value = "save()", returning = "result")
    public abstract void save(JoinPoint joinPoint, ResultData result);

    //保存和更新
    @Pointcut("@annotation(net.mingsoft.elasticsearch.annotation.ESDelete)")
    public void delete() {
    }

    @AfterReturning(pointcut = "delete()", returning = "result")
    public abstract void delete(JoinPoint joinPoint, ResultData result);
}
