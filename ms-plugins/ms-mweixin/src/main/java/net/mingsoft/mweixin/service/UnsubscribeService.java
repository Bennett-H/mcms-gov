/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.service;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import net.mingsoft.mweixin.biz.IWeixinPeopleBiz;
import net.mingsoft.mweixin.entity.WeixinPeopleEntity;
import net.mingsoft.mweixin.handle.IUnSubscribeHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 取消关注服务类
 * @author Binary Wang
 *
 */
@Component
public class UnsubscribeService extends AbstractService {

    @Autowired
    private IWeixinPeopleBiz weixinPeopleBiz;
    @Autowired(required = false)
    private IUnSubscribeHandle unSubscribeHandle;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
            Map<String, Object> context, WxMpService wxMpService,
            WxSessionManager sessionManager) {
        String openId = wxMessage.getFromUser();
        this.logger.info("取消关注用户 OPENID: " + openId);
        if(unSubscribeHandle!=null) {
            unSubscribeHandle.handleSpecial(wxMessage,wxMpService);
        }
        // TODO 可以更新本地数据库为取消关注状态
        WeixinPeopleEntity weixinPeopleEntity = new WeixinPeopleEntity();
        weixinPeopleEntity.setOpenId(openId);
        weixinPeopleEntity.setSubscribe(false);
        weixinPeopleEntity.setPeopleId(0);
        weixinPeopleBiz.updateEntity(weixinPeopleEntity);
        return null;
    }

}
