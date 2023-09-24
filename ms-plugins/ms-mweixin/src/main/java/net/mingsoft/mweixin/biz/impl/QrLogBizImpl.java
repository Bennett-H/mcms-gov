/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.mweixin.entity.QrLogEntity;
import net.mingsoft.mweixin.biz.IQrLogBiz;
import net.mingsoft.mweixin.dao.IQrLogDao;

/**
 * 场景二维码日志管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2023-6-5 14:21:08<br/>
 * 历史修订：<br/>
 */
 @Service("mweixinqrLogBizImpl")
public class QrLogBizImpl extends BaseBizImpl<IQrLogDao,QrLogEntity> implements IQrLogBiz {


	@Autowired
	private IQrLogDao qrLogDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return qrLogDao;
	}



}
