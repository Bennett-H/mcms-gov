/**
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循铭软科技《保密协议》
 */


package net.mingsoft.qa.entity;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.base.entity.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 自定义模型实体
* @author SMILE
* 创建日期：2019-11-9 15:53:59<br/>
* 历史修订：增加表单的访问地址formUrl<br/>
*/
@TableName("qa")
public class QaEntity extends BaseEntity {

private static final long serialVersionUID = 1573286039152L;


	/**
	 * 投票名称
	 */
	@TableField(whereStrategy= FieldStrategy.NOT_EMPTY)
	private String qaName;

	/**
	* 投票表名
	*/
	private String qaTableName;

	/**
	 * 是否能够删除 0-能删除 1-不能删除
	 */
	@TableField(whereStrategy = FieldStrategy.NEVER)
	private int notDel;

	/**
	 * 每个IP答题次数限制 json
	 */

	private String answerLimit;

	/**
	 * 结束时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")

	private Date endTime;

	/**
	 * 开始时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")

	private Date startTime;

	/**
	 * 投票类型
	 */
	private String qaType;

	/**
	* json
	*/
	private String modelJson;
	/**
	* 自定义字段
	*/
	private String modelField;


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

	public String getQaName() {
		return qaName;
	}

	public void setQaName(String qaName) {
		this.qaName = qaName;
	}

	public String getQaTableName() {
		return qaTableName;
	}

	public void setQaTableName(String qaTableName) {
		this.qaTableName = qaTableName;
	}

	public String getQaType() {
		return qaType;
	}

	public void setQaType(String qaType) {
		this.qaType = qaType;
	}

	public String getAnswerLimit() {
		return answerLimit;
	}

	public void setAnswerLimit(String answerLimit) {
		this.answerLimit = answerLimit;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
}
