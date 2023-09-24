/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.site.interceptor;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.mingsoft.basic.biz.IAppBiz;
import net.mingsoft.basic.constant.Const;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * App拦截器，提供给企业版站群使用
 */
public class AppInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean siteEnable = Boolean.parseBoolean(ConfigUtil.getString("站群配置", "siteEnable", "false"));
        if (!siteEnable){//如果没有开启站群
            SpringUtil.getRequest().getSession().removeAttribute(Const.APP.toString());
            return true;
        }
        AppEntity appEntity = (AppEntity) BasicUtil.getSession(Const.APP);
        if (appEntity == null){
            IAppBiz appBiz = SpringUtil.getBean(IAppBiz.class);
            appEntity = appBiz.getOne(
                    Wrappers.<AppEntity>lambdaQuery()
                            .like(AppEntity::getAppUrl, BasicUtil.getDomain()),false);
        }
        if(appEntity==null) {
            throw new BusinessException("站点信息不存在");
        }
        // todo 此处是为了用127或者local也能访问系统
        appEntity.setAppUrl(BasicUtil.getUrl());
        BasicUtil.setSession(Const.APP, appEntity);
        return true;
    }
}
