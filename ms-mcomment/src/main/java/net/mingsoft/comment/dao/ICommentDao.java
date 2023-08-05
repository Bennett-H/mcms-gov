/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.comment.dao;

import java.util.HashMap;
import java.util.List;
import net.mingsoft.comment.bean.CommentBean;
import org.apache.ibatis.annotations.Param;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.comment.entity.CommentEntity;

/**
 * 评论持久化层接
 * @author 铭飞开源团队
 * @date 2019年7月16日
 */
public interface ICommentDao extends IBaseDao<CommentEntity> {

	/**
	 * 查询评论
	 * @param comment - 评论类实体
	 * @return
	 */
	List<CommentEntity> query(CommentBean comment);




}
