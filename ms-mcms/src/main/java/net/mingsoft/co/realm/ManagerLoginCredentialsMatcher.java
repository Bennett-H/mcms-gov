/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.realm;

import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.IpUtils;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.co.bean.ManagerLoginCacheBean;
import net.mingsoft.co.cache.ManagerOnlineCache;
import net.mingsoft.co.constant.Const;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description :控制登录次数，企业版本，ShiroConfig使用到
 * @author: 铭软开发团队
 * @date: 2015年1月27日 下午3:21:47
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 */
public class ManagerLoginCredentialsMatcher extends HashedCredentialsMatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerLoginCredentialsMatcher.class);


    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        ManagerOnlineCache managerOnlineCache = SpringUtil.getBean(ManagerOnlineCache.class);
        //获取用户名
        String username = (String) token.getPrincipal();
        ManagerLoginCacheBean loginCacheBean = managerOnlineCache.get(username);
        ManagerEntity _manager = (ManagerEntity) info.getPrincipals().getPrimaryPrincipal();
        Date date = new Date();
        if (loginCacheBean == null) {
            //如果用户没有存在缓存中,则初始化缓存数据
            loginCacheBean = new ManagerLoginCacheBean();
            BeanUtils.copyProperties(_manager, loginCacheBean);
            loginCacheBean.setLoginTime(date);
            loginCacheBean.setLastLoginTime(date);
            AtomicInteger atomicInteger = new AtomicInteger(0);
            loginCacheBean.setLoginFailCount(atomicInteger);
            loginCacheBean.setManagerPassword(null);
            String hostIp = BasicUtil.getIp();
            loginCacheBean.setLoginIpAddress(hostIp);
            loginCacheBean.setLoginAddress(IpUtils.getRealAddressByIp(hostIp));
        }else {
            loginCacheBean.setLastLoginTime(date);
            String hostIp = BasicUtil.getIp();
            loginCacheBean.setLoginIpAddress(hostIp);
            loginCacheBean.setLoginAddress(IpUtils.getRealAddressByIp(hostIp));
        }


        //判断用户账号和密码是否正确
        boolean matches = super.doCredentialsMatch(token, info);
        Object accountCredentials = hashProvidedCredentials(token, info);
        LOGGER.debug("密码：{}", accountCredentials);
        if (matches) {
            loginCacheBean.setLastLoginTime(date);
            //如果正确,将登录失败次数置为初始值
            loginCacheBean.getLoginFailCount().set(0);
            loginCacheBean.setOnline(Boolean.TRUE);
            Subject subject = SecurityUtils.getSubject();
            Serializable sessionId = subject.getSession().getId();
            Const.SESSION_IDS.add(sessionId);
            loginCacheBean.setToken(sessionId.toString());
            loginCacheBean.setOnOfLockStatus(ManagerLoginCacheBean.UNLOCK);
        }
        managerOnlineCache.saveOrUpdate(loginCacheBean.getManagerName(),loginCacheBean);
        return matches;
    }


}
