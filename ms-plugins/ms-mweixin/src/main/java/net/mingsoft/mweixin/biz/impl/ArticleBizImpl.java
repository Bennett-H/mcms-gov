/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.mweixin.biz.impl;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mweixin.biz.IArticleBiz;
import net.mingsoft.mweixin.dao.IWXArticleDao;
import net.mingsoft.mweixin.entity.ArticleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文章管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2019-12-25 9:27:11<br/>
 * 历史修订：<br/>
 */
 @Service("mweixinarticleBizImpl")
public class ArticleBizImpl extends BaseBizImpl<IWXArticleDao,ArticleEntity> implements IArticleBiz {

	
	@Autowired
	private IWXArticleDao articleDao;
	
	
	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return articleDao;
	}

	@Override
	public List queryNewsArticle(ArticleEntity entity) {
		return articleDao.queryNewsArticle(entity);
	}
}
