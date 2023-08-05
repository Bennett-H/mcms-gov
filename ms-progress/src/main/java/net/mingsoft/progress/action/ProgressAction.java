/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.action;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.entity.ResultData;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.springframework.validation.BindingResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.ModelMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.*;
import net.mingsoft.progress.biz.IProgressBiz;
import net.mingsoft.progress.entity.ProgressEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;
/**
 * 进度表管理控制层
 * @author 铭飞科技
 * 创建日期：2021-3-18 11:50:14<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-进度模块接口"})
@Controller("progressProgressAction")
@RequestMapping("/${ms.manager.path}/progress/progress")
public class ProgressAction extends net.mingsoft.progress.action.BaseAction{


	/**
	 * 注入进度表业务层
	 */
	@Autowired
	private IProgressBiz progressBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("progress:progress:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/progress/progress/index";
	}

	/**
	 * 查询进度表列表
	 * 此处不可加权限标识，会导致有审批文章权限但是没有此处权限的用户无法正常使用功能
	 * @param progress 进度表实体
	 */
	@ApiOperation(value = "查询进度表列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "schemeId", value = "关联id", required =false,paramType="query"),
    	@ApiImplicitParam(name = "progressId", value = "父节点", required =false,paramType="query"),
    	@ApiImplicitParam(name = "progressNodeName", value = "进度节点名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "progressNumber", value = "进度数", required =false,paramType="query"),
    	@ApiImplicitParam(name = "progressStatus", value = "进度状态", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore ProgressEntity progress,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		LambdaQueryWrapper<ProgressEntity> wrapper = new QueryWrapper<>(progress).lambda().orderByAsc(ProgressEntity::getProgressNumber);
		List<ProgressEntity> progressList = progressBiz.list(wrapper);
		return ResultData.build().success(new EUListBean(progressList,(int)BasicUtil.endPage(progressList).getTotal()));
	}

	/**
	 * 返回编辑界面progress_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions(value = {"progress:scheme:save", "progress:scheme:update"}, logical = Logical.OR)
	public String form(@ModelAttribute ProgressEntity progress,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		if(progress.getId()!=null){
			ProgressEntity progressEntity = (ProgressEntity)progressBiz.getById(progress.getId());
			model.addAttribute("progressEntity",progressEntity);
		}
		return "/progress/progress/form";
	}
	/**
	 * 获取进度表
	 * @param progress 进度表实体
	 */
	@ApiOperation(value = "获取进度表列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore ProgressEntity progress,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(progress.getId()==null) {
			return ResultData.build().error();
		}
		ProgressEntity _progress = (ProgressEntity)progressBiz.getById(progress.getId());
		return ResultData.build().success(_progress);
	}

	@ApiOperation(value = "保存进度表列表接口")
	 @ApiImplicitParams({
		@ApiImplicitParam(name = "schemeId", value = "关联id", required =false,paramType="query"),
		@ApiImplicitParam(name = "progressId", value = "父节点", required =false,paramType="query"),
		@ApiImplicitParam(name = "progressNodeName", value = "进度节点名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "progressNumber", value = "进度数", required =true,paramType="query"),
		@ApiImplicitParam(name = "progressStatus", value = "进度状态", required =false,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})

	/**
	* 保存进度表
	* @param progress 进度表实体
	*/
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存进度表", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("progress:progress:save")
	public ResultData save(@ModelAttribute @ApiIgnore ProgressEntity progress, HttpServletResponse response, HttpServletRequest request) {
		if(!StringUtil.checkLength(progress.getSchemeId()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("scheme.id"), "0", "11"));
		}
		if(!StringUtil.checkLength(progress.getProgressNodeName()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("progress.node.name"), "0", "11"));
		}
		//验证节点名字是否合法
		if (StringUtils.isBlank(progress.getProgressNodeName())){
			return ResultData.build().error(this.getResString("error.approval.node.empty"));
		}
		//验证json的值是否合法
		if(StringUtils.isBlank(progress.getProgressStatus())){
			return ResultData.build().error(getResString("err.empty", this.getResString("progress.status.json")));
		}
		//验证进度数的值是否合法
		if(StringUtil.isBlank(progress.getProgressNumber())){
			return ResultData.build().error(getResString("err.empty", this.getResString("progress.number")));
		}
		if(!StringUtil.checkLength(progress.getProgressNumber()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("progress.number"), "0", "11"));
		}
		this.progressBiz.saveProgress(progress);
		return ResultData.build().success(progress);
	}

	/**
	 * @param progress 进度表实体
	 */
	@ApiOperation(value = "批量删除进度表列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除进度表", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("progress:progress:del")
	public ResultData delete(@RequestBody List<ProgressEntity> progress,HttpServletResponse response, HttpServletRequest request) {
		for (ProgressEntity progressEntity : progress) {
			//查询被删除的子叶节点
			List<ProgressEntity> childrenList = progressBiz.queryChildren(progressEntity);
			//获取子叶节点id
			if (CollUtil.isNotEmpty(childrenList)) {
				List<String> ids = childrenList.stream().map(ProgressEntity::getId).collect(Collectors.toList());
				ids.add(progressEntity.getId());
				progressBiz.removeByIds(ids);
			}else {
				progressBiz.removeById(progressEntity.getId());
			}

			//获取当前节点父节点
			ProgressEntity parentProgress = new ProgressEntity();
			parentProgress.setId(progressEntity.getProgressId());
			//查询父节点是否含有子节点
			List<ProgressEntity> progressEntities = progressBiz.queryChildren(parentProgress);
			//如果有子节点则leaf为false， 否则为true
			if(CollUtil.isEmpty(progressEntities)){
				parentProgress.setLeaf(true);
				progressBiz.updateById(parentProgress);
			}
		}
		return ResultData.build().success();
	}

	/**
	*	更新进度表列表
	* @param progress 进度表实体
	*/
	 @ApiOperation(value = "更新进度表列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
		@ApiImplicitParam(name = "schemeId", value = "关联id", required =false,paramType="query"),
		@ApiImplicitParam(name = "progressId", value = "父节点", required =false,paramType="query"),
		@ApiImplicitParam(name = "progressNodeName", value = "进度节点名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "progressNumber", value = "进度数", required =true,paramType="query"),
		@ApiImplicitParam(name = "progressStatus", value = "进度状态", required =false,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新进度表", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("progress:progress:update")
	public ResultData update(@ModelAttribute @ApiIgnore ProgressEntity progress, HttpServletResponse response,
			HttpServletRequest request) {
		if(!StringUtil.checkLength(progress.getSchemeId()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("scheme.id"), "0", "11"));
		}
		if(!StringUtil.checkLength(progress.getProgressNodeName()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("progress.node.name"), "0", "11"));
		}
		//验证进度数的值是否合法
		if(StringUtil.isBlank(progress.getProgressNumber())){
			return ResultData.build().error(getResString("err.empty", this.getResString("progress.number")));
		}
		if(!StringUtil.checkLength(progress.getProgressNumber()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("progress.number"), "0", "11"));
		}
		this.progressBiz.updateProgress(progress);
		return ResultData.build().success(progress);
	}

}
