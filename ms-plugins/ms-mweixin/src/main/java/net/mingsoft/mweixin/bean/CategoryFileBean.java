/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.bean;

import net.mingsoft.mweixin.entity.CategoryEntity;
import net.mingsoft.mweixin.entity.FileEntity;

import java.util.List;

/**
 * 分组文件类
 * @author 铭飞开源团队  
 * @date 2019年02月15日
 */
public class CategoryFileBean {
	/**
	 * 分组
	 */
	private CategoryEntity category;
	/**
	 * 文件集合
	 */
	private List<FileEntity> files;
	/**
	 * 文件总数
	 */
	private int total;
	public List<FileEntity> getFiles() {
		return files;
	}
	public void setFiles(List<FileEntity> files) {
		this.files = files;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public CategoryEntity getCategory() {
		return category;
	}
	public void setCategory(CategoryEntity category) {
		this.category = category;
	}
	
}
