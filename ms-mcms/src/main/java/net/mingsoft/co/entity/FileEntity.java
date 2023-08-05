/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.co.entity;

import com.baomidou.mybatisplus.annotation.*;
import net.mingsoft.base.entity.BaseEntity;

/**
 * 文件表实体
 * @author biusp
 * 创建日期：2021-12-16 18:34:59<br/>
 * 历史修订：<br/>
 * 2021-1-22 重写co  增加folderId字段，将file表与文件夹表进行关联
 */
@TableName("FILE_PATH")
public class FileEntity extends BaseEntity {

    private static final long serialVersionUID = 1639650899772L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @TableLogic
    private Integer del;

    /**
     * 所属的文件夹id
     */
    private String folderId;

	/**
	 * 文件MD5值
	 */
	private String fileIdentifier;

	/**
	 * 多行文本
	 */

    private String fileDescribe;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小
     */
    private String fileSize;

    /**
     * 文件类型
     */
    @TableField(condition = SqlCondition.ORACLE_LIKE)
    private String fileType;

    /**
     * 文件名
     */
    @TableField(condition = SqlCondition.ORACLE_LIKE)
    private String fileName;

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    /**
     * 设置多行文本
     */
    public void setFileDescribe(String fileDescribe) {
        this.fileDescribe = fileDescribe;
    }

    /**
     * 获取多行文本
     */
    public String getFileDescribe() {
        return this.fileDescribe;
    }
    /**
     * 设置文件路径
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 获取文件路径
     */
    public String getFilePath() {
        return this.filePath;
    }
    /**
     * 设置文件大小
     */
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public Integer getDel() {
        return del;
    }

    @Override
    public void setDel(Integer del) {
        this.del = del;
    }

    /**
     * 获取文件大小
     */
    public String getFileSize() {
        return this.fileSize;
    }
    /**
     * 设置文件类型
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * 获取文件类型
     */
    public String getFileType() {
        return this.fileType;
    }
    /**
     * 设置文件名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取文件名
     */
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public String getId() {
        return id;
    }

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getFileIdentifier() {
		return fileIdentifier;
	}

	public void setFileIdentifier(String fileIdentifier) {
		this.fileIdentifier = fileIdentifier;
	}
}

