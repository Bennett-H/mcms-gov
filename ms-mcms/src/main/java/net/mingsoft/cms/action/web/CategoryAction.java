/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.cms.action.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
/**
 * 分类管理控制层
 * @author 铭飞开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
@Api(tags={"前端-内容模块接口"})
@Controller("WebcmsCategoryAction")
@RequestMapping("/cms/category")
public class CategoryAction extends net.mingsoft.cms.action.BaseAction{


	/**
	 * 注入分类业务层
	 */
	@Autowired
	private ICategoryBiz categoryBiz;

	/**
	 * 查询分类列表
	 * @param category 分类实体
	 */
	@ApiOperation(value = "查询分类列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "categoryTitle", value = "栏目管理名称", required =false,paramType="query"),
    })
	@PostMapping(value="/list")
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore CategoryEntity category) {
		BasicUtil.startPage();
		category.setSqlWhere("");
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
	public ResultData get(@ModelAttribute @ApiIgnore CategoryEntity category){
		if(category.getId()==null) {
			return ResultData.build().error();
		}
		category.setSqlWhere("");
		CategoryEntity _category = (CategoryEntity)categoryBiz.getById(category.getId());
		return ResultData.build().success(_category);
	}

}
