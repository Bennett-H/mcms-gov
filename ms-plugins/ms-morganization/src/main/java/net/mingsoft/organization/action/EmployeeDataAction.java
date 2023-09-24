/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.action;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.datascope.biz.IDataBiz;
import net.mingsoft.datascope.biz.IDataConfigBiz;
import net.mingsoft.datascope.entity.DataConfigEntity;
import net.mingsoft.datascope.entity.DataEntity;
import net.mingsoft.datascope.utils.DataScopeUtil;
import net.mingsoft.organization.bean.EmployeeDataBean;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 员工权限管理控制层
 * @author 铭飞开源团队
 * 创建日期：2020-6-13 15:24:39<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-组织机构模块接口"})
@Controller("organizationEmployeeDataAction")
@RequestMapping("/${ms.manager.path}/organization/employeeData")
public class EmployeeDataAction extends BaseAction{



	@Autowired
	IDataBiz dataBiz;

	@Autowired
	private IDataConfigBiz dataConfigBiz;

	/**
	 * 查询员工权限列表
	 * @param employeeData 员工权限实体
	 */
	@ApiOperation(value = "查询员工权限列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "managerId", value = "员工Id", required =false,paramType="query"),
    	@ApiImplicitParam(name = "organizationIds", value = "组织机构ids", required =false,paramType="query"),
    	@ApiImplicitParam(name = "dataType", value = "权限类型", required =false,paramType="query"),
    	@ApiImplicitParam(name = "subSql", value = "查询sql", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore EmployeeDataBean employeeData,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		ManagerEntity manager = BasicUtil.getManager();
		BasicUtil.startPage();
		DataScopeUtil.start(manager.getId(),manager.getId(), employeeData.getDataType(),true);
		List<DataEntity> dataList = dataBiz.list();
		ArrayList<EmployeeDataBean> employeeDataList = new ArrayList<>();
		Map<String, List<DataEntity>> listMap = dataList.stream().collect(Collectors.groupingBy(DataEntity::getDataTargetId));
		listMap.entrySet().forEach(entrySet -> {
			String dataTargetId = entrySet.getKey();
			List<DataEntity> dataEntityList = entrySet.getValue();
			List<String> stringList = dataEntityList.stream().map(DataEntity::getDataId).collect(Collectors.toList());
			EmployeeDataBean employeeDataBean = new EmployeeDataBean();
			employeeDataBean.setEmployeeId(dataTargetId);
			employeeDataBean.setOrganizationIds(String.join(",", stringList));
			employeeDataList.add(employeeDataBean);
		});

		return ResultData.build().success(new EUListBean(employeeDataList,(int)BasicUtil.endPage(employeeDataList).getTotal()));
	}

	/**
	 * 获取员工权限
	 * @param employeeData 员工权限实体
	 */
	@ApiOperation(value = "获取员工权限列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore EmployeeDataBean employeeData, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model){
		if (employeeData.getEmployeeId() == null){
			return ResultData.build().error("error.id");
		}
		List<DataEntity> dataEntityList = dataBiz.list(new QueryWrapper<DataEntity>().eq("data_type",  employeeData.getDataType()).eq("DATA_TARGET_ID", employeeData.getEmployeeId()));
		List<String> stringList = dataEntityList.stream().map(DataEntity::getDataId).collect(Collectors.toList());
		employeeData.setOrganizationIds(String.join(",", stringList));
//		EmployeeDataEntity _employeeData = (EmployeeDataEntity)employeeDataBiz.getEntity(employeeData);
		return ResultData.build().success(employeeData);
	}

	/**
	* 保存员工权限
	* @param employeeData 员工权限实体
	*/
	@ApiOperation(value = "保存员工权限列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "managerId", value = "员工Id", required =false,paramType="query"),
			@ApiImplicitParam(name = "organizationIds", value = "组织机构ids", required =false,paramType="query"),
			@ApiImplicitParam(name = "dataType", value = "权限类型", required =false,paramType="query"),
			@ApiImplicitParam(name = "subSql", value = "查询sql", required =false,paramType="query"),
			@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
			@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存员工权限", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("organization:employee:authorization")
	public ResultData save(@ModelAttribute @ApiIgnore EmployeeDataBean employeeData, HttpServletResponse response, HttpServletRequest request) {
		if(!StringUtil.checkLength(employeeData.getEmployeeId()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("id"), "0", "11"));
		}
		String[] organizationIds = employeeData.getOrganizationIds().split(",");
		String employeeId = employeeData.getEmployeeId();
		// 查询数据权限配置中的sql
		DataConfigEntity dataConfigEntity = new DataConfigEntity();
		dataConfigEntity.setConfigName(employeeData.getDataType());
		DataConfigEntity dataConfig = dataConfigBiz.getOne(new QueryWrapper<>(dataConfigEntity), false);
		List<DataEntity> list = new ArrayList<>();
		for (String organizationId : organizationIds) {
			DataEntity dataEntity = new DataEntity();
			dataEntity.setDataType(employeeData.getDataType());
			// 从配置表里查出的sql
			if (dataConfig != null){
				dataEntity.setConfigId(dataConfig.getId());
			}
			dataEntity.setDataTargetId(employeeId);
			dataEntity.setDataId(organizationId);
			list.add(dataEntity);
		}
		list = list.stream().filter(dataEntity -> StringUtils.isNotBlank(dataEntity.getDataId()) && StringUtils.isNotBlank(dataEntity.getDataTargetId())).collect(Collectors.toList());
		dataBiz.remove(new QueryWrapper<DataEntity>().eq("data_type", employeeData.getDataType()).eq("DATA_TARGET_ID",employeeId));
		dataBiz.saveOrUpdateBatch(list);
		return ResultData.build().success(employeeData);
	}

	/**
	 * @param employeeDatas 员工权限实体
	 */
	@ApiOperation(value = "批量删除员工权限列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除员工权限", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("organization:employeeData:del")
	public ResultData delete(@RequestBody List<EmployeeDataBean> employeeDatas,HttpServletResponse response, HttpServletRequest request) {
		int[] ids = new int[employeeDatas.size()];
		for(int i = 0;i<employeeDatas.size();i++){
//			ids[i] =Integer.parseInt(employeeDatas.get(i).getId());
			ids[i] =Integer.parseInt(employeeDatas.get(i).getEmployeeId());
		}
		if (CollUtil.isNotEmpty(employeeDatas)){
			dataBiz.remove(new QueryWrapper<DataEntity>().eq("data_type", employeeDatas.get(0).getDataType()).in("DATA_TARGET_ID",ids));
		}
		return ResultData.build().success();
	}

	/**
	*	更新员工权限列表
	* @param employeeData 员工权限实体
	*/
	 @ApiOperation(value = "更新员工权限列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
		@ApiImplicitParam(name = "managerId", value = "员工Id", required =false,paramType="query"),
		@ApiImplicitParam(name = "organizationIds", value = "组织机构ids", required =false,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "id", value = "编号", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新员工权限", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("organization:employeeData:update")
	public ResultData update(@ModelAttribute @ApiIgnore EmployeeDataBean employeeData, HttpServletResponse response,
			HttpServletRequest request) {
		if(!StringUtil.checkLength(employeeData.getEmployeeId()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("manager.id"), "0", "11"));
		}
		 String[] organizationIds = employeeData.getOrganizationIds().split(",");
		 String employeeId = employeeData.getEmployeeId();
		 // 根据名称查询配置表的id
		 DataConfigEntity dataConfigEntity = new DataConfigEntity();
		 dataConfigEntity.setConfigName("组织权限");
		 DataConfigEntity configEntity = dataConfigBiz.getOne(new QueryWrapper<>(dataConfigEntity), false);
		 List<DataEntity> list = new ArrayList<>();
		 for (String organizationId : organizationIds) {
			 DataEntity dataEntity = new DataEntity();
			 dataEntity.setDataType(employeeData.getDataType());
			 dataEntity.setConfigId(configEntity.getId());
			 dataEntity.setDataTargetId(employeeId);
			 dataEntity.setDataId(organizationId);
			 list.add(dataEntity);
		 }
		 list = list.stream().filter(dataEntity -> StringUtils.isNotBlank(dataEntity.getDataId()) && StringUtils.isNotBlank(dataEntity.getDataTargetId())).collect(Collectors.toList());
		 dataBiz.remove(new QueryWrapper<DataEntity>().eq("data_type",  employeeData.getDataType()).eq("DATA_TARGET_ID",employeeId));
		 if (!list.isEmpty()){
			 dataBiz.saveOrUpdateBatch(list);
		 }
		return ResultData.build().success(employeeData);
	}

}
