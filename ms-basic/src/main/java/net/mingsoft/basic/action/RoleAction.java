/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.bean.RoleBean;
import net.mingsoft.basic.biz.IModelBiz;
import net.mingsoft.basic.biz.IRoleBiz;
import net.mingsoft.basic.biz.IRoleModelBiz;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.RoleEntity;
import net.mingsoft.basic.entity.RoleModelEntity;
import net.mingsoft.basic.util.BasicUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理控制层
 * @author 铭飞开发团队
 * @version
 * 版本号：1.0<br/>
 * 创建日期：2017-8-24 23:40:55<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-基础接口"})
@Controller
@RequestMapping("/${ms.manager.path}/basic/role")
public class RoleAction extends BaseAction{

	/**
	 * 注入角色业务层
	 */
	@Autowired
	private IRoleBiz roleBiz;
	/**
	 * 模块业务层
	 */
	@Autowired
	private IModelBiz modelBiz;
	/**
	 * 角色模块关联业务层
	 */
	@Autowired
	private IRoleModelBiz roleModelBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("basic:role:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/basic/role/index";
	}

	/**
	 * 返回编辑界面role_form
	 */
	@ApiOperation(value = "返回编辑界面role_form")
	@ApiImplicitParam(name = "id", value = "角色ID", required = true,paramType="query")
	@GetMapping("/form")
	@RequiresPermissions("basic:role:view")
	public String form(@ModelAttribute @ApiIgnore RoleEntity role,HttpServletResponse response,HttpServletRequest request,@ApiIgnore ModelMap model){
		if(StringUtils.isNotEmpty(role.getId())){
			RoleEntity roleEntity = roleBiz.getById(role.getId());
			model.addAttribute("roleEntity",roleEntity);
		}
		return "/basic/role/form";
	}

	@ApiOperation(value = "查询角色列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "roleName", value = "角色名称", required = false,paramType="query"),
		@ApiImplicitParam(name = "managerId", value = "该角色的创建者ID", required = false,paramType="query"),
	})
	@GetMapping("/list")
	//@RequiresPermissions("role:view") 此处权限在栏目权限管理等其他功能被调用，需放行
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore RoleEntity role,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model) {
		BasicUtil.startPage();
		List roleList = roleBiz.query(role);
		return ResultData.build().success(new EUListBean(roleList,(int)BasicUtil.endPage(roleList).getTotal()));
	}

	@ApiOperation(value = "根据角色ID查询模块集合")
	@ApiImplicitParam(name = "roleId", value = "角色ID", required = true,paramType="path")
	@GetMapping("/{roleId}/queryByRole")
	@ResponseBody
	public ResultData queryByRole(@PathVariable @ApiIgnore int roleId, HttpServletResponse response){
		List models = modelBiz.queryModelByRoleId(roleId);
		return ResultData.build().success(models);
	}

	@ApiOperation(value = "查询所有角色列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "roleName", value = "角色名称", required = false,paramType="query"),
			@ApiImplicitParam(name = "managerId", value = "该角色的创建者ID", required = false,paramType="query"),
	})
	@GetMapping("/all")
	@ResponseBody
	public ResultData all(@ModelAttribute @ApiIgnore RoleEntity role,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model) {
		BasicUtil.startPage();
		List roleList = roleBiz.query(role);
		return ResultData.build().success(new EUListBean(roleList,(int)BasicUtil.endPage(roleList).getTotal()));
	}

	@ApiOperation(value = "获取角色")
	@ApiImplicitParam(name = "id", value = "角色ID", required = true,paramType="query")
	@GetMapping("/get")
	@RequiresPermissions("basic:role:view")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore RoleEntity role,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(StringUtils.isEmpty(role.getId())) {
			return ResultData.build().error(getResString("err.error", this.getResString("role.id")));
		}
		RoleEntity _role = (RoleEntity)roleBiz.getEntity(Integer.parseInt(role.getId()));
		return ResultData.build().success(_role);
	}


	@ApiOperation(value = "保存角色实体")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "roleName", value = "角色名称", required = false,paramType="query"),
		@ApiImplicitParam(name = "managerId", value = "该角色的创建者ID", required = false,paramType="query"),
	})
	@LogAnn(title = "保存角色实体",businessType= BusinessTypeEnum.UPDATE)
	@PostMapping("/saveOrUpdateRole")
	@ResponseBody
	@RequiresPermissions(value = {"basic:role:save","basic:role:update"}, logical = Logical.OR)
	public ResultData saveOrUpdateRole(@ModelAttribute @ApiIgnore RoleBean role, HttpServletResponse response, HttpServletRequest request) {
		//组织角色属性，并对角色进行保存
		RoleBean _role = new RoleBean();
		_role.setRoleName(role.getRoleName());
		//获取管理员id
		ManagerEntity managerSession = BasicUtil.getManager();
		if(StringUtils.isEmpty(role.getRoleName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("rolrName")));
		}
		RoleBean roleBean = (RoleBean) roleBiz.getEntity(_role);
		//通过角色id判断是保存还是修改
		if(StringUtils.isNotEmpty(role.getId())){
			//若为更新角色，数据库中存在该角色名称且当前名称不为更改前的名称，则属于重名
			if(roleBean != null && !roleBean.getId().equals(role.getId())){
				return ResultData.build().error(getResString("roleName.exist"));
			}
			role.setUpdateBy(BasicUtil.getManager().getId());
			role.setUpdateDate(new Date());
			role.setCreateBy(null);
			roleBiz.updateById(role);
		}else{
			//判断角色名是否重复
			if(roleBean != null){
				return ResultData.build().error(getResString("roleName.exist"));
			}
			role.setCreateBy(BasicUtil.getManager().getId());
			role.setCreateDate(new Date());
			//获取管理员id
			roleBiz.save(role);
			roleBiz.updateCache();
		}
		//开始保存相应的关联数据。组织角色模块的列表。
		List<RoleModelEntity> roleModelList = new ArrayList<>();
		if(!StringUtils.isEmpty(role.getIds())){
			for(String id : role.getIds().split(",")){
				RoleModelEntity roleModel = new RoleModelEntity();
				roleModel.setRoleId(Integer.parseInt(role.getId()));
				roleModel.setModelId(Integer.parseInt(id));
				roleModelList.add(roleModel);
			}
			//先删除当前的角色关联菜单，然后重新添加。
			roleModelBiz.deleteByRoleId(Integer.parseInt(role.getId()));
			modelBiz.updateCache();

			//加上数量参数用于区分IBaseBiz的重名方法
			roleModelBiz.saveBatch(roleModelList, roleModelList.size());
		}else{
			roleModelBiz.deleteByRoleId(Integer.parseInt(role.getId()));
		}
		roleBiz.updateCache();
		return ResultData.build().success(role);
	}


	@ApiOperation(value = "批量删除角色")
	@PostMapping("/delete")
	@ResponseBody
	@RequiresPermissions("basic:role:del")
	@LogAnn(title = "删除角色", businessType = BusinessTypeEnum.DELETE)
	public ResultData delete(@RequestBody List<RoleEntity> roles,HttpServletResponse response, HttpServletRequest request) {
		//获取当前登录管理员的所属角色信息
		ManagerEntity managerSession = BasicUtil.getManager();

		if (roleBiz.deleteRoleByRoles(roles,managerSession)){
			return ResultData.build().success("删除成功",null);
		}
		return ResultData.build().success("删除成功，已过滤当前不可删除角色",null);
	}
}
