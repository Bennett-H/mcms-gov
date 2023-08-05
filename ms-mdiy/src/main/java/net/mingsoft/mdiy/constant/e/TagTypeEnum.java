/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.constant.e;

/**
 * @Author: 铭飞团队
 * @Description:
 * @Date: Create in 2020/06/23 14:18
 */
public enum  TagTypeEnum {
    /**
     * 单标签
     */
    SINGLE("single"),
    /**
     * 列表标签
     */
    LIST("list"),
    /**
     * 宏定义
     */
    MACRO("macro");


    TagTypeEnum(String type) {
        this.type = type;
    }

    private String type;

    public static TagTypeEnum get(String type) {
        for (TagTypeEnum e : TagTypeEnum.values()) {
            if (e.type.equals(type)) {
                return e;
            }
        }
        return null;
    }
}
