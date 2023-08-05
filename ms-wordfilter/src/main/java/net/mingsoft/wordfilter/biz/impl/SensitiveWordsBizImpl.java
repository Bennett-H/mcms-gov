/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.wordfilter.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.wordfilter.entity.SensitiveWordsEntity;
import net.mingsoft.wordfilter.biz.ISensitiveWordsBiz;
import net.mingsoft.wordfilter.dao.ISensitiveWordsDao;

/**
 * 敏感词管理持久化层
 * @author 铭软科技
 * 创建日期：2021-1-7 15:54:50<br/>
 * 历史修订：<br/>
 */
 @Service("wordfiltersensitiveWordsBizImpl")
public class SensitiveWordsBizImpl extends BaseBizImpl<ISensitiveWordsDao,SensitiveWordsEntity> implements ISensitiveWordsBiz {

	
	@Autowired
	private ISensitiveWordsDao sensitiveWordsDao;
	
	
	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return sensitiveWordsDao;
	} 
}
