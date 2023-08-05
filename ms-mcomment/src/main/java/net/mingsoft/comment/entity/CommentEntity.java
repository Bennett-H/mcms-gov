/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.comment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.base.entity.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
/**
* 评论表实体
* @author 铭飞开发团队
* 创建日期：2019-11-13 10:52:31<br/>
* 历史修订：<br/>
*/
@TableName("comments")
public class CommentEntity extends BaseEntity {

private static final long serialVersionUID = 1573613551176L;

	/**
	 * id主键
 	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	* 评论者id
	*/
	private Integer peopleId;

	/**
	 * 评论者昵称
	 */
	private String peopleName;
	/**
	* 业务Id
	*/
	private String dataId;

	/**
	 * 业务扩展信息
	 */
	private String dataInfo;

	/**
	 * 文章、商品 标题
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String dataTitle;
	/**
	* 父评论id
	*/
	private String commentId;
	/**
	 * 评论时间
	 */
    @JsonFormat(
			timezone = "GMT+8",
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @DateTimeFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
	private Date commentTime;
	/**
	* 点赞字段
	*/
	private Integer commentLike;
	/**
	* 默认赋值为false,
    * true代表审核通过，false为未通过
    * 数据库中审核通过为1，不通过为0
	*/
	private Boolean commentAudit;
	/**
	* 评价打分
	*/
	private Integer commentPoints;
	/**
	* 评论的内容
	*/
	private String commentContent;
	/**
	* 图片
	*/
	private String commentPicture;
	/**
	* 匿名
	*/
	private Integer commentIsAnonymous;
	/**
	* 附件json
	*/
	private String commentFileJson;
	/**
	 * 业务类型
	 */
	private String dataType;

	/**
	 * 父类型编号
	 */
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private String topId;

	/**
	 * ip
	 */
	private String commentIp;

	/**
	 * 评论用户扩展信息 例如头像、住址、邮箱等
	 */
	private String peopleInfo;



	/**
	* 设置评论者id
	*/
	public void setPeopleId(Integer peopleId) {
	this.peopleId = peopleId;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	* 获取评论者id
	*/
	public Integer getPeopleId() {
	return this.peopleId;
	}

	/**
	* 设置业务Id
	*/
	public String getDataId() {
		return dataId;
	}

	/**
	* 获取业务Id
	*/
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	/**
	* 设置父评论id
	*/
	public void setCommentId(String commentId) {
	this.commentId = commentId;
	}

	/**
	* 获取父评论id
	*/
	public String getCommentId() {
	return this.commentId;
	}

	public String getDataTitle() {
		return dataTitle;
	}

	public void setDataTitle(String dataTitle) {
		this.dataTitle = dataTitle;
	}
	/**
	* 设置评论时间
	*/
	public void setCommentTime(Date commentTime) {
	this.commentTime = commentTime;
	}

	/**
	* 获取评论时间
	*/
	public Date getCommentTime() {
	return this.commentTime;
	}
	/**
	* 设置点赞字段
	*/
	public void setCommentLike(Integer commentLike) {
	this.commentLike = commentLike;
	}

	/**
	* 获取点赞字段
	*/
	public Integer getCommentLike() {
	return this.commentLike;
	}
	/**
	* 设置 1:显示 0:默认 审核不通过
	*/
	public void setCommentAudit(Boolean commentAudit) {
	this.commentAudit = commentAudit;
	}

	/**
	* 获取 1:显示 0:默认 审核不通过
	*/
	public Boolean getCommentAudit() {
	return this.commentAudit;
	}
	/**
	* 设置评价打分
	*/
	public void setCommentPoints(Integer commentPoints) {
	this.commentPoints = commentPoints;
	}

	/**
	* 获取评价打分
	*/
	public Integer getCommentPoints() {
	return this.commentPoints;
	}
	/**
	* 设置评论的内容
	*/
	public void setCommentContent(String commentContent) {
	this.commentContent = commentContent;
	}

	/**
	* 获取评论的内容
	*/
	public String getCommentContent() {
	return this.commentContent;
	}
	/**
	* 设置图片
	*/
	public void setCommentPicture(String commentPicture) {
	this.commentPicture = commentPicture;
	}

	/**
	* 获取图片
	*/
	public String getCommentPicture() {
	return this.commentPicture;
	}

	/**
	* 设置附件json
	*/
	public void setCommentFileJson(String commentFileJson) {
	this.commentFileJson = commentFileJson;
	}

	/**
	* 获取附件json
	*/
	public String getCommentFileJson() {
	return this.commentFileJson;
	}

	public Integer getCommentIsAnonymous() {
		return commentIsAnonymous;
	}

	public void setCommentIsAnonymous(Integer commentIsAnonymous) {
		this.commentIsAnonymous = commentIsAnonymous;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getTopId() {
		return topId;
	}

	public void setTopId(String topId) {
		this.topId = topId;
	}

	public String getCommentIp() {
		return commentIp;
	}

	public void setCommentIp(String commentIp) {
		this.commentIp = commentIp;
	}

	public String getPeopleInfo() {
		return peopleInfo;
	}

	public void setPeopleInfo(String peopleInfo) {
		this.peopleInfo = peopleInfo;
	}

	public String getPeopleName() {
		return peopleName;
	}

	public void setPeopleName(String peopleName) {
		this.peopleName = peopleName;
	}

	public String getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(String dataInfo) {
		this.dataInfo = dataInfo;
	}
}
