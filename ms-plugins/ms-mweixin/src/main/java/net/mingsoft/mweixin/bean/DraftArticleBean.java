/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.bean;

import net.mingsoft.mweixin.entity.ArticleEntity;
import net.mingsoft.mweixin.entity.DraftEntity;

import java.util.List;


/**
 * 文章草稿类
 * @author 铭飞开源团队
 * @date 2018年12月26日
 */
public class DraftArticleBean extends DraftEntity {

    /**
     * 子文章列表
     */
    private List<ArticleEntity> articleList;

    /**
     * 主图文
     */
    private ArticleEntity masterArticle;

    /**
     * 图文标题
     */
    private String basicTitle;

    /**
     * 是否同步至微信，用于查询
     */
    private String isSync;




    public List<ArticleEntity> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<ArticleEntity> articleList) {
        this.articleList = articleList;
    }

    public ArticleEntity getMasterArticle() {
        return masterArticle;
    }

    public void setMasterArticle(ArticleEntity masterArticle) {
        this.masterArticle = masterArticle;
    }

    public String getBasicTitle() {
        return basicTitle;
    }

    public void setBasicTitle(String basicTitle) {
        this.basicTitle = basicTitle;
    }

    public String getIsSync() {
        return isSync;
    }

    public void setIsSync(String isSync) {
        this.isSync = isSync;
    }

}
