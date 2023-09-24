/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.id.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
/**
* 规则实体
* @author  会 
* 创建日期：2020-5-26 16:14:39<br/>
* 历史修订：<br/>
*/
@TableName("id_rule")
public class RuleEntity extends BaseEntity {

private static final long serialVersionUID = 1590480879523L;

	/**
	* 规则名称
	*/
	private String irName;
	/**
	* 类型
	*/
	private String irType;


	/**
	* 设置规则名称
	*/
	public void setIrName(String irName) {
	this.irName = irName;
	}

	/**
	* 获取规则名称
	*/
	public String getIrName() {
	return this.irName;
	}
	/**
	* 设置类型
	*/
	public void setIrType(String irType) {
	this.irType = irType;
	}

	/**
	* 获取类型
	*/
	public String getIrType() {
	return this.irType;
	}
}
