/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.mweixin.biz.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.mweixin.biz.IWeixinPeopleBiz;
import net.mingsoft.mweixin.constant.e.UserSubscribeSceneEnum;
import net.mingsoft.mweixin.dao.IWeixinPeopleDao;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.entity.WeixinPeopleEntity;
import net.mingsoft.people.biz.impl.PeopleUserBizImpl;
import net.mingsoft.mweixin.service.PortalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信用户业务层
 * @author 铭飞开发团队
 * @version
 * 版本号：100<br/>
 * 创建日期：2017-11-18 11:23:59<br/>
 * 历史修订：<br/>
 */
@Service("netWeixinPeopleBiz")
public class WeixinPeopleBizImpl  extends PeopleUserBizImpl implements IWeixinPeopleBiz{
	/**
	 * 注入微信用户持久化层
	 */
	@Autowired
	private IWeixinPeopleDao weixinPeopleDao;

	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return this.weixinPeopleDao;
	}

	/**
	 * 查询用户信息
	 * @param weixinPeopleOpenId 微信中对用户的唯一标识
	 * @param weixinId 关联微信的ID
	 * @return 微信用户实体
	 */
	@Override
	public WeixinPeopleEntity getEntityByOpenIdAndAppIdAndWeixinId(String weixinPeopleOpenId,int weixinId){
		return weixinPeopleDao.getWeixinPeopleEntity(weixinId, weixinPeopleOpenId);
	}

	@Override
	public void saveOrUpdate(WxOAuth2UserInfo user, int weixinId) {
		//保存用户
		WeixinPeopleEntity weixinPeople = this.organizationWeixinPeople(user,weixinId);
		if(StringUtils.isNotBlank(user.getHeadImgUrl())){
			weixinPeople.setPuIcon(user.getHeadImgUrl());
		}
		weixinPeople.setOpenId(user.getOpenid());//微信用户OpenId，用户在微信的唯一识别字段
		weixinPeople.setPuNickname(user.getNickname());//用户昵称,用openid
		weixinPeople.setPeopleDateTime(new Date());	//用户注册时间
		weixinPeople.setPeopleDateTime(new Date());	//用户注册时间
		//查询数据库中是否已经存在该用户数据
		WeixinPeopleEntity _weixin = weixinPeopleDao.getWeixinPeopleEntity(weixinId, user.getOpenid());
		//当不存在该用户信息时则执行新增
		if(_weixin == null){
			this.savePeopleUser(weixinPeople);
		}else{
			//若存在，则执行更新
			weixinPeople.setId(_weixin.getId());
			this.updatePeopleUser(weixinPeople);
		}
	}

	@Override
	public void saveOrUpdate(WxMpUser user, int weixinId) {
		//保存用户
		WeixinPeopleEntity weixinPeople = this.organizationWeixinPeople(user,weixinId);
		if(StringUtils.isNotBlank(user.getHeadImgUrl())){
			weixinPeople.setPuIcon(user.getHeadImgUrl());
		}
		weixinPeople.setOpenId(user.getOpenId());//微信用户OpenId，用户在微信的唯一识别字段
		weixinPeople.setPuNickname(user.getOpenId());//用户昵称,用openid
		weixinPeople.setSubscribeScene(UserSubscribeSceneEnum.getNameByCode(user.getSubscribeScene()));
		weixinPeople.setSubscribe(user.getSubscribe());
		weixinPeople.setSubscribeTime(new Date(user.getSubscribeTime() * 1000));
		weixinPeople.setPeopleDateTime(new Date());	//用户注册时间
		//查询数据库中是否已经存在该用户数据
		WeixinPeopleEntity _weixin = weixinPeopleDao.getWeixinPeopleEntity(weixinId, user.getOpenId());
		//当不存在该用户信息时则执行新增
		if(_weixin == null){
			this.savePeopleUser(weixinPeople);
		}else{
			//若存在，则执行更新
			weixinPeople.setId(_weixin.getId());
			this.updatePeopleUser(weixinPeople);
		}
	}


	/**
	 * 根据应用Id微信Id查找微信用户分页列表
	 * @param appId 应用ID
	 * @param weixinId :微信ID
	 * @param page :页面
	 * @param orderBy :排序依据字段
	 * @param order :排序方式   false:从大到小  true:从小到大
	 * @return 微信用户列表
	 */
//	@Override
//	public List<WeixinPeopleEntity> queryList(int appId,int weixinId,PageUtil page,String orderBy, boolean order) {
//		// TODO Auto-generated method stub
//		return weixinPeopleDao.queryList(appId,weixinId, page.getPageNo(), page.getPageSize(), orderBy, order);
//	}

	/**
	 * 根据应用ID和微信id查找用户的总数
	 * @param appId 应用ID
	 * @param weixinId 微信ID
	 * @return 微信用户总数
	 */
	@Override
	public int queryCount(int appId,int weixinId) {
		// TODO Auto-generated method stub
		return weixinPeopleDao.queryCount(weixinId);
	}

	/**
	 * 根据自定义字段查找用户实体
	 * @param weixinPeopleOpenId 微信中的唯一标识
	 * @param weixinPeopleWeixinId 微信用户对应的微信ID
	 * @return 用户实体
	 */
	@Override
	public WeixinPeopleEntity getEntityByOpenIdAndWeixinId(String weixinPeopleOpenId,int weixinPeopleWeixinId) {
		// TODO Auto-generated method stub
		Map<String,Object> whereMap = new HashMap<String,Object> ();
		whereMap.put("OPEN_ID", weixinPeopleOpenId);
		whereMap.put("WEIXIN_ID", weixinPeopleWeixinId);
		return weixinPeopleDao.getEntity(whereMap);
	}


	@Override
	public void saveEntity (WxMpUser user,int weixinId,int peopleId) {
		// TODO Auto-generated method stub
		WeixinPeopleEntity  weixinPeople = this.organizationWeixinPeople(user,weixinId);
		weixinPeople.setIntId(peopleId);
		weixinPeopleDao.saveEntity(weixinPeople);
	}

	@Override
	public void saveEntity (WxOAuth2UserInfo user,int weixinId,int peopleId) {
		// TODO Auto-generated method stub
		WeixinPeopleEntity  weixinPeople = this.organizationWeixinPeople(user,weixinId);
		weixinPeople.setIntId(peopleId);
		weixinPeopleDao.saveEntity(weixinPeople);
	}



	/**
	 * 根据用户编号获取用户信息
	 * @param peopleId 用户编号
	 * @return 微信用户实体
	 */
	@Override
	public WeixinPeopleEntity getPeopleById(int peopleId) {
		// TODO Auto-generated method stub
		Map<String,Object> whereMap = new HashMap<String,Object>();
		//查询条件
		whereMap.put("PEOPLE_ID",peopleId);
		Object obj = weixinPeopleDao.getEntity(whereMap);
		if (obj!=null) {
			return (WeixinPeopleEntity)weixinPeopleDao.getEntity(whereMap);
		}
		return null;
	}

	/**
	 * 检测微信用户的唯一性</br>
	 * 当该用户存在时返回该用户信息</br>
	 * 当该用户不存在时先持久化该用户信息然后再返回</br>
	 * @param weixinPeople 微信用户
	 * @return 微信用户实体
	 */
	@Override
	public WeixinPeopleEntity checkSoleWeixinPeople(WeixinPeopleEntity weixinPeople){
		//根据用户openId和weixinId查询该用户是否存在
		WeixinPeopleEntity weixinPeopleEntity = this.getEntityByOpenIdAndWeixinId(weixinPeople.getOpenId(),weixinPeople.getWeixinId());
		//当查询到用户不存在时则执行新增
		if(weixinPeopleEntity == null){
			//新增用户
			this.savePeopleUser(weixinPeople);
			//获取新增用户的用户ＩＤ
			weixinPeople.setPeopleId(weixinPeople.getIntId());//保存people_user表的用户ID
			return weixinPeople;
		}else{
			return weixinPeopleEntity;
		}
	}


	/**
	 * 使用递归抓取持久化用户信息
	 * @param weixin 微信实体
	 * @param openId 开始抓取的用户信息的位置
	 * @param userNum 一次抓取的数量
	 */
	@Override
	public Boolean recursionImportPeople(WeixinEntity weixin,String openId,int userNum){
		// 获取微信实体
		PortalService weixinService = SpringUtil.getBean(PortalService.class);
		PortalService wxService = weixinService.build(weixin);
		//若微信不存在
		if(weixin == null || weixin.getIntId()<=0 || StringUtils.isBlank(weixin.getWeixinAppSecret()) || StringUtils.isBlank(weixin.getWeixinAppId())){
			return false;
		}
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		try {
			listMap = (List<Map<String, Object>>) wxService.getUserService().userList(openId);
		} catch (WxErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(listMap == null || listMap.size() == 0){
			return false;
		}
		//储蓄转化后的用户信息
		List<WeixinPeopleEntity> list = new ArrayList<WeixinPeopleEntity>();
		for(int i=0;i<listMap.size();i++){
			Map<String,Object> userInfoMap = listMap.get(i);
			LOG.debug(userInfoMap.get("openid").toString()+"||"+userInfoMap.get("nickname").toString()+"||"+userInfoMap.get("sex").toString()+"||"+userInfoMap.get("city").toString()+"||"+userInfoMap.get("province").toString()+"||"+userInfoMap.get("headimgurl").toString());
			WeixinPeopleEntity weixinPeople = new WeixinPeopleEntity();
			weixinPeople.setWeixinId(weixin.getIntId());//微信用户微信ID
			weixinPeople.setOpenId(userInfoMap.get("openid").toString());//微信用户OpenId，用户在微信的唯一识别字段
			weixinPeople.setHeadimgUrl(userInfoMap.get("headimgurl").toString());//微信用户头像
			weixinPeople.setPuNickname(userInfoMap.get("nickname").toString());//用户昵称
			weixinPeople.setSubscribe(BooleanUtil.toBoolean(userInfoMap.get("subscribe").toString()));
			weixinPeople.setPeopleDateTime(new Date());	//用户注册时间
			list.add(weixinPeople);
		}
		//持久化用户信息
		this.saveOrUpdateBatchWeixinPeople(list);
		String _openId = listMap.get((listMap.size()-1)).get("openid").toString();//最后一个用户的openId
		recursionImportPeople(weixin,_openId,userNum);
		return true;
	}


	/**
	 * 批量持久化用户信息，若数据库中已经存在该用户数据则执行更新操作，若不存在，则执行保存操作
	 * @param list 用户信息集合
	 */
	private void saveOrUpdateBatchWeixinPeople(List<WeixinPeopleEntity> list){
		// TODO Auto-generated method stub
		if(list != null && list.size()>0){
			for(int i=0;i<list.size();i++){
				WeixinPeopleEntity weixinPeople = list.get(i);
				//查询数据库中是否已经存在该用户数据
				WeixinPeopleEntity _weixin = this.getEntityByOpenIdAndAppIdAndWeixinId(weixinPeople.getOpenId(),weixinPeople.getWeixinId());
				//当不存在该用户信息时则执行新增持久化
				if(_weixin == null){
					this.savePeopleUser(weixinPeople);
				}else{
					//若存在，则执行更新
					weixinPeople.setId(_weixin.getId());
					this.updatePeopleUser(weixinPeople);
				}
			}
		}
	}

	/**
	 * 根据微信openId查询用户实体
	 */
	@Override
	public WeixinPeopleEntity getByOpenId(String openId) {
		// TODO Auto-generated method stub
		return weixinPeopleDao.getByOpenId(openId);
	}


	private WeixinPeopleEntity organizationWeixinPeople(WxOAuth2UserInfo user,int weixinId){
		WeixinPeopleEntity weixinPeople = new WeixinPeopleEntity();
		weixinPeople.setWeixinId(weixinId);//微信用户微信ID
		weixinPeople.setOpenId(user.getOpenid());//微信用户OpenId，用户在微信的唯一识别字段

		weixinPeople.setHeadimgUrl(user.getHeadImgUrl());//微信用户头像
		weixinPeople.setPuNickname(user.getNickname());
		return weixinPeople;
	}

	private WeixinPeopleEntity organizationWeixinPeople(WxMpUser user,int weixinId){
		WeixinPeopleEntity weixinPeople = new WeixinPeopleEntity();
		weixinPeople.setWeixinId(weixinId);//微信用户微信ID
		weixinPeople.setOpenId(user.getOpenId());//微信用户OpenId，用户在微信的唯一识别字段
		weixinPeople.setPuNickname(user.getOpenId());//用户昵称,用openid
		weixinPeople.setSubscribeScene(UserSubscribeSceneEnum.getNameByCode(user.getSubscribeScene()));
		weixinPeople.setSubscribe(user.getSubscribe());
		weixinPeople.setSubscribeTime(new Date(user.getSubscribeTime()));
		return weixinPeople;


	}
}
