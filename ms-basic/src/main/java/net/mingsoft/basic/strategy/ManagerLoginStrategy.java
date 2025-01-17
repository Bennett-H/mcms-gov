/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.basic.strategy;

import cn.hutool.crypto.SecureUtil;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.constant.e.SessionConstEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 管理员登录列表
 *
 * @author Administrator
 * @version 创建日期：2020/11/18 18:12<br/>
 * 历史修订：<br/>
 */
public class ManagerLoginStrategy implements ILoginStrategy{


    @Autowired
    private IManagerBiz managerBiz;

    @Override
    public Boolean login(ManagerEntity manager) {
        managerBiz.updateCache();
        boolean rememberMe = BasicUtil.getBoolean("rememberMe");
        if(manager ==null || StringUtils.isEmpty(manager.getManagerName()) || StringUtils.isEmpty(manager.getManagerPassword())){
            return false;
        }
        // 根据账号获取当前管理员信息
        ManagerEntity newManager = new ManagerEntity();
        newManager.setManagerName(manager.getManagerName());
        ManagerEntity _manager = (ManagerEntity) managerBiz.getEntity(newManager);
        if (_manager == null ) {
            // 系统不存在此用户
            return false;
        } else {
            // 判断当前用户输入的密码是否正确
            if (SecureUtil.md5(manager.getManagerPassword()).equals(_manager.getManagerPassword())) {
                Subject subject = SecurityUtils.getSubject();
                UsernamePasswordToken upt = new UsernamePasswordToken(_manager.getManagerName(), _manager.getManagerPassword());
                upt.setRememberMe(rememberMe);
                subject.login(upt);
                return true;
            } else {
                // 密码错误
                return false;
            }
        }
    }
}
