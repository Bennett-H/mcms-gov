/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.basic.dao;

import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.entity.LogEntity;
import org.springframework.stereotype.Component;

/**
 * 系统日志持久层
 * @author 铭飞开发团队
 * 创建日期：2020-11-21 9:41:34<br/>
 * 历史修订：<br/>
 */
@Component("basicLogDao")
public interface ILogDao extends IBaseDao<LogEntity> {
}
