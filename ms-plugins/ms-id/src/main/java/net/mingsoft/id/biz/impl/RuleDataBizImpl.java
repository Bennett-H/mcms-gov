/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.id.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.id.entity.RuleDataEntity;
import net.mingsoft.id.biz.IRuleDataBiz;
import net.mingsoft.id.dao.IRuleDataDao;
import org.springframework.transaction.annotation.Transactional;
/**
 * 规则数据管理持久化层
 * @author  会 
 * 创建日期：2020-5-26 16:14:39<br/>
 * 历史修订：<br/>
 */
 @Service("idruleDataBizImpl")
@Transactional
public class RuleDataBizImpl extends BaseBizImpl<IRuleDataDao,RuleDataEntity> implements IRuleDataBiz {


	@Autowired
	private IRuleDataDao ruleDataDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return ruleDataDao;
	}
}
