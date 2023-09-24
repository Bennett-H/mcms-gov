/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.service;

import cn.hutool.core.util.ObjectUtil;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import net.mingsoft.mweixin.biz.IDraftBiz;
import net.mingsoft.mweixin.biz.IFileBiz;
import net.mingsoft.mweixin.biz.IMessageBiz;
import net.mingsoft.mweixin.constant.e.MessageReplyType;
import net.mingsoft.mweixin.constant.e.NewsTypeEnum;
import net.mingsoft.mweixin.entity.*;
import net.mingsoft.mweixin.builder.ImageBuilder;
import net.mingsoft.mweixin.builder.TextBuilder;
import net.mingsoft.mweixin.builder.VideoBuilder;
import net.mingsoft.mweixin.builder.VoiceBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 消息服务类
 * @author Binary Wang
 */
@Component
public class MsgService extends AbstractService {
	
	/**
	 * 注入微信被动消息回复业务层
	 */	
	@Resource(name="netMessageBizImpl")
	private IMessageBiz messageBiz;

	/**
	 * 草稿业务层注入
	 */
	@Autowired
	private IDraftBiz draftBiz;


	/**
	 * 文件业务层注入
	 */
	@Autowired
	private IFileBiz fileBiz;
	
	@Override
	public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                  Map<String, Object> context, WxMpService wxMpService,
                                  WxSessionManager sessionManager) throws WxErrorException {

		PortalService weixinService = (PortalService) wxMpService;
		WeixinEntity weixin =  ((PortalService) wxMpService).getWeixin();
		if (!wxMessage.getMsgType().equals(WxConsts.XmlMsgType.EVENT)) {
			//TODO 可以选择将消息保存到本地
		}
		String msg = wxMessage.getContent();
		if (StringUtils.isBlank(msg)){
			return null;
		}
		//获取信息
		MessageEntity passiveMessage = new MessageEntity();
		passiveMessage.setPmWeixinId(weixinService.getWeixin().getIntId());
		passiveMessage.setPmKey(msg);
		MessageEntity messageEntity = messageBiz.getEntity(passiveMessage);
		if (ObjectUtil.isNull(messageEntity)){
			passiveMessage.setPmKey("");
			passiveMessage.setPmType(MessageReplyType.PASSIVITY.toString());
			messageEntity = messageBiz.getEntity(passiveMessage);
			if(messageEntity==null){
				return null;
			}
		}
		//通过获取的信息，查询关键字表
		//被动回复
		NewsTypeEnum newsTypeEnum = NewsTypeEnum.getValue(messageEntity.getPmNewType());
		Long id = null;
		if (newsTypeEnum != newsTypeEnum.TEXT){ //若消息类型不是文本 就取出 资源的id ;
			id = Long.valueOf(messageEntity.getPmContent());
		}
			switch (newsTypeEnum) {
				case TEXT:
					String content = messageEntity.getPmContent();
					// 容错处理，关注那边编辑器显示换行功能是用了这个下面一行标签，所以得把他替空
					content = content.replaceAll("<p><br></p>|</p>|</?br>" , "");
					content = content.replaceFirst("<p>" , "");
					content = content.replaceAll("<p>", "\n");
					return new TextBuilder().build(content, wxMessage, weixinService);
				//图片消息的配置
				case IMAGE:
					FileEntity fileImage = fileBiz.getById(id);
					return new ImageBuilder().build(fileImage.getFileMediaId(), wxMessage, weixinService);
				//语音消息的配置
				case VOICE:
					FileEntity fileVoice = fileBiz.getById(id);
						if (StringUtils.isBlank(fileVoice.getFileMediaId())) {
							fileBiz.weiXinFileUpload(weixin, fileVoice);
							fileVoice =  fileBiz.getById(id);
						}
						return new VoiceBuilder().build(fileVoice.getFileMediaId(), wxMessage, weixinService);
				//视频消息的配置
				case VIDEO:
					FileEntity fileVideo = fileBiz.getById(id);
						if (StringUtils.isBlank(fileVideo.getFileMediaId())) {
							fileBiz.weiXinFileUpload(weixin, fileVideo);
							fileVideo = fileBiz.getById(id);
						}
						return new VideoBuilder().build(fileVideo.getFileMediaId(), wxMessage, weixinService);
				case IMAGE_TEXT:
					//获取关键字对应的草稿
					DraftEntity draftEntity = draftBiz.getById(id);
					if (StringUtils.isBlank(draftEntity.getArticleId())) {
						return null;
					}
					WxMpKefuMessage message = WxMpKefuMessage.MPNEWSARTICLE()
							.toUser(wxMessage.getFromUser())
							.build();
					message.setMpNewsArticleId(draftEntity.getArticleId());
					wxMpService.getKefuService().sendKefuMessage(message);
					return null;
			}
		return null;
	}
}
