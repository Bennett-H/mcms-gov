/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.dao;

import net.mingsoft.base.dao.IBaseDao;
import java.util.*;

import net.mingsoft.basic.entity.ModelEntity;
import net.mingsoft.organization.entity.EmployeeEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 员工持久层
 * @author 铭飞开源团队
 * 创建日期：2020-1-6 18:25:28<br/>
 * 历史修订：<br/>
 */
public interface IEmployeeDao extends IBaseDao<EmployeeEntity> {
    /**
     * 查询角色所拥有model权限
     * @param ids
     * @return
     */
    public List<ModelEntity> queryModelByRoleIds(@Param("ids") int[] ids);

    /**
     * 组织机构Id
     * @param employeeEntity
     * @return
     */
    List<EmployeeEntity> queryListByOrganization(EmployeeEntity employeeEntity);


    /**
     * 根据ids查询员工
     * @param ids
     * @return
     */
    List<EmployeeEntity> queryByIds(@Param("ids") String ids);

}
