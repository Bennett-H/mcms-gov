/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.datascope.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;

import java.util.Objects;

/**
 * 数据权限实体
 *
 * @author 铭软开发团队
 * 创建日期：2020-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
@TableName("DATASCOPE_DATA")
public class DataEntity extends BaseEntity {

    private static final long serialVersionUID = 1614579957900L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 关联id
     */
    private String dataId;
    /**
     * 目标编号
     */
    private String dataTargetId;
    /**
     * 业务分类
     */
    private String dataType;

    /**
     * 业务功能权限
     */
    private String dataIdModel;

    /**
     * 配置id
     */
    private String configId;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }



    /**
     * 设置关联id
     */
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    /**
     * 获取关联id
     */
    public String getDataId() {
        return this.dataId;
    }

    /**
     * 设置目标编号
     */
    public void setDataTargetId(String dataTargetId) {
        this.dataTargetId = dataTargetId;
    }

    /**
     * 获取目标编号
     */
    public String getDataTargetId() {
        return this.dataTargetId;
    }

    /**
     * 设置业务分类
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * 获取业务分类
     */
    public String getDataType() {
        return this.dataType;
    }

    public String getDataIdModel() {
        return dataIdModel;
    }

    public void setDataIdModel(String dataIdModel) {
        this.dataIdModel = dataIdModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DataEntity that = (DataEntity) o;
        return Objects.equals(dataId, that.dataId) &&
                Objects.equals(dataTargetId, that.dataTargetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataId, dataTargetId);
    }
}
