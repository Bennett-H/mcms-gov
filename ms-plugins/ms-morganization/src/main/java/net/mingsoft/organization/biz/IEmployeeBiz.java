/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.organization.bean.OrganizationEmployeeTreeBean;
import net.mingsoft.organization.bean.OrganizationRoleBean;
import net.mingsoft.organization.entity.EmployeeEntity;

import java.util.List;


/**
 * 员工业务
 * @author 铭飞开源团队
 * 创建日期：2020-1-6 14:53:26<br/>
 * 历史修订：<br/>
 */
public interface IEmployeeBiz extends IBaseBiz<EmployeeEntity> {

    /**
     * 更新所属管理员的锁定状态
     */
    void updateManagerLock(String status, String managerId);




    /**
     * 获取角色成员树形数据
     * @param employeeEntity
     * @return
     */
    List<OrganizationRoleBean> queryRoleTreeList(EmployeeEntity employeeEntity);

    /**
     * 获取数据：部门、员工
     * 组装成 PostEmployeeTreeBean；
     * @param organizationId 传入父级id,返回子集部门及员工
     * @return
     */
    List<OrganizationEmployeeTreeBean> getPostEmployeeBeans(String organizationId);

    /**
     * 构建树形集合
     * @param employee
     * @return
     */
    List<OrganizationRoleBean> getOrganizationRoleBeans(List<EmployeeEntity> employee);


    /**
     * 保存员工
     * @param employee 员工实体
     */
    void saveEmployee(EmployeeEntity employee);
}
