/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.dao;

import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.spider.entity.LogEntity;
import org.springframework.stereotype.Component;

/**
 * 日志表持久层
 * @author 铭软科技
 * 创建日期：2020-9-12 10:51:17<br/>
 * 历史修订：<br/>
 */
@Component("spiderLogDao")
public interface ILogDao extends IBaseDao<LogEntity> {
}
