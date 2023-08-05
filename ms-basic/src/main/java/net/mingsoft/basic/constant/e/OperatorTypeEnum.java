/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * 操作用户类型
 * @author by 铭软开发团队
 * @Description TODO
 * @date 2019/11/20 10:34
 */
public enum OperatorTypeEnum implements BaseEnum {
    /**
     * 其它
     */
    OTHER,

    /**
     * 后台用户
     */
    MANAGE,

    /**
     * 会员用户
     */
    PEOPLE;

    @Override
    public int toInt() {
        return 0;
    }
}
