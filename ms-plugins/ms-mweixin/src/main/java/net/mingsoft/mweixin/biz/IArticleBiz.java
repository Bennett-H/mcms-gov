/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.mweixin.entity.ArticleEntity;

import java.util.List;


/**
 * 文章业务
 * @author 铭飞开发团队
 * 创建日期：2019-12-25 9:27:11<br/>
 * 历史修订：<br/>
 */
public interface IArticleBiz extends IBaseBiz<ArticleEntity> {

    List queryNewsArticle(ArticleEntity entity);
}