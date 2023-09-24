/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.bean;


import net.mingsoft.organization.entity.EmployeeEntity;
import net.mingsoft.organization.entity.OrganizationEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @package: net.mingsoft.organization.bean
 * @description: 用来进行级联查询，能够通用到查询人员和部门
 * @author: jemor1999@qq.com
 * @create: 2020-06-10 17:08
 **/
public class OrganizationEmployeeTreeBean<T> implements Serializable {
    /**
     * value 为了 选择树 的实现
     */
    private T value;
    private String label;
    List<OrganizationEmployeeTreeBean> children = new ArrayList<>();
    private boolean isLeaf = true;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<OrganizationEmployeeTreeBean> getChildren() {
        return children;
    }

    public void setChildren(List<OrganizationEmployeeTreeBean> children) {
        this.children = children;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }
}
