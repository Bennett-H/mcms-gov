/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.id.biz.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import net.mingsoft.id.biz.IRuleDataBiz;
import net.mingsoft.id.entity.RuleDataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;

import java.io.Serializable;
import java.util.*;
import net.mingsoft.id.entity.RuleEntity;
import net.mingsoft.id.biz.IRuleBiz;
import net.mingsoft.id.dao.IRuleDao;
import org.springframework.transaction.annotation.Transactional;
/**
 * 规则管理持久化层
 * @author  会 
 * 创建日期：2020-5-26 16:14:39<br/>
 * 历史修订：<br/>
 */
 @Service("idruleBizImpl")
@Transactional
public class RuleBizImpl extends BaseBizImpl<IRuleDao,RuleEntity> implements IRuleBiz {


	@Autowired
	private IRuleDao ruleDao;
	@Autowired
	private IRuleDataBiz ruleDataBiz;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return ruleDao;
	}



}
