/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.cms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.wordfilter.annotation.Sensitive;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
* 文章实体
* @author 铭飞开发团队
* 创建日期：2019-11-28 15:12:32<br/>
* 历史修订：增加字段progressStatus<br/>
 * * 历史修订：增加字段contentStyle 内、外王字段 2021-5-17<br/>
*/
@TableName("cms_content")
public class ContentEntity extends BaseEntity implements Serializable {

private static final long serialVersionUID = 1574925152617L;

	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 逻辑删除字段
	 */
	@TableLogic
	private Integer del;

	/**
	* 文章标题
	*/
	@Sensitive
	private String contentTitle;
	/**
	 * 文章副标题
	 */
	@Sensitive
	private String contentShortTitle;
	/**
	* 所属栏目
	*/
	private String categoryId;
	/**
	* 文章类型
	*/
	private String contentType;
	/**
	* 是否显示
	*/
	private String contentDisplay;
	/**
	* 文章作者
	*/
	@Sensitive
	private String contentAuthor;
	/**
	* 文章来源
	*/
	@Sensitive
	private String contentSource;
	/**
	* 发布时间
	*/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	private Date contentDatetime;
	/**
	* 自定义顺序
	*/
	private Integer contentSort;
	/**
	* 文章缩略图
	*/
	private String contentImg;
	/**
	* 描述
	*/
	@Sensitive
	private String contentDescription;
	/**
	* 关键字
	*/
	@Sensitive
	private String contentKeyword;
	/**
	* 文章内容
	*/
	@Sensitive(isHtml = true)
	private String contentDetails;
	/**
	* 文章跳转链接地址
	*/
	private String contentOutLink;
	/**
	 * 文章标题样式
	 */
	private String contentTitleCss;
	/**
	 * 文章审核状态
	 */
	private String progressStatus;

	/**
	* 点击次数
	*/
	private Integer contentHit;

    /**
     * 发布到，保存到是对应 模版类型 多字典value值，
     * 目前已知contentStyle影响业务如下：
     * 静态化页面，action，service，定时调度job，自定义页面，动态静态化，index action 首页跳转，标签
     *
     */
	private String contentStyle;

	/**
	 * 文章标签
	 */
	private String contentTags;

	/**文章所属其他栏目的id
	 * */
	private String categoryIds;

	@TableField(exist = false)
	private String appId;

	/**
	 * 文章是否被静态化
	 */
	private Integer hasDetailHtml;

	/**
	 * 栏目是否被静态化
	 */
	private Integer hasListHtml;

	public String getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(String categoryIds) {
		this.categoryIds = categoryIds;
	}

	public String getContentStyle() {
		return contentStyle;
	}

	public void setContentStyle(String contentStyle) {
		this.contentStyle = contentStyle;
	}

	public Integer getContentHit() {
		return contentHit;
	}

	public void setContentHit(Integer contentHit) {
		this.contentHit = contentHit;
	}

	/**
	* 设置文章标题
	*/
	public void setContentTitle(String contentTitle) {
	this.contentTitle = contentTitle;
	}

	/**
	* 获取文章标题
	*/
	public String getContentTitle() {
	return this.contentTitle;
	}

	public String getContentShortTitle() {
		return contentShortTitle;
	}

	public void setContentShortTitle(String contentShortTitle) {
		this.contentShortTitle = contentShortTitle;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	* 设置文章类型
	*/
	public void setContentType(String contentType) {
	this.contentType = contentType;
	}

	/**
	* 获取文章类型
	*/
	public String getContentType() {
	return this.contentType;
	}
	/**
	* 设置是否显示
	*/
	public void setContentDisplay(String contentDisplay) {
	this.contentDisplay = contentDisplay;
	}

	/**
	* 获取是否显示
	*/
	public String getContentDisplay() {
	return this.contentDisplay;
	}
	/**
	* 设置文章作者
	*/
	public void setContentAuthor(String contentAuthor) {
	this.contentAuthor = contentAuthor;
	}

	/**
	* 获取文章作者
	*/
	public String getContentAuthor() {
	return this.contentAuthor;
	}
	/**
	* 设置文章来源
	*/
	public void setContentSource(String contentSource) {
	this.contentSource = contentSource;
	}

	public String getContentTags() {
		return contentTags;
	}

	public void setContentTags(String contentTags) {
		this.contentTags = contentTags;
	}

	/**
	* 获取文章来源
	*/
	public String getContentSource() {
	return this.contentSource;
	}
	/**
	* 设置发布时间
	*/
	public void setContentDatetime(Date contentDatetime) {
	this.contentDatetime = contentDatetime;
	}

	/**
	* 获取发布时间
	*/
	public Date getContentDatetime() {
	return this.contentDatetime;
	}
	/**
	* 设置自定义顺序
	*/
	public void setContentSort(Integer contentSort) {
	this.contentSort = contentSort;
	}

	/**
	* 获取自定义顺序
	*/
	public Integer getContentSort() {
	return this.contentSort;
	}
	/**
	* 设置文章缩略图
	*/
	public void setContentImg(String contentImg) {
	this.contentImg = contentImg;
	}

	/**
	* 获取文章缩略图
	*/
	public String getContentImg() {
	return this.contentImg;
	}
	/**
	* 设置描述
	*/
	public void setContentDescription(String contentDescription) {
	this.contentDescription = contentDescription;
	}

	/**
	* 获取描述
	*/
	public String getContentDescription() {
	return this.contentDescription;
	}
	/**
	* 设置关键字
	*/
	public void setContentKeyword(String contentKeyword) {
	this.contentKeyword = contentKeyword;
	}

	/**
	* 获取关键字
	*/
	public String getContentKeyword() {
	return this.contentKeyword;
	}
	/**
	* 设置文章内容
	*/
	public void setContentDetails(String contentDetails) {
	this.contentDetails = contentDetails;
	}

	/**
	* 获取文章内容
	*/
	public String getContentDetails() {
	return this.contentDetails;
	}
	/**
	* 设置文章跳转链接地址
	*/
	public void setContentOutLink(String contentOutLink) {
	this.contentOutLink = contentOutLink;
	}

	/**
	* 获取文章跳转链接地址
	*/
	public String getContentOutLink() {
	return this.contentOutLink;
	}

	public String getContentTitleCss() {
		return contentTitleCss;
	}

	public void setContentTitleCss(String contentTitleCss) {
		this.contentTitleCss = contentTitleCss;
	}

	public String getProgressStatus() {
		return progressStatus;
	}

	public void setProgressStatus(String progressStatus) {
		this.progressStatus = progressStatus;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Integer getHasDetailHtml() {
		return hasDetailHtml;
	}

	public void setHasDetailHtml(Integer hasDetailHtml) {
		this.hasDetailHtml = hasDetailHtml;
	}

	public Integer getHasListHtml() {
		return hasListHtml;
	}

	public void setHasListHtml(Integer hasListHtml) {
		this.hasListHtml = hasListHtml;
	}

	@Override
	public Integer getDel() {
		return del;
	}

	@Override
	public void setDel(Integer del) {
		this.del = del;
	}
}
