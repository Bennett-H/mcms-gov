/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.organization.aop;

import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.organization.biz.IEmployeeBiz;
import net.mingsoft.organization.entity.EmployeeEntity;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author: csp
 * @Description: 新增管理员的aop
 * @Date: Create in 2021/03/13 8:40
 */
@Component("ManagerAop")
@Aspect
public class ManagerAop extends BaseAop {



    @Resource
    IEmployeeBiz employeeBiz;

    @Pointcut("execution(* net.mingsoft.basic.action.ManagerAction.save(..))")
    public void save() {
    }


    /**
     * 组织管理给员工设置管理员账号的aop
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("save()")
    public Object save(ProceedingJoinPoint joinPoint) throws Throwable {
        String employeeId = BasicUtil.getString("employeeId");
        Object proceed = joinPoint.proceed();
        Map<String, Object> resultMap = (Map) proceed;
        String code = resultMap.get("code").toString();

        if (code.equals(String.valueOf(HttpStatus.OK.value()))){
            ManagerEntity resultManager = (ManagerEntity) resultMap.get("data");
            if (StringUtils.isNotEmpty(employeeId)){
                //带了员工id
                //保存后修改employee表的managerId
                EmployeeEntity employeeEntity = employeeBiz.getById(employeeId);
                employeeEntity.setManagerId(Integer.valueOf(resultManager.getId()));
                employeeBiz.updateById(employeeEntity);
            }
            return ResultData.build().success(resultManager);
        }
        return resultMap;
    }

}
