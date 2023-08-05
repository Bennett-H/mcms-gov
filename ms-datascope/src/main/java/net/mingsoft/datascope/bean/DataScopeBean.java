/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.datascope.bean;

import java.util.HashMap;

/**
 * 通过继承HashMap实现数据权限查询参数设置
 *
 * @author 铭软开发团队
 * 创建日期：2020-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
public class DataScopeBean extends HashMap {

    /**
     * 是否修改
     */
    private boolean change;

    /**
     * 是否忽略管理员
     */
    private boolean ignoreAdmin;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 目标
     */
    private String targetId;

    /**
     * 目标集合
     */
    private String targetIds;


    /**
     * 业务类型
     */
    private String type;

    public DataScopeBean(String createBy) {
        this.createBy = createBy;
    }
    public DataScopeBean(String createBy,boolean ignoreAdmin) {
        this.createBy = createBy;
        this.ignoreAdmin = ignoreAdmin;
    }

    public DataScopeBean(String createBy,String targetId, String type) {
        this.createBy = createBy;
        this.targetId = targetId;
        this.type = type;
    }
    public DataScopeBean(String createBy,String targetId, String type,boolean ignoreAdmin) {
        this.createBy = createBy;
        this.targetId = targetId;
        this.type = type;
        this.ignoreAdmin = ignoreAdmin;
    }

    public DataScopeBean(String createBy, String type,boolean ignoreAdmin, String targetIds) {
        this.createBy = createBy;
        this.type = type;
        this.ignoreAdmin = ignoreAdmin;
        this.targetIds = targetIds;
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetIds) {
        this.targetId = targetIds;
    }

    public String getTargetIds() {
        return targetIds;
    }

    public void setTargetIds(String targetIds) {
        this.targetIds = targetIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public boolean isIgnoreAdmin() {
        return ignoreAdmin;
    }

    public void setIgnoreAdmin(boolean ignoreAdmin) {
        this.ignoreAdmin = ignoreAdmin;
    }
}
