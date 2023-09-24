/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import net.mingsoft.base.entity.BaseEntity;

/**
* 文件夹表实体
* @author biusp
* 创建日期：2021-12-16 18:34:59<br/>
* 历史修订：<br/>
*/
@TableName("FILE_FOLDER")
public class FileFolderEntity extends BaseEntity {

private static final long serialVersionUID = 1639650899694L;


	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	@TableLogic
	private Integer del;



	/**
	* 文件描述}
	*/
	
	private String folderDescribe;

	/**
	* 文件夹名称}
	*/
	@TableField(condition = SqlCondition.LIKE)
	private String folderName;


	/**
	 * 文件夹路径}
	 */

	private String path;



	/**
	* 设置文件描述
	*/
	public void setFolderDescribe(String folderDescribe) {
	this.folderDescribe = folderDescribe;
	}

	/**
	* 获取文件描述
	*/
	public String getFolderDescribe() {
	return this.folderDescribe;
	}
	/**
	* 设置文件夹名称
	*/
	public void setFolderName(String folderName) {
	this.folderName = folderName;
	}

	/**
	* 获取文件夹名称
	*/
	public String getFolderName() {
	return this.folderName;
	}

	/**
	* 父节点id
	*/
	private  String parentId;

	/**
	* 父类型编号
	*/
	private String parentIds;

	/**
	* 叶子节点
	*/
	private Boolean leaf;

	/**
	* 顶级id
	*/
	private String topId;

	public Boolean getLeaf() {
	return leaf;
	}

	public void setLeaf(Boolean leaf) {
	this.leaf = leaf;
	}

	public String getTopId() {
	return topId;
	}

	public void setTopId(String topId) {
	this.topId = topId;
	}

	public String getParentId() {
	return parentId;
	}

	public void setParentId(String parentId) {
	this.parentId = parentId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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
	* 设置父类型编号
	*/
	public void setParentIds(String parentIds) {
	this.parentIds = parentIds;
	}

	/**
	* 获取父类型编号
	*/
	public String getParentIds() {
	return this.parentIds;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
}
