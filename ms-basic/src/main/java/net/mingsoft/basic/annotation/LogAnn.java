/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.annotation;

import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.OperatorTypeEnum;

import java.lang.annotation.*;

/**
 * 日志注解
 * @author by 铭软开发团队
 * @Description TODO
 * @date 2019/11/20 9:58
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnn {
    /**
     * 日志标题
     * @return
     */
    String title() default "";

    /**
     * 业务类型
     * @return
     */
    BusinessTypeEnum businessType() default BusinessTypeEnum.OTHER;

    /**
     * 操作人员类型
     * @return
     */
    OperatorTypeEnum operatorType() default OperatorTypeEnum.MANAGE;

    /**
     * 是否保存请求的参数，如果data不需要可以设置false减少日志表的内容
     * @return
     */
    boolean saveRequestData() default true;

    /**
     * 是否保存业务id,会拦截实体的getId内容
     * @return
     */
    boolean saveId() default false;
}
