/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.datascope.entity;


import com.baomidou.mybatisplus.annotation.*;
import net.mingsoft.base.entity.BaseEntity;
/**
* 数据权限实体
* @author  会 
* 创建日期：2022-7-1 16:23:56<br/>
* 历史修订：<br/>
*/
@TableName("DATASCOPE_DATA_CONFIG")
public class DataConfigEntity extends BaseEntity {

	private static final long serialVersionUID = 1656663836191L;

	public DataConfigEntity(String configName){
		this.configName = configName;
	}

	public DataConfigEntity(){
	}
	/**
	* 雪花ID规则
	*/
	@TableId(type = IdType.ASSIGN_ID)
	private String id;


	/**
	* 配置描述
	*/
	@TableField(condition = SqlCondition.ORACLE_LIKE)
	private String configDesc;

	/**
	* 子查询
	*/
	@TableField(condition = SqlCondition.ORACLE_LIKE)
	private String configSubsql;

	/**
	* 配置名称
	*/
	@TableField(condition = SqlCondition.ORACLE_LIKE)
	private String configName;


	/**
	* 设置配置描述
	*/
	public void setConfigDesc(String configDesc) {
	this.configDesc = configDesc;
	}

	/**
	* 获取配置描述
	*/
	public String getConfigDesc() {
	return this.configDesc;
	}
	/**
	* 设置子查询
	*/
	public void setConfigSubsql(String configSubsql) {
	this.configSubsql = configSubsql;
	}

	/**
	* 获取子查询
	*/
	public String getConfigSubsql() {
	return this.configSubsql;
	}
	/**
	* 设置配置名称
	*/
	public void setConfigName(String configName) {
	this.configName = configName;
	}

	/**
	* 获取配置名称
	*/
	public String getConfigName() {
	return this.configName;
	}


}
