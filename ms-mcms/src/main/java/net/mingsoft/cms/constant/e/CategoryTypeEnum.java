/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.cms.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * @Author: 铭飞团队
 * @Description:
 * @Date: Create in 2020/06/23 14:18
 */
public enum CategoryTypeEnum implements BaseEnum {

    /**
     * 列表
     */
    LIST("1"),
    /**
     * 封面
     */
    COVER("2"),
    /**
     * 链接
     */
    LINK("3"),

    /**
     * 未知类型
     */
    UN_KNOW("0");


    CategoryTypeEnum(String type) {
        this.type = type;
    }

    private String type;

    public static CategoryTypeEnum get(String type) {
        for (CategoryTypeEnum e : CategoryTypeEnum.values()) {
            if (e.type.equals(type)) {
                return e;
            }
        }
        return CategoryTypeEnum.UN_KNOW;
    }

    @Override
    public int toInt() {
        return Integer.parseInt(type);
    }

    @Override
    public String toString() {
        return type;
    }
}
