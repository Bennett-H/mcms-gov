/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.config;

import net.mingsoft.spider.util.LogBatchImportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.concurrent.Executor;

@Configuration(proxyBeanMethods = false)
public class SpiderConfig {

    @Resource
    DataSource dataSource;

    @Bean
    public LogBatchImportUtil batchImportUtil(){
        return new LogBatchImportUtil(dataSource);
    }

}
