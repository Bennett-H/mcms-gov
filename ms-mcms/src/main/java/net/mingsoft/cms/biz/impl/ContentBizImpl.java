/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.cms.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import freemarker.template.TemplateException;
import net.mingsoft.approval.constant.e.ProgressStatusEnum;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.cms.bean.CategoryBean;
import net.mingsoft.cms.bean.ContentBean;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.dao.IContentDao;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.mdiy.biz.ITagBiz;
import net.mingsoft.mdiy.entity.ModelEntity;
import net.mingsoft.mdiy.entity.TagEntity;
import net.mingsoft.mdiy.util.ParserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 文章管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：
 * 重写co
 * 2021-6-23 增加待审文章业务<br/>
 * 2021-10-15 增加回收站方法<br/>
 */
 @Service("cmscontentBizImpl")
public class ContentBizImpl  extends BaseBizImpl<IContentDao, ContentEntity> implements IContentBiz {


	@Autowired
	private IContentDao contentDao;


	@Autowired
	private ITagBiz tagBiz;

	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return contentDao;
	}


	@Override
	public List<CategoryBean> queryIdsByCategoryIdForParser(ContentBean contentBean) {
		return this.contentDao.queryIdsByCategoryIdForParser(contentBean);
	}

	@Override
	public List<CategoryBean> queryIdsByCategoryIdForParserAndNotCover(ContentBean contentBean) {
		return this.contentDao.queryIdsByCategoryIdForParserAndNotCover(contentBean);

	}

	@Override
	public List<ContentBean> queryContentIgnoreTenantLine(ContentBean contentBean) {
		return contentDao.queryContentIgnoreTenantLine(contentBean);
	}

	@Override
	public List<CategoryBean> queryIdsByCategoryId(ContentBean contentBean) {
		return this.contentDao.queryIdsByCategoryId(contentBean);
	}

	@Override
	public int getSearchCount(ModelEntity contentModel, List diyList, Map whereMap, int appId, String categoryIds) {
		if (contentModel!=null) {
			return contentDao.getSearchCount(contentModel.getModelTableName(),diyList,whereMap, appId,categoryIds, ProgressStatusEnum.APPROVED.toString());
		}
		return contentDao.getSearchCount(null,null,whereMap, appId,categoryIds, ProgressStatusEnum.APPROVED.toString());
	}

	@Override
	public List<ContentBean> listForRecycle(ContentEntity content) {
		return contentDao.listForRecycle(content);
	}

	@Override
	public void completeDelete(List<String> contentIds) {
		contentDao.completeDelete(contentIds);
	}

	@Override
	public void reduction(List<String> ids) {
		contentDao.reduction(ids,new Date());
	}

	@Override
	public List<ContentBean> auditList(String contentTitle,List<String> ids, String plStatus, String progressStatus) {
		return contentDao.auditList(contentTitle,ids, plStatus, progressStatus);
	}

	@Override
	public List list(Map map ) {
		//通过tagSqlBiz获取arclist对应的sql
		QueryWrapper<TagEntity> tagWrapper = new QueryWrapper<>();
		tagWrapper.eq("tag_name", "arclist");
		TagEntity tagEntity = tagBiz.getOne(tagWrapper);
		String sqlFtl = tagEntity.getTagSql();
		List<ContentEntity> contentEntities = null;
		//通过ParserUtil
		try {
			String sql = ParserUtil.rendering(map,sqlFtl);
			//执行原生的sql
			contentEntities = (List<ContentEntity>) tagBiz.excuteSql(sql);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		return contentEntities;
	}
}
