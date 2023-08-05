/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.mdiy.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * 自定义业务类型枚举
 * @author by 铭软开发团队
 * @Description TODO
 * @date 2022/05/31 16:34
 */
public enum ModelCustomTypeEnum implements BaseEnum {
    /**
     * 自定义配置
     */
    CONFIG("config"),

    /**
     * 自定义业务
     */
    FORM("form"),

    /**
     * 自定义模型
     */
    MODEL("model");

    /**
     * 枚举类型
     */
    public String label;

    ModelCustomTypeEnum(String label) {
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
