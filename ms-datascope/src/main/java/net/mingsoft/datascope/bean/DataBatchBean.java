/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.datascope.bean;

import java.util.List;
import java.util.Map;

public class DataBatchBean extends DataBean{
    /**
     * 功能集合
     */
    private List<Map<String,Object>> dataIdModelList;

    public List<Map<String, Object>> getDataIdModelList() {
        return dataIdModelList;
    }

    public void setDataIdModelList(List<Map<String, Object>> dataIdModelList) {
        this.dataIdModelList = dataIdModelList;
    }
}
