/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.entity;

import net.mingsoft.base.entity.BaseEntity;
/**
* 分类实体
* @author 铭飞开发团队
* 创建日期：2019-12-25 9:27:11<br/>
* 历史修订：<br/>
*/
public class CategoryEntity extends BaseEntity {

private static final long serialVersionUID = 1577237231913L;

	/**
	* 微信编号
	*/
	private Integer weixinId;
	/**
	* 类别标题
	*/
	private String categoryTitle;
	/**
	* 父类别编号
	*/
	private Integer categoryId;
	/**
	* 分类描述
	*/
	private String categoryDescription;
	/**
	* 分类略缩图
	*/
	private String categoryImg;

	public Integer getWeixinId() {
		return weixinId;
	}

	public void setWeixinId(Integer weixinId) {
		this.weixinId = weixinId;
	}

	/**
	* 设置类别标题
	*/
	public void setCategoryTitle(String categoryTitle) {
	this.categoryTitle = categoryTitle;
	}

	/**
	* 获取类别标题
	*/
	public String getCategoryTitle() {
	return this.categoryTitle;
	}
	/**
	* 设置父类别编号
	*/
	public void setCategoryId(Integer categoryId) {
	this.categoryId = categoryId;
	}

	/**
	* 获取父类别编号
	*/
	public Integer getCategoryId() {
	return this.categoryId;
	}
	/**
	* 设置分类描述
	*/
	public void setCategoryDescription(String categoryDescription) {
	this.categoryDescription = categoryDescription;
	}

	/**
	* 获取分类描述
	*/
	public String getCategoryDescription() {
	return this.categoryDescription;
	}
	/**
	* 设置分类略缩图
	*/
	public void setCategoryImg(String categoryImg) {
	this.categoryImg = categoryImg;
	}

	/**
	* 获取分类略缩图
	*/
	public String getCategoryImg() {
	return this.categoryImg;
	}
}
