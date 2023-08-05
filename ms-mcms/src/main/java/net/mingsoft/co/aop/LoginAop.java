/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.aop;

import cn.hutool.core.date.DateTime;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.constant.e.SessionConstEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.co.entity.FileEntity;
import org.apache.shiro.web.util.WebUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * login.index拦截器,判断当前账号是否被踢
 * @author 铭软开发团队
 * @version
 * 版本号：1.0<br/>
 * 创建日期：2017-8-24 23:40:55<br/>
 * 历史修订：br/>
 */
@Component
@Aspect
public class LoginAop extends BaseAop {
    @Before("execution(* net.mingsoft.basic.action.web.LoginAction.login(..))")
    public void login(JoinPoint joinPoint) {
        HttpServletRequest request = null;
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpServletRequest){
                request = (HttpServletRequest) arg;
            }
        }
        Object kick = request.getAttribute("kick");
        if (kick != null){
            throw new BusinessException(kick.toString());
        }
    }
}
