/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.spider.bean.LogBean;
import net.mingsoft.spider.entity.LogEntity;

import java.util.List;


/**
 * 日志表业务
 * @author 铭软科技
 * 创建日期：2020-9-10 14:12:40<br/>
 * 历史修订：<br/>
 */
public interface ILogBiz extends IBaseBiz<LogEntity> {

    /**
     * 根据日志信息批量导入
     * @param logs 日志内容
     */
    void batchImport(List<LogBean> logs);
}