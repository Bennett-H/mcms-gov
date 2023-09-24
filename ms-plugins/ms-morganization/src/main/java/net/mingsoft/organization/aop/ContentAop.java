/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.organization.aop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.datascope.utils.DataScopeUtil;
import net.mingsoft.organization.biz.IEmployeeBiz;
import net.mingsoft.organization.constant.e.DataTypeEnum;
import net.mingsoft.organization.entity.EmployeeEntity;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 文章aop
 */
@Component("EmployeeContentAop")
@Aspect
public class ContentAop extends BaseAop {

    @Autowired
    IEmployeeBiz employeeBiz;

    /**
     * 拦截文章查询,添加权限管理
     * @param joinPoint
     * @throws Throwable
     */
    @Before("execution(* net.mingsoft.cms.dao.IContentDao.query(..))")
    public void query(JoinPoint joinPoint) throws Throwable {
        ManagerEntity manager = BasicUtil.getManager();
        if (manager != null) {
            EmployeeEntity employee = employeeBiz.getOne(new LambdaQueryWrapper<EmployeeEntity>().eq(EmployeeEntity::getManagerId, manager.getId()));
            // 防止分页失效
            BasicUtil.startPage();
            if (employee != null){
                DataScopeUtil.start(manager.getId(),employee.getId(), DataTypeEnum.EMPLOYEE_CONTENT.toString(),true);
            }
        }
    }

}
