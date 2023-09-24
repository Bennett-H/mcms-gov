/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.exception;

import net.mingsoft.basic.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 爬虫项目统一抛出异常
 */
public class SpiderException extends BusinessException {
    public SpiderException(String msg) {
        super(msg);
    }

    public SpiderException(String code, String msg) {
        super(HttpStatus.valueOf(code), msg);
    }
}
