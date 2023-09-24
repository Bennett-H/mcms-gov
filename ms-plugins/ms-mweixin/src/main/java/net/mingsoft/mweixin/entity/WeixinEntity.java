/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.mweixin.constant.e.WeixinTypeEnum;

/**
 * mswx-铭飞微信酒店预订平台
 * Copyright: Copyright (c) 2014 - 2015
 * Company:景德镇铭飞科技有限公司
 * @author 石超
 * @version 300-001-001
 * 版权所有 铭飞科技
 * Comments: 微信公众帐号基础信息实体类
 * Create Date:2013-12-23
 * Modification history:
 */
@TableName("wx_weixin")
public class WeixinEntity extends BaseEntity {


	/**
	 * 微信号
	 */
	private String weixinNo;

	/**
	 * 微信原始ID
	 */
	private String weixinOriginId;

	/**
	 * 公众号名称
	 */
	private String weixinName;

	/**
	 * 微信号类型 0:服务号 1:订阅号 3:微信开发平台 4:微信商户平台
	 */
	private int weixinType;

	/**
	 * 应用编号
	 */
	@TableField("weixin_appid")
	private String weixinAppId;
	
	/**
	 * 应用授权码
	 */
	@TableField("weixin_appsecret")
	private String weixinAppSecret;
	
	/**
	 * 微信token
	 */
	private String weixinToken;

	/**
	 * 微信帐号的头像
	 */
	@TableField("weixin_headimg")
	private String weixinHeadImg;

	/**
	 * 微信二维码图片
	 */
	private String weixinImage;
	
	/**
	 * 微信支付key,申请认证是邮件里面有
	 */
	@TableField("weixin_paykey")
	private String weixinPayKey;
	/**
	 * 消息加解密密钥
	 */
	@TableField(exist = false)
	private String weixinAesKey;
	
	/**
	 * 微信支付mchid,申请认证是邮件里面有
	 */
	@TableField("weixin_mchid")
	private String weixinPayMchId;
	
	/**
	 * 映射内网测试网地址，需要将微信的接口地址配置为代理地址才生效
	 */
	private String weixinProxyUrl;
	
	/**
	 * 网页2.0授权跳转地址,需要http
	 */
	private String weixinOauthUrl;


	/**
	 * @return 微信token
	 */
	public String getWeixinToken() {
		return weixinToken;
	}

	/**
	 * @return 微信号类型 0:服务号 1:公众号
	 */
	public int getWeixinType() {
		return weixinType;
	}
	

	public String getWeixinProxyUrl() {
		return weixinProxyUrl;
	}

	public void setWeixinProxyUrl(String weixinProxyUrl) {
		this.weixinProxyUrl = weixinProxyUrl;
	}

	public String getWeixinPayMchId() {
		return weixinPayMchId;
	}

	public void setWeixinPayMchId(String weixinPayMchId) {
		this.weixinPayMchId = weixinPayMchId;
	}

	public String getWeixinPayKey() {
		return weixinPayKey;
	}

	public void setWeixinPayKey(String weixinPayKey) {
		this.weixinPayKey = weixinPayKey;
	}

	public String getWeixinHeadImg() {
		return weixinHeadImg;
	}

	public void setWeixinHeadImg(String weixinHeadImg) {
		this.weixinHeadImg = weixinHeadImg;
	}


	public String getWeixinNo() {
		return weixinNo;
	}

	public void setWeixinNo(String weixinNo) {
		this.weixinNo = weixinNo;
	}

	/**
	 * @param 微信token
	 */
	public void setWeixinToken(String weixinToken) {
		this.weixinToken = weixinToken;
	}
	
	/**
	 * 声明方法过期
	 * @param weixinType
	 */
	@Deprecated
	public void setWeixinType(int weixinType){
		this.weixinType = weixinType;
	}
	
	/**
	 * @param 微信号类型
	 * 0:服务号 1:公众号2:微信开发平台3:微信用户
	 */
	public void setWeixinType(WeixinTypeEnum weixinType) {
		this.weixinType = weixinType.toInt();
	}
	
	
	/**
	 * @return the weixinName
	 */
	public String getWeixinName() {
		return weixinName;
	}

	/**
	 * @param weixinName
	 *            the weixinName to set
	 */
	public void setWeixinName(String weixinName) {
		this.weixinName = weixinName;
	}

	public String getWeixinImage() {
		return weixinImage;
	}

	public void setWeixinImage(String weixinImage) {
		this.weixinImage = weixinImage;
	}

	/**
	 * @return the winxinAppID
	 */
	public String getWeixinAppId() {
		return weixinAppId;
	}

	/**
	 * @param weixinAppID the winxinAppID to set
	 */
	public void setWeixinAppId(String weixinAppId) {
		this.weixinAppId = weixinAppId;
	}

	/**
	 * @return the winxinAppSecret
	 */
	public String getWeixinAppSecret() {
		return weixinAppSecret;
	}

	/**
	 * @param weixinAppSecret the winxinAppSecret to set
	 */
	public void setWeixinAppSecret(String weixinAppSecret) {
		this.weixinAppSecret = weixinAppSecret;
	}



	public String getWeixinOriginId() {
		return weixinOriginId;
	}

	public void setWeixinOriginId(String weixinOriginId) {
		this.weixinOriginId = weixinOriginId;
	}

	public String getWeixinOauthUrl() {
		return weixinOauthUrl;
	}

	public void setWeixinOauthUrl(String weixinOauthUrl) {
		this.weixinOauthUrl = weixinOauthUrl;
	}

	public String getWeixinAesKey() {
		return weixinAesKey;
	}

	public void setWeixinAesKey(String weixinAesKey) {
		this.weixinAesKey = weixinAesKey;
	}
	
	
}
