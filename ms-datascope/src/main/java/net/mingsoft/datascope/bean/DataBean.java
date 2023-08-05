/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.datascope.bean;

import net.mingsoft.datascope.entity.DataEntity;

import java.util.List;


/**
 * 通过继承HashMap实现数据权限查询参数设置
 *
 * @author 铭软开发团队
 * 创建日期：2020-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
public class DataBean extends DataEntity {

    /**
     * 关联id集合
     */
    List<String> dataIdList;

    public List<String> getDataIdList() {
        return dataIdList;
    }

    public void setDataIdList(List<String> dataIdList) {
        this.dataIdList = dataIdList;
    }

    /**
     * 配置名称
     */
    private String configName;

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }
}
