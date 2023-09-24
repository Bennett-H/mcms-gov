/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.builder;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutVoiceMessage;
import net.mingsoft.mweixin.service.PortalService;

/**
 * 语音发送工具
 */
public class VoiceBuilder extends AbstractBuilder{
    @Override
    public WxMpXmlOutMessage build(String content, WxMpXmlMessage wxMessage, PortalService service) {
        WxMpXmlOutVoiceMessage m = WxMpXmlOutMessage.VOICE().mediaId(content)
                .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
                .build();

        return m;
    }
}
