/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.progress.bean;

import net.mingsoft.progress.entity.ProgressLogEntity;

/**
 * 创建日期：2021-10-04 16:52:32<br/>
 */
public class ProgressLogBean extends ProgressLogEntity {
    /**
     * 栏目ID
     */
    private String categoryId;


    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

}
