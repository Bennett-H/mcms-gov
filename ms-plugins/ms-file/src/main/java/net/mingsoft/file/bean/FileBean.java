/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.file.bean;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import net.mingsoft.base.entity.BaseEntity;

/**
 * 文件和文件夹的模型
 */
public class FileBean extends BaseEntity {

	private String id;

	/**
	 * 是否是文件
	 */
	private boolean file;


	/**
	 * 是否有子文件或文件夹
	 */
	private boolean childFile;


	/**
	 * 所属的文件夹id
	 */
	private String folderId;

	/**
	 * 多行文本}
	 */

	private String fileDescribe;

	/**
	 * 文件路径}
	 */
	private String filePath;

	/**
	 * 文件大小}
	 */
	private String fileSize;

	/**
	 * 文件类型}
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String fileType;

	/**
	 * 文件名}
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String fileName;




	public boolean isFile() {
		return file;
	}
	public void isFile(boolean isFile) {
		this.file = isFile;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public void setFile(boolean file) {
		this.file = file;
	}

	public boolean isChildFile() {
		return childFile;
	}

	public void setChildFile(boolean childFile) {
		this.childFile = childFile;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public String getFileDescribe() {
		return fileDescribe;
	}

	public void setFileDescribe(String fileDescribe) {
		this.fileDescribe = fileDescribe;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
