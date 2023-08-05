/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.co.bean;



/**
 * 文章bean,用于文章复制
 * @author 铭软开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
public class ContentBean extends net.mingsoft.cms.bean.ContentBean {

    /**
     * 文章编号集合
     */
    private String contentIds;
    /**
     * 栏目编号集合
     */
    private String categoryIds;
    /**
     * 操作类型
     */
    private String operationType;


    /**
     * 获取文章编号集合
     * @return
     */
    public String getContentIds() {
        return contentIds;
    }

    /**
     * 设置文章编号集合
     * @param contentIds
     */
    public void setContentIds(String contentIds) {
        this.contentIds = contentIds;
    }

    /**
     * 获取栏目编号集合
     * @return
     */
    public String getCategoryIds() {
        return categoryIds;
    }

    /**
     * 设置文章编号集合
     * @param categoryIds
     */
    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
    }


    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}
