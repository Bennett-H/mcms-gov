/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.mweixin.action;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.WxMpMassTagMessage;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mweixin.biz.IDraftBiz;
import net.mingsoft.mweixin.biz.IFileBiz;
import net.mingsoft.mweixin.biz.IMessageBiz;
import net.mingsoft.mweixin.biz.IWeixinPeopleBiz;
import net.mingsoft.mweixin.constant.e.MenuStyleEnum;
import net.mingsoft.mweixin.constant.e.MessageReplyType;
import net.mingsoft.mweixin.constant.e.NewsTypeEnum;
import net.mingsoft.mweixin.entity.DraftEntity;
import net.mingsoft.mweixin.entity.FileEntity;
import net.mingsoft.mweixin.entity.MessageEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.service.PortalService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;


/**
 * 
 * @ClassName: MessageAction
 * @Description:TODO(消息发送控制层)
 * @author: 铭飞开发团队
 * @date: 2018年4月3日 上午9:03:12
 * 
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 */
@Api(tags = {"后端-微信模块接口"})
@Controller("netMessageAction")
@RequestMapping("/${ms.manager.path}/mweixin/message")
public class MessageAction extends BaseAction {

	/**
	 * 注入素材业务层
	 */
	@Autowired
	private IDraftBiz draftBiz;

	/**
	 * 注入素材业务层
	 */
	@Autowired
	private IFileBiz fileBiz;

	/**
	 * 注入微信用户业务层
	 */
	@Autowired
	private IWeixinPeopleBiz weixinPeopleBiz;

	@Autowired
	private PortalService weixinService;

	/**
	 * 注入微信被动消息回复业务层
	 */
	@Resource(name="netMessageBizImpl")
	private IMessageBiz messageBiz;

	/**
	 * 返回关注时主界面index
	 */
	@GetMapping("/index")
	@ApiIgnore
	@RequiresPermissions("followMessage:view")
	public String index(HttpServletResponse response, HttpServletRequest request) {
		return "/mweixin/message/index";
	}

	/**
	 * 返回被动回复主界面index
	 */
	@GetMapping("/message/index")
	@ApiIgnore
	@RequiresPermissions("passiveMessage:view")
	public String messageIndex(HttpServletResponse response, HttpServletRequest request) {
		return "/mweixin/message/index";
	}

	/**
	 * 返回关键词主界面keyword
	 */
	@GetMapping("/keyword")
	@ApiIgnore
	@RequiresPermissions("keywordMessage:view")
	public String keyword(HttpServletResponse response, HttpServletRequest request) {
		return "/mweixin/message/keyword";
	}

	/**
	 * 返回关键词编辑界面keyword-form
	 */
	@GetMapping("/keyword/form")
	@ApiIgnore
	@RequiresPermissions(value = {"keywordMessage:save", "keywordMessage:edit"}, logical = Logical.OR)
	public String keywordForm(HttpServletResponse response, HttpServletRequest request) {
		return "/mweixin/message/keyword-form";
	}
	




	/**
	 * 消息群发
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws WxErrorException
	 * @throws IOException
	 * @throws IORuntimeException
	 */
	@ApiOperation(value="消息群发接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pmWeixinId", value = "微信编号", required = false,paramType="query"),
			@ApiImplicitParam(name = "pmEventId", value = "该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框", required = false,paramType="query"),
			@ApiImplicitParam(name = "pmNewsId", value = "回复的素材ID", required = false,paramType="query"),
			@ApiImplicitParam(name = "pmMessageId", value = "对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个", required =false,paramType="query"),
			@ApiImplicitParam(name = "pmReplyNum", value = "被动回复的次数;为1时表示用户第一次被动响应消息,依次递增,当超出时取值为0的进行回复", required =false,paramType="query"),
			@ApiImplicitParam(name = "pmKey", value = "事件关键字", required =false,paramType="query"),
			@ApiImplicitParam(name = "pmType", value = "回复属性:keyword,关键字回复，attention,关注回复，passivity,被动回复,all,群发", required = false,paramType="query"),
			@ApiImplicitParam(name = "pmTag", value = "回复标签", required = false,paramType="query"),
			@ApiImplicitParam(name = "pmContent", value = "消息回复内容", required = false,paramType="query"),
			@ApiImplicitParam(name = "pmNewType", value = "素材类型：imageText单图文 text文本 image图片 moreImageText多图文 link链接 voice语音 video视频", required = false,paramType="query"),
			@ApiImplicitParam(name = "newsTitle", value = "素材对应的标题（不参与表结构）", required = false,paramType="query"),

	})
	@LogAnn(title = "消息群发接口",businessType= BusinessTypeEnum.OTHER)
	@PostMapping("/sendAll")
	@ResponseBody
	@RequiresPermissions("reply:save")
	public ResultData sendAll(MessageEntity messageEntity, ModelMap model, HttpServletRequest request, HttpServletResponse response) {
		// 获取微信对象
		WeixinEntity weixin = this.getWeixinSession(request);
		// 若微信不存在
		if (weixin == null || StringUtils.isBlank(weixin.getWeixinAppSecret())
				|| StringUtils.isBlank(weixin.getWeixinAppId())) {
			return ResultData.build().error(this.getResString("weixin.not.found"));
		}
		weixinService = weixinService.build(weixin);
		WxMpMassTagMessage massMessage = new WxMpMassTagMessage();
		//判断类型

		NewsTypeEnum newType = NewsTypeEnum.getValue(messageEntity.getPmNewType());
		try {
			switch (newType) {
				case TEXT:
					massMessage.setMsgType(WxConsts.MassMsgType.TEXT);
					massMessage.setContent(messageEntity.getPmContent());
					break;
				case IMAGE:
					FileEntity fileEntity = (FileEntity) fileBiz.getEntity(Integer.valueOf(messageEntity.getPmContent()));
					if (StringUtil.isBlank(fileEntity.getFileMediaId())) {
						//上传素材到微信服务器，返回madieid和图片url
						fileBiz.weiXinFileUpload(weixin, fileEntity);
					}
					massMessage.setMsgType(WxConsts.MassMsgType.IMAGE);
					massMessage.setMediaId(fileEntity.getFileMediaId());
					break;
				case IMAGE_TEXT:
					DraftEntity draftEntity = draftBiz.getById(messageEntity.getPmContent());
					massMessage.setMsgType(WxConsts.MassMsgType.MPNEWS);
					massMessage.setMediaId(draftEntity.getMediaId());
					break;
				case VOICE:
					FileEntity fileVoice = (FileEntity) fileBiz.getEntity(Integer.valueOf(messageEntity.getPmContent()));
					if (StringUtil.isBlank(fileVoice.getFileMediaId())) {
						//上传素材到微信服务器
						fileBiz.weiXinFileUpload(weixin, fileVoice);
					}
					massMessage.setMsgType(WxConsts.MassMsgType.VOICE);
					massMessage.setMediaId(fileVoice.getFileMediaId());
					break;
				case VIDEO:
					FileEntity fileVideo = (FileEntity) fileBiz.getEntity(Integer.valueOf(messageEntity.getPmContent()));
					if (StringUtil.isBlank(fileVideo.getFileMediaId())) {
						//上传素材到微信服务器
						fileBiz.weiXinFileUpload(weixin, fileVideo);
					}
					massMessage.setMsgType(WxConsts.MassMsgType.MPVIDEO);
					massMessage.setMediaId(fileVideo.getFileMediaId());
					break;
				default:
					break;
			}
			WxMpMassSendResult wxMpMassSendResult = weixinService.getMassMessageService().massGroupMessageSend(massMessage);
			messageEntity.setPmWeixinId(weixin.getIntId());
			messageEntity.setCreateDate(new Date());
			messageBiz.saveEntity(messageEntity);
			return ResultData.build().success();
		} catch (WxErrorException e) {
			e.printStackTrace();
			return ResultData.build().error(e.getMessage());

		}
	}

	/**
	 * 给用户单独发送文本,无限发送
	 * 
	 * @param openId
	 *            用户在微信中的唯一标识
	 * @param request
	 * @param response
	 */
	@ApiOperation(value="给用户单独发送文本,无限发送")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "openId", value = "用户openId:用户在微信中的唯一标识", required =true,paramType="query"),
    	@ApiImplicitParam(name = "content", value = "发送内容", required =true,paramType="query"),
    })
	@LogAnn(title = "给用户单独发送文本",businessType= BusinessTypeEnum.OTHER)
	@PostMapping("/sendToUser")
	@ResponseBody
	public ResultData sendToUser(@RequestParam(value="openId") @ApiIgnore String openId,MessageEntity messageEntity, HttpServletRequest request,
			HttpServletResponse response) {
		if (StringUtils.isBlank(openId)) {
			return ResultData.build().error();
		}
		// 获取微信
		WeixinEntity weixin = this.getWeixinSession(request);
		// 若微信不存在
		if (weixin == null || StringUtils.isBlank(weixin.getWeixinAppSecret())
				|| StringUtils.isBlank(weixin.getWeixinAppId())) {
			return ResultData.build().error(this.getResString("weixin.not.found"));
		}

		WxMpKefuMessage kefuMessage = new WxMpKefuMessage();

		boolean result;
		NewsTypeEnum newsTypeEnum = NewsTypeEnum.getValue(messageEntity.getPmNewType());
		try {
		switch (newsTypeEnum) {
			case TEXT:
				//给指定用户发送文字消息
				kefuMessage.setMsgType(WxConsts.MassMsgType.TEXT);
				kefuMessage.setContent(messageEntity.getPmContent());
				kefuMessage.setToUser(openId);
				result = this.builderWeixinService(weixin.getWeixinNo()).getKefuService()
						.sendKefuMessage(kefuMessage);
				return ResultData.build().success(result);
			case IMAGE:
				FileEntity fileEntity = (FileEntity) fileBiz.getEntity(Integer.valueOf(messageEntity.getPmContent()));
				if (StringUtil.isBlank(fileEntity.getFileMediaId())) {
					//上传素材到微信服务器，返回madieid和图片url
						fileBiz.weiXinFileUpload(weixin, fileEntity);
				}
				//给指定用户发送图片消息
				kefuMessage.setMsgType(WxConsts.MassMsgType.IMAGE);
				kefuMessage.setMediaId(fileEntity.getFileMediaId());
				kefuMessage.setToUser(openId);
				result = this.builderWeixinService(weixin.getWeixinNo()).getKefuService()
						.sendKefuMessage(kefuMessage);
				return ResultData.build().success(result);
			case IMAGE_TEXT:
				DraftEntity draftEntity = draftBiz.getById(messageEntity.getPmContent());
				kefuMessage.setMsgType(WxConsts.KefuMsgType.MP_NEWS_ARTICLE);
				kefuMessage.setMpNewsArticleId(draftEntity.getArticleId());
				kefuMessage.setToUser(openId);
				result = this.builderWeixinService(weixin.getWeixinNo()).getKefuService()
						.sendKefuMessage(kefuMessage);
				return ResultData.build().success(result);
			case VOICE:
				FileEntity fileVoice = (FileEntity) fileBiz.getEntity(Integer.valueOf(messageEntity.getPmContent()));
				if (StringUtils.isBlank(fileVoice.getFileMediaId())) {
					//上传素材到微信服务器
					fileBiz.weiXinFileUpload(weixin, fileVoice);
				}
				kefuMessage.setMsgType(WxConsts.MassMsgType.VOICE);
				kefuMessage.setMediaId(fileVoice.getFileMediaId());
				kefuMessage.setToUser(openId);
				result = this.builderWeixinService(weixin.getWeixinNo()).getKefuService()
						.sendKefuMessage(kefuMessage);
				return ResultData.build().success(result);
			case VIDEO:
				FileEntity fileVideo = (FileEntity) fileBiz.getEntity(Integer.valueOf(messageEntity.getPmContent()));
				if (StringUtil.isBlank(fileVideo.getFileMediaId())) {
					//上传素材到微信服务器
					fileBiz.weiXinFileUpload(weixin, fileVideo);
				}
				kefuMessage.setMsgType("video");
				kefuMessage.setMediaId(fileVideo.getFileMediaId());
				kefuMessage.setToUser(openId);
				result = this.builderWeixinService(weixin.getWeixinNo()).getKefuService()
						.sendKefuMessage(kefuMessage);
				return ResultData.build().success(result);
			default:
				return ResultData.build().error();
		}

		} catch (WxErrorException e) {
			e.printStackTrace();


		}
		return ResultData.build().error("回复时间超过限制");
	}
	
	/**
	 * 查询微信被动消息回复列表
	 * @param passiveMessage 微信被动消息回复实体
	 * <i>passiveMessage参数包含字段信息参考：</i><br/>
	 * pmWeixinId 微信编号<br/>
	 * pmId 自增长ID<br/>
	 * pmEventId 该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框<br/>
	 * pmNewsId 回复的素材ID<br/>
	 * pmMessageId 对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个<br/>
	 * pmReplyNum 被动回复的次数;为1时表示用户第一次被动响应消息,依次递增,当超出时取值为0的进行回复<br/>
	 * pmKey 事件关键字<br/>
	 * pmType 回复属性:keyword.关键字回复、attention.关注回复、passivity.被动回复、all.群发<br/>
	 * pmTag <br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>[<br/>
	 * { <br/>
	 * pmWeixinId: 微信编号<br/>
	 * pmId: 自增长ID<br/>
	 * pmEventId: 该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框<br/>
	 * pmNewsId: 回复的素材ID<br/>
	 * pmMessageId: 对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个<br/>
	 * pmReplyNum: 被动回复的次数;为1时表示用户第一次被动响应消息,依次递增,当超出时取值为0的进行回复<br/>
	 * pmKey: 事件关键字<br/>
	 * pmType: 回复属性:keyword.关键字回复、attention.关注回复、passivity.被动回复、all.群发<br/>
	 * pmTag: <br/>
	 * }<br/>
	 * ]</dd><br/>	 
	 */
	@ApiOperation(value="微信菜单列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "pmEventId", value = "该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框", required =false,paramType="query"),
    	@ApiImplicitParam(name = "pmNewsId", value = "回复的素材ID", required = false,paramType="query"),
    	@ApiImplicitParam(name = "pmMessageId", value = "对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个", required =false,paramType="query"),
    	@ApiImplicitParam(name = "pmKey", value = "事件关键字", required =false,paramType="query"),
    	@ApiImplicitParam(name = "pmType", value = "回复属性:keyword.关键字回复、attention.关注回复、passivity.被动回复、all.群发", required = false,paramType="query"),
    	@ApiImplicitParam(name = "pmTag", value = "标签", required = false,paramType="query"),
    	@ApiImplicitParam(name = "pmNewType", value = "素材类型 text 回复文本消息、image 回复图片消息、voice 回复语音消息、video 回复视频消息、imageText 回复图文消息", required = false,paramType="query"),
    	@ApiImplicitParam(name = "pmWeixinId", value = "微信编号", required = false,paramType="query"),
    })
	@GetMapping("/list")
	@ResponseBody
	@RequiresPermissions(value = {"passiveMessage:view","keywordMessage:view"},logical = Logical.OR)
	public ResultData list(@ModelAttribute MessageEntity passiveMessage,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		WeixinEntity weixin = this.getWeixinSession(request);
		if(weixin == null || weixin.getIntId()<=0){
			return ResultData.build().error();
		}
		passiveMessage.setPmWeixinId(weixin.getIntId());
		BasicUtil.startPage();
		List passiveMessageList = messageBiz.query(passiveMessage);
		return ResultData.build().success(new EUListBean(passiveMessageList,(int)BasicUtil.endPage(passiveMessageList).getTotal()));
	}
	
	/**
	 * 获取微信被动消息回复
	 * @param passiveMessage 微信被动消息回复实体
	 * <i>passiveMessage参数包含字段信息参考：</i><br/>
	 * pmWeixinId 微信编号<br/>
	 * pmId 自增长ID<br/>
	 * pmEventId 该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框<br/>
	 * pmNewsId 回复的素材ID<br/>
	 * pmMessageId 对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个<br/>
	 * pmReplyNum 被动回复的次数;为1时表示用户第一次被动响应消息,依次递增,当超出时取值为0的进行回复<br/>
	 * pmKey 事件关键字<br/>
	 * pmType 回复属性:keyword.关键字回复、attention.关注回复、passivity.被动回复、all.群发<br/>
	 * pmTag <br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * pmWeixinId: 微信编号<br/>
	 * pmId: 自增长ID<br/>
	 * pmEventId: 该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框<br/>
	 * pmNewsId: 回复的素材ID<br/>
	 * pmMessageId: 对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个<br/>
	 * pmReplyNum: 被动回复的次数;为1时表示用户第一次被动响应消息,依次递增,当超出时取值为0的进行回复<br/>
	 * pmKey: 事件关键字<br/>
	 * pmType: 回复属性:keyword.关键字回复、attention.关注回复、passivity.被动回复、all.群发<br/>
	 * pmTag: <br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value="获取微信被动消息回复")
    @ApiImplicitParam(name = "pmId", value = "消息回复编号", required =false,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions(value = {"passiveMessage:view", "keywordMessage:view"},logical = Logical.OR)
	public ResultData get(@ModelAttribute MessageEntity passiveMessage,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(StringUtils.isEmpty(passiveMessage.getId())){
			return ResultData.build().error(getResString("err.empty", this.getResString("pm.id")));
		}
		MessageEntity _passiveMessage = (MessageEntity)messageBiz.getEntity(passiveMessage);
		return ResultData.build().success(_passiveMessage);
	}
	
	/**
	 * 保存微信被动消息回复实体
	 * @param passiveMessage 微信被动消息回复实体
	 * <i>passiveMessage参数包含字段信息参考：</i><br/>
	 * pmWeixinId 微信编号<br/>
	 * pmId 自增长ID<br/>
	 * pmEventId 该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框<br/>
	 * pmNewsId 回复的素材ID<br/>
	 * pmMessageId 对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个<br/>
	 * pmReplyNum 被动回复的次数;为1时表示用户第一次被动响应消息,依次递增,当超出时取值为0的进行回复<br/>
	 * pmKey 事件关键字<br/>
	 * pmType 回复属性:keyword.关键字回复、attention.关注回复、passivity.被动回复、all.群发<br/>
	 * pmTag <br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * pmWeixinId: 微信编号<br/>
	 * pmId: 自增长ID<br/>
	 * pmEventId: 该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框<br/>
	 * pmNewsId: 回复的素材ID<br/>
	 * pmMessageId: 对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个<br/>
	 * pmReplyNum: 被动回复的次数;为1时表示用户第一次被动响应消息,依次递增,当超出时取值为0的进行回复<br/>
	 * pmKey: 事件关键字<br/>
	 * pmType: 回复属性:keyword.关键字回复、attention.关注回复、passivity.被动回复、all.群发<br/>
	 * pmTag: <br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value="保存微信被动消息回复实体")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "pmKey", value = "事件关键字", required =true,paramType="query"),
		@ApiImplicitParam(name = "pmType", value = "回复属性:keyword.关键字回复、attention.关注回复、passivity.被动回复、all.群发", required = true,paramType="query"),
		@ApiImplicitParam(name = "pmNewType", value = "素材类型 text 回复文本消息、image 回复图片消息、voice 回复语音消息、video 回复视频消息、imageText 回复图文消息", required = true,paramType="query"),
		@ApiImplicitParam(name = "pmContent", value = "消息回复内容", required = true,paramType="query"),
		@ApiImplicitParam(name = "pmEventId", value = "该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框", required =false,paramType="query"),
    	@ApiImplicitParam(name = "pmNewsId", value = "回复的素材ID", required = false,paramType="query"),
    	@ApiImplicitParam(name = "pmMessageId", value = "对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个", required =false,paramType="query"),
    	@ApiImplicitParam(name = "pmTag", value = "标签", required = false,paramType="query"),
    })
	@LogAnn(title = "保存微信被动消息回复实体",businessType= BusinessTypeEnum.INSERT)
	@RequestMapping(value ="/save", method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@RequiresPermissions(value = {"passiveMessage:save", "keywordMessage:save"}, logical = Logical.OR)
	public ResultData save(@ModelAttribute @ApiIgnore MessageEntity passiveMessage, HttpServletResponse response, HttpServletRequest request,BindingResult result) {
		//验证关键字回复内容的值是否合法
		if(StringUtils.isBlank(passiveMessage.getPmContent())){
			return ResultData.build().error(getResString("err.empty", this.getResString("pm.content")));
		}
		WeixinEntity weixin = this.getWeixinSession(request);
		if(weixin == null || weixin.getIntId()<=0){
			return ResultData.build().error(getResString("err"));
		}
		passiveMessage.setPmWeixinId(weixin.getIntId());
		if(MessageReplyType.KEYWORD.toString().equals(passiveMessage.getPmType())){
			if(isRepeat(passiveMessage)){
				return ResultData.build().error(this.getResString("pm.key.repeat"));	
			}
		}
		messageBiz.saveEntity(passiveMessage);
		return ResultData.build().success(passiveMessage);
	}
	
	/**
	 * @param passiveMessages 微信关键词回复实体
	 * <i>passiveMessage参数包含字段信息参考：</i><br/>
	 * pmId:多个pmId直接用逗号隔开,例如pmId=1,2,3,4
	 * 批量删除微信被动消息回复
	 *            <dt><span class="strong">返回</span></dt><br/>
	 *            <dd>{code:"错误编码",<br/>
	 *            result:"true｜false",<br/>
	 *            resultMsg:"错误信息"<br/>
	 *            }</dd>
	 */
	@ApiOperation(value="批量删除关键词回复")
	@LogAnn(title = "批量删除关键词回复",businessType= BusinessTypeEnum.DELETE)
	@PostMapping("/delete")
	@ResponseBody
	@RequiresPermissions("keywordMessage:del")
	public ResultData delete(@RequestBody List<MessageEntity> passiveMessages,HttpServletResponse response, HttpServletRequest request) {
		int[] ids = new int[passiveMessages.size()];
		for(int i = 0;i<passiveMessages.size();i++){
			ids[i] =passiveMessages.get(i).getIntId() ;
		}
		messageBiz.delete(ids);
		return ResultData.build().success();
	}
	
	/** 
	 * 更新微信被动消息、关键字回复信息
	 * @param passiveMessage 微信被动消息回复实体
	 * <i>passiveMessage参数包含字段信息参考：</i><br/>
	 * pmWeixinId 微信编号<br/>
	 * pmId 自增长ID<br/>
	 * pmEventId 该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框<br/>
	 * pmNewsId 回复的素材ID<br/>
	 * pmMessageId 对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个<br/>
	 * pmReplyNum 被动回复的次数;为1时表示用户第一次被动响应消息,依次递增,当超出时取值为0的进行回复<br/>
	 * pmKey 事件关键字<br/>
	 * pmType 回复属性:keyword.关键字回复、attention.关注回复、passivity.被动回复、all.群发<br/>
	 * pmTag <br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * pmWeixinId: 微信编号<br/>
	 * pmId: 自增长ID<br/>
	 * pmEventId: 该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框<br/>
	 * pmNewsId: 回复的素材ID<br/>
	 * pmMessageId: 对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个<br/>
	 * pmReplyNum: 被动回复的次数;为1时表示用户第一次被动响应消息,依次递增,当超出时取值为0的进行回复<br/>
	 * pmKey: 事件关键字<br/>
	 * pmType: 回复属性:keyword.关键字回复、attention.关注回复、passivity.被动回复、all.群发<br/>
	 * pmTag: <br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value="保存微信被动消息、关键字回复实体")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "pmId", value = "被动消息编号", required =true,paramType="query"),
		@ApiImplicitParam(name = "pmKey", value = "事件关键字", required =true,paramType="query"),
		@ApiImplicitParam(name = "pmType", value = "回复属性:keyword.关键字回复、attention.关注回复、passivity.被动回复、all.群发", required = true,paramType="query"),
		@ApiImplicitParam(name = "pmNewType", value = "素材类型 text 回复文本消息、image 回复图片消息、voice 回复语音消息、video 回复视频消息、imageText 回复图文消息", required = true,paramType="query"),
		@ApiImplicitParam(name = "pmContent", value = "消息回复内容", required = true,paramType="query"),
		@ApiImplicitParam(name = "pmEventId", value = "该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框", required =false,paramType="query"),
    	@ApiImplicitParam(name = "pmNewsId", value = "回复的素材ID", required = false,paramType="query"),
    	@ApiImplicitParam(name = "pmMessageId", value = "对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个", required =false,paramType="query"),
    	@ApiImplicitParam(name = "pmTag", value = "标签", required = false,paramType="query"),
    })
	@LogAnn(title = "保存微信被动消息、关键字回复实体",businessType= BusinessTypeEnum.UPDATE)
	@PostMapping("/update")
	@ResponseBody
	@RequiresPermissions(value = {"passiveMessage:save", "keywordMessage:edit"}, logical = Logical.OR)
	public ResultData update(@ModelAttribute @ApiIgnore MessageEntity passiveMessage, HttpServletResponse response,
			HttpServletRequest request) throws WxErrorException, MalformedURLException {
		//验证关键字回复内容的值是否合法			
		if(StringUtils.isBlank(passiveMessage.getPmContent())){
			messageBiz.deleteEntity(passiveMessage.getIntId());
			return ResultData.build().success();
		}
		if(!StringUtil.checkLength(passiveMessage.getPmContent()+"", 0, 4000)){
			return ResultData.build().error(getResString("err.length", this.getResString("pm.content"), "1", "4000"));
		}
		WeixinEntity weixin = this.getWeixinSession(request);
		if(weixin == null || weixin.getIntId()<=0){
			return ResultData.build().error();
		}
		passiveMessage.setPmWeixinId(weixin.getIntId());
		if(MessageReplyType.KEYWORD.toString().equals(passiveMessage.getPmType())){
			if(isRepeat(passiveMessage)){
				return ResultData.build().error(this.getResString("pm.key.repeat"));	
			}
		}
		weixinService = weixinService.build(weixin);
		//上传素材到微信服务器，返回madieid和图片url
		if (MenuStyleEnum.IMAGE.toString().equals(passiveMessage.getPmNewType())) {
			FileEntity fileEntity = (FileEntity) fileBiz.getEntity(Integer.valueOf(passiveMessage.getPmContent()));
			fileBiz.weiXinFileUpload(weixin, fileEntity);
		}
		messageBiz.updateEntity(passiveMessage);
		return ResultData.build().success(passiveMessage);
	}

	@ApiOperation(value="保存微信被动消息回复实体")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pmId", value = "被动消息编号", required =true,paramType="query"),
			@ApiImplicitParam(name = "pmKey", value = "事件关键字", required =true,paramType="query"),
			@ApiImplicitParam(name = "pmType", value = "回复属性:keyword.关键字回复、attention.关注回复、passivity.被动回复、all.群发", required = true,paramType="query"),
			@ApiImplicitParam(name = "pmNewType", value = "素材类型 text 回复文本消息、image 回复图片消息、voice 回复语音消息、video 回复视频消息、imageText 回复图文消息", required = true,paramType="query"),
			@ApiImplicitParam(name = "pmContent", value = "消息回复内容", required = true,paramType="query"),
			@ApiImplicitParam(name = "pmEventId", value = "该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框", required =false,paramType="query"),
			@ApiImplicitParam(name = "pmNewsId", value = "回复的素材ID", required = false,paramType="query"),
			@ApiImplicitParam(name = "pmMessageId", value = "对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个", required =false,paramType="query"),
			@ApiImplicitParam(name = "pmTag", value = "标签", required = false,paramType="query"),
	})
	/**
	 * 检查关键字是否重复
	 * @param passiveMessage 关键字实体包含id或pmKey
	 * @return  
	 * ture：重名 <br/>
	 * false：不重名
	 * 
	 */
	public boolean isRepeat(@ModelAttribute MessageEntity passiveMessage) {
		boolean flag = true;
		MessageEntity passiveMessageEntity = new MessageEntity();
		if(!ObjectUtil.isNull(passiveMessage)){
			if(Validator.isNotEmpty(passiveMessage.getPmKey())){
				passiveMessageEntity.setPmKey(passiveMessage.getPmKey());
				MessageEntity _passiveMessage = (MessageEntity) messageBiz.getEntity(passiveMessageEntity);
				//先根据名称查询，无结果则不重名
				if(ObjectUtil.isNull(_passiveMessage)){
					flag = false;
				//有结果再判断判断有无id，无id就重名，有id就说明是编辑的时候没有改名称，不算重名
				}else if(StringUtils.isNotEmpty(passiveMessage.getId())){
					if(_passiveMessage.getId().equals(passiveMessage.getId())){
						flag = false;
					}
				}else if(passiveMessage.getPmWeixinId() != _passiveMessage.getPmWeixinId()){
					 flag = false;
				}
			}
		}
		return flag;
	}

}
