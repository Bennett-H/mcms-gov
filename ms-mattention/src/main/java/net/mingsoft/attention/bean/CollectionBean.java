/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.attention.bean;

/**
 * 关注记录bean对象<br>
 * 可用于批量查询关注记录数
 */
public class CollectionBean {

    /**
     * 业务编号
     */
    private String dataId;

    /**
     * 关注数
     */
    private Integer dataCount;

    /**
     * 是否点赞
     */
    private boolean isLike;

    /**
     * 点赞IP
     */
    private String collectionIp;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Integer getDataCount() {
        return dataCount;
    }

    public void setDataCount(Integer dataCount) {
        this.dataCount = dataCount;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public String getCollectionIp() {
        return collectionIp;
    }

    public void setCollectionIp(String collectionIp) {
        this.collectionIp = collectionIp;
    }
}
