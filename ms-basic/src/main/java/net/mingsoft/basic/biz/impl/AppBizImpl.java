/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.biz.IAppBiz;
import net.mingsoft.basic.dao.IAppDao;
import net.mingsoft.basic.entity.AppEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 网站基本信息业务层实现类
 * @author 史爱华
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：<br/>
 */
@Service("appBiz")
@Transactional
public class AppBizImpl extends BaseBizImpl<IAppDao,AppEntity> implements IAppBiz{

	/**
	 * 声明IAppDao持久化层
	 */
	@Autowired
	private IAppDao appDao;

	/**
	 * 获取应用持久化层
	 * @return appDao 返回应用持久化层
	 */
	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return appDao;
	}

	@Override
	public int countByUrl(String websiteUrl) {
		// TODO Auto-generated method stub
		return appDao.countByUrl(websiteUrl);
	}

	@Override
	@Deprecated
	public AppEntity getFirstApp() {
		// TODO Auto-generated method stub
		return appDao.selectList(new QueryWrapper<AppEntity>()).get(0);
	}

}
