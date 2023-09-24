/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.action;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import javax.servlet.http.HttpServletRequest;

import net.mingsoft.mweixin.constant.Const;
import org.apache.commons.lang3.StringUtils;

import net.mingsoft.base.constant.e.BaseEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.mweixin.biz.IWeixinBiz;
import net.mingsoft.mweixin.biz.IWeixinPeopleBiz;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.entity.WeixinPeopleEntity;
import net.mingsoft.people.constant.e.SessionConstEnum;
import net.mingsoft.mweixin.service.PortalService;

/**
 * mweixin基础控制层
 * 
 * @author 铭飞
 * @version 版本号：100<br/>
 *          创建日期：2017-12-22 9:43:04<br/>
 *          历史修订：<br/>
 */
public class BaseAction extends net.mingsoft.basic.action.BaseAction {

	@Override
	protected String getResString(String key) {
		// TODO Auto-generated method stub
		String str = "";
		try {
			str = super.getResString(key);
		} catch (MissingResourceException e) {
			str = getLocaleString(key, Const.RESOURCES);
		}
		return str;
	}

	/**
	 * 设置微信session
	 * 
	 * @param request
	 *            HttpServletRequest 对象
	 * @param weixinSession
	 *            键SessionConst里面定义(session名称)
	 * @param obj
	 *            对象
	 */
	protected void setWeixinSession(HttpServletRequest request, net.mingsoft.mweixin.constant.SessionConst weixinSession,
			Object obj) {
		if (StringUtils.isBlank(obj.toString())) {
			return;
		}
		request.getSession().setAttribute(weixinSession.toString(), obj);
	}

	/**
	 * 读取微信session
	 * 
	 * @param request
	 * @param 微信实体信息
	 */
	protected WeixinEntity getWeixinSession(HttpServletRequest request) {
		return (WeixinEntity) request.getSession()
				.getAttribute(net.mingsoft.mweixin.constant.SessionConst.WEIXIN_SESSION.toString());
	}

	
	/**
	 * 构建微信的服务工具，
	 * 
	 * @param weixinNo
	 *            微信号
	 * @return WeixinService
	 */
	protected PortalService builderWeixinService(String weixinNo) {
		IWeixinBiz weixinBiz = SpringUtil.getBean(IWeixinBiz.class);
		WeixinEntity weixin = weixinBiz.getByWeixinNo(weixinNo);
		PortalService weixinService = SpringUtil.getBean(PortalService.class);
		return weixinService.build(weixin);
	}

	/**
	 * 设置用户信息
	 *
	 * @param weixinPeople
	 */
	protected void setWeixinPeopleSession(WeixinPeopleEntity weixinPeople) {
		BasicUtil.setSession(SessionConstEnum.PEOPLE_SESSION,weixinPeople);
	}

	/**
	 * 根据用户openI设置用户session
	 * 
	 * @param openId
	 */
	protected void setWeixinPeopleSession(String openId) {
		// 1根据openid去查询用户是否存在
		// 2存在就设置session
		IWeixinPeopleBiz weixinPeopleBiz = SpringUtil.getBean(IWeixinPeopleBiz.class);
		WeixinPeopleEntity weixinPeople = weixinPeopleBiz.getEntityByOpenIdAndAppIdAndWeixinId(openId,0);
		if(weixinPeople != null){
			LOG.debug("设置用户session:"+weixinPeople.getPuNickname()+"-"+weixinPeople.getOpenId());
			setWeixinPeopleSession(weixinPeople);
		}
	}
	
	/**
	 * 枚举转list
	 * 
	 * @param <T>
	 * @param cls
	 *            实现了BaseEnum的子类
	 * @return 转换失败返回null
	 */
	protected <T> List<Map<String, Object>> weixinEnumToList(Class<T> cls) {
		List<Map<String, Object>> list = null;
		if (cls != null) {
			list = new ArrayList<Map<String, Object>>();
			try {
				Method method = cls.getDeclaredMethod("values");
				BaseEnum[] be = (BaseEnum[]) method.invoke(cls);
				for (BaseEnum e : be) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", e.toInt());
					map.put("value", e.toString());
					list.add(map);
				}
			} catch (Exception e) {
				return null;
			}

		}
		return list;
	}
	
	

}
