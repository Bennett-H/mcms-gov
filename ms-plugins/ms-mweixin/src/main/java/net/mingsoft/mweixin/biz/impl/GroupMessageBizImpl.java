/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.mweixin.biz.impl;

import net.mingsoft.mweixin.dao.IMessageDao;
import net.mingsoft.mweixin.entity.GroupMessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mweixin.biz.IGroupMessageBiz;
import net.mingsoft.mweixin.dao.IGroupMessageDao;

/**
 * 群发消息管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2019-6-6 12:03:07<br/>
 * 历史修订：<br/>
 */
 @Service("wxGroupMessageBizImpl")
public class GroupMessageBizImpl extends MessageBizImpl implements IGroupMessageBiz {

	
	@Autowired
	private IGroupMessageDao wxGroupMessageDao;

	
		@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return wxGroupMessageDao;
	}


}
