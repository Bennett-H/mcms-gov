/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.service;


import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mingsoft.mweixin.entity.WeixinEntity;

import me.chanjar.weixin.common.api.WxConsts.EventType;
import me.chanjar.weixin.common.api.WxConsts.MenuButtonType;
import me.chanjar.weixin.common.api.WxConsts.XmlMsgType;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.kefu.result.WxMpKfOnlineList;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.constant.WxMpEventConstants;

@Service("weixinService")
public class PortalService extends WxMpServiceImpl {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected LogService logHandler;

	@Autowired
	protected NullService nullHandler;

	@Autowired
	protected KfSessionService kfSessionHandler;

	@Autowired
	protected StoreCheckNotifyService storeCheckNotifyHandler;

	@Autowired
	private LocationService locationHandler;

	@Autowired
	private MenuService menuHandler;

	@Autowired
	private MsgService msgHandler;

	@Autowired
	private UnsubscribeService unsubscribeHandler;

	@Autowired
	private SubscribeService subscribeHandler;
	
	@Autowired
	private ScanService scanHandler;
	
	private WxMpMessageRouter router;
	private WeixinEntity weixin;
	

	public PortalService build(WeixinEntity weixin) {
		this.weixin = weixin;
		final WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
		config.setAppId(weixin.getWeixinAppId());// 设置微信公众号的appid
		config.setSecret(weixin.getWeixinAppSecret());// 设置微信公众号的app corpSecret
		config.setToken(weixin.getWeixinToken());// 设置微信公众号的token
		config.setAesKey(weixin.getWeixinAesKey());// 设置消息加解密密钥
		super.setWxMpConfigStorage(config);

		final WxMpMessageRouter newRouter = new WxMpMessageRouter(this);

		// 记录所有事件的日志
		newRouter.rule().handler(this.logHandler).next();

		// 接收客服会话管理事件
		newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
				.event(WxMpEventConstants.CustomerService.KF_CREATE_SESSION).handler(this.kfSessionHandler).end();
		newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
				.event(WxMpEventConstants.CustomerService.KF_CLOSE_SESSION).handler(this.kfSessionHandler).end();
		newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
				.event(WxMpEventConstants.CustomerService.KF_SWITCH_SESSION).handler(this.kfSessionHandler).end();

		// 门店审核事件
		newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(WxMpEventConstants.POI_CHECK_NOTIFY)
				.handler(this.storeCheckNotifyHandler).end();

		// 自定义菜单事件
		newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(MenuButtonType.CLICK).handler(this.menuHandler)
				.end();

		// 点击菜单连接事件
		newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(MenuButtonType.VIEW).handler(this.nullHandler)
				.end();

		// 关注事件
		newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(EventType.SUBSCRIBE)
				.handler(this.subscribeHandler).end();

		// 取消关注事件
		newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(EventType.UNSUBSCRIBE)
				.handler(this.unsubscribeHandler).end();

		// 上报地理位置事件
		newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(EventType.LOCATION).handler(this.locationHandler)
				.end();

		// 接收地理位置消息
		newRouter.rule().async(false).msgType(XmlMsgType.LOCATION).handler(this.locationHandler).end();

		// 扫码事件
		newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(EventType.SCAN).handler(this.scanHandler).end();

		// 默认
		//this.msgHandler = SpringUtil.getBean(MsgHandler.class);
		newRouter.rule().async(false).handler(this.msgHandler).end();

		this.router = newRouter;
		return this;
	}

	public WxMpXmlOutMessage route(WxMpXmlMessage message) {
		try {
			return this.router.route(message);
		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
		}

		return null;
	}

	public boolean hasKefuOnline() {
		try {
			WxMpKfOnlineList kfOnlineList = this.getKefuService().kfOnlineList();
			return kfOnlineList != null && kfOnlineList.getKfOnlineList().size() > 0;
		} catch (Exception e) {
			this.logger.error("获取客服在线状态异常: " + e.getMessage(), e);
		}

		return false;
	}

	public WeixinEntity getWeixin() {
		return weixin;
	}

	public void setWeixin(WeixinEntity weixin) {
		this.weixin = weixin;
	}
	
	
}
