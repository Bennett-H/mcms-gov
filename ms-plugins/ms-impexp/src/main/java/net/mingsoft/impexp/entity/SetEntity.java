/**
 * 版权所有  铭软科技(mingsoft.net)
 * 未经授权，严禁通过任何介质未经授权复制此文件
 */
package net.mingsoft.impexp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;
/**
* 导入导出配置实体
* @author 铭软科技
* 创建日期：2021-2-2 17:35:57<br/>
* 历史修订：<br/>
*/
@TableName("IMPEXP_SET")
public class SetEntity extends BaseEntity {

private static final long serialVersionUID = 1612258557061L;

	/**
	* 导入导出标识
	*/
	private String name;
	/**
	* 导出sql配置
	*/
	private String exportSql;
	/**
	* 导入主表json
	*/
	private String importJson;


	/**
	* 设置导入导出标识
	*/
	public void setName(String name) {
	this.name = name;
	}

	/**
	* 获取导入导出标识
	*/
	public String getName() {
	return this.name;
	}
	/**
	* 设置导出sql配置
	*/
	public void setExportSql(String exportSql) {
	this.exportSql = exportSql;
	}

	/**
	* 获取导出sql配置
	*/
	public String getExportSql() {
	return this.exportSql;
	}
	/**
	* 设置导入主表json
	*/
	public void setImportJson(String importJson) {
	this.importJson = importJson;
	}

	/**
	* 获取导入主表json
	*/
	public String getImportJson() {
	return this.importJson;
	}
}
