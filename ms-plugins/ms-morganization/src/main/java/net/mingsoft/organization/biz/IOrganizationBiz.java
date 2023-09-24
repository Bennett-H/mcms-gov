/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.organization.bean.OrganizationBean;
import net.mingsoft.organization.entity.OrganizationEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 组织机构业务
 * @author 铭飞开源团队
 * 创建日期：2020-1-6 14:53:26<br/>
 * 历史修订：<br/>
 */
public interface IOrganizationBiz extends IBaseBiz<OrganizationEntity> {

    void saveEntity(OrganizationEntity entity);

    void updateEntity(OrganizationEntity entity);

    List<OrganizationEntity> queryChildren(@Param("id") int id);

    /**
     * 查询当前组织机构的完整层级
     * @param entity
     * @return
     */
    OrganizationBean queryHierarchy(OrganizationEntity entity);

    @Override
    void delete(int[]  ids);
}
