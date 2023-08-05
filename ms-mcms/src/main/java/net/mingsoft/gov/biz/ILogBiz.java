/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.gov.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.basic.entity.LogEntity;

import java.util.List;

public interface ILogBiz  extends IBaseBiz<LogEntity> {

    /**
     * 1、每个月一个文件夹
     * 2、每天一个日志文件（读取当前日的日志文件，追加记录，第二天例如2021092505分读取20210924日志）文件格式list转json
     * 3、通过定时调度完成
     */
    void exportFile();

    /**
     * 获取最新一段时间内,未登录过系统的管理员名称数组
     * @param managerNameList 管理员姓名数组
     * @param day 天数
     * @return 管理员姓名数组
     */
    List<String> unLoginManagers(List<String> managerNameList, int day);
}
