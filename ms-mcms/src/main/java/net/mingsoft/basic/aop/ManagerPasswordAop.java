/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.basic.aop;

import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.ManagerModifyPwdBean;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SecureUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 管理员修改密码，因为企业版本的密码有加盐的处理，所以每次修改都需要对密码进行处理
 * @author 铭软开发团队
 * @version
 * 版本号：1.0<br/>
 * 创建日期：2021-03-11 11:40:55<br/>
 * 历史修订：修改密码为md5+盐<br/>
 */
@Component("coManagerPasswordAop")
@Aspect
public class ManagerPasswordAop extends BaseAop {

    @Resource
    IManagerBiz managerBiz;


    /**
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* net.mingsoft.basic.action.MainAction.updatePassword(..))")
    public Object updatePassword(ProceedingJoinPoint joinPoint) throws Throwable {
        LOG.debug("co ManagerPasswordAop 修改密码为md5+盐");
        ManagerModifyPwdBean managerModifyPwdBean = super.getType(joinPoint, ManagerModifyPwdBean.class);
        //获取session
        ManagerEntity managerSession =  BasicUtil.getManager();
        if (managerSession != null) {
            managerModifyPwdBean.setOldManagerPassword(SecureUtils.password(managerModifyPwdBean.getOldManagerPassword(),managerSession.getManagerName()));

            Object proceed = joinPoint.proceed();
            Map<String, Object> resultMap = (Map) proceed;
            if (!(Boolean) resultMap.get(ResultData.RESULT_KEY)) {
                return proceed;
            }

            //gov版本开始修改密码变为账号加盐处理
            managerSession.setManagerPassword(SecureUtils.password(managerModifyPwdBean.getNewManagerPassword(),managerModifyPwdBean.getManagerName()));

            //更改以前版本MD5密码为加盐密码
            managerBiz.updateUserPasswordByUserName(managerSession);
            return ResultData.build().success();
        }
        return ResultData.build().error("err.error");
    }

    /**
     * 添加管理员密码处理
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    //@Around("execution(*  net.mingsoft.basic.action.ManagerAction.update(..)) || execution(*  net.mingsoft.basic.action.ManagerAction.save(..)) || execution(* net.mingsoft.mwebsite.action.WebsiteManagerAction.update(..))")
    @Around("execution(*  net.mingsoft.basic.action.ManagerAction.update(..)) || execution(*  net.mingsoft.basic.action.ManagerAction.save(..))")
    public Object update(ProceedingJoinPoint joinPoint) throws Throwable {
        ManagerEntity manager = super.getType(joinPoint, ManagerEntity.class);
        String password = manager.getManagerPassword();
        Object proceed = joinPoint.proceed();
        if(StringUtils.isNotBlank(password)) {
            Map<String, Object> resultMap = (Map) proceed;
            if (!(Boolean) resultMap.get(ResultData.RESULT_KEY)) return proceed;
            //因为默认业务会设置md5所有要重新加密
            manager.setManagerPassword(password);
            manager.setManagerPassword(SecureUtils.password(manager.getManagerPassword(),manager.getManagerName()));
            managerBiz.updateEntity(manager);
        }
        return proceed;
    }


}
