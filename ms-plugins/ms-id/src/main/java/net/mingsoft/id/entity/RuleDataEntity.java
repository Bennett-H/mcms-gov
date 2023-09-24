/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.id.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
/**
* 规则数据实体
* @author  会 
* 创建日期：2020-5-26 16:14:39<br/>
* 历史修订：<br/>
*/
@TableName("id_rule_data")
public class RuleDataEntity extends BaseEntity {

private static final long serialVersionUID = 1590480879626L;

	/**
	* 关联id
	*/
	private Integer irId;
	/**
	* 组成类型
	*/
	private String irdType;
	/**
	* 选项
	*/
	private String irdOption;


	/**
	* 设置关联id
	*/
	public void setIrId(Integer irId) {
	this.irId = irId;
	}

	/**
	* 获取关联id
	*/
	public Integer getIrId() {
	return this.irId;
	}
	/**
	* 设置组成类型
	*/
	public void setIrdType(String irdType) {
	this.irdType = irdType;
	}

	/**
	* 获取组成类型
	*/
	public String getIrdType() {
	return this.irdType;
	}
	/**
	* 设置选项
	*/
	public void setIrdOption(String irdOption) {
	this.irdOption = irdOption;
	}

	/**
	* 获取选项
	*/
	public String getIrdOption() {
	return this.irdOption;
	}
}
