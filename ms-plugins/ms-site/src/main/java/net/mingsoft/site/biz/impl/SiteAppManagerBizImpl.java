/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.site.biz.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.proxy.jdbc.NClobProxyImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.site.biz.ISiteAppManagerBiz;
import net.mingsoft.site.constant.e.ProgressStatusEnum;
import net.mingsoft.site.dao.ISiteAppManagerDao;
import net.mingsoft.site.entity.SiteAppManagerEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Clob;
import java.sql.NClob;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 管理员站点关联表管理持久化层
 * @author 铭飞科技
 * 创建日期：2022-1-4 10:22:11<br/>
 * 历史修订：添加分发文章方法 2022-1-7
 */
 @Service("basicsiteAppManagerBizImpl")
public class SiteAppManagerBizImpl extends BaseBizImpl<ISiteAppManagerDao, SiteAppManagerEntity> implements ISiteAppManagerBiz {


	@Autowired
	private ISiteAppManagerDao siteAppManagerDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return siteAppManagerDao;
	}

	@Override
	public List<Map<String, Object>> getCategoryByAppId(String appId) {
		String queryCategory = "select id AS \"id\",category_id as \"categoryId\",leaf AS \"leaf\",top_id as \"topId\",dict_id as \"dictId\",mdiy_model_id as \"mdiyModelId\",category_parent_ids as \"categoryParentIds\" , category_title as \"categoryTitle\" ,category_flag as \"categoryFlag\",category_type as \"categoryType\",category_sort as \"categorySort\",del as \"del\",app_id as \"appId\" from cms_category where app_id = {}";
		List<Map<String,Object>> list = siteAppManagerDao.excuteSql(StrUtil.format(queryCategory, appId));
		list.forEach(map -> {
			map.put("id",map.get("id").toString());
		});

		return list;
	}

	@Override
	public String distribution(String categoryId, String contentId) {
		String queryCategory = "select app_id AS \"appId\" from cms_category where id = {}";
		List<Map<String,Object>> list = siteAppManagerDao.excuteSql(StrUtil.format(queryCategory, categoryId));
		if (list != null && list.size() > 0){
			// 使用String类型用于适配不同数据库的差异
			String appId = list.get(0).get("appId").toString();
			String queryContent = "select * from cms_content where id = {}";
			List<Map<String,Object>> contentList = siteAppManagerDao.excuteSql(StrUtil.format(queryContent, contentId));
			if (contentList != null && contentList.size() > 0){
				TreeMap<String, Object> treeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
				treeMap.putAll(contentList.get(0));
				String progressStatus = treeMap.get("PROGRESS_STATUS").toString();
				if (StringUtils.isEmpty(progressStatus) || !ProgressStatusEnum.APPROVED.toString().equals(progressStatus)){
					throw new BusinessException("文章审核未通过!");
				}
				treeMap.put("APP_ID",appId);
				Snowflake snowflake = IdUtil.getSnowflake(RandomUtil.randomInt(10), RandomUtil.randomInt(10));
				String id = snowflake.nextIdStr();
				treeMap.put("id",id);
				treeMap.put("category_id",categoryId);
				// 文章静态化标识重置，便于自动静态化
				treeMap.put("HAS_DETAIL_HTML",0);
				treeMap.put("HAS_LIST_HTML",0);
				// 文章的时间由数据库来决定默认值,防止Oracle时间转换错误
				treeMap.remove("content_datetime");
				treeMap.remove("create_date");
				treeMap.remove("update_date");
				//如果类型是 nclob 或 Clob必须要转换一次
				if (treeMap.get("CONTENT_DETAILS") instanceof Clob || treeMap.get("CONTENT_DETAILS") instanceof NClob || treeMap.get("CONTENT_DETAILS") instanceof NClobProxyImpl) {
					treeMap.put("CONTENT_DETAILS", StringUtil.clobStr((Clob)treeMap.get("CONTENT_DETAILS")));
				}
				siteAppManagerDao.insertContentByMap(treeMap,"cms_content");
				return id;
			}


		}
		return "0";

	}

	@Override
	public void saveSiteAndMDiyModel(AppEntity app) {
//		String query = "INSERT INTO mdiy_dict " +
//				"(dict_value,dict_label,dict_type,dict_description,dict_remarks,app_id,del,not_del) " +
//				"(SELECT  dict_value,dict_label,dict_type,dict_description,dict_remarks,{},del,not_del FROM " +
//				"mdiy_dict WHERE not_del=1 and app_id={})";
//		siteAppManagerDao.excuteSql(StrUtil.format(query,app.getId(), BasicUtil.getApp().getAppId()));
		String table = ConfigUtil.getString("站群配置","siteTables");
		Integer appId = app.getAppId();
		List<String> tables = StrUtil.split(table, ",");
		for (String tableName : tables) {
			try {
				siteAppManagerDao.excuteSql(StrUtil.format("ALTER TABLE `{}` ADD COLUMN `APP_ID` int(11) NULL COMMENT '站点编号';", tableName));
			}catch (Exception e) {
				LOG.warn("{}插入站群编号未成功，可能该表不存在", tableName);
			}
			try {
				siteAppManagerDao.excuteSql(StrUtil.format("UPDATE {} SET APP_ID = {} WHERE APP_ID IS NULL;",tableName, appId));
			}catch (Exception e) {
				LOG.warn("{}表更新未成功，可能该表不存在", tableName);
			}
		}
	}

	@Override
	public void deleteByAppIds(List<String> ids) {
		LambdaQueryWrapper<SiteAppManagerEntity> wrapper = new QueryWrapper<SiteAppManagerEntity>().lambda();
		wrapper.in(SiteAppManagerEntity::getAppId, ids);
		remove(wrapper);
		String del = "DELETE FROM mdiy_dict WHERE APP_ID in ({}) ";
		String appIds = ArrayUtil.join(ids.toArray(), ",");
		siteAppManagerDao.excuteSql(StrUtil.format(del, appIds));
	}


	@Override
	public void initAppId(String tableName) {
		siteAppManagerDao.initAppId(tableName);
	}

	@Override
	public void removeAppId(String tableName) {
		siteAppManagerDao.removeAppId(tableName);
	}
}
