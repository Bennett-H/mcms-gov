/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.bean;

import net.mingsoft.organization.entity.OrganizationEntity;

import java.util.List;

/**
 * 用于返回前端树形组织机构的bean
 */
public class OrganizationBean extends OrganizationEntity {
    /**
     * 子节点
     */
    List<OrganizationBean> children;

    public List<OrganizationBean> getChildren() {
        return children;
    }

    public void setChildren(List<OrganizationBean> children) {
        this.children = children;
    }
}
