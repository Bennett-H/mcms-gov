/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.constant.e;

import cn.hutool.core.util.StrUtil;

/**
 * 服务器和客户端通信消息的标识
 * 历史修订: 实时日志传输需要
 */
public enum MessageEnum {
    /**
     * 客户端通知端启动测试
     */
    START("START"),
    /**
     * 客户端通知端关闭测试
     */
    STOP("STOP"),
    /**
     * 服务端通知客户端解析出 目标链接的url
     */
    URL("URL"),
    /**
     * 服务端通知客户端解析出 内容数据
     */
    CONTENT("CONTENT"),
    /**
     * 服务端通知客户端完成
     */
    FINISH("FINISH");

    public String action;

    MessageEnum(String action) {
        this.action = action;
    }

    /**
     * 根据字符串的到相应的枚举对象
     * @param str
     * @return
     */
    public static MessageEnum findMessageEnumByStr(String str) {
        if (StrUtil.isBlank(str)){
            return null;
        }
        for (MessageEnum value : MessageEnum.values()) {
            if (value.action.equals(str)){
                return value;
            }
        }
        return null;
    }
}
