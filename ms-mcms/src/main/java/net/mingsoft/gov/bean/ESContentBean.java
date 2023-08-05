/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.gov.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.elasticsearch.bean.ESBaseBean;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 内容搜索模型
 * @author 铭软科技
 * 创建日期：2021-1-6 16:52:39<br/>
 * 历史修订：<br/>
 */
@Document(indexName = "cms-gov")
@Component("GovESContentBean")
public class ESContentBean extends ESBaseBean {

    /**
     * 存储文章作者
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer="ik_smart")
    private String author;

    /**
     * 存储自定义扩张模型
     */
    @Field(type = FieldType.Object)
    private Map<String, Object> MDiyModel;

    @Field(type = FieldType.Keyword)
    private String typeId;

    @Field(type = FieldType.Text)
    private String litPic;

    @Field(type = FieldType.Keyword)
    private String flag;

    /**
     * 存储文章发布到
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer="ik_smart")
    private String styles;

    /**
     * 存储类型
     */
    private String type;

    /**
     * 存储栏目父ID集合
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer="ik_smart")
    private String parentIds;

    /**
     * 站点ID
     */
    @Field(type = FieldType.Keyword)
    private String appId;

    /**
     * 文章副标题
     */
    @Field(type = FieldType.Text)
    private String shortTitle;

    /**
     * 栏目副标题
     */
    @Field(type = FieldType.Text)
    private String typeShortTitle;

    /**
     * 存储栏目标题
     */
    @Field(type = FieldType.Text)
    private String typeTitle;

    /**
     * 自定义排序
     */
    @Field(type = FieldType.Integer)
    private Integer sort;

    @Field(type = FieldType.Text)
    private String descrip;

    /**
     * 文章审批进度
     */
    @Field(type = FieldType.Keyword)
    private String progressStatus;

    @Field(type = FieldType.Date,format = DateFormat.custom, pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    /**
     * 链接地址
     */
    @Field(type = FieldType.Text,index=false)
    private String url;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTypeId() {
        return typeId;
    }

    public Map<String, Object> getMDiyModel() {
        return MDiyModel;
    }

    public void setMDiyModel(Map<String, Object> MDiyModel) {
        this.MDiyModel = MDiyModel;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(String progressStatus) {
        this.progressStatus = progressStatus;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getStyles() {
        return styles;
    }

    public void setStyles(String styles) {
        this.styles = styles;
    }


    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getLitPic() {
        return litPic;
    }

    public void setLitPic(String litPic) {
        this.litPic = litPic;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getTypeShortTitle() {
        return typeShortTitle;
    }

    public void setTypeShortTitle(String typeShortTitle) {
        this.typeShortTitle = typeShortTitle;
    }
}
