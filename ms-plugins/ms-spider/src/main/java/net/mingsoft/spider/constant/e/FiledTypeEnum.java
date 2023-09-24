/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.constant.e;

import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.spider.exception.SpiderException;

public enum FiledTypeEnum {
    /**
     * 映射类型
     */
    STRING("string"),
    NUMBER("number"),
    DATE("date");


    public String type;

    FiledTypeEnum(String type) {
        this.type = type;
    }

    /**
     * 根据字符串找到相应的枚举
     */
    public static FiledTypeEnum findFiledTypeEnumByStr(String str){
        for (FiledTypeEnum value : FiledTypeEnum.values()) {
            if (value.type.equals(str)){
                return value;
            }
        }
        throw new SpiderException("未知字符串");
    }
}
