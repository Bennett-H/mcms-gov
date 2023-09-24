/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import net.mingsoft.base.constant.e.BaseEnum;
import net.mingsoft.base.entity.BaseEntity;
import java.util.Date;

 /**
 * 微信被动消息回复实体
 * @author 铭飞
 * @version 
 * 版本号：100<br/>
 * 创建日期：2017-12-22 9:43:04<br/>
 * 历史修订：<br/>
 */
public class MessageEntity extends BaseEntity {

	private static final long serialVersionUID = 1513906984116L;
	
	/**
	 * 微信编号
	 */
	private Integer pmWeixinId; 

	/**
	 * 该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框
	 */
	private Integer pmEventId; 
	/**
	 * 回复的素材ID
	 */
	private Integer pmNewsId; 
	/**
	 * 对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个
	 */
	private Integer pmMessageId;
	/**
	 * 被动回复的次数;为1时表示用户第一次被动响应消息,依次递增,当超出时取值为0的进行回复
	 */
	private Integer pmReplyNum; 
	/**
	 * 事件关键字
	 */
	private String pmKey; 
	/**
	 * 回复属性:keyword,关键字回复，attention,关注回复，passivity,被动回复,all,群发
	 */
	private String pmType;
	/**
	 * 
	 */
	private String pmTag; 
	
	/**
	 * 消息回复内容
	 */
	private String pmContent; 
	
	/**
	 * 素材类型：imageText单图文 text文本 image图片 moreImageText多图文 link链接 voice语音 video视频
	 */
	private String pmNewType;
	
	/**
	 * 素材对应的标题（不参与表结构）
	 */
	private String newsTitle;
	
	/**
	 * 获取素材对应的标题（不参与表结构）
	 * @return
	 */
	public String getNewsTitle() {
		return newsTitle;
	}

	/**
	 * 设置素材对应的标题（不参与表结构）
	 * @param newsTitle
	 */
	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}

	/**
	 * 获取素材类型
	 * @return
	 */
	public String getPmNewType() {
		return pmNewType;
	}

	/**
	 * 设置素材类型
	 * @param pmNewType
	 */
	public void setPmNewType(String pmNewType) {
		this.pmNewType = pmNewType;
	}

	/**
	 * 获取消息回复内容
	 * @return
	 */
	public String getPmContent() {
		return pmContent;
	}

	/**
	 * 设置消息回复内容
	 * @param pmContent
	 */
	public void setPmContent(String pmContent) {
		this.pmContent = pmContent;
	}

	/**
	 * 设置微信编号
	 */
	public void setPmWeixinId(Integer pmWeixinId) {
		this.pmWeixinId = pmWeixinId;
	}

	/**
	 * 获取微信编号
	 */
	public Integer getPmWeixinId() {
		return this.pmWeixinId;
	}

	/**
	 * 设置该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框
	 */
	public void setPmEventId(Integer pmEventId) {
		this.pmEventId = pmEventId;
	}

	/**
	 * 获取该回复属于的分类中的事件ID,1新关注2二维码扫描5未关注扫描二维码6点击事件4文本消息3二维码扫描&提示框
	 */
	public Integer getPmEventId() {
		return this.pmEventId;
	}
	/**
	 * 设置回复的素材ID
	 */
	public void setPmNewsId(Integer pmNewsId) {
		this.pmNewsId = pmNewsId;
	}

	/**
	 * 获取回复的素材ID
	 */
	public Integer getPmNewsId() {
		return this.pmNewsId;
	}
	/**
	 * 设置对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个
	 */
	public void setPmMessageId(Integer pmMessageId) {
		this.pmMessageId = pmMessageId;
	}

	/**
	 * 获取对应自定义模板回复消息id,与PM_NEWS_ID只能同时存在一个
	 */
	public Integer getPmMessageId() {
		return this.pmMessageId;
	}
	/**
	 * 设置被动回复的次数;为1时表示用户第一次被动响应消息,依次递增,当超出时取值为0的进行回复
	 */
	public void setPmReplyNum(Integer pmReplyNum) {
		this.pmReplyNum = pmReplyNum;
	}

	/**
	 * 获取被动回复的次数;为1时表示用户第一次被动响应消息,依次递增,当超出时取值为0的进行回复
	 */
	public Integer getPmReplyNum() {
		return this.pmReplyNum;
	}
	/**
	 * 设置事件关键字
	 */
	public void setPmKey(String pmKey) {
		this.pmKey = pmKey;
	}

	/**
	 * 获取事件关键字
	 */
	public String getPmKey() {
		return this.pmKey;
	}
	/**
	 * 设置回复属性:keyword,关键字回复，attention,关注回复，passivity,被动回复,all,群发
	 */
	public void setPmType(String pmType) {
		this.pmType = pmType;
	}

	/**
	 * 获取回复属性:keyword,关键字回复，attention,关注回复，passivity,被动回复,all,群发
	 */
	public String getPmType() {
		return this.pmType;
	}
	/**
	 * 设置
	 */
	public void setPmTag(String pmTag) {
		this.pmTag = pmTag;
	}

	/**
	 * 获取
	 */
	public String getPmTag() {
		return this.pmTag;
	}
}
