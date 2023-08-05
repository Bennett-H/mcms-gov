/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.basic.aop;

import cn.hutool.crypto.SecureUtil;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.ManagerModifyPwdBean;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.constant.e.SessionConstEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: xierz
 * @Description:
 * @Date: Create in 2021/03/13 8:40
 */
@Component
@Aspect
public class ManagerPasswordAop extends BaseAop {


    @Resource
    IManagerBiz managerBiz;

    @Pointcut("execution(* net.mingsoft.basic.action.MainAction.updatePassword(..))")
    public void updatePassword() {
    }

    /**
     * 修改密码时候，将密码md5处理
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("updatePassword()")
    public Object updatePassword(ProceedingJoinPoint joinPoint) throws Throwable {
        LOG.debug("basic ManagerPasswordAop 修改密码为md5");
        ManagerModifyPwdBean managerModifyPwdBean = super.getType(joinPoint, ManagerModifyPwdBean.class);
        managerModifyPwdBean.setOldManagerPassword(SecureUtil.md5(managerModifyPwdBean.getOldManagerPassword()));
        return joinPoint.proceed();
    }



}
