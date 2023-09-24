/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.dao;

import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.organization.entity.OrganizationEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 部门管理持久层
 * @author 铭飞开源团队
 * 创建日期：2020-1-16 9:46:15<br/>
 * 历史修订：<br/>
 */
public interface IOrganizationDao extends IBaseDao<OrganizationEntity> {
    /**
     * 查找所有子集
     * @param id
     * @return
     */
    List<OrganizationEntity> queryChildren(@Param("id") int id);
}
