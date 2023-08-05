/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.basic.entity.AppEntity;

/**
 * 网站基本信息业务层接口
 * @author 史爱华
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：<br/>
 */
public interface IAppBiz extends IBaseBiz<AppEntity> {

	/**
	 * 查找域名相同站点的个数
	 * @param websiteUrl 域名
	 * @return 返回站点个数
	 */
	int countByUrl(String websiteUrl);

	/**
	 * 根据站点域名获取站点实体
	 * 建议使用 getOne(Wrapper<T> queryWrapper, boolean throwEx)方法
	 * @return 返回站点实体
	 */
	@Deprecated
	AppEntity getFirstApp();
}
