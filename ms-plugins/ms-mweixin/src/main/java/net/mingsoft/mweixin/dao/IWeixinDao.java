/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





/**
 * 
 */
package net.mingsoft.mweixin.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mweixin.entity.WeixinEntity;
import org.apache.ibatis.annotations.Param;

public interface IWeixinDao extends IBaseDao<WeixinEntity> {
	
	/**
	 * 根据微信ID集合批量删除微信
	 * @param ids 微信ID集合
	 */
	public void deleteByIds(@Param("ids")int[] ids);

	/**
	 * 根据微信公众号的微信号获取
	 * @param weixinNo
	 * @return
	 */
	@InterceptorIgnore(tenantLine = "true")
	WeixinEntity getByWeixinNo(@Param("weixinNo")String weixinNo);

}
