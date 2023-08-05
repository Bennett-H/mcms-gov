/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.cms.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.cms.entity.CategoryEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 栏目Bean
 * @author 铭飞开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：<br/>
 * 2021-04-23 增加contentType 生成的时候需要拼接上文章属性
 */
public class CategoryBean extends CategoryEntity {

    /**
     * 文章编号
     */
    private String articleId;


    /**
     * 文章属性
     */
    private String contentType = "";

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    /**
     * 文章更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date contentUpdateDate;

    public Date getContentUpdateDate() {
        return contentUpdateDate;
    }

    public void setContentUpdateDate(Date contentUpdateDate) {
        this.contentUpdateDate = contentUpdateDate;
    }


}
