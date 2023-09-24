/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.aop;

import cn.hutool.core.collection.CollUtil;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.organization.biz.IEmployeeBiz;
import net.mingsoft.organization.entity.EmployeeEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: csp
 * @Description: 员工的aop
 * @Date: Create in 2022-01-24 13:50
 */
@Component("EmployeeAop")
@Aspect
public class EmployeeAop extends BaseAop {
    @Resource
    IManagerBiz managerBiz;

    @Resource
    IEmployeeBiz employeeBiz;

    @Pointcut("execution(* net.mingsoft.organization.biz.IEmployeeBiz.removeByIds(..))")
    public void delete() {
    }

    @Pointcut("execution(* net.mingsoft.organization.action.EmployeeAction.save(..))")
    public void save() {
    }
    @Pointcut("execution(* net.mingsoft.organization.action.EmployeeAction.update(..))")
    public void update() {
    }

    @Pointcut("execution(* net.mingsoft.organization.action.EmployeeAction.delete(..))")
    public void deleteManageByIds() {
    }
    /**
     * 员工状态为离职时,更新对应管理员的状态
     * 更新员工姓名管理员昵称也需改变
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @AfterReturning("save() || update()")
    public Object update(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        EmployeeEntity employee = (EmployeeEntity) args[0];
        Integer managerId = employee.getManagerId();
        ManagerEntity manager = managerBiz.getById(managerId);
        if (manager != null){
            if ("out".equals(employee.getEmployeeStatus())){
                employeeBiz.updateManagerLock("lock",managerId.toString());
            }else {
                employeeBiz.updateManagerLock("",managerId.toString());
            }
            //管理员昵称与员工姓名不一致则修改管理员昵称
            if (!manager.getManagerNickName().equals(employee.getEmployeeNickName())){
                manager.setManagerNickName(employee.getEmployeeNickName());
                managerBiz.updateById(manager);
            }
        }
        return ResultData.build().success();
    }


    /**
     * 删除员工级联删除管理员的aop
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Before("delete()")
    public Object save(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        List<Integer> employeeIds = (List<Integer>) args[0];
        List<Integer> managerIds = employeeBiz.listByIds(employeeIds).stream().filter(employeeEntity -> employeeEntity.getManagerId() != null).map(EmployeeEntity::getManagerId).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(managerIds)){
            managerBiz.removeByIds(managerIds);
        }
        return ResultData.build().success();
    }

    /**
     * 删除员工信息同时删除管理员信息
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @AfterReturning("deleteManageByIds()")
    public void deleteManageByIds(JoinPoint joinPoint) throws Throwable {
        LOG.debug("开始删除员工关联管理员信息");
        Object[] args = joinPoint.getArgs();
        List<EmployeeEntity> employeeList = (List<EmployeeEntity>) args[0];
        int[] managerIds = new int[employeeList.size()];
        for(int i = 0; i < employeeList.size(); i++){
            if (employeeList.get(i).getManagerId() != null){
                managerIds[i] = employeeList.get(i).getManagerId();
            }
        }
        managerBiz.delete(managerIds);

        LOG.debug("删除员工关联管理员信息成功");

    }

}
