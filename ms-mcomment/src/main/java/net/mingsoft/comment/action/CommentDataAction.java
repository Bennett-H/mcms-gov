/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.comment.action;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.base.util.BundleUtil;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.CookieConstEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.comment.bean.CommentBean;
import net.mingsoft.comment.biz.ICommentBiz;
import net.mingsoft.comment.entity.CommentEntity;
import net.mingsoft.mdiy.entity.DictEntity;
import net.mingsoft.mdiy.util.DictUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Api(tags = {"后端-评论类型接口"})
@Controller
@RequestMapping("/${ms.manager.path}/comment/data")
public class CommentDataAction extends BaseAction{

	@Autowired
	private ICommentBiz commentBiz;


	@ApiIgnore
	@GetMapping("/index")
	public String index(HttpServletResponse response, HttpServletRequest request){
		// 权限验证
		if(!getPermissions("comment:comment:view","comment:dataData:" + request.getParameter("dataType") + ":view")){
			throw new UnauthorizedException();
		}
		return "comment/data/index";
	}
	/**
	 * 返回回复界面reply
	 *
	 * @param response
	 * @param request
	 * @return
	 */
	@ApiIgnore
	@GetMapping("/reply")
	public String reply(HttpServletResponse response, HttpServletRequest request) {
		// 权限验证
		if(!getPermissions("comment:comment:view","comment:dataData:" + request.getParameter("dataType") + ":reply")){
			throw new UnauthorizedException();
		}
		return "/comment/data/reply";
	}


	/**
	 * 查询评论表列表
	 *
	 * @param comment 评论表实体
	 */
	@ApiOperation(value = "查询评论表列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "peopleName", value = "评论者昵称", required = false, paramType = "query"),
			@ApiImplicitParam(name = "peopleId", value = "评论者编号", required = false, paramType = "query"),
			@ApiImplicitParam(name = "commentId", value = "评论id", required = false, paramType = "query"),
			@ApiImplicitParam(name = "commentAudit", value = "1默认 显示 0:审核不通过", required = false, paramType = "query"),
			@ApiImplicitParam(name = "dataTitle", value = "业务名称", required = false, paramType = "query"),
			@ApiImplicitParam(name = "dataType", value = "评论类型", required = true, paramType = "query"),
			@ApiImplicitParam(name = "commentStartTime", value = "开始时间", required = false, paramType = "query"),
			@ApiImplicitParam(name = "commentEndTime", value = "结束时间", required = false, paramType = "query"),
	})
	@PostMapping(value ="/list")
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore CommentBean comment, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
		// 权限验证
		if(!getPermissions("comment:comment:view","comment:dataData:" + comment.getDataType() + ":view")){
			throw new UnauthorizedException();
		}
		// 判断评论类型
		DictEntity dict = DictUtil.get("评论类型", comment.getDataType(), null);
		if (dict == null) {
			throw new BusinessException(BundleUtil.getBaseString("err.error",
					BundleUtil.getString(net.mingsoft.comment.constant.Const.RESOURCES, "comment.type")));
		}
		comment.setDataType(dict.getDictValue());
		// 开始时间
		String startTime = comment.getCommentStartTime();
		// 结束时间
		String endTime = comment.getCommentEndTime();

		LambdaQueryWrapper<CommentEntity> wrapper = new LambdaQueryWrapper<>(comment);
		wrapper.ge(StringUtils.isNotBlank(startTime), CommentEntity::getCommentTime, DateUtil.format(DateUtil.parse(startTime), "yyyy-MM-dd HH:mm:ss"))
				.or().le(StringUtils.isNotBlank(endTime), CommentEntity::getCommentTime, DateUtil.format(DateUtil.parse(endTime), "yyyy-MM-dd HH:mm:ss"));

		// 判断没有topId才走commentId查询
		if (StringUtils.isEmpty(comment.getTopId())) {
			wrapper.isNull(StringUtils.isBlank(comment.getCommentId()), CommentEntity::getCommentId)
					.eq(StringUtils.isNotBlank(comment.getCommentId()), CommentEntity::getCommentId, comment.getCommentId());
		}
		// 根据时间排序
		wrapper.orderByDesc(CommentEntity::getCommentTime);

		BasicUtil.startPage();
		List<CommentEntity> commentEntityList = commentBiz.list(wrapper);
		EUListBean list = new EUListBean(commentEntityList, (int) BasicUtil.endPage(commentEntityList).getTotal());
		return ResultData.build().success(list);
	}

	@ApiOperation(value = "批量删除评论表")
	@LogAnn(title = "批量删除评论表", businessType = BusinessTypeEnum.DELETE)
	@PostMapping("/delete")
	@ResponseBody
	public ResultData delete(@RequestBody List<CommentEntity> comments, HttpServletResponse response, HttpServletRequest request) {
		// 权限验证
		if(!getPermissions("comment:comment:view","comment:dataData:" + comments.get(0).getDataType() + ":del")){
			throw new UnauthorizedException();
		}
		// 这里会通过主外键，级联删除子评论数据
		if (CollUtil.isEmpty(comments)){
			return ResultData.build().error(getResString("err.not.exist",this.getResString("comment")));
		}
		String[] ids = new String[comments.size()];
		for (int i = 0; i < comments.size(); i++) {
			String id = comments.get(i).getId();
			// 传递的是id
			if (StringUtils.isNotBlank(id)){
				ids[i] = id;
			}
		}
		if (ids.length != 0){
			commentBiz.delete(ids);
		}
		return ResultData.build().success();
	}

	/**
	 * 回复
	 *
	 * @param comment
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "发布评论")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dataId", value = "数据Id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "commentId", value = "父评论id", required = false, paramType = "query"),
			@ApiImplicitParam(name = "commentLike", value = "点赞字段", dataType = "Integer", required = false, paramType = "query"),
			@ApiImplicitParam(name = "dataType", value = "评论类型", required = true, paramType = "query"),
			@ApiImplicitParam(name = "commentPoints", value = "评价打分", required = false, paramType = "query"),
			@ApiImplicitParam(name = "commentContent", value = "评论的内容", required = true, paramType = "query"),
			@ApiImplicitParam(name = "commentPicture", value = "图片", required = false, paramType = "query"),
			@ApiImplicitParam(name = "commentFileJson", value = "附件json", required = false, paramType = "query"),
	})
	@LogAnn(title = "发布评论", businessType = BusinessTypeEnum.INSERT)
	@PostMapping("/save")
	@ResponseBody
	public ResultData save(@ModelAttribute @ApiIgnore CommentEntity comment, HttpServletRequest request, HttpServletResponse response) {
		// 权限验证
		if(!getPermissions("comment:comment:view","comment:dataData:" + comment.getDataType() + ":reply")){
			throw new UnauthorizedException();
		}
		if (comment == null) {
			return ResultData.build().error(getResString("err.empty",this.getResString("comment")));
		}
		// 后台评论 获取管理员信息
		ManagerEntity manager = BasicUtil.getManager();
		comment.setPeopleName(manager.getManagerName());
		commentBiz.saveComment(comment);
		return ResultData.build().success(comment);
	}

	/**
	 * 更新评论表信息评论表
	 * 用于审核
	 * @param comment
	 * @param response
	 * @param request
	 */
	@ApiOperation(value = "更新评论表信息")
	@LogAnn(title = "更新评论表信息", businessType = BusinessTypeEnum.UPDATE)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "评论Id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "commentAudit", value = "1默认 显示 0:审核不通过", required = true, paramType = "query")
	})
	@PostMapping("/updateComment")
	@ResponseBody
	public ResultData updateComment(@RequestBody CommentEntity comment, HttpServletResponse response,
									HttpServletRequest request) {
		// 权限验证
		if(!getPermissions("comment:comment:view","comment:dataData:" + comment.getDataType() + ":audit")){
			throw new UnauthorizedException();
		}
		if (StringUtils.isBlank(comment.getId())) {
			return ResultData.build().error(getResString("err.empty", this.getResString("comment")));
		}
		CommentEntity commentEntity = commentBiz.getById(comment.getId());
		if (commentEntity==null){
			return ResultData.build().error(getResString("err.empty", this.getResString("comment")));
		}
		commentEntity.setCommentAudit(comment.getCommentAudit());
		commentBiz.updateById(commentEntity);
		return ResultData.build().success();
	}


	@ApiOperation(value = "评论回复")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dataType", value = "评论类型", required = true, paramType = "query"),
			@ApiImplicitParam(name = "commentId", value = "父评论id", required = false, paramType = "query"),
			@ApiImplicitParam(name = "commentContent", value = "评论的内容", required = true, paramType = "query"),
			@ApiImplicitParam(name = "commentPoints", value = "评价打分", required = false, paramType = "query"),
			@ApiImplicitParam(name = "commentPicture", value = "图片", required = false, paramType = "query"),
			@ApiImplicitParam(name = "commentFileJson", value = "附件json", required = false, paramType = "query"),
	})
	@LogAnn(title = "评论回复", businessType = BusinessTypeEnum.UPDATE)
	@PostMapping("/reply")
	@ResponseBody
	public ResultData reply(@ModelAttribute @ApiIgnore CommentEntity comment, HttpServletRequest request, HttpServletResponse response) {
		// 权限验证
		if(!getPermissions("comment:comment:view","comment:dataData:" + comment.getDataType() + ":reply")){
			throw new UnauthorizedException();
		}
		//根据评论id查找评论实体
		CommentEntity parentComment = (CommentEntity) commentBiz.getById(comment.getCommentId());
		if (parentComment != null) {
			comment.setCommentId(parentComment.getId());
			comment.setDataId(parentComment.getDataId());
			comment.setDataTitle(parentComment.getDataTitle());
			comment.setPeopleId(0);
			comment.setPeopleName(BasicUtil.getManager().getManagerName());
			this.commentBiz.saveComment(comment);
			// 获取cookie
			String cookie = BasicUtil.getCookie(CookieConstEnum.BACK_COOKIE);
			return ResultData.build().success(String.valueOf(cookie));
		}
		return ResultData.build().error(getResString("err.empty",this.getResString("comment")));
	}

	/**
	 * 根据id获取评论详情及评论人信息
	 * @param id
	 * @return
	 */
	@ApiOperation("获取评论信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id",value = "主键id",required = true,paramType = "query")
	})
	@GetMapping("/getComment")
	@ResponseBody
	public ResultData getComment(String id){
		if (StringUtils.isBlank(id)){
			return ResultData.build().error(getResString("err.not.exist",this.getResString("comment")));
		}
		CommentEntity commentEntity = commentBiz.getById(id);
		// 权限验证,数据库存储的是字典value值，权限由字典label组成
		String label = DictUtil.getDictLabel("评论类型", commentEntity.getDataType());
		if(!getPermissions("comment:comment:view","comment:dataData:" + label + ":view")){
			throw new UnauthorizedException();
		}
		return ResultData.build().success(commentEntity);
	}
}
