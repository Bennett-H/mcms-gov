/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.approval.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.approval.biz.IConfigBiz;
import net.mingsoft.approval.entity.ConfigEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.elasticsearch.annotation.ESSave;
import net.mingsoft.progress.biz.IProgressBiz;
import net.mingsoft.progress.biz.IProgressLogBiz;
import net.mingsoft.progress.biz.ISchemeBiz;
import net.mingsoft.progress.entity.ProgressEntity;
import net.mingsoft.progress.entity.ProgressLogEntity;
import net.mingsoft.progress.entity.SchemeEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 审批配置管理控制层
 * @author 铭软科技
 * 创建日期：2021-3-9 8:33:16<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-审批配置接口"})
@Controller("approvalConfigAction")
@RequestMapping("/${ms.manager.path}/approval/config")
public class ConfigAction extends BaseAction{


	/**
	 * 注入审批配置业务层
	 */
	@Autowired
	private IConfigBiz configBiz;
	/**
	 * 注入进度日志业务层
	 */
	@Autowired
	private IProgressLogBiz progressLogBiz;
	/**
	 * 注入进度业务层
	 */
	@Autowired
	private IProgressBiz progressBiz;
	/**
	 * 注入方案业务层
	 */
	@Autowired
	private ISchemeBiz schemeBiz;

	/**
	 * 注入栏目业务层
	 */
	@Autowired
	private ICategoryBiz categoryBiz;

	/**
	 * 注入文章业务层
	 */
	@Autowired
	private IContentBiz contentBiz;

	/**
	 * 返回编辑界面config_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	public String form(@ModelAttribute ConfigEntity config,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		return "/approval/config/form";
	}

	/**
	 * 返回审核页面
	 */
	@ApiIgnore
	@GetMapping("/approvalForm")
	@RequiresPermissions("approval:config:progressLog")
	public String approvalForm(HttpServletResponse response,HttpServletRequest request,ModelMap model){
		return "/approval/config/approval-form";
	}

	/**
	 * 返回全局审核控制页面
	 */
	@ApiIgnore
	@GetMapping("/approvalPermission")
	public String approvalPermission(HttpServletResponse response,HttpServletRequest request,ModelMap model){
		return "/approval/config/approval-permission";
	}

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("approval:config:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/approval/config/index";
	}

	/**
	 * 查询审批配置列表
	 * @param config 审批配置实体
	 */
	@ApiOperation(value = "查询审批配置列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "categoryId", value = "栏目ID", required =false,paramType="query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@RequiresPermissions("approval:config:view")
	public ResultData list(@ModelAttribute @ApiIgnore ConfigEntity config, HttpServletResponse response, HttpServletRequest request) {
		List<ConfigEntity> configList = configBiz.list(new QueryWrapper<>(config));
		return ResultData.build().success(configList);
	}


	/**
	 * 获取审批配置
	 * @param config 审批配置实体
	 */
	@ApiOperation(value = "获取审批配置列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions("approval:config:view")
	public ResultData get(@ModelAttribute @ApiIgnore ConfigEntity config,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(config.getId()==null) {
			return ResultData.build().error();
		}
		ConfigEntity _config = (ConfigEntity)configBiz.getOne(new QueryWrapper<>(config));
		return ResultData.build().success(_config);
	}

	/**
	* 保存审批配置
	* @param configs 审批配置实体数组
	*/
	@ApiOperation(value = "批量保存审批配置列表接口数组")
	@ApiImplicitParams({

	})
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "批量保存审批配置", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("approval:config:save")
	public ResultData save(@RequestBody List<ConfigEntity> configs, HttpServletResponse response, HttpServletRequest request) {
		// 获取所有的栏目ID
		List<String> categoryIds = configs.stream().map(ConfigEntity::getCategoryId).collect(Collectors.toList());

		List<String> schemeIds = configs.stream().map(ConfigEntity::getSchemeId).collect(Collectors.toList());
		if (CollUtil.isEmpty(schemeIds)) {
			return ResultData.build().error(getResString("err.empty", this.getResString("scheme.id")));
		}
		ProgressEntity progress = new ProgressEntity();
		progress.setSchemeId(Integer.parseInt(schemeIds.get(0)));
		int size = progressBiz.list(new QueryWrapper<>(progress)).size();
//		 权限为审批节点一组


		//预设置循环中会用到的类
		CategoryEntity categoryEntity = null;
		// 先删除所有的审批配置
		configBiz.remove(new QueryWrapper<>());
		List<ConfigEntity> configList = new ArrayList<>();
		//过滤重置的栏目id集合
		List<String> ids = new ArrayList<>();
		for (ConfigEntity config : configs) {
			// 验证方案名称的值是否合法
			if(StringUtils.isBlank(config.getSchemeId())){
				return ResultData.build().error(getResString("err.empty", this.getResString("scheme.id")));
			}
			// 验证审批节点的值是否合法
			if(StringUtils.isBlank(config.getProgressId())){
				return ResultData.build().error(getResString("err.empty", this.getResString("progress.id")));
			}
			//循环只保存完整的栏目配置,非完整的栏目配置直接跳过
			int categorySize = 0;
			for (int i = 0; i < categoryIds.size(); i++) {
				if (categoryIds.get(i).equals(config.getCategoryId())){
					categorySize += 1;
				}
			}
			String categoryId = config.getCategoryId();
			if (categorySize % size != 0){
				//categoryEntity = categoryBiz.getById(categoryId);
				ids.add(categoryId);
				LOG.debug("栏目：{} 没有设置完整",categoryId);
				continue;
			}
			// 设置创建时间
			config.setCreateDate(new Date());
			// 设置创建的管理员
			config.setCreateBy( BasicUtil.getManager().getId());
			configList.add(config);
		}
		//栏目id集合
		List<String> list = ids.stream().distinct().collect(Collectors.toList());
		if (CollectionUtil.isNotEmpty(configList)) {
			configBiz.saveBatch(configList, configList.size());
		}
		String msg = "检测出存在未配置完整的栏目如下：";
		//若有栏目配置被过滤，则拼接信息提示
		if (CollectionUtil.isNotEmpty(list)){
			List<CategoryEntity> categoryEntityList = categoryBiz.listByIds(list);
			for (CategoryEntity entity : categoryEntityList) {
				msg += entity.getCategoryTitle() + "、";
			}
			msg = msg.substring(0,msg.length()-1) + "，请重新配置";
			return ResultData.build().error(msg);
		}
		return ResultData.build().success("数据保存成功",null);
	}

	/**
	 * @param configs 审批配置实体
	 */
	@ApiOperation(value = "批量删除审批配置列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除审批配置", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("approval:config:del")
	public ResultData delete(@RequestBody List<ConfigEntity> configs,HttpServletResponse response, HttpServletRequest request) {
		List<String> ids = configs.stream().map(p -> p.getId()).collect(Collectors.toList());
		configBiz.removeByIds(ids);
		return ResultData.build().success();
	}

	/**
	*	更新审批配置列表
	* @param config 审批配置实体
	*/
	 @ApiOperation(value = "更新审批配置列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
    	@ApiImplicitParam(name = "schemeId", value = "方案名称", required =true,paramType="query"),
    	@ApiImplicitParam(name = "progressId", value = "审批节点", required =true,paramType="query"),
		@ApiImplicitParam(name = "configManagerIds", value = "等级管理员配置", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新审批配置", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("approval:config:update")
	public ResultData update(@ModelAttribute @ApiIgnore ConfigEntity config, HttpServletResponse response,
			HttpServletRequest request) {
		//验证方案名称的值是否合法
		if(StringUtils.isBlank(config.getSchemeId())){
			return ResultData.build().error(getResString("err.empty", this.getResString("scheme.id")));
		}
		//验证审批节点的值是否合法
		if(StringUtils.isBlank(config.getProgressId())){
			return ResultData.build().error(getResString("err.empty", this.getResString("progress.id")));
		}
		configBiz.updateById(config);
		return ResultData.build().success(config);
	}

	@ApiOperation(value = "根据DataId,方案,栏目ID获取未审核的进度日志信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "schemeName", value = "方案名称", required =true,paramType="query"),
			@ApiImplicitParam(name = "dataId", value = "编号", required =true,paramType="query")
	})
	@GetMapping("/getProgressLog")
	@ResponseBody
	public ResultData getProgressLog(HttpServletResponse response,HttpServletRequest request){
	 	String schemeName = BasicUtil.getString("schemeName");
	 	String dataId = BasicUtil.getString("dataId");
	 	if(StringUtils.isBlank(dataId)) {
			return ResultData.build().error("单篇不需要审核!");
		}
		if(StringUtils.isBlank(schemeName)){
			return ResultData.build().error(this.getResString("scheme.name"));
		}
		// 查询进度日志()
		ProgressLogEntity _progressLog = progressLogBiz.getProgressLogBySchemeNameAndDataId(schemeName,dataId);
		// 是否重复审核
		if(_progressLog==null){
			return ResultData.build().error(getResString("progress.approval"));
		}
		// 需要做权限校验，用户是否可以审核
		ProgressEntity progressEntity = progressBiz.getById(_progressLog.getProgressId());
		// 获取当前栏目ID
		String categoryId = contentBiz.getById(dataId).getCategoryId();
		// 方案、等级、角色编号、栏目ID查询管理员拥有的权限
		boolean approvalFlag = configBiz.auditPermissionsVerifyForRoles(schemeName, categoryId,  BasicUtil.getManager().getRoleIds(), progressEntity.getProgressNodeName());
		if(!approvalFlag){
			// 该管理员无权限审核该节点
			return ResultData.build().error(this.getResString("err.error",this.getResString("approval.authority")));
		}
		return ResultData.build().success(_progressLog);
	}

	/**
	 * 审核日志
	 * @param progressLog 进度日志实体
	 */
	@ApiOperation(value = "审核日志接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
			@ApiImplicitParam(name = "plStatus", value = "审批状态", required =true,paramType="query"),
	})
	@ESSave
	@PostMapping("/approval")
	@ResponseBody
	@LogAnn(title = "审核日志", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("approval:config:approval")
	public ResultData approval(@ModelAttribute @ApiIgnore ProgressLogEntity progressLog, HttpServletResponse response, HttpServletRequest request) {
		if(StringUtils.isEmpty(progressLog.getId())){
			return ResultData.build().error(this.getResString("id"));
		}
		if(StringUtils.isEmpty(progressLog.getPlStatus())){
			return ResultData.build().error(getResString("err.empty", this.getResString("pl.status")));
		}
		if(!StringUtil.checkLength(progressLog.getPlContent(), 0, 200)){
			return ResultData.build().error(getResString("err.length", this.getResString("progress.content"), "0", "200"));
		}
		ProgressLogEntity _progressLog = progressLogBiz.getById(progressLog.getId());
		// 是否重复审核
		if(StringUtils.isNotEmpty(_progressLog.getPlStatus())){
			return ResultData.build().error(getResString("progress.approval"));
		}
		SchemeEntity schemeEntity = new SchemeEntity();
		schemeEntity.setIntegerId(_progressLog.getSchemeId());
		SchemeEntity scheme = schemeBiz.getOne(new QueryWrapper<>(schemeEntity));
		// 需要做权限校验，用户是否可以审核
		ProgressEntity progressEntity = progressBiz.getById(_progressLog.getProgressId());
		ManagerEntity manager =  BasicUtil.getManager();
		// 获取当前管理的角色ID (目前不考虑多角色)
		String categoryId = BasicUtil.getString("categoryId");
		boolean approvalFlag = configBiz.auditPermissionsVerifyForRoles(scheme.getSchemeName(), categoryId, manager.getRoleIds(), progressEntity.getProgressNodeName());
		if(!approvalFlag){
			// 该管理员无权限审核该节点
			return ResultData.build().error(this.getResString("err.error",this.getResString("approval.authority")));
		}
		_progressLog.setPlStatus(progressLog.getPlStatus());
		_progressLog.setPlContent(progressLog.getPlContent());
		// 设置进度数
		_progressLog.setPlNumber(progressEntity.getProgressNumber());
		_progressLog.setUpdateBy(manager.getId());
		_progressLog.setPlOperator(manager.getManagerNickName());
		_progressLog.setUpdateDate(new Date());
		//获取该级栏目设置的审批级数
		LambdaQueryWrapper<ConfigEntity> wrapper = new LambdaQueryWrapper<ConfigEntity>().eq(ConfigEntity::getCategoryId, categoryId);
		int progressId = configBiz.list(wrapper).size();

		configBiz.approval(scheme,_progressLog,progressId);
		// 获取最终的审批日志
		LambdaQueryWrapper<ProgressLogEntity> progressLogWrapper = new QueryWrapper<ProgressLogEntity>().lambda();
		progressLogWrapper.eq(progressLog.getSchemeId() != null ,ProgressLogEntity::getSchemeId, progressLog.getSchemeId());
		progressLogWrapper.eq(ProgressLogEntity::getDataId, progressLog.getDataId());
		progressLogWrapper.orderByDesc(ProgressLogEntity::getId);

		// 限制一条
		PageHelper.startPage(1, 1);
		ProgressLogEntity newProgressLog = progressLogBiz.getOne(progressLogWrapper, false);
		return ResultData.build().success(newProgressLog);
	}

	/**
	 * 根据栏目ID来查询当前管理员拥有当前栏目的那些审批节点权限
	 */
	@ApiOperation(value = "根据栏目ID获取当前角色拥有该栏目的审核级数")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "categoryId", value = "栏目ID", required =true,paramType="query"),
	})
	@PostMapping("/checkApprovalForCategory")
	@ResponseBody
	public ResultData checkApprovalForCategory(HttpServletResponse response, HttpServletRequest request) {
		// 根据当前角色ID,栏目ID,审批配置节点 获取当前角色是否有权限
		String categoryId = BasicUtil.getString("categoryId");
		if (StringUtils.isEmpty(categoryId)){
			return ResultData.build().error(getResString("err.empty", this.getResString("category.id")));
		}
		LambdaQueryWrapper<ConfigEntity> wrapper = new QueryWrapper<ConfigEntity>().lambda();
		ManagerEntity session =  BasicUtil.getManager();
		wrapper.select(ConfigEntity::getProgressId);
		wrapper.eq(ConfigEntity::getCategoryId, categoryId);
		List<String> roleIds = Arrays.asList(session.getRoleIds().split(","));
		for (String roleId : roleIds) {
			if (roleIds.iterator().hasNext()) {
				wrapper.or();
			}
			wrapper.apply("FIND_IN_SET('"+roleId+"', CONFIG_ROLE_IDS) > 0");
		}
		return ResultData.build().success(configBiz.list(wrapper));
	}

}
