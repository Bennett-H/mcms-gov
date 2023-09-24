/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.statistics.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
/**
* 统计实体
* @author 铭软科技
* 创建日期：2021-4-6 16:43:01<br/>
* 历史修订：<br/>
*/
@TableName("STATISTICS_SQL")
public class SqlEntity extends BaseEntity {

private static final long serialVersionUID = 1617698581485L;

	/**
	* 统计名称
	*/
	private String ssName;

	/**
	* 统计类型
	*/
	private String ssType;
	/**
	* 统计SQL
	*/
	private String ssSql;


	/**
	* 设置统计名称
	*/
	public void setSsName(String ssName) {
	this.ssName = ssName;
	}

	/**
	* 获取统计名称
	*/
	public String getSsName() {
	return this.ssName;
	}

	/**
	* 设置统计类型
	*/
	public void setSsType(String ssType) {
	this.ssType = ssType;
	}

	/**
	* 获取统计类型
	*/
	public String getSsType() {
	return this.ssType;
	}
	/**
	* 设置统计SQL
	*/
	public void setSsSql(String ssSql) {
	this.ssSql = ssSql;
	}

	/**
	* 获取统计SQL
	*/
	public String getSsSql() {
	return this.ssSql;
	}
}
