/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */
















package net.mingsoft.cms.action.web;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.constant.Const;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.cms.bean.CategoryBean;
import net.mingsoft.cms.bean.ContentBean;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.constant.e.CategoryTypeEnum;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.co.constant.e.ProgressStatusEnum;
import net.mingsoft.co.service.RenderingService;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.mdiy.bean.PageBean;
import net.mingsoft.mdiy.biz.IModelBiz;
import net.mingsoft.mdiy.entity.ModelEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.mdiy.util.DictUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 动态生成页面，需要后台配置自定义页数据
 * duo
 *
 * @author 铭软开源团队
 * @date 2018年12月17日
 * 历史修订：
 * 20210719 多模板搜索问题
 */
@Api(tags={"前端-动态页面-只在模版制作的时候使用"})
@Controller("dynamicPageAction")
@RequestMapping("/mcms")
public class MCmsAction extends net.mingsoft.cms.action.BaseAction {

    /**
     * 文章管理业务处理层
     */
    @Autowired
    private IContentBiz contentBiz;

    /**
     * 栏目业务层
     */
    @Autowired
    private ICategoryBiz categoryBiz;

    /**
     * 自定义模型
     */
    @Autowired
    private IModelBiz modelBiz;


    @Value("${ms.diy.html-dir:html}")
    private String htmlDir;

    @Autowired
    private RenderingService renderingService;


    /**
     * 动态列表页
     */
    @ApiIgnore
    @GetMapping("/index.do")
    @ResponseBody
    public String index(HttpServletRequest req, HttpServletResponse resp) {
        boolean flag = Boolean.parseBoolean(ConfigUtil.getString("静态化配置", "dynamicSwitch", "false"));
        if (!flag) {
            throw new BusinessException("动态静态化已被关闭!");
        }
        String templateName = BasicUtil.getString("style");
        if (StringUtils.isBlank(templateName)) {
            AppEntity app = BasicUtil.getApp();
            List<Map> list = JSONUtil.toList(app.getAppStyles(),Map.class);
            if (CollUtil.isNotEmpty(list)) {
                templateName = (String)list.get(0).get("template");
            }
        }
        Map<String,Object> map = BasicUtil.assemblyRequestMap();
        map.forEach((k, v) -> {
            map.put(k, v.toString().replaceAll("('|\"|\\\\)", "\\\\$1"));
        });
        String style = ParserUtil.getDictValue(templateName);
        ParserUtil.putBaseParams(map,templateName);
        map.put(ParserUtil.IS_DO, true);
        map.put("style", style);
        map.put("modelName", "mcms");

        //解析后的内容
        String content = "";
        String buildTemplatePath = ParserUtil.buildTemplatePath();
        String templateFolder = buildTemplatePath.concat(templateName);
        String templateContent = FileUtil.readString(FileUtil.file(templateFolder, ParserUtil.INDEX + ParserUtil.HTM_SUFFIX), CharsetUtil.CHARSET_UTF_8);
        templateContent = ParserUtil.replaceTag(templateContent);
        Future<String> r = renderingService.rendering(map, templateFolder, ParserUtil.INDEX + ParserUtil.HTM_SUFFIX, templateContent, null);


        try {
            content = r.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 动态列表页
     *
     * @param req
     * @param resp
     */
    @ApiIgnore
    @ResponseBody
    @GetMapping("/list.do")
    public String list(HttpServletRequest req, HttpServletResponse resp) {
        boolean flag = Boolean.parseBoolean(ConfigUtil.getString("静态化配置", "dynamicSwitch", "false"));
        if (!flag) {
            throw new BusinessException("动态静态化已被关闭!");
        }
        String templateName = BasicUtil.getString("style");
        AppEntity app = BasicUtil.getApp();
        List<Map> list = JSONUtil.toList(app.getAppStyles(),Map.class);
        String finalTemplateName = templateName;
        String style = "";
        List<Map<String, String>> styleList = new ArrayList<Map<String, String>>();
        list.stream().forEach(map -> {
            if(map.get("template").equals(finalTemplateName)) {
                styleList.add(map);
            }
        });

        if (CollUtil.isNotEmpty(styleList) && CollUtil.isNotEmpty(styleList.get(0))) {
            style = DictUtil.getDictValue("模板类型", styleList.get(0).get("name"));
        }
        if (StringUtils.isBlank(templateName)) {
            if (CollUtil.isNotEmpty(list)) {
                templateName = (String)list.get(0).get("template");
            }
        }
        int pageNo = BasicUtil.getInt("pageNo", 1);
        Map map = BasicUtil.assemblyRequestMap();


        map.forEach((k, v) -> {
            map.put(k, v.toString().replaceAll("('|\"|\\\\)", "\\\\$1"));
        });
        //获取栏目编号
        String typeId = BasicUtil.getString(ParserUtil.TYPE_ID, "");
        // 读取请求字段
        Map<String, Object> field = BasicUtil.assemblyRequestMap();
        // 设置发布到
        field.put("style", style);
        CategoryEntity category = categoryBiz.getById(typeId);
        ContentBean contentBean = new ContentBean();
        contentBean.setCategoryId(String.valueOf(typeId));
        contentBean.setCategoryType(CategoryTypeEnum.LIST.toString());
        contentBean.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
        //获取文章总数
        List<CategoryBean> columnArticles = contentBiz.queryIdsByCategoryIdForParser(contentBean);
        //判断栏目下是否有文章

        // 模板路径
        String templateFolder = ParserUtil.buildTemplatePath(templateName);
        int size = ParserUtil.getPageSize(templateFolder,category.getCategoryListUrls(templateName), 20);
        //设置分页类
        PageBean page = new PageBean();
        page.setPageNo(BasicUtil.getPageNo());
        page.setSize(size);
        int total = PageUtil.totalPage(columnArticles.size(), size);

        // 设置模板名
        ParserUtil.putBaseParams(map,templateName);
        // 设置动态标识
        map.put(ParserUtil.IS_DO, true);

        CategoryBean categoryBean = new CategoryBean();
        if(columnArticles.size()>0) {
            categoryBean = columnArticles.get(0);
        }

        BeanUtil.copyProperties(category,categoryBean);
        map.put(ParserUtil.COLUMN, categoryBean);
        //标签中使用field获取当前栏目
        map.put(ParserUtil.FIELD, categoryBean);
        map.putAll(field);
        //获取总数
        page.setTotal(total);
        page.setRcount(columnArticles.size());


        //设置分页的统一链接
        String url = map.get(ParserUtil.URL).toString();
        if (pageNo >= total && total != 0) {
            pageNo = total;
        }

        url = url + req.getServletPath() + "?";
        String pageNoStr = "pageNo=";
        //下一页
        String nextUrl = url + pageNoStr + ((pageNo + 1 > total) ? total : pageNo + 1);
        //首页
        String indexUrl = url + pageNoStr + 1;
        //尾页
        String lastUrl = url + pageNoStr + total;
        //上一页 当前页为1时，上一页就是1
        String preUrl = url + pageNoStr + ((pageNo == 1) ? 1 : pageNo - 1);

        page.setIndexUrl(indexUrl);
        page.setNextUrl(nextUrl);
        page.setPreUrl(preUrl);
        page.setLastUrl(lastUrl);

        //设置栏目编号
        map.put(ParserUtil.TYPE_ID, typeId);

        map.put(ParserUtil.URL, BasicUtil.getUrl());
        map.put(ParserUtil.PAGE, page);

        map.put("modelName", "mcms");
        //解析后的内容
        String content = "";
        try {
            String templateContent = FileUtil.readString(FileUtil.file(templateFolder, category.getCategoryListUrls(templateName)), CharsetUtil.CHARSET_UTF_8);
            templateContent = ParserUtil.replaceTag(templateContent);
            Future<String> r = renderingService.rendering(map, templateFolder, category.getCategoryListUrl(), templateContent, null);
            content = r.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return content;
    }

    /**
     * 动态详情页
     */
    @ApiIgnore
    @GetMapping("/view.do")
    @ResponseBody
    public String view(HttpServletRequest reqeust, HttpServletResponse response) {
        boolean flag = Boolean.parseBoolean(ConfigUtil.getString("静态化配置", "dynamicSwitch", "false"));
        if (!flag) {
            throw new BusinessException("动态静态化已被关闭!");
        }
        // 模板名称
        String templateName = BasicUtil.getString("style");
        AppEntity appEntity = BasicUtil.getApp();
        List<Map> list = JSONUtil.toList(appEntity.getAppStyles(),Map.class);
        // 用于筛选
        String finalTemplateName = templateName;
        // 字典值
        String style = "";
        List<Map<String, String>> styleList = new ArrayList<Map<String, String>>();
        list.stream().forEach(map -> {
            if(map.get("template").equals(finalTemplateName)) {
                styleList.add(map);
            }
        });

        if (StringUtils.isBlank(templateName) && CollUtil.isNotEmpty(list)) {
            templateName = (String)list.get(0).get("template");
        }

        //如果存在栏目id表示是单篇内容栏目
        String _typeId = BasicUtil.getString(ParserUtil.TYPE_ID);
        String id = BasicUtil.getString(ParserUtil.ID);
        ContentEntity article = null;
        if(StringUtils.isNotBlank(_typeId)) {
            article = contentBiz.getOne(new QueryWrapper<ContentEntity>().lambda().eq(ContentEntity::getCategoryId,_typeId));
        } else if(StringUtils.isNotBlank(id)) {
            //参数文章编号
            article = contentBiz.getById(BasicUtil.getString(ParserUtil.ID));
        }

        if (ObjectUtil.isNull(article)) {
            throw new BusinessException(this.getResString("err.empty", this.getResString("id")));
        }

        if (CollUtil.isNotEmpty(styleList) && CollUtil.isNotEmpty(styleList.get(0))) {
            style = DictUtil.getDictValue("模板类型", styleList.get(0).get("name"));
        }

        PageBean page = new PageBean();
        //用于详情上下页获取当前文章列表对应的分类，根据文章查询只能获取自身分类
        String typeId = BasicUtil.getString(ParserUtil.TYPE_ID, article.getCategoryId());
        //根据文章编号查询栏目详情模版
        CategoryEntity column = categoryBiz.getById(typeId);
        //解析后的内容
        String content = "";
        Map map = BasicUtil.assemblyRequestMap();
        map.forEach((k, v) -> {
            //sql注入过滤
            map.put(k, v==null?null:v.toString().replaceAll("('|\"|\\\\)", "\\\\$1"));
        });
        ParserUtil.putBaseParams(map,templateName);

        map.put("style", style);
        //设置栏目编号
        map.put(ParserUtil.TYPE_ID, typeId);
        //设置动态请求的模块路径
        map.put(ParserUtil.PAGE, page);
        map.put(ParserUtil.ID, article.getId());

        map.put("modelName", "mcms");

        ContentBean contentBean = new ContentBean();
        contentBean.setCategoryId(String.valueOf(typeId));
        //这里会根据栏目类型执行不同的语句
        contentBean.setCategoryType(column.getCategoryType());
        AppEntity app = BasicUtil.getWebsiteApp();
        if (app != null){
            contentBean.setAppId(String.valueOf(app.getAppId()));
        }
        contentBean.setOrderBy("date");
        contentBean.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
        List<CategoryBean> articleIdList = contentBiz.queryIdsByCategoryIdForParser(contentBean);
        Map<Object, Object> contentModelMap = new HashMap<>();
        ModelEntity contentModel = null;
        // 文章总数
        int size = articleIdList.size();
        for (int artId = 0; artId < size; ) {
            //如果不是当前文章则跳过
            if (!articleIdList.get(artId).getArticleId().equals(article.getId())) {
                artId++;
                continue;
            }
            // 文章的栏目模型编号
            String columnContentModelId = articleIdList.get(artId).getMdiyModelId();
            Map<String, Object> parserParams = new HashMap<>();
            parserParams.put(ParserUtil.COLUMN, articleIdList.get(artId));
            // 判断当前栏目是否有自定义模型
            if (columnContentModelId != null && StringUtils.isNotBlank(columnContentModelId)) {
                // 通过当前栏目的模型编号获取，自定义模型表名
                if (contentModelMap.containsKey(columnContentModelId)) {
                    parserParams.put(ParserUtil.TABLE_NAME, contentModel.getModelTableName());
                } else {
                    // 通过栏目模型编号获取自定义模型实体
                    contentModel = (ModelEntity) modelBiz.getById(columnContentModelId);
                    // 将自定义模型编号设置为key值
                    contentModelMap.put(columnContentModelId, contentModel.getModelTableName());
                    parserParams.put(ParserUtil.TABLE_NAME, contentModel.getModelTableName());
                }
            }
            map.putAll(parserParams);
            // 大于0则表示文章有上一页
            if (artId > 0) {
                page.setPreId(articleIdList.get(artId - 1).getArticleId());
            }
            // 当前文章数小于文章总数则表示有下一页
            if (artId < size - 1) {
                page.setNextId(articleIdList.get(artId + 1).getArticleId());
            }
            break;
        }

        String buildTemplatePath = ParserUtil.buildTemplatePath();
        String templateFolder = buildTemplatePath.concat(templateName);

        String templateContent = "";
        try {
            templateContent = FileUtil.readString(FileUtil.file(templateFolder, column.getCategoryUrls(templateName)), CharsetUtil.CHARSET_UTF_8);
        } catch (IORuntimeException e) {
            throw new BusinessException(column.getCategoryTitle() + "栏目绑定的模版不存在");
        }
        templateContent = ParserUtil.replaceTag(templateContent);
        map.put(ParserUtil.IS_DO, true);

        Future<String> r = renderingService.rendering(map, templateFolder, column.getCategoryUrl(), templateContent, null);

        //根据模板路径，参数生成
        try {
            content = r.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return content;
    }


    /**
     * 实现前端页面的文章搜索
     * @return 渲染后的搜索页面
     */
    @ApiOperation(value = "根据条件搜索文章接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tmpl", value = "模板名称", required = false, paramType = "query"),
            @ApiImplicitParam(name = "style", value = "风格", required = true, paramType = "query"),
            @ApiImplicitParam(name = "categoryIds", value = "栏目编号", required = false, paramType = "query"),
            @ApiImplicitParam(name = "content_title", value = "标题", required = true, paramType = "query"),
    })
    @RequestMapping(value = "search",method = {RequestMethod.GET, RequestMethod.POST},produces= MediaType.TEXT_HTML_VALUE+";charset=utf-8")
    @ResponseBody
    public String search(HttpServletRequest request, HttpServletResponse response) {
        String search = BasicUtil.getString("tmpl", "search.htm");
        //设置分页类
        PageBean page = new PageBean();
        String templateName = BasicUtil.getString("style");
        String contentStyle = "";
        if (StringUtils.isNotBlank(templateName)) {
            AppEntity app = BasicUtil.getApp();
            List<Map> list = JSONUtil.toList(app.getAppStyles(),Map.class);
            if (CollUtil.isNotEmpty(list)) {
                list = list.stream().filter(
                        map -> map.get("template").equals(templateName)
                ).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(list)) {
                    contentStyle = DictUtil.getDictValue("模板类型", (String)list.get(0).get("name"));
                }
            }
        }else {
            throw new BusinessException(getResString("err.empty", "style"));
        }
        // 模板路径
        String templateFolder = ParserUtil.buildTemplatePath(templateName);
        page.setSize(ParserUtil.getPageSize(templateFolder, search, 20));

        //参数集合，提供给解析使用
        Map<String, Object> params = new HashMap<>();

        // 读取请求字段
        Map<String, Object> field = BasicUtil.assemblyRequestMap();

        // 自定义字段集合
        Map<String, String> diyFieldName = new HashMap<String, String>();

        //记录自定义模型字段名
        List filedStr = new ArrayList<>();
        // 栏目对应字段的值
        List<DiyModelMap> fieldValueList = new ArrayList<DiyModelMap>();

        // 当前栏目
        CategoryEntity column = null;
        // 栏目对应模型
        ModelEntity contentModel = null;


        //获取栏目信息
        String typeId = null;
        String categoryIds = BasicUtil.getString("categoryIds");

        //List categoryIdList = CollectionUtil.newArrayList();

        //当传递了栏目编号，但不是栏目集合
        if (StringUtils.isNotBlank(categoryIds) && !categoryIds.contains(",")) {
            typeId = categoryIds;
        }
//        else {
//            //取出所有的子栏目
//            String[] ids = categoryIds.split(",");
//            List<CategoryEntity> categoryList = categoryBiz.list(Wrappers.<CategoryEntity>lambdaQuery().ne(CategoryEntity::getCategoryType, CategoryTypeEnum.LINK.toString()));
//
//            categoryIdList = CollectionUtil.newArrayList(ids);
//            for(CategoryEntity c:categoryList) {
//                if(StringUtils.isNotEmpty(c.getParentids())) {
//                    for(String id:ids) {
//                        if(c.getParentids().indexOf(id)>-1) {
//                            categoryIdList.add(c.getId());
//                            break;
//                        }
//                    }
//                }
//            }
//        }

        //重新组织 ID
        //categoryIds = StringUtils.join(categoryIdList, ",");


        //根据栏目确定自定义模型
        if (typeId != null) {
            column = (CategoryEntity) categoryBiz.getById(typeId);
            // 获取表单类型的id
            if (column != null && ObjectUtil.isNotNull(column.getMdiyModelId())) {
                contentModel = (ModelEntity) modelBiz.getById(column.getMdiyModelId());
                if (contentModel != null) {
                    // 保存自定义模型的数据
                    Map<String, String> fieldMap = contentModel.getFieldMap();
                    for (String s : fieldMap.keySet()) {
                        filedStr.add(fieldMap.get(s));
                    }
                    // 设置自定义模型表名，方便解析的时候关联表查询
                    params.put(ParserUtil.TABLE_NAME, contentModel.getModelTableName());
                }
            }

            // 设置栏目，方便解析的时候关联表查询
            params.put(ParserUtil.COLUMN, column);
        }

        // 处理读取请求字段
        if (field != null) {
            for (Map.Entry<String, Object> entry : field.entrySet()) {
                if (entry != null) {
                    //空值不处理
                    if (ObjectUtil.isNull(entry.getValue())) {
                        continue;
                    }

                    // 对值进行安全处理
                    // 处理由get方法请求中文乱码问题
                    String value = entry.getValue().toString().replaceAll("('|\"|\\\\)", "\\\\$1");
                    //Xss过滤
                    value = clearXss(value);
                    // 如果是get方法需要将请求地址参数转码
                    if (request.getMethod().equals(RequestMethod.GET)) {
                        try {
                            value = new String(value.getBytes("ISO-8859-1"), Const.UTF8);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    // 保存至自定义字段集合
                    if (StringUtils.isNotBlank(value)) {
                        diyFieldName.put(entry.getKey(), value);
                        //判断请求中的是否是自定义模型中的字段
                        if (filedStr.contains(entry.getKey())) {
                            //设置自定义模型字段和值
                            DiyModelMap diyMap = new DiyModelMap();
                            diyMap.setKey(entry.getKey());
                            diyMap.setValue(value);
                            fieldValueList.add(diyMap);
                        }
                    }

                }
            }
        }

        //添加自定义模型的字段和值
        if (fieldValueList.size() > 0) {
            params.put("diyModel", fieldValueList);
        }


        Map<String, Object> searchMap = field;
        String contentTag = BasicUtil.getString("content_tag");
        if (StringUtils.isNotBlank(contentTag)){
            searchMap.put("content_tag",contentTag);
        }
        searchMap.put("categoryIds", categoryIds);
        StringBuilder urlParams = new StringBuilder();

        searchMap.forEach((k, v) -> {
            //sql注入过滤
            if (v != null) {
                searchMap.put(k, v.toString().replaceAll("('|\"|\\\\)", "\\\\$1"));
                searchMap.put(k, clearXss(searchMap.get(k).toString()));
                if (!"size".equals(k) && !"pageNo".equals(k)) {
                    urlParams.append(k).append("=").append(searchMap.get(k)).append("&");
                }
            }
        });
        //内外网 co没有xml判断,所以不影响
        searchMap.put("content_style", contentStyle);//字典值
        searchMap.put("style", contentStyle);//字典值

        int appId =  BasicUtil.getApp().getAppId();
        boolean siteEnable = Boolean.parseBoolean(ConfigUtil.getString("站群配置", "siteEnable", "false"));
        if (!siteEnable){//如果没有开启站群,不需要拼接站群id
            appId = 0 ;
        }

        //查询数量
        int count = contentBiz.getSearchCount(contentModel, fieldValueList, searchMap, appId, categoryIds);
        page.setRcount(count);
        params.put("search", searchMap);


        ParserUtil.putBaseParams(params,templateName);

        params.put(ParserUtil.PAGE, page);

        searchMap.put("pageNo", 0);
//        ParserUtil.read(search, map, page);
        int total = PageUtil.totalPage(count, page.getSize());

        int pageNo = BasicUtil.getInt("pageNo", 1);
        if (pageNo >= total && total != 0) {
            pageNo = total;
        }
        //获取总数
        page.setTotal(total);

        page.setPageNo(pageNo);

        //设置分页的统一链接
        String url = request.getServletPath() + "?" + urlParams;
        String pageNoStr = "size=" + page.getSize() + "&pageNo=";
        //下一页
        String nextUrl = url + pageNoStr + ((pageNo + 1 > total) ? total : pageNo + 1);
        //首页
        String indexUrl = url + pageNoStr + 1;
        //尾页
        String lastUrl = url + pageNoStr + total;
        //上一页 当前页为1时，上一页就是1
        String preUrl = url + pageNoStr + ((pageNo == 1) ? 1 : pageNo - 1);

        page.setIndexUrl(indexUrl);
        page.setNextUrl(nextUrl);
        page.setPreUrl(preUrl);
        page.setLastUrl(lastUrl);

        if (BasicUtil.getWebsiteApp() != null) {
            params.put(ParserUtil.APP_DIR, BasicUtil.getWebsiteApp().getAppDir());
        }
        params.put(ParserUtil.PAGE, page);
        // 获取isDo用于判断页面是动态还是静态页面
        boolean isDo = Boolean.parseBoolean(BasicUtil.getString("isDo", "false"));
        params.put(ParserUtil.IS_DO, isDo);
        params.put("modelName", "mcms");

        //解析后的内容
        String content = "";
        String templateContent = FileUtil.readString(FileUtil.file(templateFolder, search), CharsetUtil.CHARSET_UTF_8);
        templateContent = ParserUtil.replaceTag(templateContent);
        Future<String> r = renderingService.rendering(params, templateFolder, search, templateContent, null);
        try {
            content = r.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return content;
    }



    // 清除路径中的转义字符
    private String clearXss(String value) {

        if (value == null || "".equals(value)) {
            return value;
        }

        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replace("\\)", "&#41;");
        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']",
                "\"\"");
        value = value.replace("script", "");

        return value;
    }

    /**
     * 存储自定义模型字段和接口参数
     *
     * @author 铭软开源团队
     * @date 2019年3月5日
     */
    class DiyModelMap {
        private String key;
        private Object value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return JSONUtil.toJsonStr(this);
        }
    }

}
