/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.biz;
import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.mweixin.entity.WeixinEntity;

/**
 * 微信业务层
 * @author Administrator
 *
 */
public interface IWeixinBiz extends IBaseBiz<WeixinEntity> {
	
	/**
	 * 根据微信ID批量删除
	 * @param ids weixinID集合
	 */
	public void deleteByIds(int[] ids);
	
	/**
	 * 根据token获取微信实体
	 */
	public WeixinEntity getByWeixinNo(String weixinNo);
	
}
