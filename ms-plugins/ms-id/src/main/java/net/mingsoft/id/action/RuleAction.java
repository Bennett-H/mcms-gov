/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.id.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import net.mingsoft.id.biz.IRuleBiz;
import net.mingsoft.id.entity.RuleEntity;
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
/**
 * 规则管理控制层
 * @author  会 
 * 创建日期：2020-5-26 16:14:39<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-规则模块接口"})
@Controller("idRuleAction")
@RequestMapping("/${ms.manager.path}/id/rule")
public class RuleAction extends net.mingsoft.id.action.BaseAction{


	/**
	 * 注入规则业务层
	 */
	@Autowired
	private IRuleBiz ruleBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("id:rule:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/id/rule/index";
	}

	/**
	 * 返回编辑界面rule_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions(value = {"id:rule:save", "id:rule:update"}, logical = Logical.OR)
	public String form(@ModelAttribute RuleEntity rule,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		if(rule.getId()!=null){
			BaseEntity ruleEntity = ruleBiz.getById(Integer.parseInt(rule.getId()));
			model.addAttribute("ruleEntity",ruleEntity);
		}
		return "/id/rule/form";
	}

	/**
	 * 查询规则列表
	 * @param rule 规则实体
	 */
	@ApiOperation(value = "查询规则列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "irName", value = "规则名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "irType", value = "类型", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@RequiresPermissions("id:rule:view")
	public ResultData list(@ModelAttribute @ApiIgnore RuleEntity rule,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		List ruleList = ruleBiz.query(rule);
		return ResultData.build().success(new EUListBean(ruleList,(int)BasicUtil.endPage(ruleList).getTotal()));
	}

	/**
	 * 获取规则
	 * @param rule 规则实体
	 */
	@ApiOperation(value = "获取规则列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions("id:rule:view")
	public ResultData get(@ModelAttribute @ApiIgnore RuleEntity rule,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(rule.getId()==null) {
			return ResultData.build().error();
		}
		RuleEntity _rule = (RuleEntity)ruleBiz.getEntity(Integer.parseInt(rule.getId()));
		return ResultData.build().success(_rule);
	}

	@ApiOperation(value = "保存规则列表接口")
	 @ApiImplicitParams({
    	@ApiImplicitParam(name = "irName", value = "规则名称", required =true,paramType="query"),
    	@ApiImplicitParam(name = "irType", value = "类型", required =true,paramType="query"),
	})

	/**
	* 保存规则
	* @param rule 规则实体
	*/
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存规则", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("id:rule:save")
	public ResultData save(@ModelAttribute @ApiIgnore RuleEntity rule, HttpServletResponse response, HttpServletRequest request) {
		//验证规则名称的值是否合法
		if(StringUtil.isBlank(rule.getIrName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ir.name")));
		}
		if(!StringUtil.checkLength(rule.getIrName()+"", 1, 20)){
			return ResultData.build().error(getResString("err.length", this.getResString("ir.name"), "1", "20"));
		}
		//验证类型的值是否合法
		if(StringUtil.isBlank(rule.getIrType())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ir.type")));
		}
		//校验是否已存在同类型同名称的规则
		LambdaQueryWrapper<RuleEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(RuleEntity::getIrName,rule.getIrName()).eq(RuleEntity::getIrType,rule.getIrType());
		if (ruleBiz.getOne(wrapper)!=null){
			return ResultData.build().error(getResString("err.exist",this.getResString("ir.name")));
		}
		ruleBiz.save(rule);
		return ResultData.build().success(rule);
	}

	/**
	 * @param rules 规则实体集合
	 */
	@ApiOperation(value = "批量删除规则列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除规则", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("id:rule:del")
	public ResultData delete(@RequestBody List<RuleEntity> rules,HttpServletResponse response, HttpServletRequest request) {
		int[] ids = new int[rules.size()];
		for(int i = 0;i<rules.size();i++){
			ids[i] =Integer.parseInt(rules.get(i).getId()) ;
		}
		ruleBiz.delete(ids);
		return ResultData.build().success();
	}

	/**
	*	更新规则列表
	* @param rule 规则实体
	*/
	 @ApiOperation(value = "更新规则列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
    	@ApiImplicitParam(name = "irName", value = "规则名称", required =true,paramType="query"),
    	@ApiImplicitParam(name = "irType", value = "类型", required =true,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新规则", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("id:rule:update")
	public ResultData update(@ModelAttribute @ApiIgnore RuleEntity rule, HttpServletResponse response,
			HttpServletRequest request) {
		//验证规则名称的值是否合法
		if(StringUtil.isBlank(rule.getIrName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ir.name")));
		}
		if(!StringUtil.checkLength(rule.getIrName()+"", 1, 20)){
			return ResultData.build().error(getResString("err.length", this.getResString("ir.name"), "1", "20"));
		}
		//验证类型的值是否合法
		if(StringUtil.isBlank(rule.getIrType())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ir.type")));
		}
		 //校验是否已存在同类型同名称的规则,并且排除自身防止修改自身被拒绝
		 LambdaQueryWrapper<RuleEntity> wrapper = new LambdaQueryWrapper<>();
		 wrapper.eq(RuleEntity::getIrName,rule.getIrName()).eq(RuleEntity::getIrType,rule.getIrType());
		 if (ruleBiz.getOne(wrapper)!=null && !ruleBiz.getOne(wrapper).getId().equalsIgnoreCase(rule.getId())){
			 return ResultData.build().error(getResString("err.exist",this.getResString("ir.name")));
		 }
		ruleBiz.updateById(rule);
		return ResultData.build().success(rule);
	}

}
