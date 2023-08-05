/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.people.annotation;

import net.mingsoft.people.constant.e.PeopleLogTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 铭软科技
 * 创建日期：2023-6-5 11:48:14<br/>
 * 历史修订：<br/>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PeopleLogAnn {

    /**
     * 会员日志标题
     * @return
     */
    String title() default "";

    /**
     * 业务类型
     * @return
     */
    PeopleLogTypeEnum businessType() default PeopleLogTypeEnum.OTHER;

    /**
     * 是否保存请求的参数，如果data不需要可以设置false减少日志表的内容
     * @return
     */
    boolean saveRequestData() default true;

}
