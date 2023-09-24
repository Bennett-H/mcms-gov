/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.base.entity.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
/**
* 草稿实体
* @author 陈思鹏
* 创建日期：2022-3-1 11:23:09<br/>
* 历史修订：<br/>
*/
@TableName("WX_DRAFT")
public class DraftEntity extends BaseEntity {

private static final long serialVersionUID = 1646104989608L;


    @TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	* 微信媒体id
	*/
	private String mediaId;

	/**
	* 子图文ids
	*/
	private String childArticleIds;

	/**
	* 主图文id
	*/
	
	private Integer masterArticleId;

	/**
	* 微信id
	*/
	
	private Integer weixinId;

	/**
	 * 发布id
	 */
	private String publishId;

	/**
	 * 发布状态 0:未发布,1:发布中,2:发布成功,3:发布失败
	 */
	private Integer publishState;

	/**
	 * 发布成功后的微信服务器返回的图文id
	 */
	private String articleId;

	/**
	 * 图文url链接
	 */
	private String draftUrl;




	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate = new Date();

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createDate = new Date();

	/**
	* 设置微信媒体id
	*/
	public void setMediaId(String mediaId) {
	this.mediaId = mediaId;
	}

	/**
	* 获取微信媒体id
	*/
	public String getMediaId() {
	return this.mediaId;
	}
	/**
	* 设置子图文ids
	*/
	public void setChildArticleIds(String childArticleIds) {
	this.childArticleIds = childArticleIds;
	}

	/**
	* 获取子图文ids
	*/
	public String getChildArticleIds() {
	return this.childArticleIds;
	}
	/**
	* 设置主图文id
	*/
	public void setMasterArticleId(Integer masterArticleId) {
	this.masterArticleId = masterArticleId;
	}

	/**
	* 获取主图文id
	*/
	public Integer getMasterArticleId() {
	return this.masterArticleId;
	}
	/**
	* 设置微信id
	*/
	public void setWeixinId(Integer weixinId) {
	this.weixinId = weixinId;
	}

	/**
	* 获取微信id
	*/
	public Integer getWeixinId() {
	return this.weixinId;
	}

	@Override
	public Date getUpdateDate() {
		return updateDate;
	}

	@Override
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	@Override
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getPublishId() {
		return this.publishId;
	}

	public void setPublishId(String publishId) {
		this.publishId = publishId;
	}

	public Integer getPublishState() {
		return publishState;
	}

	public void setPublishState(Integer publishState) {
		this.publishState = publishState;
	}

	public String getArticleId() {
		return articleId;
	}

	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getDraftUrl() {
		return draftUrl;
	}

	public void setDraftUrl(String draftUrl) {
		this.draftUrl = draftUrl;
	}
}
