/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.gov.aop;

import net.mingsoft.base.entity.ResultData;
import net.mingsoft.base.util.BundleUtil;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.bean.ManagerModifyPwdBean;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SecureUtils;
import net.mingsoft.gov.biz.IPasswordBiz;
import net.mingsoft.gov.constant.Const;
import net.mingsoft.gov.constant.e.SecurityPasswordTypeEnum;
import net.mingsoft.gov.entity.PasswordEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @Author: xierz
 * @Description: 记录管理员密码的修改次数
 * @Date: Create in 2021/03/13 8:40
 */
@Component("govPasswordAop")
@Aspect
public class PasswordAop extends BaseAop {

    @Resource
    IPasswordBiz passwordBiz;
    @Resource
    IManagerBiz managerBiz;

    @Pointcut("execution(* net.mingsoft.basic.action.MainAction.updatePassword(..))")
    public void updatePassword() {
    }

    /**
     * gov版本管理员个人修改密码验证与加盐处理
     * 新增修改密码验证规则：5次内口令不能重复
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("updatePassword()")
    public Object updatePassword(ProceedingJoinPoint joinPoint) throws Throwable {
        LOG.debug("gov PasswordAop 修改密码为5次不能重复");
        ManagerModifyPwdBean managerModifyPwdBean = super.getType(joinPoint, ManagerModifyPwdBean.class);
        //获取session
        ManagerEntity managerSession =  BasicUtil.getManager();
        if (managerSession != null) {
            String newPassword = SecureUtils.password(managerModifyPwdBean.getNewManagerPassword(),managerSession.getManagerName());

            //开始验证更新的口令五次内不能重复
            PasswordEntity passwordEntity = new PasswordEntity();
            passwordEntity.setPasswordOwnerId(managerSession.getId());
            passwordEntity.setPasswordType(SecurityPasswordTypeEnum.PASS_WORD_TYPE.getPassWordType());
            int num = ConfigUtil.getInt("安全设置","updatePasswordNum",5);
            if (passwordBiz.modifyPasswordForRepeat(num, passwordEntity, newPassword)) {
                return ResultData.build().error(BundleUtil.getString( Const.RESOURCES,"error.modify.password.repeat",num+""));
            }
            passwordEntity.setCreateBy(managerSession.getId());
            passwordEntity.setCreateDate(new Date());
            Object proceed = joinPoint.proceed();
            Map<String, Object> resultMap = (Map) proceed;
            if (!(Boolean) resultMap.get(ResultData.RESULT_KEY)) {
                return proceed;
            }
            passwordEntity.setPasswordOwnerPwd(newPassword);
            //添加密码修改记录
            passwordBiz.save(passwordEntity);
            return ResultData.build().success();
        }
        return ResultData.build().error("err.error");
    }

    /**
     * 添加修改管理员信息处理，向密码记录表添加数据
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @AfterReturning("execution(*  net.mingsoft.basic.action.ManagerAction.update(..))")
    public void update(JoinPoint joinPoint) throws Throwable {
        LOG.debug("修改密码添加密码修改记录");
        ManagerEntity manager = super.getType(joinPoint, ManagerEntity.class);
        ManagerEntity managerSession =  BasicUtil.getManager();
        if (managerSession != null) {
            //增加管理员密码修改记录
            PasswordEntity passwordEntity = new PasswordEntity();
            passwordEntity.setPasswordOwnerId(manager.getId());
            passwordEntity.setPasswordType(SecurityPasswordTypeEnum.PASS_WORD_TYPE.getPassWordType());
            passwordEntity.setCreateBy(managerSession.getId());
            passwordEntity.setCreateDate(new Date());
            passwordEntity.setPasswordOwnerPwd(manager.getManagerPassword());
            //添加密码修改记录
            passwordBiz.save(passwordEntity);
            LOG.debug("添加记录成功");
        }
    }

}
