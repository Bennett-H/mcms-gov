/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;

 /**
 * 标签实体
 * @author 铭飞开发团队
 * 创建日期：2018-10-24 8:44:34<br/>
 * 历史修订：2021-05-02 合并tagsql<br/>
 */
 @TableName("mdiy_tag")
public class TagEntity extends BaseEntity {

	private static final long serialVersionUID = 1540341874663L;

	/**
	 * 标签名称
	 */
	@TableField(whereStrategy= FieldStrategy.NOT_EMPTY,condition = SqlCondition.LIKE)
	private String tagName;
	/**
	 * 标签类型
	 */
	private String tagType;

	 /**
	  * 是否能够删除 0-能删除 1-不能删除
	  */
	 @TableField(whereStrategy = FieldStrategy.NEVER)
	 private int notDel;

	 /**
	  * 标签sql
	  */
	 private String tagSql;

	 private String tagClass;
	 /**
	 * 描述
	 */
	private String tagDescription;



	/**
	 * 设置标签名称
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	/**
	 * 获取标签名称
	 */
	public String getTagName() {
		return this.tagName;
	}
	/**
	 * 设置标签类型
	 */
	public void setTagType(String tagType) {
		this.tagType = tagType;
	}

	/**
	 * 获取标签类型
	 */
	public String getTagType() {
		return this.tagType;
	}
	/**
	 * 设置描述
	 */
	public void setTagDescription(String tagDescription) {
		this.tagDescription = tagDescription;
	}

	/**
	 * 获取描述
	 */
	public String getTagDescription() {
		return this.tagDescription;
	}

	 public String getTagSql() {
		 return tagSql;
	 }

	 public void setTagSql(String tagSql) {
		 this.tagSql = tagSql;
	 }

	 public String getTagClass() {
		 return tagClass;
	 }

	 public void setTagClass(String tagClass) {
		 this.tagClass = tagClass;
	 }

	 public int getNotDel() {
		 return notDel;
	 }

	 public void setNotDel(int notDel) {
		 this.notDel = notDel;
	 }
 }
