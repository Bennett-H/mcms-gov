/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
/**
* 文章实体
* @author 铭飞开发团队
* 创建日期：2019-12-25 9:27:11<br/>
* 历史修订：<br/>
*/
@TableName("wx_article")
public class ArticleEntity extends BaseEntity {

private static final long serialVersionUID = 1577237231820L;

	/**
	* 文章标题
	*/
	private String articleTitle;
	/**
	* 文章作者
	*/
	private String articleAuthor;
	/**
	* 文章描述
	*/
	private String articleDescription;
	/**
	* 文章来源
	*/
	private String articleSource;
	/**
	* 原文链接
	*/
	private String articleUrl;
	/**
	* 素材类型
	*/
	private String articleType;
	/**
	* 微信草稿编号
	*/
	private String draftId;
	/**
	* 文章内容
	*/
	private String articleContent;
	/**
	* 是否显示
	*/
	private Boolean articleDisplay;
	/**
	* 文章点击数
	*/
	private Integer articleHit;
	/**
	* 文章排序
	*/
	private Integer articleSort;
	/**
	* 文章图片
	*/
	private String articleThumbnails;

	/**
	 * mcms文章的id,用于动态更新
	 */
	private String contentId;

	/**
	* 设置文章标题
	*/
	public void setArticleTitle(String articleTitle) {
	this.articleTitle = articleTitle;
	}

	/**
	* 获取文章标题
	*/
	public String getArticleTitle() {
	return this.articleTitle;
	}
	/**
	* 设置文章作者
	*/
	public void setArticleAuthor(String articleAuthor) {
	this.articleAuthor = articleAuthor;
	}

	/**
	* 获取文章作者
	*/
	public String getArticleAuthor() {
	return this.articleAuthor;
	}
	/**
	* 设置文章描述
	*/
	public void setArticleDescription(String articleDescription) {
	this.articleDescription = articleDescription;
	}

	/**
	* 获取文章描述
	*/
	public String getArticleDescription() {
	return this.articleDescription;
	}
	/**
	* 设置文章来源
	*/
	public void setArticleSource(String articleSource) {
	this.articleSource = articleSource;
	}

	/**
	* 获取文章来源
	*/
	public String getArticleSource() {
	return this.articleSource;
	}
	/**
	* 设置原文链接
	*/
	public void setArticleUrl(String articleUrl) {
	this.articleUrl = articleUrl;
	}

	/**
	* 获取原文链接
	*/
	public String getArticleUrl() {
	return this.articleUrl;
	}
	/**
	* 设置素材类型
	*/
	public void setArticleType(String articleType) {
	this.articleType = articleType;
	}

	/**
	* 获取素材类型
	*/
	public String getArticleType() {
	return this.articleType;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getDraftId() {
		return draftId;
	}

	public void setDraftId(String draftId) {
		this.draftId = draftId;
	}

	/**
	* 设置文章内容
	*/
	public void setArticleContent(String articleContent) {
	this.articleContent = articleContent;
	}

	/**
	* 获取文章内容
	*/
	public String getArticleContent() {
	return this.articleContent;
	}
	/**
	* 设置是否显示
	*/
	public void setArticleDisplay(Boolean articleDisplay) {
	this.articleDisplay = articleDisplay;
	}

	/**
	* 获取是否显示
	*/
	public Boolean getArticleDisplay() {
	return this.articleDisplay;
	}
	/**
	* 设置文章点击数
	*/
	public void setArticleHit(Integer articleHit) {
	this.articleHit = articleHit;
	}

	/**
	* 获取文章点击数
	*/
	public Integer getArticleHit() {
	return this.articleHit;
	}
	/**
	* 设置文章排序
	*/
	public void setArticleSort(Integer articleSort) {
	this.articleSort = articleSort;
	}

	/**
	* 获取文章排序
	*/
	public Integer getArticleSort() {
	return this.articleSort;
	}
	/**
	* 设置文章图片
	*/
	public void setArticleThumbnails(String articleThumbnails) {
	this.articleThumbnails = articleThumbnails;
	}

	/**
	* 获取文章图片
	*/
	public String getArticleThumbnails() {
	return this.articleThumbnails;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
}
