/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.comment.action.web;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.comment.bean.CommentBean;
import net.mingsoft.comment.biz.ICommentBiz;
import net.mingsoft.comment.biz.ICommentsLogBiz;
import net.mingsoft.comment.entity.CommentEntity;
import net.mingsoft.comment.entity.CommentsLogEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.mdiy.util.DictUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 前端评论通用接口，主要都是针对一条数据的评论
 * @author 铭飞开源团队
 * @date 2019年7月16日
 */
@Api(tags={"前端-评论模块接口"})
@Controller("webComment")
@RequestMapping("/comment")
public class CommentAction extends net.mingsoft.comment.action.BaseAction {



	/**
	 * 评论业务层
	 */
	@Autowired
	private ICommentBiz commentBiz;

	/**
	 * 获取具体一条数据的评论列表
	 */
	@Autowired
	private ICommentsLogBiz commentsLogBiz;

	/**
	 * 获取具体一条数据的评论列表,主要场景如：文章详情页面显示评论列表
	 * @param comment
	 * @return
	 */
	@ApiOperation(value = "获取具体一条数据的评论列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dataId", value = "数据Id", required =true,paramType="query"),
			@ApiImplicitParam(name = "dataType", value = "评论类型", required =true,paramType="query"),
			@ApiImplicitParam(name = "pageSize", value = "一页显示数量", required =false,paramType="query"),
			@ApiImplicitParam(name = "pageNo", value = "当前页面", required =false,paramType="query")
    })
	@RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore CommentBean comment) {
		// 数据id为空
		if (StringUtils.isBlank(comment.getDataId())){
			throw new BusinessException(this.getResString("err.empty",getResString("data.id")));
		}

		//只显示审核通过的评论
		comment.setCommentAudit(true);
		// 确保第一次只查询出父评论
		List<CommentEntity> list = commentBiz.query(comment);
		return ResultData.build().success().data(new EUListBean(list,(int)BasicUtil.endPage(list).getTotal()));
	}


	/**
	 * 游客模式发表评论接口
	 * @param comment 评论实体
	 * @return 携带评论数据的相应信息
	 */
	@ApiOperation(value = "保存评论接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dataId", value = "数据id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "dataType", value = "业务类型", required = true, paramType = "query"),
			@ApiImplicitParam(name = "commentId", value = "父评论id", required = false, paramType = "query"),
			@ApiImplicitParam(name = "commentPoints", value = "评价打分", required = false, paramType = "query"),
			@ApiImplicitParam(name = "commentContent", value = "评论的内容", required = true, paramType = "query"),
			@ApiImplicitParam(name = "commentPicture", value = "图片", required = false, paramType = "query"),
			@ApiImplicitParam(name = "commentFileJson", value = "附件json", required = false, paramType = "query"),
			@ApiImplicitParam(name = "rand_code", value = "游客模式必须传验证码", required = true, paramType = "query")
	})
	@PostMapping("/save")
	@ResponseBody
	public ResultData save(@ModelAttribute @ApiIgnore CommentEntity comment) {

		// 验证码验证 验证码为null 或 验证码不相等
		if (!this.checkRandCode("rand_code")) {
			return ResultData.build().error( getResString("err.error", this.getResString("rand.code")));
		}

		// 游客评论
		if (!ConfigUtil.getBoolean("评论配置","enableVisitor")){
			return ResultData.build().error(getResString("fail",this.getResString("comment")));
		}


		comment.setPeopleId(-1);  //-1 游客
		commentBiz.saveComment(comment);
		return ResultData.build().success(JSONUtil.toJsonStr(comment));
	}


	/**
	 * 获取具体一条数据的评论数,使用场景如：文章详情页面显示评论总数
	 * @param dataId 业务id
	 * @param dataType 评论类型
	 * @return 一条数据的评论数
	 */
	@ApiOperation(value = "获取具体一条数据的评论数")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dataId", value = "数据Id", required =true,paramType="query"),
			@ApiImplicitParam(name = "dataType", value = "评论类型", required =true,paramType="query")
	})
	@GetMapping("/count")
	@ResponseBody
	public ResultData count(String dataId,String dataType){
		if (StringUtils.isBlank(dataId)){
			return ResultData.build().error(getResString("err.error",this.getResString("data.id")));
		}
		dataType = DictUtil.getDictValue("评论类型", dataType);
		if (StringUtils.isBlank(dataType)){
			return ResultData.build().error(getResString("err.error",this.getResString("comment.type")));
		}
		LambdaQueryWrapper<CommentsLogEntity> wrapper = new LambdaQueryWrapper();
		wrapper.eq(StrUtil.isNotBlank(dataId),CommentsLogEntity::getDataId,dataId)
				.eq(StrUtil.isNotBlank(dataType),CommentsLogEntity::getDataType,dataType);
		CommentsLogEntity commentsLogEntity = commentsLogBiz.getOne(wrapper, true);
		Integer commentCount = 0;
		if (commentsLogEntity!=null) {
			commentCount = commentsLogEntity.getCommentsCount();
		}

		return ResultData.build().success(commentCount);
	}

}
