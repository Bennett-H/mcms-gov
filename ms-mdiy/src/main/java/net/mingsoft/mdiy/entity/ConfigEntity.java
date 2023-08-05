/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;

/**
 * 进度表实体
 *
 * @author 铭飞科技
 * 创建日期：2021-3-18 11:50:14<br/>
 * 历史修订：<br/>
 */
@TableName("mdiy_config")
public class ConfigEntity extends BaseEntity {

    private static final long serialVersionUID = 1616039414961L;

    /**
     * 是否能够删除 0-能删除 1-不能删除
     */
    @TableField(whereStrategy = FieldStrategy.NEVER)
    private int notDel;

    /**
     * 模型名称
     */
    private String configName;
    /**
     * 模型数据
     */
    private String configData;


    /**
     * 设置模型名称
     */

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    /**
     * 获取模型名称
     */
    public String getConfigName() {
        return this.configName;
    }

    /**
     * 设置模型数据
     */
    public void setConfigData(String configData) {
        this.configData = configData;
    }

    /**
     * 获取模型数据
     */
    public String getConfigData() {
        return this.configData;
    }

    public int getNotDel() {
        return notDel;
    }

    public void setNotDel(int notDel) {
        this.notDel = notDel;
    }
}
