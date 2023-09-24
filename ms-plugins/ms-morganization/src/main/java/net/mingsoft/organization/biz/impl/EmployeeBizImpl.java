/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.organization.biz.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.bean.RoleBean;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.biz.IRoleBiz;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.datascope.utils.DataScopeUtil;
import net.mingsoft.organization.bean.OrganizationEmployeeTreeBean;
import net.mingsoft.organization.bean.OrganizationRoleBean;
import net.mingsoft.organization.biz.IEmployeeBiz;
import net.mingsoft.organization.constant.e.DataTypeEnum;
import net.mingsoft.organization.dao.IEmployeeDao;
import net.mingsoft.organization.dao.IOrganizationDao;
import net.mingsoft.organization.entity.EmployeeEntity;
import net.mingsoft.organization.entity.OrganizationEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 员工管理持久化层
 * @author 铭飞开源团队
 * 创建日期：2020-1-6 14:53:26<br/>
 * 历史修订：<br/>
 */
 @Service("organizationEmployeeBizImpl")
 @Transactional
public class EmployeeBizImpl extends BaseBizImpl<IEmployeeDao, EmployeeEntity> implements IEmployeeBiz {


	@Autowired
	private IEmployeeDao employeeDao;
	@Autowired
	private IManagerBiz managerBiz;
	@Autowired
	private IRoleBiz roleBiz;
	@Autowired
	private IOrganizationDao organizationDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return employeeDao;
	}




	@Override
	public void updateManagerLock(String status, String managerId) {
		String sql = "update manager set MANAGER_LOCK = '"+status+"' where id = {}";
		employeeDao.excuteSql(StrUtil.format(sql,managerId));
	}


	@Override
	public List<OrganizationRoleBean> queryRoleTreeList(EmployeeEntity employeeEntity) {
		List<EmployeeEntity> employee = employeeDao.queryListByOrganization(employeeEntity);
		return getOrganizationRoleBeans(employee);

	}

	/**
	 * 获取数据：部门、员工
	 * 组装成 PostEmployeeTreeBean；
	 * @param organizationId 传入父级id,返回子集部门及员工
	 * @return
	 */
	@Override
	public List<OrganizationEmployeeTreeBean> getPostEmployeeBeans(String organizationId) {
		ManagerEntity manager = BasicUtil.getManager();
		String managerId = manager.getId();
		EmployeeEntity employee = employeeDao.selectOne(new QueryWrapper<EmployeeEntity>().eq("manager_id", managerId));
		if (employee != null){
			DataScopeUtil.start(managerId,employee.getId(), DataTypeEnum.EMPLOYEE_ORGANIZATION.toString(),true);
		}
		List<String> IdList = organizationDao.selectList(null).stream().map(OrganizationEntity::getId).collect(Collectors.toList());
		if (!organizationId.equals("0") && !IdList.contains(organizationId)){
			return null;
		}
		List<OrganizationEmployeeTreeBean> res = new ArrayList<>();
		OrganizationEntity organizationEntity = new OrganizationEntity();
		organizationEntity.setOrganizationId(organizationId);
		List<OrganizationEntity> organizationEntityList = organizationDao.query(organizationEntity);
		//递归子部门
		List<OrganizationEmployeeTreeBean> collect = organizationEntityList.stream().map(x -> {
			if (!IdList.contains(x.getId())){
				return null;
			}
			x.getOrganizationId();
			OrganizationEmployeeTreeBean organizationEmployeeTreeBean =
					new OrganizationEmployeeTreeBean();
			organizationEmployeeTreeBean.setValue("o-" + x.getId());
			organizationEmployeeTreeBean.setLabel(x.getOrganizationTitle());
			organizationEmployeeTreeBean.setLeaf(true);
			organizationEmployeeTreeBean.setChildren(getPostEmployeeBeans(x.getId()));
			return organizationEmployeeTreeBean;
		}).collect(Collectors.toList());
		res.addAll(collect);
		//必须存在部门
		if(StrUtil.isNotBlank(organizationEntity.getOrganizationId())){
			EmployeeEntity employeeEntity=new EmployeeEntity();
			employeeEntity.setOrganizationId(organizationEntity.getOrganizationId());
			List<EmployeeEntity> employeeEntityList = employeeDao.selectList(new QueryWrapper<>(employeeEntity));
			//加入部门员工
			List<OrganizationEmployeeTreeBean> employees = employeeEntityList.stream().map(x -> {
				OrganizationEmployeeTreeBean<Integer> organizationEmployeeTreeBean =
						new OrganizationEmployeeTreeBean<>();
				organizationEmployeeTreeBean.setValue(Integer.valueOf(x.getId()));
				organizationEmployeeTreeBean.setLabel(x.getEmployeeNickName());
				organizationEmployeeTreeBean.setLeaf(false);
				organizationEmployeeTreeBean.setChildren(null);
				if (organizationEmployeeTreeBean.getValue() == null){
					organizationEmployeeTreeBean.setValue(RandomUtil.randomInt());
				}
				return organizationEmployeeTreeBean;
			}).collect(Collectors.toList());
			res.addAll(employees);
		}
		return res;
	}

	@Override
	public List<OrganizationRoleBean> getOrganizationRoleBeans(List<EmployeeEntity> employee) {
		// roleBiz继承了RoleEntity，导致 roleBiz.queryAll() 返回类型 List<RoleEntity>，这里做一步强转
		List roles =  roleBiz.queryAll();
		List<RoleBean> roleList = (List<RoleBean>)roles;
		return roleList.stream().map(role->{
			List<EmployeeEntity> employees = employee.stream().filter(item -> {
				String[] roleIds = item.getEmployeeRoleIds().split(",");//根据roleId去匹配成员的roleIds
				return Arrays.asList(roleIds).contains(role.getId());
			}).collect(Collectors.toList());
			OrganizationRoleBean organizationRoleBean = new OrganizationRoleBean();
			organizationRoleBean.setEmployeeList(employees); //获取角色的所有成员
			if(employees.isEmpty()){ //空成员启动禁用
				organizationRoleBean.setDisabled(true);
			}
			organizationRoleBean.setManagerNickName(role.getRoleName());
			organizationRoleBean.setManagerId(role.getIntId());
			BeanUtils.copyProperties(role,organizationRoleBean);
			return  organizationRoleBean;
		}).filter(x->!x.isDisabled()).collect(Collectors.toList());
	}


	/**
	 * 保存员工
	 * @param employee 员工实体
	 */
	@Override
	public void saveEmployee(EmployeeEntity employee) {
		super.save(employee);
		// 默认使用主键id的生成策略
		employee.setEmployeeCode("EMP-"+employee.getId());

		super.updateById(employee);
	}

	@Override
	public void delete(int[] ids){
		ArrayList<String> managerIds = new ArrayList<>();

		for (int i = 0; i < ids.length; i++) {
			EmployeeEntity employeeEntity = (EmployeeEntity)employeeDao.getEntity(ids[i]);
			if(employeeEntity!=null && employeeEntity.getManagerId() != null){
				managerIds.add(employeeEntity.getManagerId().toString());
			}
		}
		super.delete(ids);

		if (managerIds.size() > 0){

			managerBiz.delete(managerIds.toArray(new String[]{}));
		}
	}


}
