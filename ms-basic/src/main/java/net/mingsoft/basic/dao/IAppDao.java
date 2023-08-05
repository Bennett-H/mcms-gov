/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.dao;

import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.basic.entity.AppEntity;
import org.apache.ibatis.annotations.CacheNamespace;
import org.mybatis.caches.ehcache.EhcacheCache;

/**
 * 网站基本信息持久化层
 * @author 史爱华
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：<br/>
 */
public interface IAppDao extends IBaseDao<AppEntity>{

	/**
	 * 根据域名查找相同域名的个数
	 * @param websiteUrl 域名
	 * @return 返回相同域名的个数
	 */
	int countByUrl(String websiteUrl);

}
