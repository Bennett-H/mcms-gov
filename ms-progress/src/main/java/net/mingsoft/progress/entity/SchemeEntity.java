/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
/**
* 进度方案实体
* @author 铭飞科技
* 创建日期：2021-3-8 8:53:25<br/>
* 历史修订：<br/>
*/
@TableName("PROGRESS_SCHEME")
public class SchemeEntity extends BaseEntity {

private static final long serialVersionUID = 1615164805651L;

	/**
	* 方案名称
	*/
	@TableField(condition = SqlCondition.ORACLE_LIKE)
	private String schemeName;
	/**
	* 类型
	*/
	private String schemeType;
	/**
	* 回调表名
	*/
	private String schemeTable;

	/**
	 * 是否能够删除 0-能删除 1-不能删除
	 */
	@TableField(whereStrategy = FieldStrategy.NEVER)
    private int notDel;

	/**
	* 设置方案名称
	*/
	public void setSchemeName(String schemeName) {
	this.schemeName = schemeName;
	}

	/**
	* 获取方案名称
	*/
	public String getSchemeName() {
	return this.schemeName;
	}
	/**
	* 设置类型
	*/
	public void setSchemeType(String schemeType) {
	this.schemeType = schemeType;
	}

	/**
	* 获取类型
	*/
	public String getSchemeType() {
	return this.schemeType;
	}
	/**
	* 设置回调表名
	*/
	public void setSchemeTable(String schemeTable) {
	this.schemeTable = schemeTable;
	}

	/**
	* 获取回调表名
	*/
	public String getSchemeTable() {
	return this.schemeTable;
	}

	public int getNotDel() {
		return notDel;
	}

	public void setNotDel(int notDel) {
		this.notDel = notDel;
	}
}
