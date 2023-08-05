/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.bean;

import net.mingsoft.progress.entity.ProgressEntity;

/**
 * @author by 铭飞开源团队
 * @Description TODO
 * @date 2020/5/28 14:39
 */
public class ProgressBean extends ProgressEntity {
    /**
     * 业务id
     */
    private String dataId;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }
}
