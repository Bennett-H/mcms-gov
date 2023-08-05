/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.wordfilter.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * 词汇类型枚举
 * 创建日期：2021-10-18 9:54:50<br/>
 * 历史修订：<br/>
 */
public enum WordTypeEnum implements BaseEnum {
    /**
     * 敏感词
     */
    SENSITIVE("sensitive"),
    /**
     * 纠错词
     */
    CORRECTION("correction");

    @Override
    public int toInt() {
        return 0;
    }

    private final String code;

    WordTypeEnum(String code){
        this.code = code;
    }

    @Override
    public String toString(){
        return this.code;
    }
}
