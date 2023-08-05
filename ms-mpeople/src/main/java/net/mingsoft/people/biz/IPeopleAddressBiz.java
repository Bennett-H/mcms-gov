/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.biz;

import java.util.List;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.people.constant.e.PeopleAddressEnum;
import net.mingsoft.people.entity.PeopleAddressEntity;

/**
 * 
 * 用户收货地址业务层
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */

public interface IPeopleAddressBiz extends IBaseBiz{
	/**
	 * 设置默认地址，
	 * 1、先把之前的默认地址重置
	 * 2、再把新的收货地址设置为默认
	 * @param peopleAddress 
	 */
	void setDefault(PeopleAddressEntity peopleAddress);
}
