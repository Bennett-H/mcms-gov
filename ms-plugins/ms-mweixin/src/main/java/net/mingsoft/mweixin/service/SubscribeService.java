/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.service;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.mweixin.biz.IDraftBiz;
import net.mingsoft.mweixin.biz.IFileBiz;
import net.mingsoft.mweixin.biz.IMessageBiz;
import net.mingsoft.mweixin.biz.IWeixinPeopleBiz;
import net.mingsoft.mweixin.builder.ImageBuilder;
import net.mingsoft.mweixin.builder.TextBuilder;
import net.mingsoft.mweixin.builder.VideoBuilder;
import net.mingsoft.mweixin.builder.VoiceBuilder;
import net.mingsoft.mweixin.constant.e.MessageReplyType;
import net.mingsoft.mweixin.constant.e.NewsTypeEnum;
import net.mingsoft.mweixin.entity.DraftEntity;
import net.mingsoft.mweixin.entity.FileEntity;
import net.mingsoft.mweixin.entity.MessageEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.handle.IScanHandle;
import net.mingsoft.mweixin.handle.ISubscribeHandle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 关注服务类
 * @author Binary Wang
 *
 */
@Component
public class SubscribeService extends AbstractService {

	@Resource(name="netWeixinPeopleBiz")
	private IWeixinPeopleBiz weixinPeopleBiz;
	/**
	 * 注入微信被动消息回复业务层
	 */
	@Resource(name="netMessageBizImpl")
	private IMessageBiz messageBiz;

    /**
     * 文件业务层注入
     */
    @Autowired
    private IFileBiz fileBiz;

    /**
     * 草稿业务层注入
     */
    @Autowired
    private IDraftBiz draftBiz;


    @Autowired
    private ScanService scanService;

    @Override
	public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
      WxSessionManager sessionManager) throws WxErrorException {

        WeixinEntity weixin =  ((PortalService) wxMpService).getWeixin();
        this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser());

        PortalService weixinService = (PortalService) wxMpService;

        // 获取微信用户基本信息
        WxMpUser userWxInfo = weixinService.getUserService().userInfo(wxMessage.getFromUser(), null);

        if (userWxInfo != null) {
          // TODO 可以添加关注用户到本地
            this.logger.debug("保存用户信息");
            int weixinId = weixinService.getWeixin().getIntId();
            weixinPeopleBiz.saveOrUpdate(userWxInfo, weixinId);
        }

        WxMpXmlOutMessage responseResult = null;
        try {
            //处理特殊请求，比如如果是扫码进来的，可以做相应处理
            this.logger.info("处理特殊请求，比如如果是扫码进来的，可以做相应处理");
            // todo 未关注扫码也获取实体的qrBeanName走扫码事件统一处理
            IScanHandle scanHandle = scanService.getScanHandleBySceneStrAndWeixinId(wxMessage.getEventKey().replace("qrscene_", ""), weixin.getId());
            if (scanHandle!=null){
                responseResult = scanHandle.handleSpecial(wxMessage,weixinService);
            }
        } catch (Exception e) {
            logger.debug("扫码实现类或关注实现类处理异常");
            e.printStackTrace();
        }

        // todo 如果还想在关注的时候做一些事情，请写一个类实现ISubscribeHandle
        try {
            ISubscribeHandle subscribeHandle = SpringUtil.getBean(ISubscribeHandle.class);
            if (subscribeHandle!=null){
                subscribeHandle.handleSpecial(wxMessage,weixinService);
            }
        } catch (NoSuchBeanDefinitionException e) {
            // TODO: 2023/6/17 不输出异常堆栈信息
            logger.debug("当前没有任何ISubscribeHandle的实现类，此处不打印堆栈信息，否则会影响性能，具体报错为: {}",e.getMessage());
//            e.printStackTrace();
        }

        if (responseResult != null) {
            logger.debug("有业务处理了返回消息，此处直接返回业务组装的消息，不会给自动回复");
            return responseResult;
        }

        MessageEntity passiveMessage = new MessageEntity();
        passiveMessage.setPmWeixinId(weixinService.getWeixin().getIntId());
        //获取设置关注回复的内容
        passiveMessage.setPmType(MessageReplyType.ATTENTION.toString());
        passiveMessage = (MessageEntity) messageBiz.getEntity(passiveMessage);
        if(passiveMessage == null){
            try {
              return new TextBuilder().build("感谢关注", wxMessage, weixinService);
            } catch (Exception e) {
              this.logger.error(e.getMessage(), e);
            }
        }else{
            //被动回复
            NewsTypeEnum newsTypeEnum = NewsTypeEnum.getValue(passiveMessage.getPmNewType());
            Long id = null;
            if (newsTypeEnum != newsTypeEnum.TEXT){ //若消息类型不是文本 就取出 资源的id ;
                id = Long.valueOf(passiveMessage.getPmContent());
            }
            switch (newsTypeEnum) {
                case TEXT:
                    String content = passiveMessage.getPmContent();
                    // 容错处理，关注那边编辑器显示换行功能是用了这个下面一行标签，所以得把他替空
                    content = content.replaceAll("<p><br></p>" , "");
                    content = content.replaceFirst("<p>" , "");
                    content = content.replaceAll("<p>", "\n");
                    content = content.replaceAll("</p>", "");
                    content = content.replaceAll("</?br>", "");
                    return new TextBuilder().build(content, wxMessage, weixinService);
                case IMAGE:
                    FileEntity fileImage = fileBiz.getById(id);
                    return new ImageBuilder().build(fileImage.getFileMediaId(), wxMessage, weixinService);
                //图片消息的配置
                case VOICE:
                    FileEntity fileVoice = fileBiz.getById(id);
                    if (StringUtils.isBlank(fileVoice.getFileMediaId())) {
                        fileBiz.weiXinFileUpload(weixin, fileVoice);
                        fileVoice = fileBiz.getById(id);
                    }
                    return new VoiceBuilder().build(fileVoice.getFileMediaId(), wxMessage, weixinService);
                //语音消息的配置
                case VIDEO:
                    FileEntity fileVideo = fileBiz.getById(id);
                    if (StringUtils.isBlank(fileVideo.getFileMediaId())) {
                        fileBiz.weiXinFileUpload(weixin, fileVideo);
                        fileVideo = fileBiz.getById(id);
                    }
                    return new VideoBuilder().build(fileVideo.getFileMediaId(), wxMessage, weixinService);
                //视频消息的配置
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
        }
        return responseResult;
  }


}
