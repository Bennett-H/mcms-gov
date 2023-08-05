/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.datascope.annotation;

import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.OperatorTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通过aop拦截的方式,监测当前用户是否具备对应的权限
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DataScope {
    //数据权限业务类型,对应DATA_TYPE
    String type();
    //业务数据id名称，可以指定形参上实体的字段名称
    String id() default "id";
    //权限标识,对应DATA_ID_MODEL中的modelUrl
    String requiresPermissions() ;
}
