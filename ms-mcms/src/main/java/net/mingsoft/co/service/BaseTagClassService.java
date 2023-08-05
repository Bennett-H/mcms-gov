/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public abstract class BaseTagClassService {
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
    public abstract Object excute(Map map);
}
