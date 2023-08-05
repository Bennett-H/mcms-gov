/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * 允许删除标识
 * @author by 铭软开发团队
 * @Description TODO
 * @date 2019/11/20 10:34
 */
public enum NotDelEnum implements BaseEnum {
    DEL(0),
    /**
     * 不允许删除
     */
    NOT_DEL(1);
    private int del;
    NotDelEnum(int del) {
        this.del = del;
    }
    @Override
    public int toInt() {
        return this.del;
    }
}
