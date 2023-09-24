/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.mweixin.entity;

import net.mingsoft.people.entity.PeopleUserEntity;

import java.util.Date;

/**
 * mswx-铭飞微信酒店预订平台
 * Copyright: Copyright (c) 2014 - 2015
 * Company:景德镇铭飞科技有限公司
 * @author 石超
 * @version 300-001-001
 * 版权所有 铭飞科技
 * Comments: 关联微信用户和微信号
 * Create Date:2013-12-23
 * Modification history:
 */
public class WeixinPeopleEntity extends PeopleUserEntity {

//	/**
//	 * 用户处于关注状态
//	 */
//	public final static int WEIXIN_PEOPLE_WATCH = 1;
//
//	/**
//	 * 该用户已取消关注
//	 */
//	public final static int WEIXIN_PEOPLE_CANCEL_WATCH = 2;
//
//	/**
//	 * 该用户通过授权访问
//	 */
//	public final static int WEIXIN_PEOPLE_OAUTH_WATCH = 3;
//
//	/**
//	 * 通过微信开放平台登录
//	 */
//	public final static int WEIXIN_PEOPLE_OPEN = 4;


//	/**
//	 * 用户关注状态
//	 * 1.关注中用户(默认)
//	 * 2.取消关注用户
//	 */
//	private int weixinPeopleState = 1;


//	/**
//	 * 用户所在省份
//	 */
//	private String weixinPeopleProvince;
//
//	/**
//	 * 用户所在城市
//	 */
//	private String weixinPeopleCity;



	/**
	 * 用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
	 */
	public Boolean subscribe;

	/**
	 * 用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
	 */
	public Date subscribeTime;

	/**
	 * 返回用户关注的渠道来源
	 */
	public String subscribeScene;

	/**
	 * 粉丝的备注
	 */
	private String remark;

	/**
	 * 用户所在的分组ID
	 */
	private Integer groupId;

	/**
	 * 关联微信Id
	 */
	private int weixinId;

	/**
	 * 用户在微信中的唯一标识
	 */
	private String openId;

	/**
	 * 用户头像链接地址
	 */
	private String headimgUrl;

	public Boolean getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(Boolean subscribe) {
		this.subscribe = subscribe;
	}

	public Date getSubscribeTime() {
		return subscribeTime;
	}

	public void setSubscribeTime(Date subscribeTime) {
		this.subscribeTime = subscribeTime;
	}

	public String getSubscribeScene() {
		return subscribeScene;
	}

	public void setSubscribeScene(String subscribeScene) {
		this.subscribeScene = subscribeScene;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public int getWeixinId() {
		return weixinId;
	}

	public void setWeixinId(int weixinId) {
		this.weixinId = weixinId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getHeadimgUrl() {
		return headimgUrl;
	}

	public void setHeadimgUrl(String headimgUrl) {
		this.headimgUrl = headimgUrl;
	}
}
