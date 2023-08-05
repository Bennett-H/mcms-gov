/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.strategy;

import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.strategy.ILoginStrategy;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.co.constant.e.ManagerEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * 管理员登录,用到slat，需要在MSConfig配置，菜单采用底层basic策略
 *
 * @author Administrator
 * @version 创建日期：2020/11/18 18:12<br/>
 * 历史修订：<br/>
 */
public class ManagerLoginStrategy implements ILoginStrategy {

    /*
     * log4j日志记录
     */
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IManagerBiz managerBiz;

    @Override
    public Boolean login(ManagerEntity manager) {
        LOG.debug("mcms-co 登录策略");
        if (manager == null || StringUtils.isEmpty(manager.getManagerName()) || StringUtils.isEmpty(manager.getManagerPassword())) {
            return false;
        }
        //每次都刷新一下缓存，方便直接修改表密码调试登陆
        managerBiz.updateCache();
        // 根据账号获取当前管理员信息
        ManagerEntity _manager = managerBiz.getManagerByManagerName(manager.getManagerName());
        if (_manager == null) {
            LOG.debug("mcms-co 系统不存在此用户");
            // 系统不存在此用户
            return false;
        } else if (ManagerEnum.LOCK.toString().equalsIgnoreCase(_manager.getManagerLock())) {
            LOG.debug("mcms-co 账户被锁定");
            throw new BusinessException(HttpStatus.LOCKED,"账户被锁定");
        } else {
            boolean rememberMe = BasicUtil.getBoolean("rememberMe");
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken upt = new UsernamePasswordToken(manager.getManagerName(), manager.getManagerPassword(),rememberMe);
            try {
                LOG.debug("mcms-co 尝试登陆");
                subject.login(upt);
                LOG.debug("mcms-co 登陆成功");
                return true;
            } catch (Exception e){
                LOG.debug("mcms-co 登陆失败");
                if (e.getCause() instanceof BusinessException){
                    throw (BusinessException) e.getCause();
                }
                e.printStackTrace();
            }
            return false;
        }
    }
}
