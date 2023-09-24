/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.ManagerAdminEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.datascope.biz.IDataBiz;
import net.mingsoft.datascope.entity.DataEntity;
import net.mingsoft.organization.bean.OrganizationEmployeeTreeBean;
import net.mingsoft.organization.bean.OrganizationRoleBean;
import net.mingsoft.organization.biz.IEmployeeBiz;
import net.mingsoft.organization.biz.IOrganizationBiz;
import net.mingsoft.organization.constant.e.DataTypeEnum;
import net.mingsoft.organization.entity.EmployeeEntity;
import net.mingsoft.organization.entity.OrganizationEntity;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.lang.UUID;

/**
 * 员工管理控制层
 * @author 铭飞开源团队
 * 创建日期：2020-1-6 14:53:26<br/>
 * 历史修订：2022-1-19 list() 添加员工数据权限判断与级联查询
 * 		   2022-1-19 save() update() 员工状态为离职时,更新对应管理员的状态的代码移动到EmployeeAop
 */
@Api(tags = {"后端-组织机构模块接口"})
@Controller("organizationEmployeeAction")
@RequestMapping("/${ms.manager.path}/organization/employee")
public class EmployeeAction extends BaseAction{


	/**
	 * 注入员工业务层
	 */
	@Autowired
	private IEmployeeBiz employeeBiz;

	@Autowired
	private IManagerBiz managerBiz;

	@Autowired
	IOrganizationBiz organizationBiz;

	@Autowired
	IDataBiz dataBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("organization:employee:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/organization/employee/index";
	}

	/**
	 * 查询员工列表 带权限
	 * @param employee 员工实体
	 */
	@ApiOperation(value = "查询员工列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "employeeCode", value = "员工编号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "employeeStatus", value = "员工状态", required =false,paramType="query"),
    	@ApiImplicitParam(name = "employeeSex", value = "性别", required =false,paramType="query"),
    	@ApiImplicitParam(name = "employeeRole", value = "所属角色", required =false,paramType="query"),
    	@ApiImplicitParam(name = "postIds", value = "岗位", required =false,paramType="query"),
    	@ApiImplicitParam(name = "organizationId", value = "所属部门", required =false,paramType="query"),
    	@ApiImplicitParam(name = "politics", value = "政治面貌", required =false,paramType="query"),
    	@ApiImplicitParam(name = "education", value = "员工学历", required =false,paramType="query"),
    	@ApiImplicitParam(name = "age", value = "年龄", required =false,paramType="query"),
    	@ApiImplicitParam(name = "employeePhone", value = "手机号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "id", value = "编号", required =false,paramType="query"),
    })
	@RequestMapping(value = "/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore EmployeeEntity employee,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		//List employeeList = employeeBiz.queryListByOrganization(employee);
		ManagerEntity manager = BasicUtil.getManager();
		String managerId = manager.getId();
		EmployeeEntity employeeEntity = employeeBiz.getOne(new QueryWrapper<EmployeeEntity>().eq("manager_id", managerId));
		List<String> organizationList = new ArrayList<>();
		if (employeeEntity != null){
			organizationList = dataBiz.list(
					new QueryWrapper<DataEntity>()
							.eq("DATA_TARGET_ID", employeeEntity.getId())
							.eq("DATA_TYPE",  DataTypeEnum.EMPLOYEE_ORGANIZATION.toString())
			).stream().map(DataEntity::getDataId).collect(Collectors.toList());
		}
		Set<String> organizations = new HashSet<>();
		if (StrUtil.isNotBlank(employee.getOrganizationId())){
			organizations = organizationBiz.queryChildren(Integer.valueOf(employee.getOrganizationId())).stream().map(OrganizationEntity::getId).collect(Collectors.toSet());
			organizations.add(employee.getOrganizationId());
			employee.setOrganizationId(null);
		}
		QueryWrapper<EmployeeEntity> wrapper = new QueryWrapper<>(employee);
		//wrapper.likeLeft("employee_nick_name",employee.getEmployeeNickName());
		if (!organizationList.isEmpty()){
			if (organizations != null && !organizations.isEmpty()) {
				organizationList = CollUtil.intersection(organizations,organizationList).stream().collect(Collectors.toList());
			}
			wrapper.in("organization_id",organizationList);
		}else if (CollUtil.isNotEmpty(organizations)){
			wrapper.in("organization_id",organizations);
		}else if (CollUtil.isEmpty(organizations) && CollUtil.isEmpty(organizationList) && !ManagerAdminEnum.SUPER.toString().equalsIgnoreCase(manager.getManagerAdmin())){
		    return ResultData.build().success();
		}
		List<EmployeeEntity> employeeList = employeeBiz.list(wrapper);
		return ResultData.build().success(new EUListBean(employeeList,(int)BasicUtil.endPage(employeeList).getTotal()));
	}
	/**
	 * 查询角色员工树形列表
	 * @param employee 员工实体
	 */
	@ApiOperation(value = "查询角色员工树形列表")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "employeeCode", value = "员工编号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "employeeStatus", value = "员工状态", required =false,paramType="query"),
    	@ApiImplicitParam(name = "employeeSex", value = "性别", required =false,paramType="query"),
    	@ApiImplicitParam(name = "employeeRole", value = "所属角色", required =false,paramType="query"),
    	@ApiImplicitParam(name = "postIds", value = "岗位", required =false,paramType="query"),
    	@ApiImplicitParam(name = "organizationId", value = "所属部门", required =false,paramType="query"),
    	@ApiImplicitParam(name = "politics", value = "政治面貌", required =false,paramType="query"),
    	@ApiImplicitParam(name = "education", value = "员工学历", required =false,paramType="query"),
    	@ApiImplicitParam(name = "age", value = "年龄", required =false,paramType="query"),
    	@ApiImplicitParam(name = "employeePhone", value = "手机号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "id", value = "编号", required =false,paramType="query"),
    })
	@RequestMapping(value = "/roleTreelist",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData roleTreelist(@ModelAttribute @ApiIgnore EmployeeEntity employee,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		List<OrganizationRoleBean> employeeList = employeeBiz.queryRoleTreeList(employee);
		return ResultData.build().success(employeeList);
	}

	/**
	 * 返回编辑界面employee_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions(value = {"organization:employee:update", "organization:employee:save"}, logical = Logical.OR)
	public String form(@ModelAttribute EmployeeEntity employee,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		if(employee.getId()!=null){
			BaseEntity employeeEntity = employeeBiz.getEntity(Integer.parseInt(employee.getId()));
			model.addAttribute("employeeEntity",employeeEntity);
		}
		return "/organization/employee/form";
	}
	/**
	 * 获取员工
	 * @param employee 员工实体
	 */
	@ApiOperation(value = "获取员工列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore EmployeeEntity employee,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		EmployeeEntity _employee = (EmployeeEntity)employeeBiz.getById(employee.getId());
		return ResultData.build().success(_employee);
	}

	@ApiOperation(value = "保存员工列表接口")
	 @ApiImplicitParams({
		@ApiImplicitParam(name = "employeeCode", value = "员工编号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "employeeStatus", value = "员工状态", required =true,paramType="query"),
    	@ApiImplicitParam(name = "employeeSex", value = "性别", required =true,paramType="query"),
    	@ApiImplicitParam(name = "employeeRole", value = "所属角色", required =true,paramType="query"),
    	@ApiImplicitParam(name = "postIds", value = "岗位", required =true,paramType="query"),
		@ApiImplicitParam(name = "organizationId", value = "所属部门", required =false,paramType="query"),
    	@ApiImplicitParam(name = "employeePolitics", value = "政治面貌", required =true,paramType="query"),
    	@ApiImplicitParam(name = "education", value = "员工学历", required =true,paramType="query"),
    	@ApiImplicitParam(name = "age", value = "年龄", required =true,paramType="query"),
    	@ApiImplicitParam(name = "employeePhone", value = "手机号", required =true,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	/**
	* 保存员工
	* @param employee 员工实体
	*/
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存员工", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("organization:employee:save")
	public ResultData save(@ModelAttribute @ApiIgnore EmployeeEntity employee, HttpServletResponse response, HttpServletRequest request) {
		// 验证员工状态的值是否合法
		if(StringUtil.isBlank(employee.getEmployeeStatus())){
			return ResultData.build().error(getResString("err.empty", this.getResString("employee.status")));
		}
		// 验证性别的值是否合法
		if(StringUtil.isBlank(employee.getEmployeeSex())){
			return ResultData.build().error(getResString("err.empty", this.getResString("employee.sex")));
		}

		// 验证岗位的值是否合法
		if(StringUtil.isBlank(employee.getPostIds())){
			return ResultData.build().error(getResString("err.empty", this.getResString("post.ids")));
		}
		// 验证政治面貌的值是否合法
		if(StringUtil.isBlank(employee.getEmployeePolitics())){
			return ResultData.build().error(getResString("err.empty", this.getResString("politics")));
		}
		// 验证员工学历的值是否合法
		if(StringUtil.isBlank(employee.getEmployeeEducation())){
			return ResultData.build().error(getResString("err.empty", this.getResString("education")));
		}
		// 验证年龄的值是否合法birth.date=出生日期
		if(StringUtil.isBlank(employee.getEmployeeBirthDate())){
			return ResultData.build().error(getResString("err.empty", this.getResString("birth.date")));
		}
		// 验证手机号的值是否合法
		if(StringUtil.isBlank(employee.getEmployeePhone())){
			return ResultData.build().error(getResString("err.empty", this.getResString("employee.phone")));
		}
		//判断账号是否已经存在
		//	if (managerBiz.getManagerByManagerName(employee.getManagerName()) != null){
		//		return ResultData.build().error(getResString("err.exist", this.getResString("employee.managerName")));
		//	}
		//查询手机号是否已存在
		LambdaQueryWrapper<EmployeeEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(EmployeeEntity::getEmployeePhone,employee.getEmployeePhone());
		EmployeeEntity one = employeeBiz.getOne(wrapper);
		if (one != null){
			return ResultData.build().error(getResString("err.exist",getResString("employee.phone")));
		}
		employeeBiz.saveEmployee(employee);

		// 需导入ms-id依赖
		// employee.setEmployeeCode(IDUtil.getId("员工编号",Long.parseLong(employee.getId()),null));
		// 默认使用主键id的生成策略
		//employee.setEmployeeCode("EMP-"+employee.getId());


		return ResultData.build().success(employee);
	}

	/**
	 * @param employees 员工实体
	 */
	@ApiOperation(value = "批量删除员工列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除员工", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("organization:employee:del")
	public ResultData delete(@RequestBody List<EmployeeEntity> employees,HttpServletResponse response, HttpServletRequest request) {
		List<Integer> ids = new ArrayList<>();
		for(int i = 0;i<employees.size();i++){
			ids.add(Integer.parseInt(employees.get(i).getId()));
		}
		employeeBiz.removeByIds(ids);
		return ResultData.build().success();
	}

	/**
	*	更新员工列表
	* @param employee 员工实体
	*/
	 @ApiOperation(value = "更新员工列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
		@ApiImplicitParam(name = "employeeCode", value = "员工编号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "employeeStatus", value = "员工状态", required =true,paramType="query"),
    	@ApiImplicitParam(name = "employeeSex", value = "性别", required =true,paramType="query"),
    	@ApiImplicitParam(name = "employeeRole", value = "所属角色", required =true,paramType="query"),
    	@ApiImplicitParam(name = "postIds", value = "岗位", required =true,paramType="query"),
		@ApiImplicitParam(name = "organizationId", value = "所属部门", required =false,paramType="query"),
    	@ApiImplicitParam(name = "politics", value = "政治面貌", required =true,paramType="query"),
    	@ApiImplicitParam(name = "education", value = "员工学历", required =true,paramType="query"),
    	@ApiImplicitParam(name = "age", value = "年龄", required =true,paramType="query"),
    	@ApiImplicitParam(name = "employeePhone", value = "手机号", required =true,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "id", value = "编号", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新员工", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("organization:employee:update")
	public ResultData update(@ModelAttribute @ApiIgnore EmployeeEntity employee, HttpServletResponse response,
			HttpServletRequest request) {
		 //判断账号是否已经存在
//		 ManagerEntity managerEntity = managerBiz.getManagerByManagerName(employee.getManagerName());
//		 if (managerEntity != null){
//		 	if (managerEntity.getIntId() != employee.getManagerId()){
//				return ResultData.build().error(getResString("err.exist", this.getResString("employee.managerName")));
//			}
//		 }
		//验证员工状态的值是否合法
		if(StringUtil.isBlank(employee.getEmployeeStatus())){
			return ResultData.build().error(getResString("err.empty", this.getResString("employee.status")));
		}
		//验证性别的值是否合法
		if(StringUtil.isBlank(employee.getEmployeeSex())){
			return ResultData.build().error(getResString("err.empty", this.getResString("employee.sex")));
		}

		//验证岗位的值是否合法
		if(StringUtil.isBlank(employee.getPostIds())){
			return ResultData.build().error(getResString("err.empty", this.getResString("post.ids")));
		}
		//验证政治面貌的值是否合法
		if(StringUtil.isBlank(employee.getEmployeePolitics())){
			return ResultData.build().error(getResString("err.empty", this.getResString("politics")));
		}
		//验证员工学历的值是否合法
		if(StringUtil.isBlank(employee.getEmployeeEducation())){
			return ResultData.build().error(getResString("err.empty", this.getResString("education")));
		}
		//验证年龄的值是否合法
		if(StringUtil.isBlank(employee.getEmployeeBirthDate())){
			return ResultData.build().error(getResString("err.empty", this.getResString("birth.date")));
		}
		//验证手机号的值是否合法
		if(StringUtil.isBlank(employee.getEmployeePhone())){
			return ResultData.build().error(getResString("err.empty", this.getResString("employee.phone")));
		}
		 //查询手机号是否已存在
		 LambdaQueryWrapper<EmployeeEntity> wrapper = new LambdaQueryWrapper<>();
		 wrapper.eq(EmployeeEntity::getEmployeePhone,employee.getEmployeePhone());
		 EmployeeEntity one = employeeBiz.getOne(wrapper);
		 //存在相同手机号且不是更新人信息则拦截
		 if (one != null && !one.getId().equals(employee.getId())){
			 return ResultData.build().error(getResString("err.exist",this.getResString("employee.phone")));
		 }
		employeeBiz.updateById(employee);
		return ResultData.build().success(employee);
	}


	/**
	 * 查询所有员工列表
	 *
	 */
	@ApiOperation(value = "查询所有员工列表")
	@RequestMapping(value = "/queryAll",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData queryAll() {
		BasicUtil.startPage();
		ManagerEntity manager = BasicUtil.getManager();
		String managerId = manager.getId();
		EmployeeEntity employeeEntity = employeeBiz.getOne(new QueryWrapper<EmployeeEntity>().eq("manager_id", managerId));
		BasicUtil.startPage();
		Set<Integer> organizationLeaderList = new HashSet<>();
		List<String> organizationIds = new ArrayList<>();
		if (employeeEntity != null){
			organizationIds = dataBiz.list(
					new QueryWrapper<DataEntity>()
							.eq("DATA_TARGET_ID", employeeEntity.getId())
							.eq("DATA_TYPE",  DataTypeEnum.EMPLOYEE_ORGANIZATION.toString())
			).stream().map(DataEntity::getDataId).collect(Collectors.toList());
		}
		if (!organizationIds.isEmpty()){
			organizationLeaderList = organizationBiz.listByIds(organizationIds).stream().map(OrganizationEntity::getOrganizationLeader).collect(Collectors.toSet());
		}

		QueryWrapper<EmployeeEntity> wrapper = new QueryWrapper<>();
		organizationLeaderList.remove(null);
		if (!organizationLeaderList.isEmpty()){
			wrapper.in("id", organizationLeaderList);
		}
		List<EmployeeEntity> employeeList = employeeBiz.list(wrapper);


		return ResultData.build().success(new EUListBean(employeeList,(int)BasicUtil.endPage(employeeList).getTotal()));
	}

	/**
	 * 获取级联选择的树形数据
	 * @param organizationEntity
	 * @return
	 */
	@ApiOperation(value = "获取级联选择的树形数据接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "organizationTitle", value = "部门名称", required =false,paramType="query"),
			@ApiImplicitParam(name = "organizationId", value = "所属部门", required =false,paramType="query"),
			@ApiImplicitParam(name = "organizationStatus", value = "部门状态", required =false,paramType="query"),
			@ApiImplicitParam(name = "organizationCode", value = "部门编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "organizationLeaders", value = "分管领导", required =false,paramType="query"),
			@ApiImplicitParam(name = "organizationLeader", value = "负责人", required =false,paramType="query"),
			@ApiImplicitParam(name = "organizationType", value = "机构类型", required =false,paramType="query"),
			@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
			@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@GetMapping("/getPostEmployeeBeans")
	@ResponseBody
	public ResultData getPostEmployeeBeans(@ModelAttribute @ApiIgnore OrganizationEntity organizationEntity){
		if(StrUtil.isBlank(organizationEntity.getOrganizationId())){
			organizationEntity.setOrganizationId("0");
		}
		Set<OrganizationEmployeeTreeBean> postEmployeeTreeBeans = employeeBiz.getPostEmployeeBeans(organizationEntity.getOrganizationId()).stream().collect(Collectors.toSet());
		postEmployeeTreeBeans.remove(null);

	 	return ResultData.build().success(postEmployeeTreeBeans);
	}


}
