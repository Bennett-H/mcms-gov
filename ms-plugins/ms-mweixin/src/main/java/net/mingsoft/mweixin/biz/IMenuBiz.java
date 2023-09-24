/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.biz;

import me.chanjar.weixin.common.error.WxErrorException;
import net.mingsoft.base.biz.IBaseBiz;
import java.util.*;

import net.mingsoft.mweixin.bean.MenuBean;
import net.mingsoft.mweixin.entity.MenuEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;

/**
 * 微信菜单业务
 * @author 铭飞
 * @version 
 * 版本号：100<br/>
 * 创建日期：2017-12-22 9:43:04<br/>
 * 历史修订：<br/>
 */
public interface IMenuBiz extends IBaseBiz {

	/**
	 * 获取微信菜单实体
	 * @param menu
	 * @return
	 */
	public MenuEntity getEntity(MenuEntity menu);

	/**
	 * 菜单的保存和更新,save代表没有在数据库的是否保存
	 * @param weixin
	 * @param menuBean
	 * @param isSave 数据库没有的数据是否保存进数据库，false代表只update
	 */
	void saveOrUpdate(WeixinEntity weixin,List<MenuBean> menuBean,boolean isSave);

	/**
	 * 发布菜单
	 * @param weixin
	 */
	void releaseMenu(WeixinEntity weixin) throws WxErrorException;

}
