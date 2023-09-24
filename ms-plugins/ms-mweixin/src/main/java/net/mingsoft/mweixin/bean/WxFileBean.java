/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.bean;

/**
 * 微信文件详情
 * @author 铭飞开源团队-Administrator  
 * @date 2018年12月29日
 */
public class WxFileBean {

	private static final long serialVersionUID = 1546046290978L;
	
	/**
	 * 文件编号
	 */
	private Integer fileId; 
	/**
	 * 微信编号
	 */
	private Integer weixinId; 
	/**
	 * 是否同步至微信
	 */
	private String isSync;

	/**
	 * 文件mediaid
	 */
	private String fileMediaId;

	/**
	 * 设置mediaid
	 */
	public String getFileMediaId() {
		return fileMediaId;
	}

	/**
	 * 获取mediaid
	 */
	public void setFileMediaId(String fileMediaId) {
		this.fileMediaId = fileMediaId;
	}
		
	/**
	 * 设置文件编号
	 */
	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	/**
	 * 获取文件编号
	 */
	public Integer getFileId() {
		return this.fileId;
	}
	/**
	 * 设置微信编号
	 */
	public void setWeixinId(Integer weixinId) {
		this.weixinId = weixinId;
	}

	/**
	 * 获取微信编号
	 */
	public Integer getWeixinId() {
		return this.weixinId;
	}
	/**
	 * 设置是否同步至微信
	 */
	public void setIsSync(String isSync) {
		this.isSync = isSync;
	}

	/**
	 * 获取是否同步至微信
	 */
	public String getIsSync() {
		return this.isSync;
	}

	/**
	 * 文件分类编号
	 */
	private Integer categoryId;
	/**
	 * APP编号
	 */
	private Integer appId;
	/**
	 * 文件名称
	 */
	private String fileName;
	/**
	 * 文件链接
	 */
	private String fileUrl;
	/**
	 * 文件大小
	 */
	private Integer fileSize;
	/**
	 * 文件详情Json数据
	 */
	private String fileJson;
	/**
	 * 文件类型：图片、音频、视频等
	 */
	private String fileType;
	/**
	 * 子业务
	 */
	private String isChild;

	/**
	 * 设置文件分类编号
	 */
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * 获取文件分类编号
	 */
	public Integer getCategoryId() {
		return this.categoryId;
	}
	/**
	 * 设置APP编号
	 */
	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	/**
	 * 获取APP编号
	 */
	public Integer getAppId() {
		return this.appId;
	}
	/**
	 * 设置文件名称
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 获取文件名称
	 */
	public String getFileName() {
		return this.fileName;
	}
	/**
	 * 设置文件链接
	 */
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	/**
	 * 获取文件链接
	 */
	public String getFileUrl() {
		return this.fileUrl;
	}
	/**
	 * 设置文件大小
	 */
	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * 获取文件大小
	 */
	public Integer getFileSize() {
		return this.fileSize;
	}
	/**
	 * 设置文件详情Json数据
	 */
	public void setFileJson(String fileJson) {
		this.fileJson = fileJson;
	}

	/**
	 * 获取文件详情Json数据
	 */
	public String getFileJson() {
		return this.fileJson;
	}
	/**
	 * 设置文件类型：图片、音频、视频等
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * 获取文件类型：图片、音频、视频等
	 */
	public String getFileType() {
		return this.fileType;
	}
	/**
	 * 设置子业务
	 */
	public void setIsChild(String isChild) {
		this.isChild = isChild;
	}

	/**
	 * 获取子业务
	 */
	public String getIsChild() {
		return this.isChild;
	}
}
