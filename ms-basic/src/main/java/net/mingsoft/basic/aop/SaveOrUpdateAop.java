/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.aop;

import cn.hutool.core.util.ObjectUtil;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * 拦截保存更新方法，设置操作人和时间
 * @author by Administrator
 * @Description TODO
 * @date 2019/11/12 10:14
 */
@Component
@Aspect
public class SaveOrUpdateAop extends BaseAop {

    @Pointcut("execution(* net.mingsoft..*Action.save(..))")
    public void save() {

    }

    @Pointcut("execution(* net.mingsoft..*Action.update(..))")
    public void update() {
    }

    @Before("save()")
    public void save(JoinPoint jp) { setField(jp, "createDate", new Date());
        setField(jp, "updateDate", new Date());
        ManagerEntity manager = BasicUtil.getManager();
        if (manager != null) {
            setField(jp, "createBy", manager.getId());
        }

    }


    @Before("update()")
    public void update(JoinPoint jp) {
        setField(jp, "updateDate", new Date());
        ManagerEntity manager = BasicUtil.getManager();
        if (manager != null) {
            setField(jp, "updateBy", manager.getId());
        }
    }


    private void setField(JoinPoint jp, String name, Object obj) {
        try {
            Object[] objs = jp.getArgs();
            if (objs.length == 0 || ObjectUtil.isNull(objs[0])) {
                return;
            }
            //获取对象所有字段
            Field[] allFields = BasicUtil.getAllFields(objs[0]);
            for (Field field : allFields) {
                //判断是否存在
                if (name.equals(field.getName())) {
                    field.setAccessible(true);
                    //设置时间
                    field.set(objs[0], obj);
                }
            }
        } catch (Exception e) {
            LOG.error("Aop错误：", e);
        }
    }

}
