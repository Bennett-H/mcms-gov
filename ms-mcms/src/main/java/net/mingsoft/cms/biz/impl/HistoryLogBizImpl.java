/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.cms.biz.impl;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.cms.biz.IHistoryLogBiz;
import net.mingsoft.cms.dao.ICmsHistoryLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 文章浏览记录管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2019-12-23 9:24:03<br/>
 * 历史修订：<br/>
 */
 @Service("cmshistoryLogBizImpl")
public class HistoryLogBizImpl extends BaseBizImpl implements IHistoryLogBiz {

	
	@Autowired
	private ICmsHistoryLogDao historyLogDao;
	
	
	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return historyLogDao;
	} 
}
