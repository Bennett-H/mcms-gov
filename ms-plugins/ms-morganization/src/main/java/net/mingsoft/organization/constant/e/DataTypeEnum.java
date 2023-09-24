/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.organization.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * 员工的权限类型枚举
 */
public enum DataTypeEnum implements BaseEnum {
    /**
     * 组织机构权限管理
     */
    EMPLOYEE_ORGANIZATION("组织机构员工部门权限"),
    /**
     * 组织机构权限管理
     */
    EMPLOYEE_CONTENT("组织机构员工内容权限");

    /**
     * 权限类型
     */
    public String dataType;

    DataTypeEnum(String dataType) {
        this.dataType = dataType;
    }

    public String getDataType() {
        return this.dataType;
    }

    @Override
    public String toString() {
        return this.dataType;
    }

    @Override
    public int toInt() {
        return 0;
    }
}
