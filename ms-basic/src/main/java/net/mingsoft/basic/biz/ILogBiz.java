/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.basic.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.basic.entity.LogEntity;


/**
 * 系统日志业务
 * @author 铭飞开发团队
 * 创建日期：2020-11-21 9:41:34<br/>
 * 历史修订：<br/>
 */
public interface ILogBiz extends IBaseBiz<LogEntity> {
    /**
     * 异步保存数据
     * @param logEntity
     * @throws InterruptedException
     */
    void saveData(LogEntity logEntity) throws InterruptedException;
}
