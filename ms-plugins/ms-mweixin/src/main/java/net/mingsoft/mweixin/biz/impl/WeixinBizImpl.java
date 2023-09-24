/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.biz.impl;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mweixin.biz.IWeixinBiz;
import net.mingsoft.mweixin.dao.IWeixinDao;
import net.mingsoft.mweixin.entity.WeixinEntity;

import net.mingsoft.basic.util.BasicUtil;

/**
 * mswx-铭飞微信酒店预订平台
 * Copyright: Copyright (c) 2014 - 2015
 * Company:景德镇铭飞科技有限公司
 * @author 石超   
 * @version 300-001-001
 * 版权所有 铭飞科技
 * Comments:微信公众帐号基础信息biz层实现类
 * Create Date:2013-12-23
 * Modification history:
 */
@Service("weixinBiz")
public class WeixinBizImpl extends BaseBizImpl<IWeixinDao, WeixinEntity> implements IWeixinBiz {

	/**
	 * 注入微信持久化层
	 */
	@Autowired
	private IWeixinDao weixinDao;
	
	@Override
	public IBaseDao getDao() {
		return this.weixinDao;
	}


	/**
	 * 根据微信ID集合批量删除微信
	 * @param ids 微信ID集合
	 */
	@Override
	public void deleteByIds(int[] ids) {
		weixinDao.deleteByIds(ids);
	}


	@Override
	public WeixinEntity getByWeixinNo(String weixinNo) {
		if (StringUtils.isEmpty(weixinNo)) {
			return null;
		}
		//通过微信token获取微信实体
		WeixinEntity weixinEntity = weixinDao.getByWeixinNo(weixinNo);
		return weixinEntity;
	}
}
