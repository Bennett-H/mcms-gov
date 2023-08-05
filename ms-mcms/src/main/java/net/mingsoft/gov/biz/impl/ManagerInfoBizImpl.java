/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.gov.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.gov.entity.ManagerInfoEntity;
import net.mingsoft.gov.biz.IManagerInfoBiz;
import net.mingsoft.gov.dao.IManagerInfoDao;

/**
 * 管理员扩展信息管理持久化层
 * @author 铭飞科技
 * 创建日期：2022-5-25 16:03:06<br/>
 * 历史修订：<br/>
 */
 @Service("govManagerInfoBizImpl")
public class ManagerInfoBizImpl extends BaseBizImpl<IManagerInfoDao,ManagerInfoEntity> implements IManagerInfoBiz {


	@Autowired
	private IManagerInfoDao managerInfoDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return managerInfoDao;
	}



	// todo 测试使用
	public void testQuartz(){
		LOG.error("本地定时调度执行");
	}
}
