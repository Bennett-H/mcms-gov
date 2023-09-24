/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.statistics.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.mingsoft.statistics.biz.IAccessStatisticsBiz;
import net.mingsoft.statistics.entity.AccessStatisticsEntity;
import net.mingsoft.statistics.service.StatisticsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    IAccessStatisticsBiz statisticsBiz;

    @Override
    public Integer getViewNumber(Date beginTime, Date endTime, String url) {
        return statisticsBiz.list(new LambdaQueryWrapper<AccessStatisticsEntity>()
                .eq(StringUtils.isNotBlank(url),AccessStatisticsEntity::getUrl,url)
                .ge(AccessStatisticsEntity::getCreateDate,beginTime)
                .le(AccessStatisticsEntity::getCreateDate,endTime)).size();
    }

    @Override
    public Integer getNewIpNumber(Date beginTime, Date endTime) {
        return statisticsBiz.list(new LambdaQueryWrapper<AccessStatisticsEntity>()
                .ge(AccessStatisticsEntity::getCreateDate,beginTime)
                .le(AccessStatisticsEntity::getCreateDate,endTime)
                .groupBy(AccessStatisticsEntity::getIp)).size();
    }

    @Override
    public Integer getYesterdayViewNumber() {

        return getYesterdayViewNumber(null);
    }

    @Override
    public Integer getYesterdayViewNumber(String url) {
        DateTime date = DateUtil.date();
        //获取昨天对象
        Date yesterday = DateUtil.offsetDay(date, -1);
        //昨天的开始时间
        Date beginTime = DateUtil.beginOfDay(yesterday).toSqlDate();
        //昨天的结束时间
        Date endTime = DateUtil.endOfDay(yesterday).toSqlDate();
        return getViewNumber(beginTime,endTime,url);

    }

    @Override
    public Integer getYesterdayNewIpNumber() {
        DateTime date = DateUtil.date();
        //获取昨天对象
        Date yesterday = DateUtil.offsetDay(date, -1);
        //昨天的开始时间
        Date beginTime = DateUtil.beginOfDay(yesterday).toSqlDate();
        //昨天的结束时间
        Date endTime = DateUtil.endOfDay(yesterday).toSqlDate();
        return getNewIpNumber(beginTime,endTime);

    }

    @Override
    public Long getViewCount() {
        return statisticsBiz.count();
    }

    @Override
    public Integer getIpCount() {
        return statisticsBiz.list(new LambdaQueryWrapper<AccessStatisticsEntity>().groupBy(AccessStatisticsEntity::getIp)).size();
    }
}
