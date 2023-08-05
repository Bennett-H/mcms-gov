/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.people.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * 登陆类型
 */
public enum LoginTypeEnum implements BaseEnum {
    /**
     * 账号登陆
     */
    NAME(" 账号"),

    /**
     * 邮箱登陆
     */
    MAIL(" 邮箱"),

    /**
     * 手机号登陆
     */
    PHONE("手机号");

    /**
     * 枚举类型
     */
    public String label;

    LoginTypeEnum(String label) {
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
