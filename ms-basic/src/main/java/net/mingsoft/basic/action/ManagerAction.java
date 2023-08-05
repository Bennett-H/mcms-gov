/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.ManagerAdminEnum;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员管理控制层
 * @author 铭飞开发团队
 * @version
 * 版本号：1.0<br/>
 * 创建日期：2017-8-24 23:40:55<br/>
 * 历史修订： 2022-1-27 12:00 list(), query() 添加站群插件查询管理员的容错 <br/>
 */
@Api(tags={"后端-基础接口"})
@Controller
@RequestMapping("/${ms.manager.path}/basic/manager")
public class ManagerAction extends BaseAction{

	/**
	 * 注入管理员业务层
	 */
	@Autowired
	private IManagerBiz managerBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("basic:manager:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/basic/manager/index";
	}


	@ApiOperation(value = "查询管理员列表")
	@GetMapping("/list")
	@RequiresPermissions("basic:manager:view")
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore ManagerEntity manager,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model) {
		BasicUtil.startPage();
		AppEntity websiteApp = BasicUtil.getWebsiteApp();
		List<ManagerEntity> managerList;
		if (websiteApp != null){
			int appId = websiteApp.getAppId();
			LambdaQueryWrapper<ManagerEntity> wrapper = new LambdaQueryWrapper<>();
			wrapper.like(StringUtils.isNotBlank(manager.getManagerName()), ManagerEntity::getManagerName, manager.getManagerName());
			wrapper.like(StringUtils.isNotBlank(manager.getManagerNickName()), ManagerEntity::getManagerNickName, manager.getManagerNickName());
			wrapper.apply("APP_ID={0}",appId);
			managerList = managerBiz.list(wrapper);
		}else {
			managerList = managerBiz.query(manager);
		}
		List<ManagerEntity> allManager = managerBiz.queryAllManager(managerList);
		return ResultData.build().success( BasicUtil.filter(new EUListBean(allManager, (int) BasicUtil.endPage(allManager).getTotal()), "managerPassword",
				"updateBy",
				"createBy",
				"del"));

	}

	@ApiOperation(value = "查询管理员列表,去掉当前管理员id，确保不能删除和修改自己")
	@GetMapping("/query")
	@RequiresPermissions("basic:manager:view")
	@ResponseBody
	public ResultData query(HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
		ManagerEntity manager = BasicUtil.getManager();
		BasicUtil.startPage();
		AppEntity websiteApp = BasicUtil.getWebsiteApp();
		List<ManagerEntity> managerList;
		if (websiteApp != null){
			int appId = websiteApp.getAppId();
			QueryWrapper<ManagerEntity> wrapper = new QueryWrapper<ManagerEntity>().eq("APP_ID", appId);
			managerList = managerBiz.list(wrapper);
		}else {
			managerList = managerBiz.list();

		}
		List<ManagerEntity> allManager = managerBiz.queryAllManager(managerList);
		for (ManagerEntity _manager : allManager) {
			assert manager != null;
			if (_manager.getId().equals(manager.getId())) {
				_manager.setId("0");
			}
		}
		return ResultData.build().success(new EUListBean(allManager, (int) BasicUtil.endPage(allManager).getTotal()));

	}

	@ApiOperation(value="获取管理员接口")
	@GetMapping("/get")
	@RequiresPermissions("basic:manager:view")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore ManagerEntity manager,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		ManagerEntity managerEntity;
		//判断是否传managerId
		if (StringUtils.isNotEmpty(manager.getId())) {
			managerEntity = managerBiz.getById(manager.getId());
		} else {
			ManagerEntity managerSession = BasicUtil.getManager();
			if (managerSession == null) {
				return ResultData.build().error("管理员已失效!");
			}
			managerEntity = managerBiz.getById(managerSession.getId());
		}
		if (managerEntity != null){
			managerEntity.setManagerPassword("");
		}
		return ResultData.build().success(managerEntity);
	}

	@ApiOperation(value="获取当前管理员信息接口")
	@GetMapping("/info")
	@ResponseBody
	public ResultData info(HttpServletResponse response, HttpServletRequest request){
		ManagerEntity managerEntity;
		ManagerEntity manager =  BasicUtil.getManager();
		if (manager == null) {
			return ResultData.build().error("管理员已失效!");
		}
		managerEntity = managerBiz.getById(manager.getId());
		if (managerEntity != null){
			managerEntity.setManagerPassword("");
		}
		return ResultData.build().success(managerEntity);
	}


	@ApiOperation(value = "保存管理员实体")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "managerName", value = "帐号", required = true,paramType="query"),
			@ApiImplicitParam(name = "managerNickName", value = "昵称", required = true,paramType="query"),
			@ApiImplicitParam(name = "managerPassword", value = "密码", required = true,paramType="query"),
			@ApiImplicitParam(name = "roleId", value = "角色ID", required = false,paramType="query"),
			@ApiImplicitParam(name = "peopleId", value = "用户ID", required = false,paramType="query"),
	})
	@LogAnn(title = "保存管理员实体",businessType= BusinessTypeEnum.INSERT)
	@PostMapping("/save")
	@ResponseBody
	@RequiresPermissions("basic:manager:save")
	public ResultData save(@ModelAttribute @ApiIgnore ManagerEntity manager, HttpServletResponse response, HttpServletRequest request) {
		//用户名是否存在
		if(managerBiz.getManagerByManagerName(manager.getManagerName())!= null){
			return ResultData.build().error(getResString("err.exist", this.getResString("manager.name")));
		}
		//验证管理员用户名的值是否合法
		if(StringUtils.isBlank(manager.getManagerName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("manager.name")));
		}
		if(!StringUtil.checkLength(manager.getManagerName()+"", 6, 30)){
			return ResultData.build().error(getResString("err.length", this.getResString("manager.name"), "6", "30"));
		}
		if (!manager.getManagerPassword().matches("^[a-zA-Z0-9_]{6,30}$")) {
			ResultData.build().error(getResString("err.error", this.getResString("manager.name")));
		}

		//验证管理员昵称的值是否合法
		if(StringUtils.isBlank(manager.getManagerNickName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("manager.nickname")));
		}
		if(!StringUtil.checkLength(manager.getManagerNickName()+"", 1, 30)){
			return ResultData.build().error(getResString("err.length", this.getResString("manager.nickname"), "1", "30"));
		}
		//验证管理员密码的值是否合法
		if(StringUtils.isBlank(manager.getManagerPassword())){
			return ResultData.build().error(getResString("err.empty", this.getResString("manager.password")));
		}
		if(!StringUtil.checkLength(manager.getManagerPassword()+"", 1, 30)){
			return ResultData.build().error(getResString("err.length", this.getResString("manager.password"), "1", "30"));
		}
		if (!manager.getManagerPassword().matches("(?!^(\\d+|[a-zA-Z]+|[~!@#$%^&*?]+)$)^[\\w~!@#$%^&*?]{6,30}$")) {
			ResultData.build().error(getResString("err.error", this.getResString("manager.password")));
		}

		manager.setManagerPassword(SecureUtil.md5(manager.getManagerPassword()));
		managerBiz.save(manager);
		managerBiz.updateCache();
		return ResultData.build().success(manager);
	}


	@ApiOperation(value = "批量删除管理员")
	@LogAnn(title = "批量删除管理员",businessType= BusinessTypeEnum.DELETE)
	@PostMapping("/delete")
	@ResponseBody
	@RequiresPermissions("basic:manager:del")
	public ResultData delete(@RequestBody List<ManagerEntity> managers,HttpServletResponse response, HttpServletRequest request) {
		// 查询自己Id,不允许删除自己
		ManagerEntity manager = BasicUtil.getManager();
		Integer[] ids = new Integer[managers.size()];
		for(int i = 0;i<managers.size();i++){
			ids[i] = Integer.parseInt(managers.get(i).getId());
		}
		// 先查出需要删除id的信息
		List<ManagerEntity> managerEntities = managerBiz.listByIds(Arrays.asList(ids));
		managerEntities = managerEntities.stream().filter(managerEntity -> {
			return ManagerAdminEnum.SUPER.toString().equals(managerEntity.getManagerAdmin()) || ManagerAdminEnum.SUPERADMIN.toString().equals(managerEntity.getManagerAdmin()) || manager.getId().equals(managerEntity.getId());
		}).collect(Collectors.toList());
		if (CollectionUtil.isNotEmpty(managerEntities)) {
			LOG.error("非法操作删除超管账号或自己账号");
			return ResultData.build().error(getResString("fail", getResString("remove")));
		}
		managerBiz.removeByIds(Arrays.asList(ids));
		managerBiz.updateCache();
		return ResultData.build().success();
	}

	@ApiOperation(value = "更新管理员信息管理员")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "managerName", value = "帐号", required = true,paramType="query"),
			@ApiImplicitParam(name = "managerNickName", value = "昵称", required = true,paramType="query"),
			@ApiImplicitParam(name = "managerPassword", value = "密码", required = true,paramType="query"),
			@ApiImplicitParam(name = "roleId", value = "角色ID", required = false,paramType="query"),
			@ApiImplicitParam(name = "peopleId", value = "用户ID", required = false,paramType="query"),
	})
	@LogAnn(title = "更新管理员信息管理员",businessType= BusinessTypeEnum.UPDATE)
	@PostMapping("/update")
	@ResponseBody
	@RequiresPermissions("basic:manager:update")
	public ResultData update(@ModelAttribute @ApiIgnore ManagerEntity manager) {
		managerBiz.updateCache();
		ManagerEntity _manager = managerBiz.getManagerByManagerName(manager.getManagerName());
		//用户名是否存在
		if(_manager != null){
			if(!_manager.getId().equals(manager.getId())){
				return ResultData.build().error(getResString("err.exist", this.getResString("manager.name")));
			}
		}
		_manager = managerBiz.getById(manager.getId());
		if (_manager == null) {
			return ResultData.build().error(getResString("err.not.exist", getResString("managerName")));
		}
		// 重新把管理员标识放回去，防止有人非法篡改管理员标识
		manager.setManagerAdmin(_manager.getManagerAdmin());
		//验证管理员用户名的值是否合法
		if(StringUtils.isBlank(manager.getManagerName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("manager.name")));
		}
		if(!StringUtil.checkLength(manager.getManagerName()+"", 6, 30)){
			return ResultData.build().error(getResString("err.length", this.getResString("manager.name"), "6", "30"));
		}
		if (!manager.getManagerPassword().matches("^[a-zA-Z0-9_]{6,30}$")) {
			ResultData.build().error(getResString("err.error", this.getResString("manager.name")));
		}
		//验证管理员昵称的值是否合法
		if(StringUtils.isBlank(manager.getManagerNickName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("manager.nickname")));
		}
		if(!StringUtil.checkLength(manager.getManagerNickName()+"", 1, 30)){
			return ResultData.build().error(getResString("err.length", this.getResString("manager.nickname"), "1", "30"));
		}
		//验证管理员密码的值是否合法
		if(!StringUtils.isBlank(manager.getManagerPassword())){
			if(!StringUtil.checkLength(manager.getManagerPassword()+"", 6, 30)){
				return ResultData.build().error(getResString("err.length", this.getResString("manager.password"), "6", "30"));
			}
			if (!manager.getManagerPassword().matches("(?!^(\\d+|[a-zA-Z]+|[~!@#$%^&*?]+)$)^[\\w~!@#$%^&*?]{6,30}$")) {
				ResultData.build().error(getResString("err.error", this.getResString("manager.password")));
			}
			manager.setManagerPassword(SecureUtil.md5(manager.getManagerPassword()));
		} else {
			manager.setManagerPassword(null);
		}
		managerBiz.updateById(manager);
		return ResultData.build().success(manager);
	}

}
