/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.id.action;

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
import net.mingsoft.id.biz.IRuleDataBiz;
import net.mingsoft.id.entity.RuleDataEntity;
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
 * 规则数据管理控制层
 * @author  会 
 * 创建日期：2020-5-26 16:14:39<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-规则模块接口"})
@Controller("idRuleDataAction")
@RequestMapping("/${ms.manager.path}/id/ruleData")
public class RuleDataAction extends net.mingsoft.id.action.BaseAction{


	/**
	 * 注入规则数据业务层
	 */
	@Autowired
	private IRuleDataBiz ruleDataBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("id:ruleData:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/id/rule-data/index";
	}

	/**
	 * 返回编辑界面ruleData_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions(value = {"id:ruleData:save", "id:ruleData:update"}, logical = Logical.OR)
	public String form(@ModelAttribute RuleDataEntity ruleData,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		if(ruleData.getId()!=null){
			BaseEntity ruleDataEntity = ruleDataBiz.getById(Integer.parseInt(ruleData.getId()));
			model.addAttribute("ruleDataEntity",ruleDataEntity);
		}
		return "/id/rule-data/form";
	}

	/**
	 * 查询规则数据列表
	 * @param ruleData 规则数据实体
	 */
	@ApiOperation(value = "查询规则数据列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "irId", value = "关联id", required =false,paramType="query"),
    	@ApiImplicitParam(name = "irdType", value = "组成类型", required =false,paramType="query"),
    	@ApiImplicitParam(name = "irdOption", value = "选项", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "del", value = "删除标记", required =false,paramType="query"),
    	@ApiImplicitParam(name = "id", value = "编号", required =false,paramType="query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@RequiresPermissions("id:ruleData:view")
	public ResultData list(@ModelAttribute @ApiIgnore RuleDataEntity ruleData,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		List ruleDataList = ruleDataBiz.query(ruleData);
		return ResultData.build().success(new EUListBean(ruleDataList,(int)BasicUtil.endPage(ruleDataList).getTotal()));
	}

	/**
	 * 获取规则数据
	 * @param ruleData 规则数据实体
	 */
	@ApiOperation(value = "获取规则数据列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions("id:ruleData:view")
	public ResultData get(@ModelAttribute @ApiIgnore RuleDataEntity ruleData,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(ruleData.getId()==null) {
			return ResultData.build().error();
		}
		RuleDataEntity _ruleData = (RuleDataEntity)ruleDataBiz.getById(Integer.parseInt(ruleData.getId()));
		return ResultData.build().success(_ruleData);
	}

	@ApiOperation(value = "保存规则数据列表接口")
	 @ApiImplicitParams({
		@ApiImplicitParam(name = "irId", value = "关联id", required =false,paramType="query"),
    	@ApiImplicitParam(name = "irdType", value = "组成类型", required =true,paramType="query"),
		@ApiImplicitParam(name = "irdOption", value = "选项", required =false,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "del", value = "删除标记", required =false,paramType="query"),
		@ApiImplicitParam(name = "id", value = "编号", required =false,paramType="query"),
	})

	/**
	* 保存规则数据
	* @param ruleData 规则数据实体
	*/
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存规则数据", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("id:ruleData:save")
	public ResultData save(@ModelAttribute @ApiIgnore RuleDataEntity ruleData, HttpServletResponse response, HttpServletRequest request) {
		if(!StringUtil.checkLength(ruleData.getIrId()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("ir.id"), "0", "11"));
		}
		//验证组成类型的值是否合法
		if(StringUtil.isBlank(ruleData.getIrdType())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ird.type")));
		}
		//验证规则数据内容长度
		if (!StringUtil.checkLength(ruleData.getIrdOption()+"",1,50)){
			return ResultData.build().error(getResString("err.length", this.getResString("ird.option"), "1", "50"));
		}
		ruleDataBiz.save(ruleData);
		return ResultData.build().success(ruleData);
	}

	/**
	 * @param ruleDatas 规则数据实体集合
	 */
	@ApiOperation(value = "批量删除规则数据列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除规则数据", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("id:ruleData:del")
	public ResultData delete(@RequestBody List<RuleDataEntity> ruleDatas,HttpServletResponse response, HttpServletRequest request) {
		int[] ids = new int[ruleDatas.size()];
		for(int i = 0;i<ruleDatas.size();i++){
			ids[i] =Integer.parseInt(ruleDatas.get(i).getId()) ;
		}
		ruleDataBiz.delete(ids);
		return ResultData.build().success();
	}

	/**
	*	更新规则数据列表
	* @param ruleData 规则数据实体
	*/
	 @ApiOperation(value = "更新规则数据列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
		@ApiImplicitParam(name = "irId", value = "关联id", required =false,paramType="query"),
    	@ApiImplicitParam(name = "irdType", value = "组成类型", required =true,paramType="query"),
		@ApiImplicitParam(name = "irdOption", value = "选项", required =false,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "del", value = "删除标记", required =false,paramType="query"),
		@ApiImplicitParam(name = "id", value = "编号", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新规则数据", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("id:ruleData:update")
	public ResultData update(@ModelAttribute @ApiIgnore RuleDataEntity ruleData, HttpServletResponse response,
			HttpServletRequest request) {
		if(!StringUtil.checkLength(ruleData.getIrId()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("ir.id"), "0", "11"));
		}
		//验证组成类型的值是否合法
		if(StringUtil.isBlank(ruleData.getIrdType())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ird.type")));
		}
		 //验证规则数据内容长度
		 if (!StringUtil.checkLength(ruleData.getIrdOption()+"",1,50)){
			 return ResultData.build().error(getResString("err.length", this.getResString("ird.option"), "1", "50"));
		 }
		ruleDataBiz.updateById(ruleData);
		return ResultData.build().success(ruleData);
	}

}
