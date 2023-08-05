/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.mdiy.aop;

import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.co.biz.IContentBiz;
import net.mingsoft.co.cache.ConfigCache;
import net.mingsoft.co.cache.ContentCache;
import net.mingsoft.co.service.FileTagCacheService;
import net.mingsoft.mdiy.action.ConfigAction;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义配置的aop,当自定义配置变动时,删除缓存
 */
@Component("configAop")
@Aspect
public class ConfigAop extends BaseAop {

    @After("execution(* net.mingsoft.mdiy.action.ConfigDataAction.update(..)) || execution(* net.mingsoft.mdiy.action.ConfigAction.delete(..)) || execution(* net.mingsoft.mdiy.action.ConfigAction.updateJson(..))")
    public void updateConfigCache(JoinPoint joinPoint) throws Throwable {
        ConfigCache configCache = SpringUtil.getBean(ConfigCache.class);
        configCache.deleteAll();
    }



}
