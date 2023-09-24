/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.service;

import java.util.Map;

import javax.annotation.Resource;

import net.mingsoft.mweixin.constant.e.MenuStyleEnum;
import org.springframework.stereotype.Component;

import me.chanjar.weixin.common.api.WxConsts.MenuButtonType;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import net.mingsoft.mweixin.biz.IMenuBiz;
import net.mingsoft.mweixin.entity.MenuEntity;
import net.mingsoft.mweixin.builder.AbstractBuilder;
import net.mingsoft.mweixin.builder.ImageBuilder;
import net.mingsoft.mweixin.builder.TextBuilder;

/**
 * 菜单服务类
 * @author Binary Wang
 *
 */
@Component
public class MenuService extends AbstractService {

	/**
	 * 注入微信菜单业务层
	 */
	@Resource(name = "netMenuBizImpl")
	private IMenuBiz menuBiz;

	@Override
	public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
			WxSessionManager sessionManager) {

		PortalService weixinService = (PortalService) wxMpService;
		String key = wxMessage.getEventKey();

		WxMpXmlOutMessage outMessage = null;

		String event = wxMessage.getEvent();
		if (event.equalsIgnoreCase(MenuButtonType.CLICK)) {
			MenuEntity menu = (MenuEntity) menuBiz.getEntity(Integer.parseInt(key));
			if (menu == null) {
				return null;
			}
			MenuStyleEnum menuStyleEnum = MenuStyleEnum.getValue(menu.getMenuStyle());
			switch (menuStyleEnum) {
			// 文本
			case TEXT:
				AbstractBuilder builder = new TextBuilder();
				outMessage = builder.build(menu.getMenuContent(), wxMessage, weixinService);
				break;
			// 图片
			case IMAGE:
				AbstractBuilder imageBuilder = new ImageBuilder();
				outMessage = imageBuilder.build(menu.getMenuContent(), wxMessage, weixinService);
				break;
			// 图文
			case IMAGE_TEXT:
				break;
			default:

			}
		}

		if (outMessage != null) {
			return outMessage;
		}

		return null;

	}

}
