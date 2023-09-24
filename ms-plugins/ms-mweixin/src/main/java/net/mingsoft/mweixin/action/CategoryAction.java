/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.action;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mweixin.biz.ICategoryBiz;
import net.mingsoft.mweixin.biz.IFileBiz;
import net.mingsoft.mweixin.entity.CategoryEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
/**
 * 分类管理控制层
 * @author 铭飞开发团队
 * 创建日期：2019-12-25 9:27:11<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-微信模块接口"})
@Controller("mweixinCategoryAction")
@RequestMapping("/${ms.manager.path}/mweixin/category")
public class CategoryAction extends BaseAction{
	
	
	/**
	 * 注入分类业务层
	 */	
	@Autowired
	private ICategoryBiz categoryBiz;

	/**
	 * 注入微信文件表业务层
	 */
	@Autowired
	private IFileBiz fileBiz;

	
	/**
	 * 查询分类列表
	 * @param category 分类实体
	 */
	@ApiOperation(value = "查询分类列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "categoryTitle", value = "类别标题", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryId", value = "父类别编号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryDescription", value = "分类描述", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryImg", value = "分类略缩图", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	@RequiresPermissions("picture:view")
	public ResultData list(@ModelAttribute @ApiIgnore CategoryEntity category,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		//取出微信实体
		WeixinEntity weixin = this.getWeixinSession(request);
		//若微信或者微信相关数据不存在
		if (weixin == null || weixin.getIntId() <= 0) {
			return ResultData.build().error(this.getResString("weixin.not.found"));
		}
		category.setWeixinId(weixin.getIntId());
		BasicUtil.startPage();
		List categoryList = categoryBiz.query(category);
		return ResultData.build().success(new EUListBean(categoryList,(int)BasicUtil.endPage(categoryList).getTotal()));
	}


	/**
	 * 获取分类
	 * @param category 分类实体
	 */
	@ApiOperation(value = "获取分类列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions("picture:view")
	public ResultData get(@ModelAttribute @ApiIgnore CategoryEntity category,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(category.getId()==null) {
			return ResultData.build().error();
		}
		CategoryEntity _category = (CategoryEntity)categoryBiz.getEntity(Integer.parseInt(category.getId()));
		return ResultData.build().success(_category);
	}
	

	/**
	* 保存分类
	* @param category 分类实体
	*/
	@ApiOperation(value = "保存分类列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "appId", value = "应用编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "categoryTitle", value = "类别标题", required =false,paramType="query"),
			@ApiImplicitParam(name = "categoryId", value = "父类别编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "categoryDescription", value = "分类描述", required =false,paramType="query"),
			@ApiImplicitParam(name = "categoryImg", value = "分类略缩图", required =false,paramType="query"),
			@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
			@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存分类", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("picture:save")
	public ResultData save(@ModelAttribute @ApiIgnore CategoryEntity category, HttpServletRequest request) {
		if(!StringUtil.checkLength(category.getCategoryTitle()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("category.title"), "0", "11"));
		}
		if(!StringUtil.checkLength(category.getCategoryId()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("category.id"), "0", "11"));
		}
		if(!StringUtil.checkLength(category.getCategoryDescription()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("category.description"), "0", "11"));
		}
		category.setWeixinId(this.getWeixinSession(request).getIntId());
		category.setCreateDate(new Date());
		categoryBiz.saveEntity(category);
		return ResultData.build().success(category);
	}
	
	/**
	 * @param categorys 分类实体集合
	 */
	@ApiOperation(value = "批量删除分类列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除分类", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("picture:del")
	public ResultData delete(@RequestBody List<CategoryEntity> categorys, HttpServletRequest request) {
		WeixinEntity weixin = this.getWeixinSession(request);
		//若微信或者微信相关数据不存在
		if(weixin == null || weixin.getIntId()<=0){
			return ResultData.build().error(this.getResString("weixin.not.found"));
		}
		int[] ids = new int[categorys.size()];
		for(int i = 0;i<categorys.size();i++){
			ids[i] =Integer.parseInt(categorys.get(i).getId()) ;
			fileBiz.deleteByCategoryId(categorys.get(i).getId(),weixin.getId());
		}

		categoryBiz.delete(ids);
		return ResultData.build().success();
	}
	/**
	*	更新分类列表
	* @param category 分类实体
	*/
	 @ApiOperation(value = "更新分类列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
		@ApiImplicitParam(name = "appId", value = "应用编号", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryTitle", value = "类别标题", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryId", value = "父类别编号", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryDescription", value = "分类描述", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryImg", value = "分类略缩图", required =false,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新分类", businessType = BusinessTypeEnum.UPDATE)
	 @RequiresPermissions("picture:update")
	public ResultData update(@ModelAttribute @ApiIgnore CategoryEntity category) {
		if(!StringUtil.checkLength(category.getWeixinId()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("app.id"), "0", "11"));
		}
		if(!StringUtil.checkLength(category.getCategoryTitle()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("category.title"), "0", "11"));
		}
		if(!StringUtil.checkLength(category.getCategoryId()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("category.id"), "0", "11"));
		}
		if(!StringUtil.checkLength(category.getCategoryDescription()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("category.description"), "0", "11"));
		}
		category.setUpdateDate(new Date());
		categoryBiz.updateEntity(category);
		return ResultData.build().success(category);
	}


		
}
