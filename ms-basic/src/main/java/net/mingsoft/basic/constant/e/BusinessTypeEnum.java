/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * 操作类型常量
 * @author by 铭软开发团队
 * @Description TODO
 * @date 2019/11/20 10:34
 */
public enum BusinessTypeEnum implements BaseEnum {
    /**
     * 其它
     */
    OTHER("其他"),

    /**
     * 异常
     */
    ERROR("异常"),


    /**
     * 登录
     */
    LOGIN("登录"),

    /**
     * 新增
     */
    INSERT("新增"),

    /**
     * 修改
     */
    UPDATE("修改"),

    /**
     * 删除
     */
    DELETE("删除");

    /**
     * 枚举类型
     */
    public String label;

    BusinessTypeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public int toInt() {
        return 0;
    }
}
