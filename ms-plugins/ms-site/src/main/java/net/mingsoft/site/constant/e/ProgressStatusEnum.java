/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.site.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * @Description:
 * @Date: Create in 2022/03/11 9:32
 */
public enum ProgressStatusEnum implements BaseEnum {
    DRAFT("草稿"),
    REVIEW("待审核"),
    APPROVED("终审通过");

    //审批类型
    private final String progressStatus;

    ProgressStatusEnum(String progressStatus) {
        this.progressStatus = progressStatus;
    }

    public String toString() {
        return this.progressStatus;
    }

    @Override
    public int toInt() {
        return 0;
    }
}
