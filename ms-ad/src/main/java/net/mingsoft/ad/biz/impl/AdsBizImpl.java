/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.ad.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.ad.entity.AdsEntity;
import net.mingsoft.ad.biz.IAdsBiz;
import net.mingsoft.ad.dao.IAdsDao;

/**
 * 广告管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2019-11-23 8:49:39<br/>
 * 历史修订：<br/>
 */
 @Service("adadsBizImpl")
public class AdsBizImpl extends BaseBizImpl<IAdsDao,AdsEntity> implements IAdsBiz {

	
	@Autowired
	private IAdsDao adsDao;
	
	
	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return adsDao;
	} 
}
