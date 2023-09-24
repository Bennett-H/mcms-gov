/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.constant.e;

import cn.hutool.core.util.StrUtil;

/**
 * 服务器和客户端通信消息的标识
 */
public enum MessageEnum {
    /**
     * 客户端通知端启动测试
     */
    START_UP("1"),
    /**
     * 客户端通知端关闭测试
     */
    STOP("2"),
    /**
     * 服务端通知客户端解析出 目标链接的url
     */
    URL("11"),
    /**
     * 服务端通知客户端解析出 内容数据
     */
    CONTENT("12"),
    /**
     * 服务端通知客户端爬取内容完成
     */
    FINISH("13");

    public String action;

    MessageEnum(String action) {
        this.action = action;
    }

    /**
     * 根据字符串的到相应的枚举对象
     * @param str
     * @return
     */
    public static MessageEnum findMessageEnumByStr(String str){
        if (!StrUtil.isBlank(str)){
            for (MessageEnum value : MessageEnum.values()) {
                if (value.action.equals(str)){
                    return value;
                }
            }
        }
        return null;
    }
}
