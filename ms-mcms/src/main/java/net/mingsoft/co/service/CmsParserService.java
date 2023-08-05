/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.co.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;

import cn.hutool.json.JSONUtil;
import net.mingsoft.base.constant.Const;
import net.mingsoft.base.exception.BusinessException;
import net.mingsoft.base.util.BundleUtil;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.constant.e.CategoryDisplayEnum;
import net.mingsoft.co.cache.GeneraterProgressCache;
import net.mingsoft.cms.bean.CategoryBean;
import net.mingsoft.cms.bean.ContentBean;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.constant.e.CategoryTypeEnum;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.co.constant.e.ProgressStatusEnum;
import net.mingsoft.mdiy.bean.PageBean;
import net.mingsoft.mdiy.biz.IModelBiz;
import net.mingsoft.mdiy.biz.impl.ModelBizImpl;
import net.mingsoft.mdiy.entity.ModelEntity;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: 铭软开发团队
 * @Date: 2020/8/9 20:47
 * 2021-05-6 增加文章属性生成规则<br/>
 */
@Service
public class CmsParserService {

    /*
     * log4j日志记录
     */
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /**
     * 文章管理业务层
     */
    @Autowired
    private IContentBiz contentBiz;

    @Autowired
    private RenderingService renderingService;


    @Value("${ms.diy.html-dir:html}")
    private String htmlDir;

    @Autowired
    public GeneraterProgressCache generaterProgressCache;

    /**
     * 指定路径进行生成静态页面
     *
     * @param templatePath 模版路径
     * @param targetPath   生成后的路径
     * @throws IOException
     */
    public void generate(String templateName, String templatePath, String targetPath, Map<String, Object> parserParams) throws IOException {
        LOG.debug("主页生成路径：{}",targetPath);
        String buildTemplatePath = ParserUtil.buildTemplatePath(parserParams);
        String templateFolder = buildTemplatePath.concat(templateName);
        String templateContent = FileUtil.readString(FileUtil.file(templateFolder, File.separator + templatePath), CharsetUtil.CHARSET_UTF_8);
        //替换标签
        templateContent = ParserUtil.replaceTag(templateContent);
        renderingService.rendering(parserParams, templateFolder, templatePath, templateContent, ParserUtil.buildGenerateHtmlPath(htmlDir, parserParams.get(ParserUtil.APP_DIR).toString(), parserParams.get(ParserUtil.APP_TEMPLATE).toString(), targetPath));

    }

    /**
     * 栏目生成
     *
     * @param category 栏目
     * @param templateName    对应模版名称
     */
    @Async("cmsParserServiceCategoryThreadPool")
    public void category(CategoryEntity category, String templateName, Map<String, Object> parserParams) throws Exception {
        synchronized (this) {
            // 1.参数检查
            if (StringUtils.isBlank(category.getCategoryType())) {
                throw new BusinessException(StrUtil.format("栏目没有类型,请检查{}栏目ID:{}是否正常", category.getCategoryTitle(), category.getId()));
            }
            // 2.设置进度缓存
            //文章列表
            int arrayMap = generaterProgressCache.getInt("category".concat(parserParams.get("contentStyle").toString()));

            // 手动和自动统一在这里做一次性处理
            if (CategoryDisplayEnum.DISABLE.toString().equalsIgnoreCase(category.getCategoryDisplay())){
                LOG.warn("column {} is disable",category.getId());
                if (arrayMap > 0){
                    generaterProgressCache.saveOrUpdate(parserParams.get("progressKey").toString(),--arrayMap);
                }
                return;
            }
            generaterProgressCache.flush();

            long s = System.currentTimeMillis();

            // 3.查询当前栏目下的所有文章ID
            ContentBean contentBean = new ContentBean();
            contentBean.setCategoryId(category.getId());
            contentBean.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
            contentBean.setContentStyle(parserParams.get("contentStyle").toString());
            //这里会根据栏目类型执行不同的语句
            contentBean.setCategoryType(category.getCategoryType());
            List<String> articleIdList = contentBiz.queryIdsByCategoryIdForParser(contentBean).stream().map(CategoryBean::getArticleId).collect(Collectors.toList());
            LOG.debug("category：{},id：{};have：{} contents needs to generate",category.getCategoryTitle(),category.getId(),articleIdList.size());
            // 4.初始化静态化所需要的一些参数
            String buildTemplatePath = ParserUtil.buildTemplatePath(parserParams);
            String templateFolder = buildTemplatePath.concat(templateName);
            ModelEntity contentModel = null;
            // 判断当前栏目是否有自定义模型, 初始化自定义模型参数
            if (StringUtils.isNotBlank(category.getMdiyModelId())) {
                // 通过栏目模型编号获取自定义模型实体
                contentModel = SpringUtil.getBean(ModelBizImpl.class).getById(category.getMdiyModelId());
            }

            if (contentModel != null) {
                // 将自定义模型编号设置为key值
                parserParams.put(ParserUtil.TABLE_NAME, contentModel.getModelTableName());
            }

            // 栏目实体
            parserParams.put(ParserUtil.COLUMN, category);
            // 模板名称
            parserParams.put(ParserUtil.TEMPLATE_NAME, templateName);
            // 标签中使用field获取当前栏目
            parserParams.put(ParserUtil.FIELD, category);


            // 5.判断列表类型,进行对应的静态化方式
            switch (CategoryTypeEnum.get(category.getCategoryType())) {
                //TODO 暂时先用字符串代替
                case LIST: // 列表
                    if (StringUtils.isBlank(category.getCategoryListUrls(templateName)) || !FileUtil.exist(ParserUtil.buildTemplatePath(parserParams, templateName).concat(File.separator).concat(category.getCategoryListUrls(templateName)))) {
                        LOG.debug("列表栏目:{}，模版:{} 不存在", category.getCategoryTitle(), category.getCategoryListUrls(templateName));
                        break;
                    }
                    // 文章的栏目模型编号
                    PageBean page = new PageBean();
                    int pageSize = ParserUtil.getPageSize(templateFolder, category.getCategoryListUrls(templateName), 20);
                    page.setSize(pageSize);
                    String templateContent = FileUtil.readString(FileUtil.file(templateFolder, File.separator + category.getCategoryListUrls(templateName)), CharsetUtil.CHARSET_UTF_8);
                    //读取模版内容\替换标签
                    templateContent = ParserUtil.replaceTag(templateContent);

                    //如果存在分页标签就解析栏目列表，生成分页静态文件,否则只生成index.html文件
                    if (pageSize > 0) {

                        //获取列表中的size
                        page.setRcount(articleIdList.size());

                        int totalPageSize = PageUtil.totalPage(articleIdList.size(), page.getSize());
                        page.setTotal(totalPageSize);
                        LOG.debug("分类：{}，总记录:{}，一页显示数量:{}，总页数:{}", category.getCategoryTitle(), articleIdList.size(), page.getSize(), page.getTotal());

                        String writeFilePath;


                        int pageNo = 1;
                        //文章列表页没有写文章列表标签，总数为0
                        if (totalPageSize <= 0) {
                            // 数据库中第一页是从开始0*size
                            //是否开启短链
                            if ((Boolean) parserParams.get(ParserUtil.SHORT_SWITCH)) {
                                writeFilePath = ParserUtil.buildGenerateHtmlPath(htmlDir, parserParams.get(ParserUtil.APP_DIR).toString(), parserParams.get(ParserUtil.APP_TEMPLATE).toString(), category.getCategoryPinyin());
                            } else {
                                writeFilePath = ParserUtil.buildGenerateHtmlPath(htmlDir, parserParams.get(ParserUtil.APP_DIR).toString(), parserParams.get(ParserUtil.APP_TEMPLATE).toString(), category.getCategoryPath().concat(File.separator).concat(ParserUtil.INDEX));
                            }
                            LOG.debug("generate write path：{}",writeFilePath);
                            // 设置分页的起始位置
                            page.setPageNo(pageNo);
                            parserParams.put(ParserUtil.PAGE, page);

                            Map temp = MapUtil.newHashMap();
                            temp.putAll(parserParams);

                            PageBean _page = new PageBean();
                            BeanUtil.copyProperties(page, _page);
                            temp.put(ParserUtil.PAGE, _page);


                            CategoryEntity _category = new CategoryEntity();
                            BeanUtil.copyProperties(category, _category);
                            temp.put(ParserUtil.COLUMN, _category);
                            temp.put(ParserUtil.FIELD, _category);
                            temp.remove(ParserUtil.ID);

                            renderingService.rendering(temp, templateFolder, File.separator + category.getCategoryListUrls(templateName), templateContent, writeFilePath);

                        } else {
                            // 遍历分页
                            for (int i = 0; i < totalPageSize; i++) {
                                if (i == 0) {

                                    // 首页路径index.html
                                    //是否开启短链
                                    if ((Boolean) parserParams.get(ParserUtil.SHORT_SWITCH)) {
                                        writeFilePath = ParserUtil.buildGenerateHtmlPath(htmlDir, parserParams.get(ParserUtil.APP_DIR).toString(), parserParams.get(ParserUtil.APP_TEMPLATE).toString(), category.getCategoryPinyin());

                                    } else {
                                        writeFilePath = ParserUtil.buildGenerateHtmlPath(htmlDir, parserParams.get(ParserUtil.APP_DIR).toString(), parserParams.get(ParserUtil.APP_TEMPLATE).toString(), category.getCategoryPath().concat(File.separator).concat(ParserUtil.INDEX));
                                    }
                                } else {

                                    // 其他路径list-2.html
                                    //是否开启短链
                                    if ((Boolean) parserParams.get(ParserUtil.SHORT_SWITCH)) {
                                        writeFilePath = ParserUtil.buildGenerateHtmlPath(htmlDir, parserParams.get(ParserUtil.APP_DIR).toString(), parserParams.get(ParserUtil.APP_TEMPLATE).toString(), category.getCategoryPinyin().concat("-").concat(String.valueOf(pageNo)));
                                    } else {
                                        writeFilePath = ParserUtil.buildGenerateHtmlPath(htmlDir, parserParams.get(ParserUtil.APP_DIR).toString(), parserParams.get(ParserUtil.APP_TEMPLATE).toString(), category.getCategoryPath().concat(File.separator).concat(ParserUtil.PAGE_LIST).concat(String.valueOf(pageNo)));

                                    }


                                }
                                // 设置分页的起始位置
                                page.setPageNo(pageNo);

                                //由于开启了多线程，parserParams的对象类型存在线程安全，必须进行深度复制创建新的parserParams对象
                                Map temp = MapUtil.newHashMap();
                                temp.putAll(parserParams);

                                PageBean _page = new PageBean();
                                BeanUtil.copyProperties(page, _page);
                                temp.put(ParserUtil.PAGE, _page);


                                CategoryEntity _category = new CategoryEntity();
                                BeanUtil.copyProperties(category, _category);
                                temp.put(ParserUtil.COLUMN, _category);
                                temp.put(ParserUtil.FIELD, _category);
                                temp.remove(ParserUtil.ID);

                                renderingService.rendering(temp, templateFolder, File.separator + category.getCategoryListUrls(templateName), templateContent, writeFilePath);

                                pageNo++;


                            }
                        }

                    } else {
                        // 数据库中第一页是从开始0*size
                        String writeFilePath = ParserUtil.buildGenerateHtmlPath(htmlDir, parserParams.get(ParserUtil.APP_DIR).toString(), parserParams.get(ParserUtil.APP_TEMPLATE).toString(), category.getCategoryPath().concat(File.separator).concat(ParserUtil.INDEX));
                        //是否开启短链
                        if ((Boolean) parserParams.get(ParserUtil.SHORT_SWITCH)) {
                            writeFilePath = ParserUtil.buildGenerateHtmlPath(htmlDir, parserParams.get(ParserUtil.APP_DIR).toString(), parserParams.get(ParserUtil.APP_TEMPLATE).toString(), category.getCategoryPinyin());
                        }
                        LOG.debug("generate write path：{}",writeFilePath);

                        // 设置分页的起始位置
                        page.setPageNo(1);
                        parserParams.put(ParserUtil.PAGE, page);

                        Map temp = MapUtil.newHashMap();
                        temp.putAll(parserParams);

                        PageBean _page = new PageBean();
                        BeanUtil.copyProperties(page, _page);
                        temp.put(ParserUtil.PAGE, _page);


                        CategoryEntity _category = new CategoryEntity();
                        BeanUtil.copyProperties(category, _category);
                        temp.put(ParserUtil.COLUMN, _category);
                        temp.put(ParserUtil.FIELD, _category);
                        temp.remove(ParserUtil.ID);


                        renderingService.rendering(temp, templateFolder, File.separator + category.getCategoryListUrls(templateName), templateContent, writeFilePath);

                    }

                    break;
                case COVER:// 单页
                    if (StringUtils.isBlank(category.getCategoryUrls(templateName)) || !FileUtil.exist(ParserUtil.buildTemplatePath(parserParams, templateName).concat(File.separator).concat(category.getCategoryUrls(templateName)))) {
                        LOG.debug("单篇栏目:{}，模版:{} 不存在", category.getCategoryTitle(), category.getCategoryUrls(templateName));
                        break;
                    }

                    // 如果栏目下没有文章则跳过静态化
                    if (CollUtil.isEmpty(articleIdList)) {
                        break;
                    }
                    //读取模版内容
                    String _templateContent = FileUtil.readString(FileUtil.file(templateFolder, File.separator + category.getCategoryUrls(templateName)), CharsetUtil.CHARSET_UTF_8);
                    //替换标签
                    _templateContent = ParserUtil.replaceTag(_templateContent);
                    try {
                        // 单篇没有上一篇下一篇
                        this.generateBasic(parserParams, new ArrayList<>(), articleIdList, templateFolder, _templateContent);
                    } catch (Exception e) {
                        // 防止抛出异常后进度条卡死
                        generaterProgressCache.saveOrUpdate("category".concat(parserParams.get("contentStyle").toString()), --arrayMap);
                        generaterProgressCache.flush();
                        LOG.error(e.getMessage(), e);
                    }
                    break;
            }

            // 6.刷新缓存
            if (arrayMap > 0) {
                generaterProgressCache.saveOrUpdate("category".concat(parserParams.get("contentStyle").toString()), --arrayMap);
                generaterProgressCache.flush();
            }

            LOG.debug("耗时：{}", System.currentTimeMillis() - s);
        }
    }


    /**
     * 文章生成
     *
     * @param category 栏目
     * @param templateName    对应模版名称
     * @param dateTime 时间范围
     */
    @Async("cmsParserServiceContentThreadPool")
    public void content(CategoryEntity category, String templateName, String dateTime, Map<String, Object> parserParams) throws Exception {
        synchronized (this) {
            // 1.设置进度缓存
            int arrayMap = generaterProgressCache.getInt("content".concat(parserParams.get("contentStyle").toString()));
            // 单篇由栏目生成
            if (category.getCategoryType().equals(CategoryTypeEnum.COVER.toString())) {
                if (arrayMap > 0) {
                    generaterProgressCache.saveOrUpdate("content".concat(parserParams.get("contentStyle").toString()), --arrayMap);
                    generaterProgressCache.flush();
                }
                return;
            }
            // 判断栏目是否开启，手动和自动统一在这里做一次性处理
            if (CategoryDisplayEnum.DISABLE.toString().equalsIgnoreCase(category.getCategoryDisplay())){
                LOG.warn("column {} is disable",category.getId());
                if (arrayMap > 0){
                    generaterProgressCache.saveOrUpdate(parserParams.get("progressKey").toString(),--arrayMap);
                }
                return;
            }
            generaterProgressCache.flush();

            // 2.查询当前栏目下的所有文章ID
            ContentBean contentBean = new ContentBean();
            contentBean.setContentStyle(parserParams.get("contentStyle").toString());
            contentBean.setCategoryId(category.getId());
            contentBean.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
            //这里会根据栏目类型执行不同的语句
            contentBean.setCategoryType(category.getCategoryType());
            Object appId = parserParams.get("appId");
            if (appId != null){
                contentBean.setAppId(appId.toString());
            }
            contentBean.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
            contentBean.setOrderBy("date");
            // 存放需要被静态化的文章ID
            List<CategoryBean> articleList = contentBiz.queryIdsByCategoryId(contentBean);
            // 统一栏目下所有终审通过的文章id数组,用于文章上一页下一页
            List<String> allArticleIds = articleList.stream().map(CategoryBean::getArticleId).collect(Collectors.toList());
            if (StringUtils.isNotBlank(dateTime)) {
                Date date = DateUtil.parseDate(dateTime);
                articleList = articleList.stream().filter(
                        entity -> {
                            // 筛选时间之后的文章
                            if (entity.getUpdateDate() == null) {
                                LOG.error("文章["+entity.getArticleId()+"]没有更新时间,请检查数据是否正常!");
                                return false;
                            }
                            return date.getTime() <= entity.getUpdateDate().getTime();
                        }
                ).collect(Collectors.toList());
            }
            List<String> articleIds = articleList.stream().map(CategoryBean::getArticleId).collect(Collectors.toList());
            LOG.debug("Belong to category：{},id：{};have：{} contents needs to generate",category.getCategoryTitle(),category.getId(),articleIds.size());

            // 3.如果有文章则表示需要静态化
            if (CollUtil.isNotEmpty(articleIds)) {
                if (StringUtils.isBlank(category.getCategoryUrls(templateName)) || !FileUtil.exist(ParserUtil.buildTemplatePath(parserParams,templateName).concat(File.separator).concat(category.getCategoryUrls(templateName)))) {
                    LOG.debug("栏目:{}，模版:{} 不存在", category.getCategoryTitle(), category.getCategoryUrls(templateName));
                } else {
                    // 3.1. 初始化静态化所需要的一些参数
                    String buildTemplatePath = ParserUtil.buildTemplatePath(parserParams);
                    String templateFolder = buildTemplatePath.concat(templateName);//需要模板文件名
                    parserParams.put(ParserUtil.DATE_TIME, dateTime);
                    parserParams.put(ParserUtil.HTML, htmlDir);
                    parserParams.put(ParserUtil.TEMPLATE_NAME, templateName);
                    parserParams.put(ParserUtil.COLUMN, category);

                    // 3.2. 渲染模板
                    String _templateContent = FileUtil.readString(FileUtil.file(templateFolder, File.separator + category.getCategoryUrls(templateName)), CharsetUtil.CHARSET_UTF_8);
                    //替换标签
                    LOG.debug("栏目:{}，模版:{} ", category.getCategoryTitle(), category.getCategoryUrls(templateName));
                    _templateContent = ParserUtil.replaceTag(_templateContent);
                    try {
                        this.generateBasic(parserParams, allArticleIds, articleIds, templateFolder, _templateContent);
                    } catch (Exception e) {
                        // 防止抛出异常后进度条卡死
                        generaterProgressCache.saveOrUpdate("content".concat(parserParams.get("contentStyle").toString()), --arrayMap);
                        generaterProgressCache.flush();
                        LOG.error(e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
            }
            // 4.刷新缓存
            if (arrayMap > 0) {
                generaterProgressCache.saveOrUpdate("content".concat(parserParams.get("contentStyle").toString()), --arrayMap);
                generaterProgressCache.flush();
            }

        }

    }

    @Async
    public void cache(String key,List arrayMap) {
        synchronized (this) {
            LOG.info("刷新缓存 {}",key);
            generaterProgressCache.saveOrUpdate(key, arrayMap);
            generaterProgressCache.flush();
        }
    }

    public void contentById(CategoryEntity category, List<String> contentIds, String templateName, Map<String, Object> parserParams) throws Exception {
        synchronized (this) {

            // 1.查询当前栏目下的所有文章ID
            ContentBean contentBean = new ContentBean();
            contentBean.setContentStyle(parserParams.get("contentStyle").toString());
            contentBean.setCategoryId(category.getId());
            contentBean.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
            //这里会根据栏目类型执行不同的语句
            contentBean.setCategoryType(category.getCategoryType());
            Object appId = parserParams.get("appId");
            if (appId != null){
                contentBean.setAppId(appId.toString());
            }
            contentBean.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
            contentBean.setOrderBy("date");
            List<CategoryBean> articleIdList = contentBiz.queryIdsByCategoryId(contentBean);
            List<String> articleIds = articleIdList.stream().map(CategoryBean::getArticleId).collect(Collectors.toList());

            // 2.有符合条件的就更新
            if (CollUtil.isNotEmpty(articleIdList)) {
                if (StringUtils.isBlank(category.getCategoryUrls(templateName)) || !FileUtil.exist(ParserUtil.buildTemplatePath(parserParams,templateName).concat(File.separator).concat(category.getCategoryUrls(templateName)))) {
                    LOG.debug("栏目:{}，模版:{} 不存在", category.getCategoryTitle(), category.getCategoryUrls(templateName));
                } else {

                    // 2.1. 初始化静态化所需要的一些参数
                    String buildTemplatePath = ParserUtil.buildTemplatePath(parserParams);
                    String templateFolder = buildTemplatePath.concat(templateName);//需要模板文件名
                    parserParams.put(ParserUtil.HTML, htmlDir);
                    parserParams.put(ParserUtil.TEMPLATE_NAME, templateName);
                    parserParams.put(ParserUtil.COLUMN, category);

                    String _templateContent = FileUtil.readString(FileUtil.file(templateFolder, File.separator + category.getCategoryUrls(templateName)), CharsetUtil.CHARSET_UTF_8);
                    //替换标签
                    LOG.debug("栏目:{}，模版:{} ", category.getCategoryTitle(), category.getCategoryUrls(templateName));
                    _templateContent = ParserUtil.replaceTag(_templateContent);
                    try {
                        this.generateBasic(parserParams, articleIds, contentIds, templateFolder, _templateContent);
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * @param contentStyle 文章模板类型
     * @param category 栏目实体
     * @param contentId 文章ID
     * 根据文章实体删除静态文件
     */
    public void deleteHtml(String contentStyle,  CategoryEntity category,String contentId) {
        // 栏目不是列表类型直接返回
        if (!CategoryTypeEnum.LIST.toString().equalsIgnoreCase(category.getCategoryType())){
            LOG.debug("category:{} Not a list",category.getCategoryTitle());
            return;
        }
        // html真实路径
        String htmlPath = BasicUtil.getRealPath(htmlDir);
        // appDir
        String appDir = BasicUtil.getApp().getAppDir();
        // 获取发布到数组
        String[] contentStyles = contentStyle.split(",");
        // 短链情况下的文章静态html位置不同
        Boolean shortSwitch = ConfigUtil.getBoolean("静态化配置", "shortSwitch", false);
        Boolean siteEnable = ConfigUtil.getBoolean("站群配置", "siteEnable");
        String path = "";
        String url = "";
        boolean singleTmpl = false;
        if (contentStyles.length==1){
            singleTmpl = true;
        }
        // 遍历栏目style
        for (String templateName : contentStyles) {
            // 开启了短链并且没有开启站群的情况下不拼接appDir
            if (shortSwitch && !siteEnable){
                url = htmlPath + File.separator + (singleTmpl?"":ParserUtil.getTemplateName(templateName)+ File.separator);
            } else {
                url = htmlPath + File.separator + appDir + File.separator + (singleTmpl?"":ParserUtil.getTemplateName(templateName)+ File.separator);
            }

            // 判断具体文件路径
            if (shortSwitch){
                path = url + contentId + ParserUtil.HTML_SUFFIX;
            } else {
                path = url + category.getCategoryPath() + File.separator + contentId + ParserUtil.HTML_SUFFIX;
            }
            // 删除静态文件
            // 文件路径组成 html真实路径 + appdir + 模板名称 + 栏目路径 + 文章ID + .html
            if (StringUtils.isNotBlank(path)){
                // 校验路径是否合法
                if (path.contains("..") || path.contains("../") || path.contains("..\\")) {
                    LOG.error("非法路径："+path);
                    throw new BusinessException(BundleUtil.getString(Const.RESOURCES,"err.error",BundleUtil.getString(net.mingsoft.basic.constant.Const.RESOURCES,"file.path")));
                }
                boolean flag = FileUtil.del(path);
                if (flag) {
                    LOG.info("删除静态文件成功！");
                }else {
                    LOG.info("删除失败！");
                }
            }
        }
    }

    /**
     * @param contentStyle 文章模板类型
     * @param category 栏目实体
     * 删除栏目静态文件
     */
    public void deleteCategoryHtml(String contentStyle,  CategoryEntity category) {
        // html真实路径
        String htmlPath = BasicUtil.getRealPath(htmlDir);
        // appDir
        String appDir = BasicUtil.getApp().getAppDir();
        // 获取发布到数组
        String[] contentStyles = contentStyle.split(",");
        // 短链及站群情况下的文章静态html目录结构不同
        Boolean shortSwitch = ConfigUtil.getBoolean("静态化配置", "shortSwitch", false);
        Boolean siteEnable = ConfigUtil.getBoolean("站群配置", "siteEnable");
        String path = "";
        String url = "";
        boolean singleTmpl = false;
        if (contentStyles.length==1){
            singleTmpl = true;
        }
        // 遍历栏目style
        for (String templateName : contentStyles) {
            // 开启了短链并且没有开启站群的情况下不拼接appDir
            if (shortSwitch && !siteEnable){
                url = htmlPath + File.separator + (singleTmpl?"":ParserUtil.getTemplateName(templateName)+ File.separator);
            } else {
                url = htmlPath + File.separator + appDir + File.separator + (singleTmpl?"":ParserUtil.getTemplateName(templateName)+ File.separator);
            }

            if (shortSwitch){
                path = url + category.getCategoryPinyin() + ParserUtil.HTML_SUFFIX;
                // 开启一个线程去删除短链情况下的所属文章html文件
                ICategoryBiz categoryBiz = SpringUtil.getBean(ICategoryBiz.class);
                List<CategoryEntity> categoryEntities = categoryBiz.queryChildren(category);
                List<String> categoryIds = categoryEntities.stream().map(CategoryEntity::getId).collect(Collectors.toList());
                categoryIds.add(category.getId());
                List<Map> contents = (List<Map>) contentBiz.excuteSql(StrUtil.format("select id from cms_content where category_id in ({})",StrUtil.join(",",categoryIds)));
                LOG.debug("短链情况下删除栏目:{}需要删除的文章数:{}",category.getCategoryTitle(),contents.size());
                String finalUrl = url;
                List<String> contentPaths = contents.stream().map(contentEntity -> {
                    return finalUrl + contentEntity.get("id") + ParserUtil.HTML_SUFFIX;
                }).collect(Collectors.toList());
                Thread thread = new Thread(new DeleteContentHtml(contentPaths));
                thread.run();

            } else {
                path = url + category.getCategoryPath();
            }
            // 删除静态文件
            // 校验路径是否合法
            if (path.contains("..") || path.contains("../") || path.contains("..\\")) {
                LOG.error("非法路径："+path);
                throw new BusinessException(BundleUtil.getString(Const.RESOURCES,"err.error",BundleUtil.getString(net.mingsoft.basic.constant.Const.RESOURCES,"file.path")));
            }
            // 文件路径组成 html真实路径 + appdir + 模板名称 + 栏目路径
            boolean flag = FileUtil.del(path);
            if (flag) {
                LOG.info("删除静态文件成功！");
            }else {
                LOG.info("删除失败！");
            }
        }
    }


    /**
     * 生成内容
     * @param parserParams 静态化所需要的一些参数
     * @param allArticleIds 所有的文章id数组,用于文章上一页下一页
     * @param articleIds 文章集合
     * @param templateFolder 模板所在目录
     * @param templateContent 模板内容
     */
    public void generateBasic(Map<String, Object> parserParams, List<String> allArticleIds, List<String> articleIds, String templateFolder, String templateContent) {


        Map<Object, Object> contentModelMap = new HashMap<>();
        ModelEntity contentModel = null;
        // 记录已经生成了文章编号
        List<String> generateIds = new ArrayList<>();
        // 所有的文章id数组

        // 时间内的文章数
        int size = articleIds.size();
        // 所有文章数,从0开始
        int totalSize = allArticleIds.size() - 1;
        for (int artId = 0; artId < size; ) {

            // 文章编号
            String articleId = articleIds.get(artId);
            // 文章是否已经生成了，生成了就跳过
            if (generateIds.contains(articleId)) {
                artId++;
                continue;
            }
            //设置分页类
            PageBean page = new PageBean();
            String writeFilePath = null;
            CategoryEntity category = (CategoryEntity) parserParams.get(ParserUtil.COLUMN);
            // 文章的栏目路径
            String articleColumnPath = category.getCategoryPath();
            // 该文章相关分类
//            String categoryParentId = categoryBean.getId();
//            if (StringUtils.isNotBlank(categoryBean.getCategoryParentIds())) {
//                categoryParentId += ',' + categoryBean.getCategoryParentIds();
//            }
            // 文章的模板路径
            String templateFile = category.getCategoryUrls(parserParams.get(ParserUtil.TEMPLATE_NAME).toString());
            // 文章的栏目模型编号
            String columnContentModelId = null;
            if (category.getMdiyModelId() != null && StringUtils.isNotBlank(category.getMdiyModelId())) {
                columnContentModelId = category.getMdiyModelId();
            }

            // 判断文件是否存在，若不存在弹出返回信息
            if (category.getId() == null || category.getCategoryType() == null) {
                artId++;
                continue;
            }

            // 将
            generateIds.add(articleId);
            // 如果是封面就生成index.html
            if (category.getCategoryType().equals(CategoryTypeEnum.COVER.toString())) {
                LOG.debug("before cover content assign writePath");
                //是否开启短链
                if ((Boolean) parserParams.get(ParserUtil.SHORT_SWITCH)){
                    writeFilePath = ParserUtil.buildGenerateHtmlPath(htmlDir, parserParams.get(ParserUtil.APP_DIR).toString(),parserParams.get(ParserUtil.APP_TEMPLATE).toString(), category.getCategoryPinyin());

                }else {
                    writeFilePath = ParserUtil.buildGenerateHtmlPath(htmlDir, parserParams.get(ParserUtil.APP_DIR).toString(),parserParams.get(ParserUtil.APP_TEMPLATE).toString(), articleColumnPath.concat(File.separator).concat(ParserUtil.INDEX));

                }
                LOG.debug("after cover content assign writePath：{}",writeFilePath);
            } else {
                // 组合文章路径如:html/站点id/栏目id/文章id.html
                //是否开启短链
                LOG.debug("before list content assign writePath");
                if ((Boolean) parserParams.get(ParserUtil.SHORT_SWITCH)){
                    writeFilePath = ParserUtil.buildGenerateHtmlPath(htmlDir, parserParams.get(ParserUtil.APP_DIR).toString(),parserParams.get(ParserUtil.APP_TEMPLATE).toString(), articleId);

                }else {
                    writeFilePath = ParserUtil.buildGenerateHtmlPath(htmlDir, parserParams.get(ParserUtil.APP_DIR).toString(),parserParams.get(ParserUtil.APP_TEMPLATE).toString(), articleColumnPath.concat(File.separator).concat(articleId));

                }
                LOG.debug("after cover content assign writePath：{}",writeFilePath);
            }

            // 判断当前栏目是否有自定义模型
            if (columnContentModelId != null) {
                // 通过当前栏目的模型编号获取，自定义模型表名
                if (contentModelMap.containsKey(columnContentModelId)) {
                    parserParams.put(ParserUtil.TABLE_NAME, contentModel.getModelTableName());
                } else {
                    // 通过栏目模型编号获取自定义模型实体
                    contentModel = SpringUtil.getBean(IModelBiz.class).getById(columnContentModelId);
                    // 将自定义模型编号设置为key值
                    contentModelMap.put(columnContentModelId, contentModel.getModelTableName());
                    parserParams.put(ParserUtil.TABLE_NAME, contentModel.getModelTableName());
                }
            }
            parserParams.put(ParserUtil.ID, articleId);

            // 只有列表有上一页下一页
            if (category.getCategoryType().equals(CategoryTypeEnum.LIST.toString())) {
                // 大于0则表示文章有上一页
                if (artId > 0) {
                    page.setPreId(allArticleIds.get(artId - 1));
                }
                // 当前文章数小于文章总数则表示有下一页
                if (artId < totalSize) {
                    page.setNextId(allArticleIds.get(artId + 1));
                }
            }
            parserParams.put(ParserUtil.PAGE, page);
            //由于开启了多线程，parserParams的对象类型存在线程安全，必须进行深度复制创建新的parserParams对象
            HashMap<Object, Object> _parserParams = MapUtil.newHashMap();
            _parserParams.putAll(parserParams);

            PageBean _page = new PageBean();
            BeanUtil.copyProperties(page, _page);
            _parserParams.put(ParserUtil.PAGE, _page);

            CategoryBean _categoryBean = new CategoryBean();
            BeanUtil.copyProperties(category, _categoryBean);
            _parserParams.put(ParserUtil.COLUMN, _categoryBean);

            LOG.debug("before call renderingService method writePath：{}",writeFilePath);
            renderingService.rendering(_parserParams, templateFolder, templateFile, templateContent, writeFilePath);

            artId++;

        }
    }

    /**
     * 在短链情况下选择开启额外线程提高删除效率
     */
    class DeleteContentHtml implements Runnable {

        private List<String> contentPaths;

        public DeleteContentHtml(List<String> contentPaths) {
            this.contentPaths = contentPaths;
        }

        @Override
        public void run() {
            LOG.debug("内置线程类开始工作");
            for (String path : contentPaths) {
                // 校验路径是否合法
                if (path.contains("..") || path.contains("../") || path.contains("..\\")) {
                    LOG.error("非法路径："+path);
                    throw new BusinessException(BundleUtil.getString(Const.RESOURCES,"err.error",BundleUtil.getString(net.mingsoft.basic.constant.Const.RESOURCES,"file.path")));
                }
                FileUtil.del(path);
            }
        }
    }
}
