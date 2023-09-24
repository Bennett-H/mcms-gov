/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.action.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mweixin.action.BaseAction;
import net.mingsoft.mweixin.biz.IWeixinBiz;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.service.PortalService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 微信入口
 * @author 铭飞开发团队
 * @version 
 * 版本号：100<br/>
 * 创建日期：2017-11-18 11:23:59<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"前端-微信模块接口"})
@RestController
public class PortalAction extends BaseAction {
	
	@Autowired
	private PortalService wxService;
	@Autowired
	private IWeixinBiz weixinBiz;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@ApiOperation(value = "微信入口")
	@GetMapping(value = "/mweixin/portal",produces = "text/plain;charset=utf-8")
	public String get(@RequestParam(name = "signature", required = false) String signature,
			@RequestParam(name = "timestamp", required = false) String timestamp,
			@RequestParam(name = "nonce", required = false) String nonce,
			@RequestParam(name = "echostr", required = false) String echostr) {
		String weixinNo = BasicUtil.getString("weixinNo");
		//获取微信实体，构建服务
		WeixinEntity weixin = weixinBiz.getByWeixinNo(weixinNo);
		if(weixin==null){
			return "未找到微信号信息";
		}
		wxService = wxService.build(weixin);
		
		this.logger.debug("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", new String[] { signature, timestamp, nonce, echostr });
		if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
			throw new IllegalArgumentException("请求参数非法，请核实!");
		}

		if (wxService.checkSignature(timestamp, nonce, signature)) {
			return echostr;
		}

		return "非法请求";
	}

	@ApiOperation(value = "微信入口")
	@PostMapping(value = "/mweixin/portal",produces = "application/xml; charset=UTF-8")
	public String post(@RequestBody String requestBody, @RequestParam("signature") String signature,
			@RequestParam(name = "encrypt_type", required = false) String encType,
			@RequestParam(name = "msg_signature", required = false) String msgSignature,
			@RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce) {
		
		String weixinNo = BasicUtil.getString("weixinNo");
		this.logger.debug(
				"\n接收微信请求：[weixinNo＝[{}]signature=[{}], encType=[{}], msgSignature=[{}],"
						+ " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
				new String[] { weixinNo,signature, encType, msgSignature, timestamp, nonce, requestBody });

		//获取微信实体，构建服务
		WeixinEntity weixin = weixinBiz.getByWeixinNo(weixinNo);
		wxService = wxService.build(weixin);
		if (!this.wxService.checkSignature(timestamp, nonce, signature)) {
			throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
		}

		String out = null;
		if (encType == null) {
			// 明文传输的消息
			WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
			WxMpXmlOutMessage outMessage = wxService.route(inMessage);
			if (outMessage == null) {
				return "";
			}
			out = outMessage.toXml();
		} else if ("aes".equals(encType)) {
			// aes加密的消息
			WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(requestBody, wxService.getWxMpConfigStorage(),
					timestamp, nonce, msgSignature);

			this.logger.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
			WxMpXmlOutMessage outMessage = wxService.route(inMessage);
			if (outMessage == null) {
				return "";
			}
			out = outMessage.toEncryptedXml(wxService.getWxMpConfigStorage());
		}

		this.logger.debug("\n组装回复信息：{}", out);

		return out;
	}

}
