/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.co.biz.impl;

import cn.hutool.core.map.CaseInsensitiveMap;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.cms.bean.CategoryBean;
import net.mingsoft.cms.dao.ICategoryDao;
import net.mingsoft.cms.dao.IContentDao;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.co.bean.ContentBean;
import net.mingsoft.co.biz.IContentBiz;
import net.mingsoft.co.constant.e.ArticleOperationTypeEnum;
import net.mingsoft.mdiy.dao.IModelDao;
import net.mingsoft.mdiy.entity.ModelEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章管理持久化层
 * @author 铭软开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：<br/>
 * 2021-7-7 对批量复制与批量移动做了文章扩张模型的判定
 * 58行增加了modelDao 对removeOrCopy方法进行了改写</br>
 */
@Primary
@Service("cmsCoContentBizImpl")
public class ContentBizImpl extends net.mingsoft.cms.biz.impl.ContentBizImpl implements IContentBiz {

	/*
	 * log4j日志记录
	 */
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IContentDao contentDao;
	/**
	 * 栏目管理业务层
	 */
	@Autowired
	private ICategoryDao categoryDao;

	/**
	 * 自定义配置模型业务层
	 */
	@Autowired
	private IModelDao modelDao;

	@Value("${ms.diy.html-dir:html}")
	private String htmlDir;



	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return contentDao;
	}

	@Override
	public List<String> removeOrCopy(ContentBean contentBean ,String type) {
		String[] contentIds = contentBean.getContentIds().split(",");
		String[] categoryIds = contentBean.getCategoryIds().split(",");
		List<String> ids = new ArrayList<>();
		for (String contentId:contentIds){
			// 当前文章实体
			ContentEntity contentEntity = contentDao.selectById(contentId);
			if(contentEntity != null){
				for (String categoryId:categoryIds){
					if(categoryDao.selectById(categoryId)!=null){
						//获取当前文章的栏目实体
						CategoryEntity oldCategory = categoryDao.selectById(contentEntity.getCategoryId());
						//将当前文章的栏目ID替换为目标栏目ID
						contentEntity.setCategoryId(categoryId);
						//获取到目标栏目实体
						CategoryEntity targetCategory = categoryDao.selectById(categoryId);
						if(targetCategory.getLeaf() && ArticleOperationTypeEnum.COPY.toString().equals(type)){
							contentEntity.setId(null);
							contentEntity.setUpdateDate(new Date());
							// TODO 文章审核类型需要清理
							contentDao.insert(contentEntity);
							ids.add(contentEntity.getId());

							//如果目标栏目表内的自定义模型内容为空
							if (StringUtils.isBlank(targetCategory.getMdiyModelId())) {
								break;
							}
							//配置link_id Map
							CaseInsensitiveMap<Object, Object> modelMap = new CaseInsensitiveMap<>();
							//在目标模型表插入link_id
							ModelEntity objectModelEntity = modelDao.selectById(targetCategory.getMdiyModelId());
							//如果当前栏目自定义模型为空 或 与目标栏目自定义模型不一致
							if (StringUtils.isBlank(oldCategory.getMdiyModelId()) || !StringUtils.equals(oldCategory.getMdiyModelId(),targetCategory.getMdiyModelId())) {
								//当前insert后,contentId已自动生成
								modelMap.put("link_id", contentEntity.getId());
								//直接插入空自定义模型
								contentDao.insertBySQL(objectModelEntity.getModelTableName(), modelMap);
								break;
							}
							// 将当前的自定义模型内容复制到目标自定义模型内容
							// 放入当前文章ID
							modelMap.put("link_id", contentId);
							List<Map<Object, Object>> modelList = contentDao.queryBySQL(
									objectModelEntity.getModelTableName(),
									null ,
									modelMap, null, null, null, null, null);
							assert modelList.size() > 0;
							for (Map<Object, Object> model : modelList) {
								CaseInsensitiveMap<Object, Object> caseMap = new CaseInsensitiveMap<>(model);
								// 删除id link_id
								caseMap.remove("id");
								caseMap.remove("link_id");
								modelMap.put("link_id", contentEntity.getId());
								modelMap.putAll(caseMap);
								break;
							}
							contentDao.insertBySQL(objectModelEntity.getModelTableName(), modelMap);

						}else if(targetCategory.getLeaf() && ArticleOperationTypeEnum.REMOVE.toString().equals(type)){
							contentEntity.setUpdateDate(new Date());
							contentDao.updateById(contentEntity);
							// 如果目标栏目与当前栏目的文章模型都为空break
							if(StringUtils.isBlank(oldCategory.getMdiyModelId()) && StringUtils.isBlank(targetCategory.getMdiyModelId())) {
								break;
							}
							//配置link_id Map
							Map<String, String> modelMap = new HashMap<>();
							modelMap.put("link_id", contentId);
							//获取当前自定义模型实体
							ModelEntity modelEntity = modelDao.selectById(oldCategory.getMdiyModelId());
							//自定义模型一致时link_id，不做操作
							if(StringUtils.equals(oldCategory.getMdiyModelId(), targetCategory.getMdiyModelId())){
								break;
							}
							//如果目标栏目表内的自定义模型内容为空
							if (StringUtils.isBlank(targetCategory.getMdiyModelId())) {
								//删除当前栏目自定义模型link_id
								contentDao.deleteBySQL(modelEntity.getModelTableName(), modelMap);
								break;
							}
							//获取目标栏目绑定的自定义模型配置实体
							ModelEntity objModelEntity = modelDao.selectById(targetCategory.getMdiyModelId());
							//当前栏目自定义模型为空，插入目标模型表link_id
							if (StringUtils.isBlank(oldCategory.getMdiyModelId())){
								contentDao.insertBySQL(objModelEntity.getModelTableName(), modelMap);
								break;
							}
							//如果当前文章栏目绑定的自定义模型与目标栏目绑定的自定义模型不一致
							if (!StringUtils.equals(oldCategory.getMdiyModelId(),targetCategory.getMdiyModelId())){
								//删除当前栏目自定义模型link_id
								contentDao.deleteBySQL(modelEntity.getModelTableName(), modelMap);
								//插入目标模型表link_id
								contentDao.insertBySQL(objModelEntity.getModelTableName(), modelMap);
								break;
							}
						}
					}else {
						LOG.debug("栏目"+categoryId+"不存在"+"或目标栏目是父节点");
					}
				}
			}else {
				LOG.debug("文章"+contentId+"不存在");
			}
		}
		return ids;
	}


	@Override
	public List<net.mingsoft.cms.bean.ContentBean> queryContentIgnoreTenantLine(net.mingsoft.cms.bean.ContentBean contentBean) {
		return contentDao.queryContentIgnoreTenantLine(contentBean);
	}

	@Override
	public List<CategoryBean> queryIdsByCategoryId(net.mingsoft.cms.bean.ContentBean contentBean) {
		return contentDao.queryIdsByCategoryId(contentBean);
	}


}
