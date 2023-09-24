/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.constant.e;

/**
 * @package: net.mingsoft.mweixin.constant.e
 * @description: 消息回复类型的注解
 * @author: jemor1999@qq.com
 * @create: 2020-07-03 09:10
 **/
public enum MessageReplyType {
    KEYWORD("keyword"),
    ATTENTION("attention"),
    PASSIVITY("passivity"),
    ALL("all");

    private final String value;

    MessageReplyType(String value){
        this.value = value;
    }

    public static MessageReplyType getValue(String value){
        for (MessageReplyType messageReplyType:
                values()) {
            if(messageReplyType.toString().equals(value)){
                return messageReplyType;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return value;
    }
}
