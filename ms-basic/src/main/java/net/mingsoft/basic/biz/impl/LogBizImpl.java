/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.biz.impl;

import net.mingsoft.basic.util.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.basic.entity.LogEntity;
import net.mingsoft.basic.biz.ILogBiz;
import net.mingsoft.basic.dao.ILogDao;

/**
 * 系统日志管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2020-11-21 9:41:34<br/>
 * 历史修订：<br/>
 */
 @Service("basiclogBizImpl")
public class LogBizImpl extends BaseBizImpl<ILogDao, LogEntity> implements ILogBiz {


	@Autowired
	private ILogDao logDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return logDao;
	}

	@Override
	@Async
	public void saveData(LogEntity logEntity) throws InterruptedException {
		String address = IpUtils.getRealAddressByIp(logEntity.getLogIp());
		logEntity.setLogLocation(address);
		this.save(logEntity);
	}
}
