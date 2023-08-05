/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.service;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.druid.proxy.jdbc.NClobProxyImpl;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import freemarker.template.TemplateException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.cms.constant.e.CategoryTypeEnum;
import net.mingsoft.co.cache.ContentCache;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.mdiy.biz.IModelBiz;
import net.mingsoft.mdiy.biz.ITagBiz;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.mdiy.entity.ModelEntity;
import net.mingsoft.mdiy.entity.TagEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.sql.Clob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class FileTagCacheService extends BaseTagClassService {



    @Autowired
    private ITagBiz tagBiz;

    @Autowired
    private IModelBiz modelBiz;


    @Autowired
    public ContentCache contentCache;

    @Autowired
    private ICategoryBiz categoryBiz;

    @Autowired
    private IContentBiz contentBiz;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * 缓存所有的文章内容，方便生成
     */
    @Async
    public void cacheAllContent(String url) {
        long count = contentBiz.count();
        int cacheCount = contentCache.count();

        LOG.debug("总记录{},缓存数{}",count,cacheCount);
        //如果已经缓存就不需要进行操作
        if (cacheCount == count) {
            return;
        }
        Map<String, Object> parserParams = new HashMap<String, Object>();
        parserParams.put(ParserUtil.IS_DO, false);
        if (BasicUtil.getWebsiteApp() != null) {
            parserParams.put(ParserUtil.APP_DIR, BasicUtil.getWebsiteApp().getAppDir());
            parserParams.put(ParserUtil.URL, BasicUtil.getWebsiteApp().getAppHostUrl());
            parserParams.put(ParserUtil.APP_ID, BasicUtil.getWebsiteApp().getAppId());

        } else {
            parserParams.put(ParserUtil.URL, url);
            parserParams.put(ParserUtil.APP_DIR, BasicUtil.getApp().getAppDir());
        }
        // 短链开关
        parserParams.put(ParserUtil.SHORT_SWITCH,ConfigUtil.getBoolean("静态化配置", "shortSwitch", false));
        // 查询field标签
        QueryWrapper<TagEntity> tagWrapper = new QueryWrapper<>();
        tagWrapper.eq("tag_name", "field");
        TagEntity tagEntity = tagBiz.getOne(tagWrapper);
        String sqlFtl = tagEntity.getTagSql();

        //便利所有栏目
        List<CategoryEntity> categoryList = categoryBiz.list();

        categoryList.forEach(category -> {
            //只获取子节点
            if (category!=null && !category.getLeaf()) {
                return;
            }
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
                List list = jdbcTemplate.queryForList(sql);
                for(Object item:list) {

                    Map o = (Map) item;
                    Map map = contentCache.get(String.valueOf(o.get("id")));
                    //如果缓存存在就不处理
                    if(map.size()==0) {
                        //如果类型是 clob必须要转换一次
                        if (o.get("content") instanceof Clob || o.get("content") instanceof NClobProxyImpl) {
                            o.put("content", StringUtil.clobStr((Clob)o.get("content")));
                        }
                        contentCache.saveOrUpdate(String.valueOf(o.get("id")),o);
                    }

                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (TemplateException e) {
                e.printStackTrace();
            }
            LOG.debug("正在缓存{}",category.getCategoryTitle());
            contentCache.flush();
        });
        LOG.debug("缓存成功{}",System.getProperty("java.io.tmpdir"));
    }

    /**
     * 缓存内容
     * @param contentEntity 文章实体
     */
    public void cacheContent(ContentEntity contentEntity) {
        if (contentEntity == null) {
            LOG.debug("文章内容为空,请检查文章是否存在");
            return;
        }
        Map<String, Object> parserParams = new HashMap<>();
        parserParams.put(ParserUtil.IS_DO, false);
        if (BasicUtil.getWebsiteApp() != null) {
            parserParams.put(ParserUtil.APP_DIR, BasicUtil.getWebsiteApp().getAppDir());
            parserParams.put(ParserUtil.URL, BasicUtil.getWebsiteApp().getAppHostUrl());
            if (StringUtils.isNotBlank(contentEntity.getAppId())) {
                parserParams.put(ParserUtil.APP_ID, contentEntity.getAppId());
            }else {
                parserParams.put(ParserUtil.APP_ID, BasicUtil.getWebsiteApp().getAppId());
            }
        } else {
            parserParams.put(ParserUtil.URL, BasicUtil.getApp().getAppHostUrl());
            parserParams.put(ParserUtil.APP_DIR, BasicUtil.getApp().getAppDir());

        }
        // 短链开关
        parserParams.put(ParserUtil.SHORT_SWITCH,ConfigUtil.getBoolean("静态化配置", "shortSwitch", false));
        // 查询field标签
        QueryWrapper<TagEntity> tagWrapper = new QueryWrapper<TagEntity>();
        tagWrapper.eq("tag_name", "field");
        TagEntity tagEntity = tagBiz.getOne(tagWrapper);
        String sqlFtl = tagEntity.getTagSql();
        CategoryEntity category = this.categoryBiz.getEntityById(contentEntity.getCategoryId());
        //只获取子节点
        if (!category.getLeaf() && !CategoryTypeEnum.COVER.toString().equalsIgnoreCase(category.getType())) {
            return;
        }
        // 设置栏目，方便解析的时候关联表查询
        parserParams.put(ParserUtil.COLUMN, category);
        //根据栏目自定义模型id
        // 获取表单类型的id
        if (StringUtils.isNotBlank(category.getMdiyModelId())) {
            ModelEntity contentModel = (ModelEntity) modelBiz.getById(category.getMdiyModelId());
            if (contentModel != null) {
                // 设置自定义模型表名，方便解析的时候关联表查询
                parserParams.put(ParserUtil.TABLE_NAME, contentModel.getModelTableName());
            }
        }
        parserParams.put("id",contentEntity.getId());
        try {
            String sql = ParserUtil.rendering(parserParams, sqlFtl);
            List list = (List) tagBiz.excuteSql(sql);
//            jdbcTemplate.queryForList(sql);
            for(Object item:list){
                Map o = (Map) item;
                o.put("typeleaf",category.getLeaf());
                if(String.valueOf(o.get("id")).equals(contentEntity.getId())) {
                    //如果类型是 nclob必须要转换一次
                    if (o.get("content") instanceof Clob || o.get("content") instanceof NClobProxyImpl) {
                        Clob content = (Clob) o.get("content");
                        o.put("content", StringUtil.clobStr(content));
                    }
                    contentCache.saveOrUpdate(String.valueOf(o.get("id")),o);
                    LOG.debug("缓存文章-{}", contentEntity.getId());
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        contentCache.flush();
    }


    @Override
    public Object excute(Map map) {
        if(!map.containsKey("id")) {
            return  null;
        }
        try {

            Object obj = contentCache.get(String.valueOf(map.get("id")));
            if(obj == null) {
                LOG.debug("文章id {} 缓存文件不存在 ",map.get("id"));
                return null;
            }
            if((boolean) map.get(ParserUtil.IS_DO)) {
                Map _obj = (Map)obj;
                _obj.put("typelink","/mcms/list.do?style="+map.get(ParserUtil.APP_TEMPLATE)+"&typeid="+map.get("typeid"));
                _obj.put("link","/mcms/view.do?style="+map.get(ParserUtil.APP_TEMPLATE)+"&id="+map.get("id"));
            }
            return  Optional.ofNullable(obj).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

}
