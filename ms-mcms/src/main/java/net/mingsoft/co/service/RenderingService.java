/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.service;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapWrapper;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.mingsoft.base.constant.Const;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.co.tag.CustomTag;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.mdiy.biz.ITagBiz;
import net.mingsoft.mdiy.constant.e.TagTypeEnum;
import net.mingsoft.mdiy.entity.TagEntity;
import net.mingsoft.mdiy.tag.IncludeExTag;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Service
public class RenderingService {
    /*
     * log4j日志记录
     */
    protected final static Logger LOG = LoggerFactory.getLogger(RenderingService.class);

    /**
     * 标签指令前缀
     */
    public static final String TAG_PREFIX = "ms_";

    /**
     * 系统预设需要特殊条件的标签
     */
    public static List<String> systemTag = CollUtil.toList("field", "pre", "page", "next");


    private static TimedCache<String, List> timedCache = CacheUtil.newTimedCache(10000);


    /**
     * 多线程渲染页面
     *
     * @param map             渲染变量
     * @param templateFolder   模版文件夹，例如 template/
     * @param templateFile    模版文件名称 ，例如 index.htm
     * @param templateContent 模版文件具体模版内容，避免外部循环重复读取，直接在入口处一次性读取好模版内容
     * @param writeFilePath   写入文件路径
     */
    @Async("renderingServiceThreadPool")
    public Future<String> rendering(Map map, String templateFolder, String templateFile, String templateContent, String writeFilePath) {
        LOG.debug("generate Path：{}",writeFilePath);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);
        //读取标签
        ITagBiz tagSqlBiz = SpringUtil.getBean(ITagBiz.class);
        // 这里使用xml方法,可以读取缓存
        List<TagEntity> list = tagSqlBiz.queryAll();
        //组织模板路径

        //初始化
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        FileTemplateLoader ftl = null;
        try {
            ftl = new FileTemplateLoader(new File(templateFolder));
        } catch (IOException e) {
            LOG.error("模版文件 {} 目录异常 ", templateFolder);
            e.printStackTrace();
        }
        MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(new TemplateLoader[]{stringLoader, ftl});
        cfg.setNumberFormat("#");
        cfg.setTemplateLoader(multiTemplateLoader);
        //自动导入宏
        ClassPathResource classPathResource = new ClassPathResource("WEB-INF/macro.ftl");
        String template = null;
        try {
            template = IOUtils.toString(classPathResource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error("宏定义模版错误 WEB-INF/macro.ftl异常 ");
            e.printStackTrace();
        }
        StringBuffer sb = new StringBuffer(template);
        //读取自定义宏
        list.forEach(tag -> {
            TagTypeEnum typeEnum = TagTypeEnum.get(tag.getTagType());
            if (typeEnum == TagTypeEnum.MACRO) {//列表标签
                sb.append(tag.getTagSql());
            }
        });

        stringLoader.putTemplate("macro.ms", sb.toString());
        cfg.setClassicCompatible(true);
        cfg.addAutoInclude("macro.ms");

        //添加自定义模板
        stringLoader.putTemplate("ms:custom:" + templateFile, templateContent);

        //获取自定义模板
        Template _template = null;
        try {
            _template = cfg.getTemplate("ms:custom:" + templateFile, Const.UTF8);
        } catch (Exception e) {
            LOG.error("模版内容错误，文件名 {}", templateFile);
            e.printStackTrace();
        }

        //设置兼容模式
        cfg.setClassicCompatible(true);
        //设置扩展include
        cfg.setSharedVariable(TAG_PREFIX + "includeEx", new IncludeExTag(templateFolder, stringLoader));


        list.forEach(tagSql -> {
            //添加自定义标签
            TagTypeEnum typeEnum = TagTypeEnum.get(tagSql.getTagType());
            if (typeEnum == TagTypeEnum.LIST) {//列表标签
                CustomTag customTag = new CustomTag(timedCache, map, tagSql);
                cfg.setSharedVariable(TAG_PREFIX + tagSql.getTagName(), customTag);
            }
            if (typeEnum == TagTypeEnum.SINGLE ) {
                String sql = null;
                try {
                    List _list = null;
                    if(StrUtil.isNotEmpty(tagSql.getTagClass())) {
                        BaseTagClassService baseTagClassService = (BaseTagClassService)SpringUtil.getBean(tagSql.getTagClass());
                        Object obj = baseTagClassService.excute(map);
                        if(obj!=null) {
                            map.put(tagSql.getTagName(), baseTagClassService.excute(map));
                        }
                    } else {

                        sql = ParserUtil.rendering(map, tagSql.getTagSql());

                       if(StringUtils.isNotBlank(sql)) {
                           if (timedCache.get(sql) != null) {
                               _list = timedCache.get(sql);
                           } else {
                               try {
                                   _list = (List) tagSqlBiz.excuteSql(sql);//执行一条查询
                                   timedCache.put(sql, _list);
                               } catch (Exception e) {
                                   LOG.error("标签{}错误",tagSql.getTagName());
                                   e.printStackTrace();
                               }
                           }
                           if (_list !=null && _list.size() > 0 && _list.get(0) != null) {
                               MapWrapper<String, Object> mw = new MapWrapper<>((HashMap<String, Object>) _list.get(0));
                               //把NClob类型转化成string
                               mw.forEach(x -> {
                                   if (x.getValue() instanceof Clob) {
                                       x.setValue(StringUtil.clobStr((Clob) x.getValue()));
                                   }
                               });
                               map.put(tagSql.getTagName(), _list.get(0));
                           }
                       }
                    }


                } catch (IOException e) {
                    LOG.error("标签{}ftl渲染错误,sql:{}",tagSql.getTagName(),tagSql.getTagSql());
                    e.printStackTrace();
                } catch (TemplateException e) {
                    LOG.error("标签{}ftl渲染错误,sql:{}",tagSql.getTagName(),tagSql.getTagSql());
                    e.printStackTrace();
                }
            }
        });
        StringWriter writer = new StringWriter();
        try {
            _template.process(map, writer);
        } catch (TemplateException e) {
            LOG.error("模版文件 {} TemplateException异常 map {} ", templateFile, JSONUtil.toJsonStr(map));
            LOG.debug(_template.toString());
            e.printStackTrace();
        } catch (IOException e) {
            LOG.error("模版文件 {} IOException异常 ", templateFile);
            LOG.debug(_template.toString());
            e.printStackTrace();
        } catch (Exception e) {
            LOG.error("模版文件 {} 其他异常 ", templateFile);
            LOG.debug(_template.toString());
            e.printStackTrace();
        }
        String content = writer.toString();
        if (writeFilePath != null) {
            FileUtil.writeString(content, writeFilePath, Const.UTF8);
        }
        LOG.info("生成:"+writeFilePath);
        return new AsyncResult<String>(content);
    }


}


