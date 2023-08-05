/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.biz.impl;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mdiy.biz.IConfigBiz;
import net.mingsoft.mdiy.dao.IConfigDao;
import net.mingsoft.mdiy.entity.ConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 自定义配置管理持久化层
 * @author SMILE
 * 创建日期：2021-3-25 11:42:09<br/>
 * 历史修订：<br/>
 */
@Service("mdiycoConfigBizImpl")
public class ConfigBizImpl extends BaseBizImpl<IConfigDao, ConfigEntity> implements IConfigBiz {


	@Autowired
	private IConfigDao configDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return configDao;
	}
}
