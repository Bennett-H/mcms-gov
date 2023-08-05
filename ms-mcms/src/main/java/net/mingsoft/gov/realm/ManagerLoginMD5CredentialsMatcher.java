/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.gov.realm;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.IpUtils;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.co.bean.ManagerLoginCacheBean;
import net.mingsoft.co.cache.ManagerOnlineCache;
import net.mingsoft.co.constant.Const;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 控制登录次数，政府版本，ShiroConfig使用到
 *
 * @author 铭飞团体
 * @version 版本号：<br/>
 *          创建日期：2015年9月9日<br/>
 * 历史修订：
 * 2021-07-13 账户被锁定<br/>
 */
public class ManagerLoginMD5CredentialsMatcher extends HashedCredentialsMatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerLoginMD5CredentialsMatcher.class);


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
        } else {
            // 这里不能设置最后登录时间否则会出现锁定账户无法解锁的情况
            String hostIp = BasicUtil.getIp();
            loginCacheBean.setLoginIpAddress(hostIp);
            loginCacheBean.setLoginAddress(IpUtils.getRealAddressByIp(hostIp));
        }

        if (ManagerLoginCacheBean.LOCK.equalsIgnoreCase(loginCacheBean.getOnOfLockStatus())) {//账号锁定了
            if (isLockLastLoginTime(loginCacheBean.getLastLoginTime())) {//超出锁定时间解锁
                loginCacheBean.setLastLoginTime(date);
                loginCacheBean.getLoginFailCount().set(0);
                loginCacheBean.setOnOfLockStatus(ManagerLoginCacheBean.UNLOCK);
            } else {
                throw new LockedAccountException("账户被锁定" + ConfigUtil.getInt("安全设置", "timeout") + "分钟,请稍后再试");
            }
        }

        boolean matches = super.doCredentialsMatch(token, info);

        LOGGER.debug("MD5{}",this.hashProvidedCredentials(token, info));
        if (matches) {
            loginCacheBean.setLastLoginTime(date);
            loginCacheBean.setOnline(Boolean.TRUE);
            Subject subject = SecurityUtils.getSubject();
            Serializable sessionId = subject.getSession().getId();
            Const.SESSION_IDS.add(sessionId);
            loginCacheBean.setToken(sessionId.toString());
            loginCacheBean.setOnOfLockStatus(ManagerLoginCacheBean.UNLOCK);
        } else {

            if (loginCacheBean.getLoginFailCount().get() + 1 >= ConfigUtil.getInt("安全设置", "errorLogin")) {//登录超过N次数锁定账号
                loginCacheBean.setLastLoginTime(date);
                loginCacheBean.setOnOfLockStatus(ManagerLoginCacheBean.LOCK);
                loginCacheBean.getLoginFailCount().set(loginCacheBean.getLoginFailCount().get() + 1);
            } else {
                loginCacheBean.getLoginFailCount().set(loginCacheBean.getLoginFailCount().get() + 1);
            }

        }
        managerOnlineCache.saveOrUpdate(loginCacheBean.getManagerName(), loginCacheBean);
        return matches;
    }


    /**
     * 锁住时间判断, 账号锁住超时则可以登录,超时时间在后台“安全设置”菜单中设置
     *
     * @param date 最后登录时间
     * @return false 未超时，不可登录；true 超时，可以继续登录
     */
    private boolean isLockLastLoginTime(Date date) {
        if (date == null) {
            return false;
        }
        //获取安全设置的timeout
        //注意:代码生成器组件需要是“下划线命名法” ,取值是才是用"驼峰命名法“;
        //如果不是按照“下划线命名法”的话,取值需要根据表(mdiy_config)中的字段
        Date newDate = DateUtil.offsetMinute(date, ConfigUtil.getInt("安全设置", "timeout"));
        if (DateUtil.between(new Date(), newDate, DateUnit.MINUTE, false) <= 0) {
            return true;
        }
        return false;
    }
}
