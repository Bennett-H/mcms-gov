/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.cms.action.people;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.approval.biz.IConfigBiz;
import net.mingsoft.approval.constant.e.ProgressStatusEnum;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.OperatorTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.cms.action.BaseAction;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.datascope.utils.DataScopeUtil;
import net.mingsoft.elasticsearch.annotation.ESDelete;
import net.mingsoft.people.constant.e.SessionConstEnum;
import net.mingsoft.people.entity.PeopleEntity;
import net.mingsoft.progress.biz.IProgressBiz;
import net.mingsoft.progress.entity.ProgressEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 文章管理控制层
 * @author 铭飞开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：提供会员的curd接口<br/>
 */
@Api(tags = {"前端-用户-文章接口"})
@Controller("peopleCmsContentAction")
@RequestMapping("/people/cms/content")
public class ContentAction extends BaseAction {


	/**
	 * 注入文章业务层
	 */
	@Autowired
	private IContentBiz contentBiz;

	@Autowired
	private IConfigBiz configBiz;

	@Autowired
	private IProgressBiz progressBiz;

	/**
	 * 查询文章列表
	 * @param content 文章实体
	 */
	@ApiOperation(value = "查询文章列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "contentTitle", value = "文章标题", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryId", value = "所属栏目", required =false,paramType="query"),
    	@ApiImplicitParam(name = "contentType", value = "文章类型", required =false,paramType="query"),
    	@ApiImplicitParam(name = "contentDisplay", value = "是否显示", required =false,paramType="query"),
    	@ApiImplicitParam(name = "contentAuthor", value = "文章作者", required =false,paramType="query"),
    	@ApiImplicitParam(name = "contentSource", value = "文章来源", required =false,paramType="query"),
    	@ApiImplicitParam(name = "contentDatetime", value = "发布时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "contentSort", value = "自定义顺序", required =false,paramType="query"),
    	@ApiImplicitParam(name = "contentDescription", value = "描述", required =false,paramType="query"),
    	@ApiImplicitParam(name = "contentKeyword", value = "关键字", required =false,paramType="query"),
    	@ApiImplicitParam(name = "contentDetails", value = "文章内容", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@RequestMapping(value = "/list", method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore ContentEntity content, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
		PeopleEntity peopleEntity = (PeopleEntity) BasicUtil.getSession(SessionConstEnum.PEOPLE_SESSION);
		BasicUtil.startPage();
		// 创建人“u_用户编号”
		DataScopeUtil.start("u_"+peopleEntity.getId(),true);
		QueryWrapper<ContentEntity> queryWrapper = new QueryWrapper<>(content);
		queryWrapper.in("progress_status", Arrays.asList(ProgressStatusEnum.DRAFT.toString(), ProgressStatusEnum.APPROVED.toString()));
		List contentList = contentBiz.list(queryWrapper);
		return ResultData.build().success(new EUListBean(contentList,(int) BasicUtil.endPage(contentList).getTotal()));
	}

	/**
	 * 查询文章待审列表
	 * @param content 文章实体
	 */
	@ApiOperation(value = "查询文章待审列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "contentTitle", value = "文章标题", required =false,paramType="query"),
			@ApiImplicitParam(name = "categoryId", value = "所属栏目", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentType", value = "文章类型", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentDisplay", value = "是否显示", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentAuthor", value = "文章作者", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentSource", value = "文章来源", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentDatetime", value = "发布时间", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentSort", value = "自定义顺序", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentDescription", value = "描述", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentKeyword", value = "关键字", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentDetails", value = "文章内容", required =false,paramType="query"),
			@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
			@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping(value = "/auditList")
	@ResponseBody
	public ResultData auditList(@ModelAttribute @ApiIgnore ContentEntity content, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
		PeopleEntity peopleEntity = (PeopleEntity) BasicUtil.getSession(SessionConstEnum.PEOPLE_SESSION);
		List<ProgressEntity> progressList = progressBiz.queryProgress("文章审核");
		List<Object> statusList = new ArrayList<>();
		Map<String, Object> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (ProgressEntity progress : progressList) {
			map.putAll(JSONUtil.toBean(progress.getProgressStatus(), Map.class));
			if (map.get("ADOPT").toString().equals(ProgressStatusEnum.APPROVED.toString())) {
				// 跳过终审通过
				continue;
			}
			statusList.add(map.get("ADOPT").toString());
		}
		statusList.add(ProgressStatusEnum.REVIEW.toString());
		BasicUtil.startPage();
		// 创建人“u_用户编号”
		DataScopeUtil.start("u_"+peopleEntity.getId(),true);
		QueryWrapper<ContentEntity> queryWrapper = new QueryWrapper<>(content);
		queryWrapper.in("progress_status", statusList);
		List<ContentEntity> contentList = contentBiz.list(queryWrapper);
		return ResultData.build().success(new EUListBean(contentList,(int) BasicUtil.endPage(contentList).getTotal()));
	}

	/**
	 * 查询文章退回列表
	 * @param content 文章实体
	 */
	@ApiOperation(value = "查询文章退回列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "contentTitle", value = "文章标题", required =false,paramType="query"),
			@ApiImplicitParam(name = "categoryId", value = "所属栏目", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentType", value = "文章类型", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentDisplay", value = "是否显示", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentAuthor", value = "文章作者", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentSource", value = "文章来源", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentDatetime", value = "发布时间", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentSort", value = "自定义顺序", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentDescription", value = "描述", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentKeyword", value = "关键字", required =false,paramType="query"),
			@ApiImplicitParam(name = "contentDetails", value = "文章内容", required =false,paramType="query"),
			@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
			@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping("/rejectList")
	@ResponseBody
	public ResultData rejectList(@ModelAttribute @ApiIgnore ContentEntity content, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
		PeopleEntity peopleEntity = (PeopleEntity) BasicUtil.getSession(SessionConstEnum.PEOPLE_SESSION);
		List<ProgressEntity> progressList = progressBiz.queryProgress("文章审核");
		List<Object> statusList = new ArrayList<>();
		Map<String, Object> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (ProgressEntity progress : progressList) {
			map.putAll(JSONUtil.toBean(progress.getProgressStatus(), Map.class));
			statusList.add(map.get("REJECT").toString());
		}
		BasicUtil.startPage();
		// 创建人“u_用户编号”
		DataScopeUtil.start("u_"+peopleEntity.getId(),true);
		QueryWrapper<ContentEntity> queryWrapper = new QueryWrapper<>(content);
		queryWrapper.in("progress_status", statusList);
		List contentList = contentBiz.list(queryWrapper);
		return ResultData.build().success(new EUListBean(contentList,(int) BasicUtil.endPage(contentList).getTotal()));
	}
	/**
	 * 获取文章
	 * @param content 文章实体
	 */
	@ApiOperation(value = "获取文章列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore ContentEntity content, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model){
		if(content.getId()==null) {
			return ResultData.build().error("id不能为空!");
		}
		PeopleEntity peopleEntity = (PeopleEntity) BasicUtil.getSession(SessionConstEnum.PEOPLE_SESSION);
		if(this.permissionValidation(content.getId(),peopleEntity.getId())){
			return ResultData.build().error(getResString("err.not.permissions"));
		}
		ContentEntity _content = contentBiz.getById(content.getId());
		return ResultData.build().success(_content);
	}

	/**
	 * 保存文章
	 * @param content 文章实体
	 */
	@ApiOperation(value = "保存文章列表接口")
	 @ApiImplicitParams({
    	@ApiImplicitParam(name = "contentTitle", value = "文章标题", required =true,paramType="query"),
		@ApiImplicitParam(name = "categoryId", value = "所属栏目", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentType", value = "文章类型", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentDisplay", value = "是否显示", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentAuthor", value = "文章作者", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentSource", value = "文章来源", required =false,paramType="query"),
    	@ApiImplicitParam(name = "contentDatetime", value = "发布时间", required =true,paramType="query"),
		@ApiImplicitParam(name = "contentSort", value = "自定义顺序", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentImg", value = "文章缩略图", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentDescription", value = "描述", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentKeyword", value = "关键字", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentDetails", value = "文章内容", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentUrl", value = "文章跳转链接地址", required =false,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@LogAnn(title = "会员保存稿件", operatorType = OperatorTypeEnum.PEOPLE, businessType = BusinessTypeEnum.CONTENT_INSERT)
	@PostMapping("/save")
	@ResponseBody
	public ResultData save(@ModelAttribute @ApiIgnore ContentEntity content, HttpServletResponse response, HttpServletRequest request) {
		//验证文章标题的值是否合法
		if(StringUtil.isBlank(content.getContentTitle())){
			return ResultData.build().error(getResString("err.empty", this.getResString("content.title")));
		}
		if(!StringUtil.checkLength(content.getContentTitle()+"", 0, 200)){
			return ResultData.build().error(getResString("err.length", this.getResString("content.title"), "0", "200"));
		}
		if(!StringUtil.checkLength(content.getContentAuthor()+"", 0, 200)){
			return ResultData.build().error(getResString("err.length", this.getResString("content.author"), "0", "200"));
		}
		if(!StringUtil.checkLength(content.getContentSource()+"", 0, 200)){
			return ResultData.build().error(getResString("err.length", this.getResString("content.source"), "0", "200"));
		}
		//验证发布时间的值是否合法
		if(StringUtil.isBlank(content.getContentDatetime())){
			return ResultData.build().error(getResString("err.empty", this.getResString("content.datetime")));
		}
		PeopleEntity peopleEntity = (PeopleEntity) BasicUtil.getSession(SessionConstEnum.PEOPLE_SESSION);
		BasicUtil.startPage();
		// 创建人“u_用户编号”
		DataScopeUtil.start("u_"+peopleEntity.getId());
		// 创建人“u_用户编号”
		content.setCreateBy("u_"+peopleEntity.getId());
		content.setCreateDate(new Date());
		content.setProgressStatus(ProgressStatusEnum.DRAFT.toString());
		contentBiz.save(content);
		return ResultData.build().success(content);
	}

	/**
	 * @param contents 文章实体
	 */
	@ApiOperation(value = "批量删除文章列表接口")
	@PostMapping("/delete")
	@LogAnn(title = "会员提交稿件审核", operatorType = OperatorTypeEnum.PEOPLE, businessType = BusinessTypeEnum.CONTENT_DELETE)
	@ESDelete
	@ResponseBody
	public ResultData delete(@RequestBody List<ContentEntity> contents, HttpServletResponse response, HttpServletRequest request) {
		PeopleEntity peopleEntity = (PeopleEntity) BasicUtil.getSession(SessionConstEnum.PEOPLE_SESSION);
		List<String> ids = new ArrayList<>();
		for (ContentEntity content : contents) {
			if (this.permissionValidation(content.getId(), peopleEntity.getId())) {
				return ResultData.build().error(getResString("err.not.permissions"));
			}
			ids.add(content.getId());
		}
		contentBiz.removeByIds(ids);
		return ResultData.build().success();
	}
	/**
	*	更新文章列表
	* @param content 文章实体
	*/
	 @ApiOperation(value = "更新文章列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
    	@ApiImplicitParam(name = "contentTitle", value = "文章标题", required =true,paramType="query"),
		@ApiImplicitParam(name = "categoryId", value = "所属栏目", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentType", value = "文章类型", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentDisplay", value = "是否显示", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentAuthor", value = "文章作者", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentSource", value = "文章来源", required =false,paramType="query"),
    	@ApiImplicitParam(name = "contentDatetime", value = "发布时间", required =true,paramType="query"),
		@ApiImplicitParam(name = "contentSort", value = "自定义顺序", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentImg", value = "文章缩略图", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentDescription", value = "描述", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentKeyword", value = "关键字", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentDetails", value = "文章内容", required =false,paramType="query"),
		@ApiImplicitParam(name = "contentUrl", value = "文章跳转链接地址", required =false,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@LogAnn(title = "会员更新稿件", operatorType = OperatorTypeEnum.PEOPLE, businessType = BusinessTypeEnum.CONTENT_UPDATE)
	@PostMapping("/update")
	@ResponseBody
	public ResultData update(@ModelAttribute @ApiIgnore ContentEntity content, HttpServletResponse response,
                             HttpServletRequest request) {
		//验证文章标题的值是否合法
		if(StringUtil.isBlank(content.getContentTitle())){
			return ResultData.build().error(getResString("err.empty", this.getResString("content.title")));
		}
		if(!StringUtil.checkLength(content.getContentTitle()+"", 0, 200)){
			return ResultData.build().error(getResString("err.length", this.getResString("content.title"), "0", "200"));
		}
		if(!StringUtil.checkLength(content.getContentAuthor()+"", 0, 200)){
			return ResultData.build().error(getResString("err.length", this.getResString("content.author"), "0", "200"));
		}
		if(!StringUtil.checkLength(content.getContentSource()+"", 0, 200)){
			return ResultData.build().error(getResString("err.length", this.getResString("content.source"), "0", "200"));
		}
		//验证发布时间的值是否合法
		if(StringUtil.isBlank(content.getContentDatetime())){
			return ResultData.build().error(getResString("err.empty", this.getResString("content.datetime")));
		}
		 PeopleEntity peopleEntity = (PeopleEntity) BasicUtil.getSession(SessionConstEnum.PEOPLE_SESSION);
		 if(this.permissionValidation(content.getId(),peopleEntity.getId())){
			 return ResultData.build().error(getResString("err.not.permissions"));
		 }
		 BasicUtil.startPage();
		// 创建人“u_用户编号”
		DataScopeUtil.start("u_"+peopleEntity.getId());
		// 创建人“u_用户编号”
		content.setUpdateBy("u_"+peopleEntity.getId());
		content.setUpdateDate(new Date());
		 content.setProgressStatus(ProgressStatusEnum.DRAFT.toString());
		contentBiz.updateById(content);
		return ResultData.build().success(content);
	}

	/**
	 * 验证用户是否有权限操作这篇文章
	 * @param dataId 文章编号
	 * @param peopleId 用户编号
	 * @return true 有权限；false非法操作
	 */
	private boolean permissionValidation(String dataId, String peopleId){
		ContentEntity contentEntity = new ContentEntity();
		contentEntity.setId(dataId);
		contentEntity.setCreateBy("u_"+peopleId);
		ContentEntity _contentEntity = contentBiz.getOne(new QueryWrapper<>(contentEntity));
		return _contentEntity==null;
	}


	/**
	 * 提交审核
	 */
	@ApiOperation(value = "提交审核接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dataId", value = "文章ID", required = true, paramType = "query"),
			@ApiImplicitParam(name = "schemeName", value = "方案名称", required = true, paramType = "query"),
	})
	@PostMapping("/submit")
	@LogAnn(title = "会员提交稿件审核",operatorType = OperatorTypeEnum.PEOPLE, businessType = BusinessTypeEnum.CONTENT_SUBMIT)
	@ResponseBody
	public ResultData submit(String schemeName, String dataId, HttpServletResponse response, HttpServletRequest request) {
		if(StringUtils.isEmpty(schemeName)){
			return ResultData.build().error(this.getResString("id"));
		}
		if(StringUtils.isEmpty(dataId)){
			return ResultData.build().error(getResString("err.empty", this.getResString("pl.status")));
		}
		PeopleEntity peopleEntity = (PeopleEntity) BasicUtil.getSession(SessionConstEnum.PEOPLE_SESSION);
		if(this.permissionValidation(dataId,peopleEntity.getId())){
			return ResultData.build().error(getResString("err.not.permissions"));
		}
		boolean flag = configBiz.submit(schemeName,dataId,"u_"+peopleEntity.getId());
		if(flag){
			return ResultData.build().success(getResString("submit.success"));
		}else {
			return ResultData.build().error(getResString("submit.fail"));
		}
	}

}
