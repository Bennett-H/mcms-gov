/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.entity;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.*;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.basic.util.BasicUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 自定义模型实体
* @author SMILE
* 创建日期：2019-11-9 15:53:59<br/>
* 历史修订：增加表单的访问地址formUrl<br/>
*/
@TableName("mdiy_model")
public class ModelEntity extends BaseEntity {

private static final long serialVersionUID = 1573286039152L;

	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	* 模型名称
	*/
	@TableField(whereStrategy= FieldStrategy.NOT_EMPTY)
	private String modelName;
	/**
	* 模型表名
	*/
	private String modelTableName;

	/**
	 * 是否能够删除 0-能删除 1-不能删除
	 */
	@TableField(whereStrategy = FieldStrategy.NEVER)
	private int notDel;

	/**
	* 类型
	*/
	private String modelType;
	/**
	* 模型业务类型
	*/
	private String modelCustomType;
	/**
	* json
	*/
	private String modelJson;
	/**
	* 自定义字段
	*/
	private String modelField;

	/**
	 * 表单的访问地址
	 */
	@TableField(exist = false)
	private String formUrl;

	public String getFormUrl() {
		if(StringUtils.isNotBlank(this.id)){
			formUrl= SecureUtil.aes(SecureUtil.md5(BasicUtil.getApp().getAppId() +"").substring(16).getBytes()).encryptHex(this.id);
		}
		return formUrl;
	}

	public void setFormUrl(String formUrl) {
		this.formUrl = formUrl;
	}

	/**
	* 设置模型名称
	*/
	public void setModelName(String modelName) {
	this.modelName = modelName;
	}

	/**
	* 获取模型名称
	*/
	public String getModelName() {
	return this.modelName;
	}
	/**
	* 设置模型表名
	*/
	public void setModelTableName(String modelTableName) {
	this.modelTableName = modelTableName;
	}

	/**
	* 获取模型表名
	*/
	public String getModelTableName() {
	return this.modelTableName;
	}

	/**
	* 设置类型
	*/
	public void setModelType(String modelType) {
	this.modelType = modelType;
	}

	/**
	* 获取类型
	*/
	public String getModelType() {
	return this.modelType;
	}
	/**
	* 设置下拉选择框
	*/
	public void setModelCustomType(String modelCustomType) {
	this.modelCustomType = modelCustomType;
	}

	/**
	* 获取下拉选择框
	*/
	public String getModelCustomType() {
	return this.modelCustomType;
	}
	/**
	* 设置json
	*/
	public void setModelJson(String modelJson) {
	this.modelJson = modelJson;
	}

	/**
	* 获取json
	*/
	public String getModelJson() {
	return this.modelJson;
	}
	/**
	* 设置自定义字段
	*/
	public void setModelField(String modelField) {
	this.modelField = modelField;
	}

	/**
	* 获取自定义字段
	*/
	public String getModelField() {
	return this.modelField;
	}

	public Map getFieldMap(){
		Map map=new  HashMap();
		List<Map> list= JSONUtil.toList(modelField,Map.class);
		if(ObjectUtil.isNotNull(list)){
			for (Map field : list) {
				map.put(field.get("model"),field.get("key"));
			}
		}
		return map;
	}
	public int getNotDel() {
		return notDel;
	}

	public void setNotDel(int notDel) {
		this.notDel = notDel;
	}
}
