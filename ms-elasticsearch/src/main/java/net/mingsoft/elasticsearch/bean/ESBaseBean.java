/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.elasticsearch.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * ES文档模型bean
 *
 * @author 铭软科技
 * 创建日期：2021-1-6 15:13:32<br/>
 * 历史修订：<br/>
 */
public abstract class ESBaseBean {

    @Id
    protected String id;

    /**
     * 此处不能加ik分词，优先级很高会强制分词导致精确搜索失败
     */
    @Field(type = FieldType.Text)
    protected String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer="ik_smart")
    protected String content;

    @Field(type = FieldType.Date,format = DateFormat.custom, pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
