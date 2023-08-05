/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.datascope.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.datascope.entity.DataConfigEntity;
import net.mingsoft.datascope.biz.IDataConfigBiz;
import net.mingsoft.datascope.dao.IDataConfigDao;

/**
 * 数据权限管理持久化层
 * @author  会 
 * 创建日期：2022-7-1 16:23:56<br/>
 * 历史修订：<br/>
 */
 @Service("datascopedataConfigBizImpl")
public class DataConfigBizImpl extends BaseBizImpl<IDataConfigDao,DataConfigEntity> implements IDataConfigBiz {


	@Autowired
	private IDataConfigDao dataConfigDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return dataConfigDao;
	}

}
