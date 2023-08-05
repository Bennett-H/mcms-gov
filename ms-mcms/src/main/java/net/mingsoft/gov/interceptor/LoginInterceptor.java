/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.gov.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.mingsoft.base.util.BundleUtil;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.co.constant.e.ManagerEnum;
import net.mingsoft.config.MSProperties;
import net.mingsoft.gov.biz.IManagerInfoBiz;
import net.mingsoft.gov.biz.IPasswordBiz;
import net.mingsoft.gov.constant.Const;
import net.mingsoft.gov.constant.e.SecurityPasswordTypeEnum;
import net.mingsoft.gov.entity.ManagerInfoEntity;
import net.mingsoft.gov.entity.PasswordEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 双因子登录的拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ManagerEntity manager = BasicUtil.getManager();
        //没有登录,返回
        if (manager == null){
            return true;
        }
        //判断账号是否被锁定
        IManagerBiz managerBiz = SpringUtil.getBean(IManagerBiz.class);
        manager = managerBiz.getById(manager.getId());
        if(ManagerEnum.LOCK.toString().equalsIgnoreCase(manager.getManagerLock())) {
            throw  new BusinessException(BundleUtil.getString(net.mingsoft.gov.constant.Const.RESOURCES,"error.account.locked"));
        }

        //判断新用户是否需要修改密码
        IPasswordBiz passwordBiz = SpringUtil.getBean(IPasswordBiz.class);
        boolean isFirst = passwordBiz.modifyPasswordForFirst();
        if(isFirst) {
            //todo 转发到修改密码页面
            throw  new BusinessException(BundleUtil.getString(net.mingsoft.gov.constant.Const.RESOURCES,"error.first.update.passwords"));
        }
        //判断密码是否达到修改时间
        int day = passwordBiz.modifyPasswordForTime();
        //超过时间则添加提示
        if(day > 0) {
            //todo 转发到修改密码页面
            throw  new BusinessException(BundleUtil.getString(net.mingsoft.gov.constant.Const.RESOURCES,"password.max.day"));
        }
        //检查是否开启双因子登录
        String doubleFactorEnable = ConfigUtil.getString("安全设置", "doubleFactorEnable", "false");
        if (!"true".equalsIgnoreCase(doubleFactorEnable)){
            return true;
        }
        //是否验证了手机号
        Object verification = BasicUtil.getSession(Const.MANAGER_VERIFICATION);
        if (verification != null){
            return true;
        }
        IManagerInfoBiz managerInfoBiz = SpringUtil.getBean(IManagerInfoBiz.class);

        ManagerInfoEntity managerInfo = managerInfoBiz.getOne(new LambdaQueryWrapper<ManagerInfoEntity>().eq(ManagerInfoEntity::getManagerId, manager.getId()));
        //是否绑定了手机
        if (managerInfo == null){
            //todo 转发到绑定手机页面
            request.getRequestDispatcher(MSProperties.manager.path +"/gov/code/index.do").forward(request, response);
            return false;
        }else {
            //转发到短信验证页面
            request.getRequestDispatcher(MSProperties.manager.path +"/gov/code/index.do").forward(request, response);
            return false;
        }
    }

}
