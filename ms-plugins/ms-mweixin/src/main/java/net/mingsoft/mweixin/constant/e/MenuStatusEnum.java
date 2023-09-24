/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * 菜单状态
 * @Author: 铭飞开源团队--huise
 * @Date: 2019/6/6 9:18
 */
public enum MenuStatusEnum implements BaseEnum {
    /**
     *禁用
     */
    DISABLE(0,"禁用"),
    /**
     *启用
     */
    ENABLE(1,"启用");
    MenuStatusEnum(int id, Object code) {
        this.id = id;
        this.code = code;
    }

    private Object code;

    private int id;

    @Override
    public int toInt() {
        // TODO Auto-generated method stub
        return id;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return code.toString();
    }
}
