/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.biz;

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import net.mingsoft.people.biz.IPeopleUserBiz;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.entity.WeixinPeopleEntity;

import java.util.List;

import me.chanjar.weixin.mp.bean.result.WxMpUser;

/**
 * 微信用户业务层接口
 * @author 铭飞开发团队
 * @version
 * 版本号：100<br/>
 * 创建日期：2017-11-18 11:23:59<br/>
 * 历史修订：<br/>
 */
public interface IWeixinPeopleBiz  extends IPeopleUserBiz  {

	/**
	 * 查询用户信息
	 * @param openId 微信用户的唯一标识
	 * @param weixinId 关联的微信ID
	 * @return 微信用户实体
	 */
	WeixinPeopleEntity getEntityByOpenIdAndAppIdAndWeixinId(String openId,int weixinId);
	/**
	 * 根据user保存或更新用户
	 * @param user 微信工具获取的实体
	 * @param weixinId 当前微信ID。
	 */
	void saveOrUpdate(WxMpUser user, int weixinId);
	/**
	 * 根据user保存或更新用户
	 * @param user 微信工具获取的实体
	 * @param weixinId 当前微信ID。
	 */
	void saveOrUpdate(WxOAuth2UserInfo user, int weixinId);

	/**
	 * 微信授权保存微信用户基本信息，不包括people用户注册
	 * @param user
	 * @param weixinId
	 * @param peopleId
	 */
	void saveEntity(WxMpUser user,int weixinId,int peopleId);

	/**
	 * 根据user保存或更新用户
	 * @param user 微信工具获取的实体
	 * @param weixinId 当前微信ID。
	 */
	void saveEntity(WxOAuth2UserInfo user,int weixinId,int peopleId);
	/**
	 * 根据应用Id和微信id查找用户的总数
	 * @param appId 应用ID
	 * @param weixinId 微信ID
	 * @return 微信用户总数
	 */
	@Deprecated
	int queryCount(int appId,int weixinId);

	/**
	 * 根据自定义字段查找用户实体
	 * @param openId 用户在微信中的唯一标识
	 * @param weixinId 微信编号
	 * @return 用户实体
	 */
	WeixinPeopleEntity getEntityByOpenIdAndWeixinId(String openId,int weixinId);

	/**
	 * 使用递归抓取持久化用户信息
	 * @param weixin 微信实体
	 * @param openId 用户在微信的唯一标识
	 * @param userNum 一次抓取的数量
	 */
	Boolean recursionImportPeople(WeixinEntity weixin,String openId,int userNum);


	/**
	 * 检测微信用户的唯一性</br>
	 * 当该用户存在时返回该用户信息</br>
	 * 当该用户不存在时先持久化该用户信息然后再返回</br>
	 * @param weixinPeople 微信用户
	 * @return 微信用户实体
	 */
	WeixinPeopleEntity checkSoleWeixinPeople(WeixinPeopleEntity weixinPeople);

	/**
	 * 通过自增长ID获取用户实体
	 * @param peopleId 微信用户自增长ID
	 * @return 微信用户实体
	 */
	WeixinPeopleEntity getPeopleById(int peopleId);

	/**
	 * 根据微信openId查询用户实体
	 * @param openId
	 * @return
	 */
	WeixinPeopleEntity getByOpenId(String openId);

}
