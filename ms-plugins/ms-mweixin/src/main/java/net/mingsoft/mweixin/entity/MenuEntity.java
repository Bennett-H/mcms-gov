/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.entity;

import net.mingsoft.base.entity.BaseEntity;

 /**
 * 微信菜单实体
 * @author 铭飞
 * @version 
 * 版本号：100<br/>
 * 创建日期：2017-12-22 9:43:04<br/>
 * 历史修订：<br/>
 */
public class MenuEntity extends BaseEntity implements Comparable<MenuEntity>{

	private static final long serialVersionUID = 1513906984073L;

	/**
	 * 菜单名称
	 */
	private String menuTitle; 
	/**
	 * 菜单链接地址
	 */
	private String menuUrl; 
	/**
	 * 菜单状态 0：不启用 1：启用
	 */
	private Integer menuStatus; 
	/**
	 * 父菜单编号
	 */
	private Integer menuMenuId;

	/**
	 * 菜单属性 0:链接 1:回复
	 */
	private Integer menuType;

	/**
	 * 菜单排序
	 */
	private Integer menuSort; 
	/**
	 * 类型：text文本 image图片 link外链接 miniprogram小程序
	 */
	private String menuStyle;
	/**
	 * 授权数据编号
	 */
	private Integer menuOauthId; 
	/**
	 * 微信编号
	 */
	private Integer menuWeixinId;
	/**
	 * 微信菜单内容
	 */
	private String menuContent;
    /**
     * 小程序页面路径
     */
	private String menuPagePath;
	 /**
	  * 小程序AppId
	  */
	private String miniprogramAppid;

	public String getMenuContent() {
		return menuContent;
	}

	public void setMenuContent(String menuContent) {
		this.menuContent = menuContent;
	}



	 /**
	 * 设置菜单名称
	 */
	public void setMenuTitle(String menuTitle) {
		this.menuTitle = menuTitle;
	}

	/**
	 * 获取菜单名称
	 */
	public String getMenuTitle() {
		return this.menuTitle;
	}
	/**
	 * 设置菜单链接地址
	 */
	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	/**
	 * 获取菜单链接地址
	 */
	public String getMenuUrl() {
		return this.menuUrl;
	}
	/**
	 * 设置菜单状态 0：不启用 1：启用
	 */
	public void setMenuStatus(Integer menuStatus) {
		this.menuStatus = menuStatus;
	}

	/**
	 * 获取菜单状态 0：不启用 1：启用
	 */
	public Integer getMenuStatus() {
		return this.menuStatus;
	}
	/**
	 * 设置父菜单编号
	 */
	public void setMenuMenuId(Integer menuMenuId) {
		this.menuMenuId = menuMenuId;
	}

	/**
	 * 获取父菜单编号
	 */
	public Integer getMenuMenuId() {
		return this.menuMenuId;
	}
	/**
	 * 设置菜单属性 0:链接 1:回复
	 */
	public void setMenuType(Integer menuType) {
		this.menuType = menuType;
	}

	/**
	 * 获取菜单属性 0:链接 1:回复
	 */
	public Integer getMenuType() {
		return this.menuType;
	}
	/**
	 * 设置
	 */
	public void setMenuSort(Integer menuSort) {
		this.menuSort = menuSort;
	}

	/**
	 * 获取
	 */
	public Integer getMenuSort() {
		return this.menuSort;
	}
	/**
	 * 设置类型：text文本 image图片 link外链接
	 */
	public void setMenuStyle(String menuStyle) {
		this.menuStyle = menuStyle;
	}

	/**
	 * 获取类型：text文本 image图片 link外链接
	 */
	public String getMenuStyle() {
		return this.menuStyle;
	}
	/**
	 * 设置授权数据编号
	 */
	public void setMenuOauthId(Integer menuOauthId) {
		this.menuOauthId = menuOauthId;
	}

	/**
	 * 获取授权数据编号
	 */
	public Integer getMenuOauthId() {
		return this.menuOauthId;
	}
	/**
	 * 设置微信编号
	 */
	public void setMenuWeixinId(Integer menuWeixinId) {
		this.menuWeixinId = menuWeixinId;
	}

	/**
	 * 获取微信编号
	 */
	public Integer getMenuWeixinId() {
		return this.menuWeixinId;
	}

	 /**
	  * 获取小程序路径
	  * @return
	  */
	 public String getMenuPagePath() {
		 return menuPagePath;
	 }

	 /**
	  * 设置小程序路径
	  * @param menuPagePath
	  */
	 public void setMenuPagePath(String menuPagePath) {
		 this.menuPagePath = menuPagePath;
	 }

	 /**
	  * 获取小程序Appid
	  */
	 public String getMiniprogramAppid() {
		 return miniprogramAppid;
	 }
	 /**
	  * 设置小程序AppId
	  * @param miniprogramAppid
	  */
	 public void setMiniprogramAppid(String miniprogramAppid) {
		 this.miniprogramAppid = miniprogramAppid;
	 }




	 @Override
	 public int compareTo(MenuEntity o) {
		 return this.menuSort-o.menuSort;
	 }

	 public enum MenuStyleType{
		TEXT("text"),//文本
		IMAGE("image"),//图片
		IMAGE_TEXT("imageText"),//图文
		LINK("link"),//链接
		VOICE("voice"),//声音
		VIDEO("video"),//视频
		MINIPROGRAM("miniprogram");//小程序


		private final String value;
		 MenuStyleType(String value){
			this.value = value;
		}

		 @Override
		 public String toString() {
			 return value;
		 }
	 }
}
