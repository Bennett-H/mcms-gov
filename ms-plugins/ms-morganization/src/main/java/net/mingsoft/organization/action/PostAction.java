/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.action;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.organization.biz.IPostBiz;
import net.mingsoft.organization.entity.PostEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import cn.hutool.core.lang.UUID;
/**
 * 岗位管理管理控制层
 * @author 铭飞开源团队
 * 创建日期：2020-1-6 14:53:26<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-组织机构模块接口"})
@Controller("organizationPostAction")
@RequestMapping("/${ms.manager.path}/organization/post")
public class PostAction extends BaseAction{


	/**
	 * 注入岗位管理业务层
	 */
	@Autowired
	private IPostBiz postBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("organization:post:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/organization/post/index";
	}

	/**
	 * 查询岗位管理列表
	 * @param post 岗位管理实体
	 */
	@ApiOperation(value = "查询岗位管理列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "postName", value = "岗位名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "postCode", value = "岗位编号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "postDesc", value = "岗位描述", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@RequestMapping(value = "/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore PostEntity post,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		List postList = postBiz.query(post);
		return ResultData.build().success(new EUListBean(postList,(int)BasicUtil.endPage(postList).getTotal()));
	}

	/**
	 * 返回编辑界面post_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions(value = {"organization:post:update", "organization:post:update"}, logical = Logical.OR)
	public String form(@ModelAttribute PostEntity post,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		if(post.getId()!=null){
			BaseEntity postEntity = postBiz.getEntity(Integer.parseInt(post.getId()));
			model.addAttribute("postEntity",postEntity);
		}
		return "/organization/post/form";
	}
	/**
	 * 获取岗位管理
	 * @param post 岗位管理实体
	 */
	@ApiOperation(value = "获取岗位管理列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore PostEntity post,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(post.getId()==null) {
			return ResultData.build().error();
		}
		PostEntity _post = (PostEntity)postBiz.getEntity(Integer.parseInt(post.getId()));
		return ResultData.build().success(_post);
	}

	@ApiOperation(value = "保存岗位管理列表接口")
	 @ApiImplicitParams({
		@ApiImplicitParam(name = "postName", value = "岗位名称", required =false,paramType="query"),
		@ApiImplicitParam(name = "postCode", value = "岗位编号", required =false,paramType="query"),
		@ApiImplicitParam(name = "postDesc", value = "岗位描述", required =false,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})

	/**
	* 保存岗位管理
	* @param post 岗位管理实体
	*/
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存岗位管理", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("organization:post:save")
	public ResultData save(@ModelAttribute @ApiIgnore PostEntity post, HttpServletResponse response, HttpServletRequest request) {
		if(!StringUtil.checkLength(post.getPostName()+"", 1, 10)){
			return ResultData.build().error(getResString("err.length", this.getResString("post.name"), "1", "10"));
		}
		postBiz.save(post);

		// 需要导入ms-id依赖
		// post.setPostCode(IDUtil.getId("岗位编号",Long.parseLong(post.getId()),null));

		// 默认使用主键id的生成策略
		post.setPostCode("POST-"+post.getId());

		 postBiz.updateById(post);
		return ResultData.build().success(post);
	}

	/**
	 * @param posts 岗位管理实体列表
	 */
	@ApiOperation(value = "批量删除岗位管理列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除岗位管理", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("organization:post:del")
	public ResultData delete(@RequestBody List<PostEntity> posts,HttpServletResponse response, HttpServletRequest request) {
		int[] ids = new int[posts.size()];
		for(int i = 0;i<posts.size();i++){
			ids[i] =Integer.parseInt(posts.get(i).getId()) ;
		}
		postBiz.delete(ids);
		return ResultData.build().success();
	}

	/**
	*	更新岗位管理列表
	* @param post 岗位管理实体
	*/
	 @ApiOperation(value = "更新岗位管理列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
		@ApiImplicitParam(name = "postName", value = "岗位名称", required =false,paramType="query"),
		@ApiImplicitParam(name = "postCode", value = "岗位编号", required =false,paramType="query"),
		@ApiImplicitParam(name = "postDesc", value = "岗位描述", required =false,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "id", value = "编号", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新岗位管理", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("organization:post:update")
	public ResultData update(@ModelAttribute @ApiIgnore PostEntity post, HttpServletResponse response,
			HttpServletRequest request) {
		if(!StringUtil.checkLength(post.getPostName()+"", 1, 10)){
			return ResultData.build().error(getResString("err.length", this.getResString("post.name"), "1", "10"));
		}
		if(!StringUtil.checkLength(post.getPostCode()+"", 1, 10)){
			return ResultData.build().error(getResString("err.length", this.getResString("post.code"), "1", "10"));
		}
		postBiz.updateEntity(post);
		return ResultData.build().success(post);
	}

	@ApiOperation(value = "验证岗位名称接口")
	@GetMapping("/verify")
	@ResponseBody
	public ResultData verify(String fieldName, String fieldValue, String id,String idName){
		boolean verify = false;
		if(StringUtils.isBlank(id)){
			verify = super.validated("organization_post",fieldName,fieldValue);
		}else{
			verify = super.validated("organization_post",fieldName,fieldValue,id,idName);
		}
		if(verify){
			return ResultData.build().success(false);
		}else {
			return ResultData.build().success(true);
		}
	}


	/**
	 * 获取每个岗位人员的数量
	 * @return
	 */
	@ApiOperation(value = "获取每个岗位人员的数量")
	@GetMapping("eachPostMembers")
	@ResponseBody
	public ResultData eachPostMembers(){
	 	List<PostEntity> eachPostMembersList = postBiz.eachPostMembers();
	 	return ResultData.build().success(eachPostMembersList);
	}


	/**
	 * 通过ids 查询 所有的post
	 * @param postIds
	 * @return
	 */
	@ApiOperation(value = "通过ids查询所有的post")
	@ApiImplicitParam(name = "postIds", value = "岗位编号", required =true,paramType="query")
	@PostMapping("getPosts")
	@ResponseBody
	public ResultData getPosts(String postIds){
		List<PostEntity> postEntities = postBiz.getPostsByIds(postIds);
		return  ResultData.build().success(postEntities);
	}

}
