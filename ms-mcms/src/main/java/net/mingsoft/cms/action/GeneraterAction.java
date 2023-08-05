/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.cms.action;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.constant.e.CategoryDisplayEnum;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.co.bean.TemplateBean;
import net.mingsoft.co.cache.GeneraterProgressCache;
import net.mingsoft.co.service.CmsParserService;
import net.mingsoft.co.service.FileTagCacheService;
import net.mingsoft.co.service.ThreadPoolTaskExecutorService;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.mdiy.util.DictUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: GeneraterAction
 * @Description:TODO 生成器
 * @author: 铭软开发团队
 * @date: 2018年1月31日 下午2:52:07
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 * 历史修订：
 * 2021-03-20 增多模版多处理<br/>
 * 2021-04-24 增多线程生成文章、生成栏目、并前端对应显示进度<br/>
 */
@Api(tags={"后端-静态化模块接口"})
@Controller("cmsGenerater")
@RequestMapping("/${ms.manager.path}/cms/generate")
public class GeneraterAction extends BaseAction {

    /*
     * log4j日志记录
     */
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /**
     * 文章管理业务层
     */
    @Autowired
    private IContentBiz contentBiz;

    /**
     * 栏目管理业务层
     */
    @Autowired
    private ICategoryBiz categoryBiz;


    @Autowired
    private CmsParserService cmsParserService;


    @Autowired
    private FileTagCacheService fileTagCacheService;


    @Autowired
    public GeneraterProgressCache generaterProgressCache;

    /**
     * /**
     * 更新主页
     *
     * @return
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("cms:generate:view")
    public String index(HttpServletRequest request, ModelMap model) {
        return "/cms/generate/index";
    }

    /**
     * 缓存所有的文章
     *
     * @param request
     * @return
     */
    @GetMapping("/cache")
    @ApiIgnore
    @ResponseBody
    public ResultData cache(HttpServletRequest request) {
        fileTagCacheService.cacheAllContent(BasicUtil.getUrl(request.getServerName()));
        return ResultData.build().success();
    }

    /**
     * 生成主页
     *
     * @param request
     * @param response
     */
    @PostMapping("/generateIndex")
    @RequiresPermissions("cms:generate:index")
    @LogAnn(title = "生成主页", businessType = BusinessTypeEnum.UPDATE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "style", value = "字典管理-模板类型-字典值,用于表示静态化对应的皮肤", required =true,paramType="query"),
            @ApiImplicitParam(name = "file", value = "生成首页的文件名", required =true,paramType="query"),
            @ApiImplicitParam(name = "path", value = "生成首页的目录地址", required =true,paramType="query"),
    })
    @ResponseBody
    public ResultData generateIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 模版文件名称
        String file = request.getParameter("file");
        // 生成后的文件名称
        String path = request.getParameter("path");
        String style = BasicUtil.getString("style");
        String templateName = getTemplateName(style);

        LOG.debug("mcms co style {}", style);

        Map<String, Object> parserParams = new HashMap<String, Object>();
        parserParams.put("contentStyle",style);
        parserParams.put("style",style);
        ParserUtil.putBaseParams(parserParams,templateName);
        CategoryEntity column = new CategoryEntity();
        //内容管理栏目编码
        parserParams.put(ParserUtil.COLUMN, column);

        // 获取文件所在路径 首先判断用户输入的模版文件是否存在
        if (!FileUtil.exist(ParserUtil.buildTemplatePath(templateName, file))) {
            return ResultData.build().error(getResString("templet.file"));
        } else {
            cmsParserService.generate(templateName, file, path, parserParams);
            return ResultData.build().success();
        }

    }


    /**
     * 生成列表的静态页面
     *
     * @param request
     * @param response
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "style", value = "字典管理-模板类型-字典值,用于表示静态化对应的皮肤", required =true,paramType="query"),
            @ApiImplicitParam(name = "categoryId", value = "所属栏目,0表示静态化所有栏目,默认为0,如果所属栏目有子栏目会静态化栏目下的所有子栏目", required =false,paramType="query"),
            @ApiImplicitParam(name = "categoryIds", value = "指定栏目ID静态化栏目,多个栏目ID用逗号隔开,传值后categoryId参数无效", required =false,paramType="query"),
    })
    @RequestMapping(value = "/generateColumn", method = {RequestMethod.GET, RequestMethod.POST})
    @LogAnn(title = "生成栏目", businessType = BusinessTypeEnum.UPDATE)
    @RequiresPermissions("cms:generate:column")
    @ResponseBody
    public ResultData generateColumn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ThreadPoolTaskExecutorService threadPoolTaskExecutor = (ThreadPoolTaskExecutorService) SpringUtil.getBean("cmsParserServiceCategoryThreadPool");


        if (threadPoolTaskExecutor.getActiveCount() > 0) {
            return ResultData.build().error("后台正在静态化,请稍等片刻后再静态化");
        }
        // 获取站点id
        String categoryId = BasicUtil.getString("categoryId", "0");
        String style = BasicUtil.getString("style");
        String categoryIds = BasicUtil.getString("categoryIds");
        String templateName = getTemplateName(style);
        //栏目列表
        List<CategoryEntity> columns;

        if (StringUtils.isNotBlank(categoryIds)) {
            // 查询指定栏目ID的栏目
            columns = categoryBiz.listByIds(Arrays.asList(categoryIds.split(",")));
        } else if ("0".equals(categoryId)) {
            // 0更新所有栏目
            columns = categoryBiz.queryChildren(new CategoryEntity());
        } else { //选择栏目更新
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setId(categoryId);
            columns = categoryBiz.queryChildren(categoryEntity);
        }
        // 需要过滤处理静态化进度,防止卡死
        columns = columns.stream().filter(categoryEntity -> CategoryDisplayEnum.ENABLE.toString().equalsIgnoreCase(categoryEntity.getCategoryDisplay())).collect(Collectors.toList());
        String progressKey = "category".concat(style);
        generaterProgressCache.saveOrUpdate(progressKey, columns.size());

        Map<String, Object> parserParams = new HashMap<>();
        // 将进度缓存key压入Map方便异常处理
        parserParams.put("progressKey", progressKey);
        parserParams.put("contentStyle",style);
        parserParams.put("style",style);
        ParserUtil.putBaseParams(parserParams,templateName);

        LOG.debug("开始生成栏目，栏目数量:{}", columns.size());
        // 获取栏目列表模版
        for (CategoryEntity column : columns) {
            try {
                cmsParserService.category(column, templateName, parserParams);
            }catch (Exception e) {
                // 防止抛出异常后进度条卡死
                generaterProgressCache.saveOrUpdate(progressKey, generaterProgressCache.getInt(progressKey) - 1);
                generaterProgressCache.flush();
                LOG.error(e.getMessage(), e);
                return ResultData.build().error("静态化失败,请查看后台日志");
            }
        }
        return ResultData.build().success();
    }

    /**
     * 根据栏目id更新所有的文章
     *
     * @param request
     * @param response
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "style", value = "字典管理-模板类型-字典值,用于表示静态化对应的皮肤", required =true,paramType="query"),
            @ApiImplicitParam(name = "categoryId", value = "所属栏目,0表示静态化所有栏目,默认为0,如果所属栏目有子栏目会静态化栏目下的所有子栏目", required =false,paramType="query"),
            @ApiImplicitParam(name = "dateTime", value = "指定时间之后,对应文章update_date", required =false,paramType="query"),
    })
    @PostMapping("/generateContent")
    @RequiresPermissions("cms:generate:article")
    @LogAnn(title = "生成文章", businessType = BusinessTypeEnum.UPDATE)
    @ResponseBody
    public ResultData generateContent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ThreadPoolTaskExecutorService threadPoolTaskExecutor = (ThreadPoolTaskExecutorService) SpringUtil.getBean("cmsParserServiceContentThreadPool");
        if (threadPoolTaskExecutor.getActiveCount() > 0) {
            return ResultData.build().error("后台正在静态化,请稍等片刻后再静态化");
        }
        LOG.debug("tread name {}", Thread.currentThread().getName());
        String dateTime = BasicUtil.getString("dateTime");
        String categoryId = BasicUtil.getString("categoryId", "0");
        String style = BasicUtil.getString("style");
        String templateName = getTemplateName(style);
        // 网站风格物理路径
        List<CategoryEntity> categoryList = new ArrayList<CategoryEntity>();

        Map<String, Object> parserParams = new HashMap<String, Object>();
        parserParams.put("contentStyle",style);
        parserParams.put("style",style);
        ParserUtil.putBaseParams(parserParams,templateName);

        // 生成所有栏目的文章
        if ("0".equals(categoryId)) {
//            CategoryEntity categoryEntity = new CategoryEntity();
//            categoryEntity.setLeaf(true);
//            categoryEntity.setCategoryType("1");
//            LambdaQueryWrapper<CategoryEntity> wrapper = new LambdaQueryWrapper<>(categoryEntity);
//            categoryList = categoryBiz.list(wrapper);

            LambdaQueryWrapper<ContentEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(ContentEntity::getUpdateDate,DateUtil.parse(dateTime));
            //只静态化最新的的栏目文章
            List<ContentEntity> contentEntities = contentBiz.list(wrapper);//contentBiz.queryByUpdateDate(dateTime, DateUtil.formatDate(new Date()));
            if(contentEntities.size()>0) {
                List<CategoryEntity> finalCategoryList = categoryList;
                Set set = new HashSet();
                contentEntities.stream().forEach(content->{
                    if(!set.contains(content.getCategoryId())) {
                        CategoryEntity category = categoryBiz.getById(content.getCategoryId());
                        if (category!=null){
                            finalCategoryList.add(category);
                        }
                        set.add(content.getCategoryId());
                    }
                });
            }
        } else { //选择栏目更新
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setId(categoryId);
            categoryList = categoryBiz.queryChildren(categoryEntity);
        }
        // 需要过滤处理静态化进度,防止卡死
        categoryList = categoryList.stream().filter(categoryEntity -> CategoryDisplayEnum.ENABLE.toString().equalsIgnoreCase(categoryEntity.getCategoryDisplay())).collect(Collectors.toList());
        String progressKey = "content".concat(style);
        // 将进度缓存key压入Map方便异常处理
        parserParams.put("progressKey", progressKey);
        generaterProgressCache.saveOrUpdate(progressKey, categoryList.size());
        for (CategoryEntity category : categoryList) {
            try {
                cmsParserService.content(category, templateName, dateTime, parserParams);
            }catch (Exception e) {
                LOG.error("progressKey:"+(generaterProgressCache.getInt(progressKey) - 1));
                // 防止抛出异常后进度条卡死
                generaterProgressCache.saveOrUpdate(progressKey, generaterProgressCache.getInt(progressKey) - 1);
                generaterProgressCache.flush();
                LOG.error(e.getMessage(), e);
                return ResultData.build().error("静态化失败,请查看后台日志");
            }
        }
        return ResultData.build().success();
    }

    /**
     * 根据文章id更新所有的文章
     *
     * @param request
     * @param response
     */
    @PostMapping("/generateContentById")
    @RequiresPermissions("cms:generate:article")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentId", value = "文章ID", required =true,paramType="query"),
    })
    @LogAnn(title = "生成文章", businessType = BusinessTypeEnum.UPDATE)
    @ResponseBody
    public ResultData generateContentById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.debug("tread name {}", Thread.currentThread().getName());
        String contentId = BasicUtil.getString("contentId");
        if (StringUtils.isBlank(contentId)) {
            return ResultData.build().error("文章ID不能为空");
        }
        // 网站风格物理路径
        ContentEntity content = contentBiz.getById(contentId);
        CategoryEntity category = categoryBiz.getById(content.getCategoryId());
        Map<String, Object> parserParams = new HashMap<>();
        List<TemplateBean> templateBeanList = JSONUtil.toList(category.getCategoryUrls(), TemplateBean.class);
        List<String> contentIds = new ArrayList<>();
        contentIds.add(contentId);
        for (TemplateBean templateBean : templateBeanList) {
            try {
                ParserUtil.putBaseParams(parserParams, templateBean.getTemplate());
                cmsParserService.contentById(category, contentIds, templateBean.getTemplate(), parserParams);
            }catch (Exception e) {
                LOG.error(e.getMessage(), e);
                return ResultData.build().error("静态化失败,请查看后台日志");
            }
        }
        return ResultData.build().success();
    }


    /**
     * 用户预览主页
     *
     * @param request
     * @return
     */
    @ApiIgnore
    @GetMapping("/viewIndex")
    public String viewIndex(HttpServletRequest request, HttpServletResponse response) {
        AppEntity app = BasicUtil.getApp();
        // 生成后的文件名称
        String path = request.getParameter("path");
        String style = BasicUtil.getString("style");
        String appHostUrl = app.getAppHostUrl();
        if (StringUtils.isNotBlank(appHostUrl) && !appHostUrl.endsWith("/")){
            appHostUrl += "/";
        }

        HashMap<String, Object> map = new HashMap<>();
        ParserUtil.putBaseParams(map,style);

        String htmlPath = ParserUtil.buildViewHtmlPath(map.get(ParserUtil.APP_DIR).toString(),map.get(ParserUtil.APP_TEMPLATE).toString(), path);

        String index = appHostUrl + htmlPath;

        // 组织主页预览地址
        return "redirect:" + index;
    }

    /**
     * 生成进度
     *
     * @param request
     * @return
     */
    @ApiIgnore
    @GetMapping("/progress")
    @ResponseBody
    public ResultData progress(HttpServletRequest request, HttpServletResponse response) {
        String key = BasicUtil.getString("key", "");
        int count = generaterProgressCache.getInt(key);
        return ResultData.build().success(count);
    }


    /**
     * 根据模板字典值查询模板名
     * @param style 模板字典值
     * @return
     */
    private String getTemplateName(String style){
        if (StringUtils.isBlank(style)){
            return null;
        }
        String dictLabel = DictUtil.getDictLabel("模板类型", style);
        List<TemplateBean> templateList = JSONUtil.toList(BasicUtil.getApp().getAppStyles(), TemplateBean.class);
        List<TemplateBean> result = templateList.stream().filter(templateBean -> templateBean.getName().equals(dictLabel)).collect(Collectors.toList());
        if(result.size()>0){
            return result.get(0).getTemplate();
        }
        return null;
    }

    /**
     * 重置线程池
     * @return
     */
    @ApiIgnore
    @PostMapping("/resetGenerate")
    @ResponseBody
    public ResultData resetGenerate(){
        try {
            //静态化文章所用线程池
            ThreadPoolTaskExecutorService contentThreadPoolTaskExecutor = (ThreadPoolTaskExecutorService) SpringUtil.getBean("cmsParserServiceContentThreadPool");
            // 若后台有活动线程才重置，避免无意义操作消耗资源
            if (contentThreadPoolTaskExecutor.getActiveCount() > 0){
                contentThreadPoolTaskExecutor.restart();
            }
            //静态化栏目所用线程池
            ThreadPoolTaskExecutorService categoryThreadPoolTaskExecutor = (ThreadPoolTaskExecutorService) SpringUtil.getBean("cmsParserServiceCategoryThreadPool");
            if (categoryThreadPoolTaskExecutor.getActiveCount() > 0){
                categoryThreadPoolTaskExecutor.restart();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultData.build().error("重置失败,请重试");
        }

        return ResultData.build().success("重置成功，请在五分钟后再进行静态化操作",null);
    }

}
