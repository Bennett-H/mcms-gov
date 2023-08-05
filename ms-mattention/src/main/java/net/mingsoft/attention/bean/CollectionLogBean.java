/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.attention.bean;

import net.mingsoft.attention.entity.CollectionLogEntity;

/**
 * 关注实体Bean
 * 用来接收DataTitle
 */
public class CollectionLogBean extends CollectionLogEntity {

    /**
     * 关注标题
     */
    private String collectionDataTitle;

    /**
     * 获取关注标题
     */
    public String getCollectionDataTitle() {
        return collectionDataTitle;
    }

    /**
     * 设置关注标题
     */
    public void setCollectionDataTitle(String collectionDataTitle) {
        this.collectionDataTitle = collectionDataTitle;
    }
}
