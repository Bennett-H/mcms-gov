/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.cms.bean;

import net.mingsoft.cms.entity.ContentEntity;

/**
 * 新增方法和审批状态字段
 * 文章实体bean
 */
public class ContentBean extends ContentEntity {

//    /**
//     * 静态化地址
//     */
//    private String staticUrl;
    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 属性标记
     */
    private String flag;

    /**
     * 不包含属性标记
     */
    private String noflag;

    /**
     * 方案名称
     */
    private String schemeName;

    /**
     * 进度日志状态
     */
    private String plStatus;

    /**
     * 栏目类型，用于筛选文章列表
     */
    private String categoryType;

    /**
     * 栏目属性，用于筛选文章列表
     */
    private String categoryFlag;

    /**
     * 栏目标题
     */
    private String categoryTitle;

    /**
     * 录入人
     */
    private String managerName;

    /**
     * 所属站点Id
     */
    private String appId;

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryFlag() {
        return categoryFlag;
    }

    public void setCategoryFlag(String categoryFlag) {
        this.categoryFlag = categoryFlag;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getNoflag() {
        return noflag;
    }

    public void setNoflag(String noflag) {
        this.noflag = noflag;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getPlStatus() {
        return plStatus;
    }

    public void setPlStatus(String plStatus) {
        this.plStatus = plStatus;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
}
