/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.handle;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * @author by 铭飞开源团队
 * @Description 取消关注公众号回调
 * @date 2020/6/11 14:39
 */
public interface IUnSubscribeHandle {
    /**
     * 回调接口
     * @param wxMessage
     * @param weixinService
     * @return
     */
    WxMpXmlOutMessage handleSpecial(WxMpXmlMessage wxMessage, WxMpService weixinService);
}
