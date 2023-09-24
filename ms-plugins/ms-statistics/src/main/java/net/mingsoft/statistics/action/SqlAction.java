/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.statistics.action;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import net.mingsoft.statistics.biz.ISqlBiz;
import net.mingsoft.statistics.entity.SqlEntity;
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
import java.util.stream.Collectors;
/**
 * 统计管理控制层
 * @author 铭软科技
 * 创建日期：2021-1-15 9:32:36<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-统计模块接口"})
@Controller("statisticsSqlAction")
@RequestMapping("/${ms.manager.path}/statistics/sql")
public class SqlAction extends BaseAction{


	/**
	 * 注入统计业务层
	 */
	@Autowired
	private ISqlBiz sqlBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("statistics:sql:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/statistics/sql/index";
	}

	/**
	 * 返回编辑界面sql_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions(value = {"statistics:sql:save", "statistics:sql:update"}, logical = Logical.OR)
	public String form(@ModelAttribute SqlEntity sql,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		return "/statistics/sql/form";
	}

	/**
	 * 查询统计列表
	 * @param sql 统计实体
	 */
	@ApiOperation(value = "查询统计列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ssName", value = "统计名称", required =false,paramType="query"),
			@ApiImplicitParam(name = "ssKey", value = "统计key值", required =false,paramType="query"),
			@ApiImplicitParam(name = "ssType", value = "统计类型", required =false,paramType="query"),
	})
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore SqlEntity sql,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		LambdaQueryWrapper<SqlEntity> wrapper = new LambdaQueryWrapper<>(sql);
		if (StringUtils.isNotBlank(sql.getSsName())) {
			wrapper.like(SqlEntity::getSsName, sql.getSsName());
			sql.setSsName(null);
		}
		List<SqlEntity> sqlList = sqlBiz.list(wrapper);
		return ResultData.build().success(new EUListBean(sqlList,(int)BasicUtil.endPage(sqlList).getTotal()));
	}

	/**
	 * 获取统计
	 * @param sql 统计实体
	 */
	@ApiOperation(value = "获取统计列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore SqlEntity sql,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if (StringUtils.isEmpty(sql.getId())) {
			return ResultData.build().error(getResString("err.empty",getResString("id")));
		}
		SqlEntity _sql = sqlBiz.getById(sql.getId());
		return ResultData.build().success(_sql);
	}

	@ApiOperation(value = "保存统计列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ssName", value = "统计名称", required =true,paramType="query"),
			@ApiImplicitParam(name = "ssKey", value = "统计key值", required =true,paramType="query"),
			@ApiImplicitParam(name = "ssType", value = "统计类型", required =true,paramType="query"),
			@ApiImplicitParam(name = "ssSql", value = "统计SQL", required =true,paramType="query"),
	})

	/**
	* 保存统计
	* @param sql 统计实体
	*/
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存统计", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("statistics:sql:save")
	public ResultData save(@ModelAttribute @ApiIgnore SqlEntity sql, HttpServletResponse response, HttpServletRequest request) {
		if(super.validated("statistics_sql","ss_name",sql.getSsName())){
			return ResultData.build().error(getResString("err.exist", this.getResString("ss.name")));
		}
		if(StringUtils.isBlank(sql.getSsName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ss.name")));
		}
		if(!StringUtil.checkLength(sql.getSsName()+"", 0, 20)){
			return ResultData.build().error(getResString("err.length", this.getResString("ss.name"), "0", "20"));
		}
		//验证统计类型的值是否合法
//		if(StringUtils.isBlank(sql.getSsType())){
//			return ResultData.build().error(getResString("err.empty", this.getResString("ss.type")));
//		}
		//验证统计SQL的值是否合法
		if(StringUtils.isBlank(sql.getSsSql())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ss.sql")));
		}
		sqlBiz.save(sql);
		return ResultData.build().success(sql);
	}

	/**
	 * @param sqls 统计实体
	 */
	@ApiOperation(value = "批量删除统计列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除统计", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("statistics:sql:del")
	public ResultData delete(@RequestBody List<SqlEntity> sqls,HttpServletResponse response, HttpServletRequest request) {
		List<String> ids = sqls.stream().map(SqlEntity::getId).collect(Collectors.toList());
		if(CollUtil.isNotEmpty(ids)){
			if (sqlBiz.removeByIds(ids)) {
				return ResultData.build().success();
			}else {
				return ResultData.build().error();
			}
		}else {
			return ResultData.build().error(getResString("err.empty",getResString("id")));
		}
	}

	/**
	*	更新统计列表
	* @param sql 统计实体
	*/
	 @ApiOperation(value = "更新统计列表接口")
	 @ApiImplicitParams({
			 @ApiImplicitParam(name = "ssName", value = "统计名称", required =true,paramType="query"),
			 @ApiImplicitParam(name = "ssKey", value = "统计key值", required =true,paramType="query"),
			 @ApiImplicitParam(name = "ssType", value = "统计类型", required =true,paramType="query"),
			 @ApiImplicitParam(name = "ssSql", value = "统计SQL", required =true,paramType="query"),
			 @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
	 })
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新统计", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("statistics:sql:update")
	public ResultData update(@ModelAttribute @ApiIgnore SqlEntity sql, HttpServletResponse response,
			HttpServletRequest request) {
		 if(super.validated("statistics_sql", "ss_name", sql.getSsName(), sql.getId(), "id")){
			 return ResultData.build().error(getResString("err.exist", this.getResString("ss.name")));
		 }
		 //验证统计名称的值是否合法
		 if(StringUtils.isBlank(sql.getSsName())){
			 return ResultData.build().error(getResString("err.empty", this.getResString("ss.name")));
		 }
		 if(!StringUtil.checkLength(sql.getSsName()+"", 0, 50)){
			 return ResultData.build().error(getResString("err.length", this.getResString("ss.name"), "0", "20"));
		 }
		 //验证统计类型的值是否合法
//		 if(StringUtils.isBlank(sql.getSsType())){
//			 return ResultData.build().error(getResString("err.empty", this.getResString("ss.type")));
//		 }
		 //验证统计SQL的值是否合法
		 if(StringUtils.isBlank(sql.getSsSql())){
			 return ResultData.build().error(getResString("err.empty", this.getResString("ss.sql")));
		 }
		sqlBiz.updateById(sql);
		return ResultData.build().success(sql);
	}

}
