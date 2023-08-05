/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.comment.bean;

import net.mingsoft.comment.entity.CommentEntity;
import org.apache.commons.lang3.StringUtils;

/**
 * 评论实体类，目前只用于后台的查询搜索功能
 * @author 铭飞开源团队
 * @date 2019年7月16日
 */
public class CommentBean extends CommentEntity{

	/**
	 * 时间  用于查询
	 */
	private String commentDateTimes;
	/**
	 * 开始时间，用于查询 
	 */
	private String commentStartTime;
	/**
	 * 开始时间，用于查询 
	 */
	private String commentEndTime;

	/**
	 * 未审核子评论数量
	 */
	private long unAuditedChildCommentCount;



	public String getCommentStartTime() {
		if (StringUtils.isEmpty(this.commentDateTimes)){
			return null;
		}
		return this.commentDateTimes != null && this.commentDateTimes != "" ? this.commentDateTimes.split("至")[0] : this.commentStartTime;
	}
	public void setCommentStartTime(String commentStartTime) {
		this.commentStartTime = commentStartTime;
	}
	public String getCommentEndTime() {
		if (StringUtils.isEmpty(this.commentDateTimes)){
			return null;
		}
		return this.commentDateTimes != null && this.commentDateTimes != "" ? this.commentDateTimes.split("至")[1] : this.commentStartTime;
	}
	public void setCommentEndTime(String commentEndTime) {
		this.commentEndTime = commentEndTime;
	}
	public String getCommentDateTimes() {
		return commentDateTimes;
	}
	public void setCommentDateTimes(String commentDateTimes) {
		this.commentDateTimes = commentDateTimes;
	}

	public long getUnAuditedChildCommentCount() {
		return unAuditedChildCommentCount;
	}

	public void setUnAuditedChildCommentCount(long unAuditedChildCommentCount) {
		this.unAuditedChildCommentCount = unAuditedChildCommentCount;
	}
}
