/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.people.entity.PeopleOpenEntity;


/**
 * 
 * 开发平台用户接口 例如qq模块 
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
public interface IPeopleOpenBiz extends IBaseBiz{

	/**
	 * 根据平台openid读取用户编号
	 * @param openId 平台openid信息
	 * @return null:无数据
	 */
	PeopleOpenEntity getByOpenId(String openId); 
	
	/**
	 * 保存开源用户信息，
	 * @param peopleOpen
	 */
	void savePeopleOpen(PeopleOpenEntity peopleOpen);
}
