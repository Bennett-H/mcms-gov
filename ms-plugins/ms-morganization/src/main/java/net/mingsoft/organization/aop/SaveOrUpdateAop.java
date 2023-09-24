/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.organization.aop;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.organization.biz.IEmployeeBiz;
import net.mingsoft.organization.biz.impl.EmployeeBizImpl;
import net.mingsoft.organization.entity.EmployeeEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * 拦截保存更新方法，设置组织机构id
 * @author by Administrator
 * @Description TODO
 * @date 2019/11/12 10:14
 */
@Component("EmployeeSaveOrUpdateAop")
@Aspect
public class SaveOrUpdateAop extends BaseAop {
    @Autowired
    IEmployeeBiz employeeBiz;

    @Pointcut("execution(* net.mingsoft.cms.action.ContentAction.save(..))")
    public void save() {

    }

    @Pointcut("execution(* net.mingsoft.cms.action.ContentAction.update(..))")
    public void update() {
    }

    @AfterReturning("save() || update()")
    public void save(JoinPoint jp) {
        ManagerEntity manager = BasicUtil.getManager();
        if (manager != null) {
            EmployeeEntity employee = employeeBiz.getOne(new LambdaQueryWrapper<EmployeeEntity>().eq(EmployeeEntity::getManagerId, manager.getId()));
            if (employee != null){
                BaseEntity baseEntity = getType(jp, BaseEntity.class, true);
                String id = baseEntity.getId();
                String sql =  "update cms_content set organization_id = {} where id = {}";
                employeeBiz.excuteSql(StrUtil.format(sql,employee.getOrganizationId(),id));
            }
        }

    }


}
