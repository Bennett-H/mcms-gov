/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 城市数据格式bean
 * @author qiu
 *
 */

public class CityBean implements Serializable {


	/**
	 * 城市id
	 */
	private Long id;

	/**
	 * 城市名称
	 */
	private String name;

	/**
	 * 父级id
	 */
	private Long parentId;

	/**
	 * 子城市数据集合
	 */
	private List<CityBean> childrensList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public List<CityBean> getChildrensList() {
		return childrensList;
	}

	public void setChildrensList(List<CityBean> childrensList) {
		this.childrensList = childrensList;
	}

}
