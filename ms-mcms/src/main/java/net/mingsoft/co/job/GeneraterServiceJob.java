/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */











package net.mingsoft.co.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.cms.bean.ContentBean;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.co.constant.e.ProgressStatusEnum;
import net.mingsoft.co.service.CmsParserService;
import net.mingsoft.co.service.FileTagCacheService;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service("generaterServiceJob")
public class GeneraterServiceJob {

    /*
     * log4j日志记录
     */
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Value("${ms.diy.html-dir:html}")
    private String htmlDir;
    @Autowired
    private CmsParserService cmsParserService;

    /**
     * 栏目管理业务层
     */
    @Autowired
    private ICategoryBiz categoryBiz;

    /**
     * 栏目管理业务层
     */
    @Autowired
    private IContentBiz contentBiz;

    @Autowired
    private FileTagCacheService fileTagCacheService;

    /**
     * 定时缓存
     * @param url 网址
     */
    public void cache(String url) {
        fileTagCacheService.cacheAllContent(url);
    }

    /**
     * 定时生成主页
     * @param file 主页模版
     * @param path 生成路径
     * @param dictValue 模版字典值
     * @param url 网址
     */
    public void index(String file,String path,String dictValue,String url){
        LOG.debug("generating index");
        AppEntity app = BasicUtil.getApp(url);
        // TODO 不确定集群的情况
        if (app == null) {
            throw new BusinessException(StrUtil.format("域名地址{}不存在", url));
        }
        Map<String, Object> parserParams = new HashMap<>();
        parserParams.put(ParserUtil.URL, url);
        String style = ParserUtil.getTemplateName(dictValue, app);
        if(style == null) {
            LOG.error("模板类型字典不存在"+dictValue);
            return;
        }
        ParserUtil.putBaseParams(parserParams, app,style);

        // 获取文件所在路径 首先判断用户输入的模版文件是否存在
        if (!FileUtil.exist(ParserUtil.buildTemplatePath(app,null,style, file))) {
            LOG.error("模版不存在"+style);
        } else {
            try {
                cmsParserService.generate(style, file, path, parserParams);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 定时生成栏目
     * @param dictValue 模版字典值
     * @param url 网址
     */
    public void category(String dictValue,String url) throws Exception {
        AppEntity app = BasicUtil.getApp(url);
        // TODO 不确定集群的情况
        if (app == null) {
            throw new BusinessException(StrUtil.format("域名地址{}不存在", url));
        }
        ContentBean contentBean = new ContentBean();
        contentBean.setUpdateDate(DateUtil.parse(DateUtil.today()));
        contentBean.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
        contentBean.setHasListHtml(0);
        if (ConfigUtil.getBoolean("站群配置", "siteEnable")) {
            // 开启了站群表示需要拼接AppId
            contentBean.setAppId(app.getId());
        }
        //只静态化最新的的栏目文章
        List<ContentBean> contentEntities = contentBiz.queryContentIgnoreTenantLine(contentBean);
        if (CollUtil.isEmpty(contentEntities)) {
            LOG.info("当前没有栏目需要静态化");
            return;
        }
        Map<String, Object> parserParams = new HashMap<>();
        parserParams.put(ParserUtil.URL, url);
        parserParams.put("contentStyle", dictValue);
        String style = ParserUtil.getTemplateName(dictValue, app);
        if(style == null) {
            LOG.error("模板类型字典不存在"+dictValue);
            return;
        }
        ParserUtil.putBaseParams(parserParams, app,style);
        //栏目列表
        List<CategoryEntity> columns = new ArrayList<>();
        if(contentEntities.size()>0) {
            Set<String> set = new HashSet<>();
            contentEntities.forEach(content->{
                if(!set.contains(content.getCategoryId())) {
                    // 添加当前栏目及其所有父栏目,文章已经拼接站点处理
                    CategoryEntity category = categoryBiz.getEntityById(content.getCategoryId());
                    columns.add(category);
                    set.add(content.getCategoryId());
                    if(StringUtils.isNotBlank(category.getCategoryParentIds())) {
                        List<CategoryEntity> categoryList = categoryBiz.queryCategoryIgnoreSite(category.getCategoryParentIds().split(","));
                        columns.addAll(categoryList);
                        set.addAll(categoryList.stream().map(CategoryEntity::getId).collect(Collectors.toList()));
                    }

                }
            });
        }

        // 获取栏目列表模版
        for (CategoryEntity column : columns) {
            cmsParserService.category( column, style, parserParams);
        }
        // 设置静态化标识
        List<String> contentIds = contentEntities.stream().map(ContentEntity::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(contentIds)) {
            LambdaUpdateWrapper<ContentEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(ContentEntity::getId, contentIds);
            updateWrapper.set(ContentEntity::getHasListHtml, 1);
            contentBiz.update(updateWrapper);
        }
    }

    /**
     * 定时生成指定栏目,一般用于生成聚合页面
     * @param dictValue 模版字典值
     * @param url 网址
     * @param categoryIds 栏目ID,执行栏目ID生成,多个用 | 号分隔
     */
    public void category(String dictValue,String url, String categoryIds) throws Exception {
        if (StringUtils.isEmpty(categoryIds)) {
            throw new BusinessException("categoryIds不能为空");
        }

        this.category(dictValue,url,CollUtil.newArrayList(categoryIds));
    }


    /**
     * 定时生成指定栏目,一般用于生成聚合页面
     * @param dictValue 模版字典值
     * @param url 网址
     * @param categoryIds 栏目ID,执行栏目ID生成,多个用 | 号分隔
     */
    public void category(String dictValue,String url, List categoryIds) throws Exception {
        if (CollUtil.isEmpty(categoryIds)) {
            throw new BusinessException("categoryIds不能为空");
        }
        AppEntity app = BasicUtil.getApp(url);
        // TODO 不确定集群的情况
        if (app == null) {
            throw new BusinessException(StrUtil.format("域名地址{}不存在", url));
        }
        Map<String, Object> parserParams = new HashMap<>();
        parserParams.put(ParserUtil.URL, url);
        parserParams.put("contentStyle", dictValue);
        parserParams.put("style", dictValue);
        String style = ParserUtil.getTemplateName(dictValue, app);
        if(style == null) {
            LOG.error("模板类型字典不存在"+dictValue);
            return;
        }
        ParserUtil.putBaseParams(parserParams, app,style);
        //栏目列表
        String[] ids = new String[categoryIds.size()];
        for (int i = 0; i < categoryIds.size(); i++) {
            ids[i] = (String) categoryIds.get(i);
        }
        List<CategoryEntity> columns = categoryBiz.queryCategoryIgnoreSite(ids);
        LOG.debug("have：{} categorys list page needs to be generated",columns.size());
        // 获取栏目列表模版
        for (CategoryEntity column : columns) {
            if(StringUtils.isNotBlank(style)) {
                cmsParserService.category( column, style, parserParams);
            }
        }
    }

    /**
     * 定时生成内容
     * @param dictValue 模版字典值
     * @param url 网址
     */
    public void content(String dictValue,String url) throws Exception {
        AppEntity app = BasicUtil.getApp(url);
        // TODO 不确定集群的情况
        if (app == null) {
            throw new BusinessException(StrUtil.format("域名地址{}不存在", url));
        }
        //只静态化最新的的栏目文章
        ContentBean contentBean = new ContentBean();
        contentBean.setUpdateDate(DateUtil.parse(DateUtil.today()));
        contentBean.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
        contentBean.setHasDetailHtml(0);
        if (ConfigUtil.getBoolean("站群配置", "siteEnable")) {
            // 开启了站群表示需要拼接AppId
            contentBean.setAppId(app.getId());
        }
        //只静态化最新的的栏目文章
        List<ContentBean> contentEntities = contentBiz.queryContentIgnoreTenantLine(contentBean);
        if (CollUtil.isEmpty(contentEntities)) {
            LOG.info("当前没有文章需要静态化");
            return;
        }

        // 网站风格物理路径
        List<CategoryEntity> categoryList = new ArrayList<>();
        String dateTime = DateUtil.today();
        Map<String, Object> parserParams = new HashMap<>();
        parserParams.put(ParserUtil.URL, url);
        parserParams.put("contentStyle", dictValue);
        String style = ParserUtil.getTemplateName(dictValue, app);
        if(style == null) {
            LOG.error("模板类型字典不存在"+dictValue);
            return;
        }
        ParserUtil.putBaseParams(parserParams, app,style);

        if(contentEntities.size()>0) {
            Set<String> set = new HashSet<>();
            contentEntities.forEach(content->{
                if(!set.contains(content.getCategoryId())) {
                    categoryList.add(categoryBiz.getEntityById(content.getCategoryId()));
                    set.add(content.getCategoryId());
                }
            });
        }
//        categoryList = categoryBiz.list();
        parserParams.put("isParser", 0);
        LOG.debug("have：{} categorys,and：{} contents needs to be generated", categoryList.size(), contentEntities.size());
        for (CategoryEntity category : categoryList) {
            cmsParserService.content(category, style, dateTime, parserParams);
        }
        // 设置静态化标识
        List<String> contentIds = contentEntities.stream().map(ContentEntity::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(contentIds)) {
            LambdaUpdateWrapper<ContentEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(ContentEntity::getId, contentIds);
            updateWrapper.set(ContentEntity::getHasDetailHtml, 1);
            contentBiz.update(updateWrapper);
        }
    }

}
