/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.msend.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;

import java.util.*;
import net.mingsoft.msend.entity.LogEntity;
import net.mingsoft.msend.biz.ILogBiz;
import net.mingsoft.msend.dao.ILogDao;

/**
 * 发送日志管理持久化层
 * @author 伍晶晶
 * @version
 * 版本号：0.0<br/>
 * 创建日期：2017-8-24 14:41:18<br/>
 * 历史修订：<br/>
 */
 @Service("sendLogBizImpl")
public class LogBizImpl extends BaseBizImpl<ILogDao, LogEntity> implements ILogBiz {


	@Autowired
	private ILogDao logDao;


		@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return logDao;
	}
}
