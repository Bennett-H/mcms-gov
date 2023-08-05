/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.comment.aop;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.comment.biz.ICommentBiz;
import net.mingsoft.comment.biz.ICommentsLogBiz;
import net.mingsoft.comment.entity.CommentEntity;
import net.mingsoft.comment.entity.CommentsLogEntity;
import net.mingsoft.mdiy.util.DictUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评论插件
 * @author 铭飞开源团队
 * @date 2019年7月16日
 */
@Component
@Aspect
public class CommentAop extends BaseAop {

	@Resource(name = "commentBiz")
	private ICommentBiz commentBiz;

	@Resource(name = "commentcommentsLogBizImpl")
	private ICommentsLogBiz commentsLogBiz;

	@Pointcut("execution(* net.mingsoft.comment.biz.impl.CommentBizImpl.saveComment(..))")
	public void save() {
	}

	/**
	 * 评论时需要更新基础信息的评论数量
	 * @param jp
	 * @return
	 * @throws Throwable
	 */
	@After("save()")
	public Object save(JoinPoint jp) throws Throwable {
		//获取文章 商品 id
		String dataId = BasicUtil.getString("dataId");
		//业务类型
		String dataType = BasicUtil.getString("dataType");
		dataType = DictUtil.getDictValue("评论类型", dataType);
		if (StringUtils.isBlank(dataType)){
			return ResultData.build().error();
		}
		//查询评论记录是否有该评论
		CommentsLogEntity data = commentsLogBiz.getOne(new QueryWrapper<CommentsLogEntity>().eq("data_id",dataId).eq("data_type",dataType));
		//没有该文章记录则新增 否则增加评论总数
		if(ObjectUtils.isEmpty(data)){
			//获取该文章 商品总记录
			List list = commentBiz.list(new QueryWrapper<CommentEntity>().eq("data_id",dataId).eq("data_type",dataType));
			CommentsLogEntity commentsLogEntity = new CommentsLogEntity();
			commentsLogEntity.setDataId(dataId);
			commentsLogEntity.setDataType(dataType);
			commentsLogEntity.setCommentsCount((int)list.stream().count());
			commentsLogBiz.save(commentsLogEntity);
		}else {
			//评论数+1
			data.setCommentsCount(data.getCommentsCount()+1);
			commentsLogBiz.updateById(data);
		}
		return ResultData.build().success();
	}
}
