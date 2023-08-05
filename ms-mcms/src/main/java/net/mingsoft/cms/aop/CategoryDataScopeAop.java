/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.cms.aop;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.datascope.biz.IDataBiz;
import net.mingsoft.datascope.entity.DataEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * 此AOP是处理当前管理创建栏目，没有此栏目的增删改权限，需要重新在超级管理员重新赋权的需求
 */

@Aspect
@Component("CategoryDataScopeAop")
public class CategoryDataScopeAop extends net.mingsoft.basic.aop.BaseAop {

    @Autowired
    private IDataBiz dataBiz;

    /**
     * 栏目接口切面
     */
    @Pointcut("execution(* net.mingsoft.cms.action.CategoryAction.save(..)) ")
    public void save() {}

    /**
     * 当一个普通管理员创建了栏目，应该自动赋予对应栏目的文章增删改权限
     */
    @After("save()")
    public void save(JoinPoint jp) {
        // 判断当前是否是超级管理员(超级管理员不需要添加权限)
        ManagerEntity manager = BasicUtil.getManager();
        if(manager == null || "super".equals(manager.getManagerAdmin())) {
            return;
        }
        // 获取栏目实体
        CategoryEntity category = getType(jp, CategoryEntity.class);
        DataEntity data = new DataEntity();
        // 设置data_target_id 为当前管理员ID
        data.setDataTargetId(manager.getRoleIds().split(",")[0]);
        // 取一条进行复制
        DataEntity _data = dataBiz.getOne(new QueryWrapper<>(data), false);
        // 设置data_id 取栏目ID
        if (_data != null && category != null){
            _data.setId(null);
            _data.setDataId(category.getId());
            dataBiz.save(_data);
        }

    }
}
