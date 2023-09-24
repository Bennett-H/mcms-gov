/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
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
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.datascope.biz.IDataBiz;
import net.mingsoft.datascope.entity.DataEntity;
import net.mingsoft.datascope.utils.DataScopeUtil;
import net.mingsoft.organization.bean.OrganizationBean;
import net.mingsoft.organization.biz.IEmployeeBiz;
import net.mingsoft.organization.biz.IOrganizationBiz;
import net.mingsoft.organization.constant.e.DataTypeEnum;
import net.mingsoft.organization.entity.EmployeeEntity;
import net.mingsoft.organization.entity.OrganizationEntity;
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
import java.util.*;
import java.util.stream.Collectors;
/**
 * 组织机构管理控制层
 * @author 铭飞开源团队
 * 创建日期：2020-1-6 14:53:26<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-组织机构模块接口"})
@Controller("organizationOrganizationAction")
@RequestMapping("/${ms.manager.path}/organization/organization")
public class OrganizationAction extends BaseAction{


	/**
	 * 注入组织机构业务层
	 */
	@Autowired
	private IOrganizationBiz organizationBiz;

	@Autowired
	IDataBiz dataBiz;
	/**
	 * 注入员工业务层
	 */
	@Autowired
	private IEmployeeBiz employeeBiz;
	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("organization:organization:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/organization/organization/index";
	}

	/**
	 * 查询组织机构列表
	 * @param organization 组织机构实体
	 */
	@ApiOperation(value = "查询组织机构列表接口")
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
    	@ApiImplicitParam(name = "del", value = "删除标记", required =false,paramType="query"),
    	@ApiImplicitParam(name = "id", value = "编号", required =false,paramType="query"),
    })
	@RequestMapping(value = "/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore OrganizationEntity organization,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		ManagerEntity manager = BasicUtil.getManager();
		String managerId = manager.getId();
		EmployeeEntity employeeEntity = employeeBiz.getOne(new QueryWrapper<EmployeeEntity>().eq("manager_id", managerId));
		BasicUtil.startPage();
		//防止没有绑定员工的管理员登录报错
		if (employeeEntity != null){
			DataScopeUtil.start(manager.getId(),employeeEntity.getId(), DataTypeEnum.EMPLOYEE_ORGANIZATION.toString(),true);
		}
		List<OrganizationEntity> organizationList = organizationBiz.query(organization);

		//因为数据权限值保存跟节点的数据，所以需要组织部门的树形结构。需要遍历部门数据
		Set ids = CollUtil.newHashSet();
		organizationList.forEach(o-> {
			ids.add(o.getId());
			if(StringUtils.isNotBlank(o.getOrganizationParentId())) {
				ids.addAll(Arrays.asList(o.getOrganizationParentId().split(",")));
			}
		});

		if(CollUtil.isNotEmpty(ids)) {
			//重新通过ids去查询部门数据
			organizationList = organizationBiz.listByIds(ids);
		}

		return ResultData.build().success(new EUListBean(organizationList,(int)BasicUtil.endPage(organizationList).getTotal()));
	}

	@ApiOperation(value = "查询部门下的员工列表接口")
	@GetMapping("/departments")
	@ResponseBody
	public ResultData departments(@ModelAttribute @ApiIgnore OrganizationEntity organization, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
		List organizationList = organizationBiz.query(organization);
		EmployeeEntity employeeEntity = new EmployeeEntity();
		HashMap<Object, Object> data = MapUtil.newHashMap();
		if(StrUtil.isNotBlank(organization.getOrganizationId())){
			employeeEntity.setOrganizationId(organization.getOrganizationId());
			List employees = employeeBiz.query(employeeEntity);
			data.put("employees",employees);
		}else {
			data.put("employees",Collections.EMPTY_LIST);
		}
		data.put("childDepartments",organizationList);
		return ResultData.build().success(data);
	}
	/**
	 * 返回编辑界面organization_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions(value = {"organization:organization:update", "organization:organization:save"}, logical = Logical.OR)
	public String form(@ModelAttribute OrganizationEntity organization,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		if(organization.getId()!=null){
			BaseEntity organizationEntity = organizationBiz.getEntity(Integer.parseInt(organization.getId()));
			model.addAttribute("organizationEntity",organizationEntity);
		}
		return "/organization/organization/form";
	}
	/**
	 * 获取组织机构
	 * @param organization 组织机构实体
	 */
	@ApiOperation(value = "获取组织机构列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore OrganizationEntity organization,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(organization.getId()==null) {
			return ResultData.build().error();
		}
		OrganizationEntity _organization = (OrganizationEntity)organizationBiz.getEntity(Integer.parseInt(organization.getId()));
		return ResultData.build().success(_organization);
	}

	@ApiOperation(value = "保存组织机构列表接口")
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
		@ApiImplicitParam(name = "del", value = "删除标记", required =false,paramType="query"),
		@ApiImplicitParam(name = "id", value = "编号", required =false,paramType="query"),
	})

	/**
	* 保存组织机构
	* @param organization 组织机构实体
	*/
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存组织机构", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("organization:organization:save")
	public ResultData save(@ModelAttribute @ApiIgnore OrganizationEntity organization, HttpServletResponse response, HttpServletRequest request) {
		if(!StringUtil.checkLength(organization.getOrganizationTitle()+"", 1, 30)){
			return ResultData.build().error(getResString("err.length", this.getResString("organization.title"), "1", "30"));
		}
		organizationBiz.save(organization);

		// 需要导入ms-id插件
		// organization.setOrganizationCode(IDUtil.getId("部门编号",Long.parseLong(organization.getId()),null));

		ManagerEntity manager = BasicUtil.getManager();
		String managerId = manager.getId();
		EmployeeEntity employeeEntity = employeeBiz.getOne(new QueryWrapper<EmployeeEntity>().eq("manager_id", managerId));
		// 给新增的员工添加权限
		if (employeeEntity != null){
			DataEntity dataEntity = new DataEntity();
			dataEntity.setDataType( DataTypeEnum.EMPLOYEE_ORGANIZATION.toString());
			dataEntity.setDataTargetId(employeeEntity.getId());
			dataEntity.setDataId(organization.getId());
			dataBiz.save(dataEntity);
		}

		// 默认使用主键id的生成策略
		organization.setOrganizationCode("ORG-"+organization.getId());

		 organizationBiz.updateById(organization);

		return ResultData.build().success(organization);
	}

	/**
	 * @param organizations 组织机构实体
	 */
	@ApiOperation(value = "批量删除组织机构列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除组织机构", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("organization:organization:del")
	public ResultData delete(@RequestBody List<OrganizationEntity> organizations,HttpServletResponse response, HttpServletRequest request) {
		int[] ids = new int[organizations.size()];
		Set<String> idSet = new HashSet<>();
		for(int i = 0;i<organizations.size();i++){
			ids[i] =Integer.parseInt(organizations.get(i).getId());
			idSet.add(organizations.get(i).getId());
			idSet.addAll(organizationBiz.queryChildren(organizations.get(i).getIntegerId()).stream().map(OrganizationEntity::getId).collect(Collectors.toList()));
		}
		organizationBiz.delete(ids);
		LambdaQueryWrapper<EmployeeEntity> wrapper = new QueryWrapper<EmployeeEntity>().lambda().in(EmployeeEntity::getOrganizationId, idSet);
		List<String> employeeIds = employeeBiz.list(wrapper).stream().map(EmployeeEntity::getId).collect(Collectors.toList());
		if (!employeeIds.isEmpty()){
			employeeBiz.removeByIds(employeeIds);
		}
		return ResultData.build().success();
	}

	/**
	*	更新组织机构列表
	* @param organization 组织机构实体
	*/
	 @ApiOperation(value = "更新组织机构列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
		@ApiImplicitParam(name = "organizationTitle", value = "部门名称", required =false,paramType="query"),
		@ApiImplicitParam(name = "organizationId", value = "所属部门", required =false,paramType="query"),
		@ApiImplicitParam(name = "organizationStatus", value = "部门状态", required =false,paramType="query"),
		@ApiImplicitParam(name = "organizationCode", value = "部门编号", required =false,paramType="query"),
		@ApiImplicitParam(name = "organizationLeaders", value = "分管领导", required =false,paramType="query"),
		@ApiImplicitParam(name = "organizationLeader", value = "负责人", required =false,paramType="query"),
		@ApiImplicitParam(name = "organizationType", value = "机构类型", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "id", value = "编号", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新组织机构", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("organization:organization:update")
	public ResultData update(@ModelAttribute @ApiIgnore OrganizationEntity organization, HttpServletResponse response,
			HttpServletRequest request) {
		if(!StringUtil.checkLength(organization.getOrganizationTitle()+"", 1, 30)){
			return ResultData.build().error(getResString("err.length", this.getResString("organization.title"), "1", "30"));
		}
		if(!StringUtil.checkLength(organization.getOrganizationCode()+"", 1, 10)){
			return ResultData.build().error(getResString("err.length", this.getResString("organization.code"), "1", "10"));
		}
		organizationBiz.updateEntity(organization);
		return ResultData.build().success(organization);
	}

	/**
	 * 根据当前session获取员工信息
	 * @return
	 */
	@ApiOperation(value = "根据当前session获取员工信息")
	@GetMapping("/getOrganizationBySession")
	@ResponseBody
	public ResultData getOrganizationBySession(){
		LambdaQueryWrapper<EmployeeEntity> employeeWrapper = new LambdaQueryWrapper<>();
		employeeWrapper.eq(EmployeeEntity::getManagerId, BasicUtil.getManager().getId());
		EmployeeEntity employee = employeeBiz.getOne(employeeWrapper);
		OrganizationEntity organization = null;
		if (employee != null) {
			organization = organizationBiz.getById(employee.getOrganizationId());
		}
		//获取当前组织机构的完整层级
		OrganizationBean organizationBean = organizationBiz.queryHierarchy(organization);
		return ResultData.build().success(organizationBean);
	}


	/**
	 * 根据managerId获取组织机构信息
	 * @return
	 */
	@ApiOperation(value = "根据管理员id获取对应的员工部门")
	@ApiImplicitParam(name = "managerId", value = "管理员id", required =true,paramType="query")
	@GetMapping("/getOrganizationByManagerId")
	@ResponseBody
	public ResultData getOrganizationByManagerId(@ApiIgnore String managerId){
		LambdaQueryWrapper<EmployeeEntity> employeeWrapper = new LambdaQueryWrapper<>();
		employeeWrapper.eq(EmployeeEntity::getManagerId, managerId);
		EmployeeEntity employee = employeeBiz.getOne(employeeWrapper);
		OrganizationEntity organization = null;
		if (employee != null) {
			organization = organizationBiz.getById(employee.getOrganizationId());
		}
		//获取当前组织机构的完整层级
		OrganizationBean organizationBean = organizationBiz.queryHierarchy(organization);
		return ResultData.build().success(organizationBean);
	}


	@ApiOperation(value = "校验部门是否重复")
	@GetMapping("verify")
	@ResponseBody
	public ResultData verify(String fields, String id,String idName){
		boolean verify = false;
		HashMap field = JSONUtil.toBean(fields, HashMap.class);
		if(StringUtils.isBlank(id)){
			verify = validated("organization",field);
		}else{
			verify = validated("organization",field,id,idName);
		}
		if(verify){
			return ResultData.build().success(false);
		}else {
			return ResultData.build().success(true);
		}
	}
	/**
	 * 适用于insert save数据时进行唯一性判断
	 * 判断指定字段在数据库是否已经存在
	 * @param tableName 表名
	 * @return
	 */
	protected boolean validated(String tableName,Map<String,Object> field) {
		Map where = new HashMap<>(1);
		field.forEach((key, value) -> {
			if(!value.toString().isEmpty()){
				where.put(key, value);
			}
		});
		List list = organizationBiz.queryBySQL(tableName, null, where);
		if (ObjectUtil.isNotNull(list) && !list.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * 适用于update 更新 数据时进行唯一性判断
	 * 判断指定字段在数据库是否已经存在
	 * 主键id用来防止跟自身字段验证重复
	 * @param tableName 表名
	 * @param id 要更新的主键id
	 * @param idName 要更新的主键名称
	 * @return
	 */
	protected boolean validated(String tableName, Map<String,Object> field, String id,String idName) {
		Map where = new HashMap<>(1);
		field.forEach((key, value) -> {
			if(!value.toString().isEmpty()){
				where.put(key, value);
			}
		});
		// TODO: 2021/10/27  更改为MybatisPlus方法
		List<HashMap<String,Object>> list = organizationBiz.queryBySQL(tableName, null, where);
		if (ObjectUtil.isNotNull(list) && !list.isEmpty()) {
			//更新时判断是否是本身
			if(list.size() == 1){
				Object organizationId = list.get(0).get(idName) == null ? list.get(0).get(idName.toUpperCase()) : list.get(0).get(idName);
				if(id.equals(organizationId.toString())){
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
