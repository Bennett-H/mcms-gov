/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.datascope.action;

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
import net.mingsoft.datascope.biz.IDataConfigBiz;
import net.mingsoft.datascope.entity.DataConfigEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 数据权限管理控制层
 * @author  会 
 * 创建日期：2022-7-1 16:23:56<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-数据权限配置接口"})
@Controller("datascopeDataConfigAction")
@RequestMapping("/${ms.manager.path}/datascope/dataConfig")
public class DataConfigAction extends BaseAction{


	/**
	 * 注入数据权限业务层
	 */
	@Autowired
	private IDataConfigBiz dataConfigBiz;

	/**
	 * 返回主界面index
	 */
	@ApiOperation(value = "返回主界面index")
	@GetMapping("/index")
	@RequiresPermissions("datascope:dataConfig:view")
	public String index(HttpServletResponse response,HttpServletRequest request) {
		return "/datascope/data-config/index";
	}

	/**
	 * 查询数据权限列表
	 * @param dataConfig 数据权限实体
	 */
	@ApiOperation(value = "查询数据权限列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "configDesc", value = "配置描述", paramType = "query"),
			@ApiImplicitParam(name = "configSubsql", value = "子查询", paramType = "query"),
			@ApiImplicitParam(name = "configName", value = "配置名称", paramType = "query"),
    })
	@PostMapping(value ="/list")
	@ResponseBody
	@RequiresPermissions("datascope:dataConfig:view")
	public ResultData list(@ModelAttribute @ApiIgnore DataConfigEntity dataConfig,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		List dataConfigList = null;
		if ( dataConfig.getSqlWhere() != null){
			dataConfigList = dataConfigBiz.query(dataConfig);
		} else {
			LambdaQueryWrapper<DataConfigEntity> wrapper = new LambdaQueryWrapper<>(dataConfig).orderByDesc(DataConfigEntity::getCreateDate);
			dataConfigList = dataConfigBiz.list(wrapper);
		}
		return ResultData.build().success(new EUListBean(dataConfigList,(int)BasicUtil.endPage(dataConfigList).getTotal()));
	}

	/**
	 * 返回编辑界面dataConfig的form
	 */
	@GetMapping("/form")
	@RequiresPermissions(value = {"datascope:dataConfig:update", "datascope:dataConfig:save"}, logical = Logical.OR)
	public String form(@ModelAttribute DataConfigEntity dataConfig,HttpServletResponse response,HttpServletRequest request,ModelMap model) {
		return "/datascope/data-config/form";
	}


	/**
	 * 获取数据权限
	 * @param dataConfig 数据权限实体
	 */
	@ApiOperation(value = "获取数据权限列表接口")
    @ApiImplicitParam(name = "id", value = "主键ID", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions("datascope:dataConfig:view")
	public ResultData get(@ModelAttribute @ApiIgnore DataConfigEntity dataConfig,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model) {
		if (dataConfig.getId()==null) {
			return ResultData.build().error();
		}
		DataConfigEntity _dataConfig = (DataConfigEntity)dataConfigBiz.getById(dataConfig.getId());
		return ResultData.build().success(_dataConfig);
	}


	/**
	* 保存数据权限
	* @param dataConfig 数据权限实体
	*/
	@ApiOperation(value = "保存数据权限列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "configDesc", value = "配置描述", required =true, paramType = "query"),
			@ApiImplicitParam(name = "configSubsql", value = "子查询", required =true, paramType = "query"),
			@ApiImplicitParam(name = "configName", value = "配置名称", required =true, paramType = "query"),
	})
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存数据权限", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("datascope:dataConfig:save")
	public ResultData save(@ModelAttribute @ApiIgnore DataConfigEntity dataConfig, HttpServletResponse response, HttpServletRequest request) {
		if (super.validated("datascope_DATA_CONFIG","CONFIG_NAME",dataConfig.getConfigName())) {
			return ResultData.build().error(getResString("err.exist", this.getResString("config.name")));
		}
		if (!StringUtil.checkLength(dataConfig.getConfigDesc()+"", 0, 255)) {
			return ResultData.build().error(getResString("err.length", this.getResString("config.desc"), "0", "255"));
		}
		if (!StringUtil.checkLength(dataConfig.getConfigSubsql()+"", 0, 255)) {
			return ResultData.build().error(getResString("err.length", this.getResString("config.subsql"), "0", "255"));
		}
		if (!StringUtil.checkLength(dataConfig.getConfigName()+"", 0, 25)) {
			return ResultData.build().error(getResString("err.length", this.getResString("config.name"), "0", "255"));
		}
		dataConfigBiz.save(dataConfig);
		return ResultData.build().success(dataConfig);
	}

	/**
     *  删除数据权限
     *
	 * @param dataConfigs 数据权限实体
	 */
	@ApiOperation(value = "批量删除数据权限列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除数据权限", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("datascope:dataConfig:del")
	public ResultData delete(@RequestBody List<DataConfigEntity> dataConfigs,HttpServletResponse response, HttpServletRequest request) {
	    List<String> ids = (List)dataConfigs.stream().map((p) -> {return p.getId();}).collect(Collectors.toList());
		return this.dataConfigBiz.removeByIds(ids) ? ResultData.build().success() : ResultData.build().error(this.getResString("err.error", new String[]{this.getResString("id")}));
		}

	/**
	 *	更新数据权限列表
	 *
	 * @param dataConfig 数据权限实体
	 */
	 @ApiOperation(value = "更新数据权限列表接口")
	 @ApiImplicitParams({
    		@ApiImplicitParam(name = "configDesc", value = "配置描述", required =true, paramType = "query"),
    		@ApiImplicitParam(name = "configSubsql", value = "子查询", required =true, paramType = "query"),
    		@ApiImplicitParam(name = "configName", value = "配置名称", required =true, paramType = "query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新数据权限", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("datascope:dataConfig:update")
	public ResultData update(@ModelAttribute @ApiIgnore DataConfigEntity dataConfig, HttpServletResponse response,
			HttpServletRequest request) {
		if (super.validated("datascope_DATA_CONFIG", "CONFIG_NAME", dataConfig.getConfigName(), dataConfig.getId(), "id")) {
			return ResultData.build().error(getResString("err.exist", this.getResString("config.name")));
		}
		if (!StringUtil.checkLength(dataConfig.getConfigDesc()+"", 0, 255)) {
			return ResultData.build().error(getResString("err.length", this.getResString("config.desc"), "0", "255"));
		}
		if (!StringUtil.checkLength(dataConfig.getConfigSubsql()+"", 0, 255)) {
			return ResultData.build().error(getResString("err.length", this.getResString("config.subsql"), "0", "255"));
		}
		if (!StringUtil.checkLength(dataConfig.getConfigName()+"", 0, 255)) {
			return ResultData.build().error(getResString("err.length", this.getResString("config.name"), "0", "255"));
		}
			dataConfigBiz.updateById(dataConfig);
		return ResultData.build().success(dataConfig);
	}

	/**
	 *	配置项是否重复接口
	 *
	 */
	@ApiOperation(value = "配置项是否重复接口")
	@PostMapping("verify")
	@ResponseBody
	public ResultData verify(String fieldName, String fieldValue, String id,String idName) {
		boolean verify = false;
		if (StringUtils.isBlank(id)) {
			verify = super.validated("datascope_DATA_CONFIG",fieldName,fieldValue);
		} else {
			verify = super.validated("datascope_DATA_CONFIG",fieldName,fieldValue,id,idName);
		}
		if (verify) {
			return ResultData.build().success(false);
		}else {
			return ResultData.build().success(true);
		}
	}
}
