/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.statistics.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;

import java.util.Date;

/**
 * 访问统计实体
 *
 * @author 铭飞科技
 * 创建日期：2022-4-2 14:07:51<br/>
 * 历史修订：<br/>
 */
@TableName("ACCESS_STATISTICS")
public class AccessStatisticsEntity extends BaseEntity {

    private static final long serialVersionUID = 1648879671010L;


    /**
     * 浏览时长
     */
    @TableField(condition = SqlCondition.LIKE)
    private Long accessTime;

    /**
     * 结束浏览时间
     */
    protected Date endViewTime;


    /**
     * 浏览器标识
     */
    @TableField(condition = SqlCondition.LIKE)
    private String userAgent;

    /**
     * 设备类型
     */
    @TableField(condition = SqlCondition.LIKE)
    private String deviceType;

    /**
     * 访问链接
     */
    @TableField(condition = SqlCondition.LIKE)
    private String url;

    /**
     * 用户ip
     */
    @TableField(condition = SqlCondition.LIKE)
    private String ip;

    /**
     * 用户cookie
     */
    @TableField(condition = SqlCondition.LIKE)
    private String cookie;

    /**
     * 用户地址
     */
    @TableField(condition = SqlCondition.LIKE)
    private String address;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 设置浏览时长
     */
    public void setAccessTime(Long accessTime) {
        this.accessTime = accessTime;
    }

    /**
     * 获取浏览时长
     */
    public Long getAccessTime() {
        return this.accessTime;
    }

    /**
     * 设置浏览器标识
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * 获取浏览器标识
     */
    public String getUserAgent() {
        return this.userAgent;
    }

    /**
     * 设置设备类型
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * 获取设备类型
     */
    public String getDeviceType() {
        return this.deviceType;
    }

    /**
     * 设置访问链接
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取访问链接
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * 设置用户ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取用户ip
     */
    public String getIp() {
        return this.ip;
    }

    public Date getEndViewTime() {
        return endViewTime;
    }

    public void setEndViewTime(Date endViewTime) {
        this.endViewTime = endViewTime;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}
