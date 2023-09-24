/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.dao;

import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mweixin.entity.ArticleEntity;

import java.util.List;

/**
 * 文章持久层
 * @author 铭飞开发团队
 * 创建日期：2019-12-25 9:27:11<br/>
 * 历史修订：<br/>
 */
public interface IWXArticleDao extends IBaseDao<ArticleEntity> {
    /**
     * 排除除了主文章的文章
     * @param entity
     * @return
     */
    List queryNewsArticle(ArticleEntity entity);
}
