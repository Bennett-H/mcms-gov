/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.comment.entity;

import com.baomidou.mybatisplus.annotation.*;
import net.mingsoft.base.entity.BaseEntity;
/**
* 评论记录实体
* @author 铭飞开发团队
* 创建日期：2022-1-21 10:14:30<br/>
* 历史修订：<br/>
*/
@TableName("COMMENTS_LOG")
public class CommentsLogEntity extends BaseEntity {

private static final long serialVersionUID = 1642731270002L;

	/**
	 * id主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	* 评论数
	*/
	@TableField(condition = SqlCondition.LIKE)
	private Integer commentsCount;

	/**
	* 业务类型
	*/
	@TableField(condition = SqlCondition.LIKE)
	private String dataType;

	/**
	* 文章、商品 id
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

	public Integer getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(Integer commentsCount) {
		this.commentsCount = commentsCount;
	}

	/**
	 * 设置业务类型
	 */
	public String getDataType() {
		return dataType;
	}
	/**
	 * 获取业务类型
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	/**
	* 设置文章、商品 id
	*/
	public void setDataId(String dataId) {
	this.dataId = dataId;
	}

	/**
	* 获取文章、商品 id
	*/
	public String getDataId() {
	return this.dataId;
	}


}
