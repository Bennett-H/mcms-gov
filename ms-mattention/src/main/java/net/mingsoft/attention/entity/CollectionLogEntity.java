/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.attention.entity;

import com.baomidou.mybatisplus.annotation.*;
import net.mingsoft.base.entity.BaseEntity;
/**
* 关注实体
* @author 铭飞开发团队
* 创建日期：2022-1-21 16:28:31<br/>
* 历史修订：<br/>
*/
@TableName("COLLECTION_LOG")
public class CollectionLogEntity extends BaseEntity {

private static final long serialVersionUID = 1574404489302L;

	/**
	 * id主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	 * 关注数
	 */
	@TableField(condition = SqlCondition.LIKE)
	private Integer dataCount;

	/**
	 * 业务类型
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String dataType;

	/**
	 * 数据id
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String dataId;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 设置关注数
	 */
	public void setDataCount(Integer dataCount) {
		this.dataCount = dataCount;
	}

	/**
	 * 获取关注数
	 */
	public Integer getDataCount() {
		return dataCount;
	}

	/**
	 * 设置业务类型
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * 获取业务类型
	 */
	public String getDataType() {
		return this.dataType;
	}
	/**
	 * 设置数据id
	 */
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	/**
	 * 获取数据id
	 */
	public String getDataId() {
		return this.dataId;
	}

}
