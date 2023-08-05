/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.co.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.proxy.jdbc.NClobProxyImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.cms.action.BaseAction;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.constant.e.CategoryTypeEnum;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.co.bean.ContentBean;
import net.mingsoft.co.biz.IContentBiz;
import net.mingsoft.co.cache.ContentCache;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.mdiy.biz.IModelBiz;
import net.mingsoft.mdiy.biz.ITagBiz;
import net.mingsoft.mdiy.entity.ModelEntity;
import net.mingsoft.mdiy.entity.TagEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 缓存控制层
 * 创建日期：2021-11-24 10:48:32<br/>
 * 历史修订：增加缓存管理界面<br/>
 * 历史修订：增加清除,查询,缓存接口<br/>
 */
@Api(tags = {"后端-企业模块接口"})
@Controller("CacheAction")
@RequestMapping("/${ms.manager.path}/co/cache")
public class CacheAction extends BaseAction {

    @Autowired
    public ContentCache contentCache;
    @Autowired
    private ICategoryBiz categoryBiz;

    @Autowired
    private IContentBiz contentBiz;


    @Autowired
    private ITagBiz tagBiz;

    @Autowired
    private IModelBiz modelBiz;

    @Value("${spring.cache.type}")
    private String cacheType;

    @Value("${spring.redis.host:''}")
    private String host;

    @Value("${spring.redis.port:''}")
    private String port;

    /**
     * 返回缓存管理页面
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("co:cache:view")
    public String auth(HttpServletResponse response, HttpServletRequest request) {
        return "/co/cache/index";
    }


    @ApiOperation(value = "根据ID查询缓存接口")
    @ApiImplicitParam(name = "id", value = "文章ID", required = true, paramType = "query")
    @RequiresPermissions("co:cache:view")
    @GetMapping("/getCacheById")
    @ResponseBody
    public ResultData getCacheById(HttpServletResponse response, HttpServletRequest request) {
        String id = BasicUtil.getString("id");
        LOG.debug("get cache by id {}", id);
        Object obj = contentCache.get(id);
        contentCache.flush();
        return ResultData.build().data(obj);
    }

    /**
     * 根据当前站点id删除所有缓存
     */
    @ApiOperation(value = "清除缓存接口")
    @GetMapping("/clear")
    @LogAnn(title = "清空缓存", businessType = BusinessTypeEnum.DELETE)
    @RequiresPermissions("co:cache:clear")
    @ResponseBody
    public ResultData clear(HttpServletResponse response, HttpServletRequest request) {
        LOG.debug("clear all cache by appId!");
        // 文章集合
        List list = new ArrayList<>();
        if (ConfigUtil.getBoolean("站群配置", "siteEnable")){
            list = (List) contentBiz.excuteSql(StrUtil.format("select id from cms_content where app_id = '{}'",BasicUtil.getApp().getAppId()));

            // 遍历集合根据文章id删除所有文章缓存
            if (CollectionUtil.isNotEmpty(list)){
                for (Object map : list) {
                    contentCache.delete(((Map)map).get("id").toString());
                }
            }
        } else {
            contentCache.deleteAll();
        }

        LOG.debug("delete {} contentCache",list.size());
        contentCache.flush();
        return ResultData.build().success();
    }


    @ApiOperation(value = "缓存单个栏目或所有文章接口")
    @LogAnn(title = "缓存栏目文章", businessType = BusinessTypeEnum.OTHER)
    @ApiImplicitParam(name = "id", value = "栏目ID(不传默认缓存全部)", required = false, paramType = "query")
    @RequiresPermissions("co:cache:cacheAll")
    @GetMapping("/cacheCategory")
    @ResponseBody
    public ResultData cacheCategory(HttpServletResponse response, HttpServletRequest request) {
        String id = BasicUtil.getString("id");
        LOG.debug("cache Category id {}", id);
        Map<String, Object> parserParams = new HashMap<>();
        ParserUtil.putBaseParams(parserParams,BasicUtil.getString("style"));

        QueryWrapper<TagEntity> tagWrapper = new QueryWrapper<>();
        tagWrapper.eq("tag_name", "field");
        TagEntity tagEntity = tagBiz.getOne(tagWrapper);
        String sqlFtl = tagEntity.getTagSql();

        List<CategoryEntity> categoryList = new ArrayList<>();
        //获取栏目
        if (StringUtils.isNotBlank(id)) {
            CategoryEntity categoryEntity = categoryBiz.getById(id);
            if (categoryEntity == null) {
                return ResultData.build().error("栏目不存在!");
            }
            categoryList.add(categoryEntity);
        } else {
            LambdaQueryWrapper<CategoryEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.ne(CategoryEntity::getCategoryType, CategoryTypeEnum.LINK.toString());
            categoryList = categoryBiz.list(wrapper);
        }
        // 缓存的文章总数
        int total = 0;
        for (CategoryEntity category : categoryList) {
            // 设置栏目，方便解析的时候关联表查询
            parserParams.put(ParserUtil.COLUMN, category);
            //根据栏目自定义模型id
            // 获取表单类型的id
            if (StringUtils.isNotBlank(category.getMdiyModelId())) {
                ModelEntity contentModel = modelBiz.getById(category.getMdiyModelId());
                if (contentModel != null) {
                    // 设置自定义模型表名，方便解析的时候关联表查询
                    parserParams.put(ParserUtil.TABLE_NAME, contentModel.getModelTableName());
                }
            }
            try {
                String sql = ParserUtil.rendering(parserParams, sqlFtl);
                List list = (List) tagBiz.excuteSql(sql);
                total += list.size();
                for (Object item : list) {
                    Map o = (Map) item;
                    Map map = contentCache.get(String.valueOf(o.get("id")));
                    //如果缓存存在就不处理
                    if (map == null) {
                        //如果类型是 nclob必须要转换一次
                        if (o.get("content") instanceof Clob || o.get("content") instanceof NClobProxyImpl) {
                            o.put("content", StringUtil.clobStr((Clob) o.get("content")));
                        }
                        contentCache.saveOrUpdate(String.valueOf(o.get("id")), o);
                    }

                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (TemplateException e) {
                e.printStackTrace();
            }
            LOG.debug("正在缓存{}", category.getCategoryTitle());
            contentCache.flush();
        }
        if ("redis".equalsIgnoreCase(cacheType)) {
            LOG.debug("缓存成功,类型{},地址{},缓存文章总数{}", cacheType, (host + ":" + port), total);

        } else {
            LOG.debug("缓存成功,类型{},地址{},缓存文章总数{}", cacheType, System.getProperty("java.io.tmpdir"), total);
        }
        return ResultData.build().success();

    }

    @ApiOperation(value = "缓存单篇文章")
    @ApiImplicitParam(name = "id", value = "文章ID", required = true, paramType = "query")
    @LogAnn(title = "缓存单篇文章", businessType = BusinessTypeEnum.OTHER)
    @GetMapping("/cacheContent")
    @ResponseBody
    public ResultData cacheContent(HttpServletResponse response, HttpServletRequest request) {
        String id = BasicUtil.getString("id");
        LOG.debug("cache Content id {}", id);
        Map<String, Object> parserParams = new HashMap<>();
        ParserUtil.putBaseParams(parserParams,BasicUtil.getString("style"));

        QueryWrapper<TagEntity> tagWrapper = new QueryWrapper<>();
        tagWrapper.eq("tag_name", "field");
        TagEntity tagEntity = tagBiz.getOne(tagWrapper);
        String sqlFtl = tagEntity.getTagSql();
        ContentEntity content = contentBiz.getById(id);
        if (content == null) {
            return ResultData.build().error("文章" + id + "不存在");
        }
        CategoryEntity category = categoryBiz.getById(content.getCategoryId());
        //获取栏目
        // 设置ID，方便解析的时候关联表查询
        parserParams.put(ParserUtil.ID, id);
        //根据栏目自定义模型id
        // 获取表单类型的id
        if (StringUtils.isNotBlank(category.getMdiyModelId())) {
            ModelEntity contentModel = modelBiz.getById(category.getMdiyModelId());
            if (contentModel != null) {
                // 设置自定义模型表名，方便解析的时候关联表查询
                parserParams.put(ParserUtil.TABLE_NAME, contentModel.getModelTableName());
            }
        }
        try {
            String sql = ParserUtil.rendering(parserParams, sqlFtl);
            List list = (List) tagBiz.excuteSql(sql);
            for (Object item : list) {

                Map o = (Map) item;
                Map map = contentCache.get(String.valueOf(o.get("id")));
                //如果缓存存在就不处理
                if (map == null) {
                    //如果类型是 nclob必须要转换一次
                    if (o.get("content") instanceof Clob || o.get("content") instanceof NClobProxyImpl) {
                        o.put("content", StringUtil.clobStr((Clob) o.get("content")));
                    }
                    contentCache.saveOrUpdate(String.valueOf(o.get("id")), o);
                } else {
                    return ResultData.build().error("文章已存在缓存中！");
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        LOG.debug("正在缓存{}", content.getContentTitle());
        contentCache.flush();

        if ("redis".equalsIgnoreCase(cacheType)) {
            LOG.debug("缓存成功,类型{},地址{}", cacheType, (host + ":" + port));

        } else {
            LOG.debug("缓存成功,类型{},地址{}", cacheType, System.getProperty("java.io.tmpdir"));
        }
        return ResultData.build().success();

    }


    @ApiOperation(value = "获取缓存基本信息接口")
    @GetMapping("/info")
    @RequiresPermissions("co:cache:view")
    @ResponseBody
    public ResultData info(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> cacheInfoMap = new HashMap<>();
        AppEntity app = BasicUtil.getWebsiteApp();
        if (app != null) {
            cacheInfoMap.put("缓存总数", contentCache.listByAppId(app.getAppId()).size());
        } else {
            cacheInfoMap.put("缓存总数", contentCache.count());
        }
        if ("redis".equalsIgnoreCase(cacheType)){
            cacheInfoMap.put("缓存路径",host+":"+port);
        }else {
            cacheInfoMap.put("缓存路径", System.getProperty("java.io.tmpdir"));
        }
        cacheInfoMap.put("文章总数", contentBiz.count(new QueryWrapper<>()));
        cacheInfoMap.put("缓存类型", cacheType);
        return ResultData.build().success(cacheInfoMap);
    }


}
