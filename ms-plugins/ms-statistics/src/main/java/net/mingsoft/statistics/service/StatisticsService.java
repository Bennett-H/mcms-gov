/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.statistics.service;

import java.util.Date;

/**
 * 统计服务类
 */
public interface StatisticsService {
    /**
     * 获取指定url和时间段的浏览数
     * @return
     */
    Integer getViewNumber(Date beginTime, Date endTime, String url);

    /**
     * 获取指定时间段新增访问ip数量
     * @return
     */
    Integer getNewIpNumber(Date beginTime, Date endTime);



    /**
     * 获取昨日总浏览数
     * @return
     */
     Integer getYesterdayViewNumber();

    /**
     * 获取昨日指定url总浏览数
     * @return
     */
    Integer getYesterdayViewNumber(String url);

    /**
     * 获取昨日新增访问ip数量
     * @return
     */
    Integer getYesterdayNewIpNumber();

    /**
     * 获取总浏览数
     */
    Long getViewCount();

    /**
     * 获取总访问ip数量
     */
    Integer getIpCount();



}
