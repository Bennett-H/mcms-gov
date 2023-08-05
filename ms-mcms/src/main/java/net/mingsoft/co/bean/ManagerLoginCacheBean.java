/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.basic.entity.ManagerEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: xierz
 * @Description:管理员缓存实体
 * @Date: Create in 2021/03/11 9:35
 */
public class ManagerLoginCacheBean extends ManagerEntity {

    public static final String LOCK="lock",UNLOCK="unlock";

    /**
     * 登录失败次数
     */
    private AtomicInteger loginFailCount;
    /**
     *  最后登录时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;
    /**
     * 登录时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    /**
     * 是否在线
     */
    private Boolean isOnline = Boolean.FALSE;
    /**
     * 登录ip
     */
    private String loginIpAddress;
    /**
     * 会话编号，对应sessionId
     */
    private String token;
    /**
     * 登录地点
     */
    private String loginAddress;
    /**
     * 解锁或锁住状态 0:锁住 1:解锁
     */
    private String onOfLockStatus = UNLOCK;


    public AtomicInteger getLoginFailCount() {
        return loginFailCount;
    }

    public void setLoginFailCount(AtomicInteger loginFailCount) {
        this.loginFailCount = loginFailCount;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public String getLoginIpAddress() {
        return loginIpAddress;
    }

    public void setLoginIpAddress(String loginIpAddress) {
        this.loginIpAddress = loginIpAddress;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLoginAddress() {
        return loginAddress;
    }

    public void setLoginAddress(String loginAddress) {
        this.loginAddress = loginAddress;
    }

    public String getOnOfLockStatus() {
        return onOfLockStatus;
    }

    public void setOnOfLockStatus(String onOfLockStatus) {
        this.onOfLockStatus = onOfLockStatus;
    }


}
