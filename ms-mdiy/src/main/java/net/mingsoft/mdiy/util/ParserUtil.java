/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.map.MapWrapper;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.ParseException;
import freemarker.core.TemplateClassResolver;
import freemarker.template.*;
import net.mingsoft.base.constant.Const;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.config.MSProperties;
import net.mingsoft.mdiy.bean.PageBean;
import net.mingsoft.mdiy.biz.ITagBiz;
import net.mingsoft.mdiy.constant.e.TagTypeEnum;
import net.mingsoft.mdiy.entity.TagEntity;
import net.mingsoft.mdiy.tag.CustomTag;
import net.mingsoft.mdiy.tag.IncludeExTag;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Clob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtil {
    /*
     * log4j日志记录
     */
    protected final static Logger LOG = LoggerFactory.getLogger(ParserUtil.class);

    /**
     * 是否开启短链
     */
    public static final String SHORT_SWITCH = "shortSwitch";

    /**
     * 静态文件生成路径;例如：mcms/html/1
     */
    public static final String HTML = "html";

    /**
     * index
     */
    public static final String INDEX = "index";

    /**
     * 文件夹路径名后缀;例如：1/58/71.html
     */
    public static final String HTML_SUFFIX = ".html";

    /**
     * 标签指令前缀
     */
    public static final String TAG_PREFIX = "ms_";

    /**
     * 生成的静态列表页面名;例如：list1.html
     */
    public static final String PAGE_LIST = "list-";
    /**
     * 模板文件后缀名;例如：index.htm
     */
    public static final String HTM_SUFFIX = ".htm";

    /**
     * 是否是动态解析;true:动态、false：静态
     */
    public static final String IS_DO = "isDo";

    /**
     * 当前系统访问路径
     */
    public static final String URL = "url";

    /**
     * 栏目实体;
     */
    public static final String COLUMN = "column";

    /**
     * 文章编号
     */
    public static final String ID = "id";

    /**
     * 字段名称
     */
    public static final String FIELD = "field";


    /**
     * 自定义模型表名;
     */
    public static final String TABLE_NAME = "tableName";

    /**
     * 模块路径;
     */
    public static final String MODEL_NAME = "modelName";


    /**
     * 分页，提供給解析传递给sql解析使用
     */
    public static final String PAGE = "pageTag";


    /**
     * 栏目编号;原标签没有使用驼峰命名
     */
    public static final String TYPE_ID = "typeid";


    /**
     * 站点编号
     */
    public static final String APP_ID = "appId";

    /**
     * 站点目录
     */
    public static final String APP_DIR = "appDir";


    /**
     * 初始化Configuration
     */
    public static Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);

    /**
     * 文件模板渲染
     */
    public static FileTemplateLoader ftl = null;

    /**
     * 字符串模板渲染
     */
    public static StringTemplateLoader stringLoader;


    /**
     * 系统预设需要特殊条件的标签
     */
    public static List<String> systemTag1 = CollUtil.toList("field", "pre", "page", "next");


    /**
     * 线程锁，Configuration的共享变量不是现成安全的，这会导致在执行列表标签的时候会执行到重复的sql，因此需要给方法上锁
     */
    public static final Lock LOCK = new ReentrantLock();

    /**
     * 获取模板文件夹
     * @return ms.upload.template + 应用编号
     */
    public static String buildTemplatePath() {
        return ParserUtil.buildTemplatePath(null);
    }

    /**
     * 拼接模板文件路径
     *
     * @param path 主题下对应的htm模板文件
     * @return 完整的模板文件路径
     */
    public static String buildTemplatePath(String path) {
        return ParserUtil.buildTemplatePath(null,path);
    }

    /**
     * 上下文路径
     */
    public static final String CONTEXT_PATH = "contextPath";

    /**
     * 更具指定皮肤生成模板
     * @param style 指定主题获取模板
     * @param path 主题下对应的htm模板文件
     * @return
     */
    public static String buildTemplatePath(String style,String path) {
        String uploadTemplatePath = MSProperties.upload.template;
        if (BasicUtil.getWebsiteApp() != null) {
            return BasicUtil.getRealPath(uploadTemplatePath + File.separator + BasicUtil.getWebsiteApp().getAppId() + File.separator
                    + (style != null ? (File.separator + style) : BasicUtil.getWebsiteApp().getAppStyle() ) + (path != null ? (File.separator + path) : ""));
        } else {
            return BasicUtil.getRealPath(uploadTemplatePath + File.separator + BasicUtil.getApp().getAppId() + File.separator
                    + (style != null ? (File.separator + style) : BasicUtil.getApp().getAppStyle()) + (path != null ? (File.separator + path) : ""));
        }
    }


    /**
     * 拼接生成后的路径地址
     * @param path 当前业务路径
     * @param appDir 站点路径，根据应用设置配置
     * @param htmlDir 静态文件根路径，根据yml配置，默认html
     * @return
     */
    public static String buildHtmlPath(String path,String htmlDir,String appDir) {
        return BasicUtil.getRealPath(htmlDir) + File.separator + appDir + File.separator + path
                + HTML_SUFFIX;
    }


    /**
     * 根据文本内容渲染模板
     *
     * @param root    参数值
     * @param content 模板内容
     * @return 渲染后的内容
     */
    public static String rendering(Map root, String content) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("template", content);
        cfg.setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);
        cfg.setNumberFormat("#");
        cfg.setTemplateLoader(stringLoader);

        Template template = cfg.getTemplate("template", "utf-8");
        StringWriter writer = new StringWriter();
        template.process(root, writer);
        return writer.toString();

    }


    /**
     * 根据模板文件渲染
     *
     * @param templatePath 模板路径
     * @return
     * @throws TemplateNotFoundException
     * @throws MalformedTemplateNameException
     * @throws ParseException
     * @throws IOException
     */
    public static int getPageSize(String templatePath, int defaultSize) {
        //组织模板路径
        String buildTempletPath = ParserUtil.buildTemplatePath();
        // 读取模板文件
        String content = FileUtil.readString(FileUtil.file(buildTempletPath, templatePath), CharsetUtil.CHARSET_UTF_8);

        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile("\\{(.*?)ispaging=true(.*?)\\}");
        // 现在创建 matcher 对象
        Matcher m = pattern.matcher(content);

        String size = null;
        if (m.find()) {
            size = ReUtil.extractMulti("size=(\\d*)", m.group(1), "$1");
            //没有找到继续找
            if (size == null) {
                size = ReUtil.extractMulti("size=(\\d*)", m.group(2), "$1");
            }

            if (size != null) {
                defaultSize = Integer.parseInt(size);
            }
            LOG.debug("获取分页的size:{}", size);
        }

        return defaultSize;
    }


    /**
     * 渲染模板
     *
     * @param templatePath 模板路径
     * @param map          传入参数
     * @return
     * @throws TemplateNotFoundException
     * @throws MalformedTemplateNameException
     * @throws ParseException
     * @throws IOException
     */
    public static String rendering(String templatePath, Map map)
            throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {

        //组织模板路径
        String buildTempletPath = ParserUtil.buildTemplatePath();
        //读取标签
        ITagBiz tagBiz = SpringUtil.getBean(ITagBiz.class);
        List<TagEntity> list = tagBiz.list();
        //初始化
        if (ftl == null || !buildTempletPath.equals(ftl.baseDir.getPath())) {

            stringLoader = new StringTemplateLoader();
            ftl = new FileTemplateLoader(new File(buildTempletPath));
            MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(new TemplateLoader[]{stringLoader, ftl});
            cfg.setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);
            cfg.setNumberFormat("#");
            cfg.setTemplateLoader(multiTemplateLoader);

            //自动导入宏
            ClassPathResource classPathResource = new ClassPathResource("WEB-INF/macro.ftl");
            String template = IOUtils.toString(classPathResource.getInputStream(), "UTF-8");
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
        }



        // 读取模板文件
        String temp = FileUtil.readString(FileUtil.file(buildTempletPath, templatePath), CharsetUtil.CHARSET_UTF_8);

        //获取自定义模板
        Template template = null;
        // todo 上锁
        LOCK.lock();
        //替换标签
        try {
            temp = replaceTag(temp);
            //添加自定义模板
            stringLoader.putTemplate("ms:custom:" + templatePath, temp);

            try {
                template = cfg.getTemplate("ms:custom:" + templatePath, Const.UTF8);
            } catch (Exception e) {
                LOG.debug("模板错误");
                e.printStackTrace();
                LOG.debug(temp);
            }

            //设置兼容模式
            cfg.setClassicCompatible(true);
            //设置扩展include
            cfg.setSharedVariable(TAG_PREFIX + "includeEx", new IncludeExTag(buildTempletPath, stringLoader));

            list.forEach(tag -> {
                //添加自定义标签
                if (StrUtil.isNotBlank(tag.getTagName())) {

                    TagTypeEnum typeEnum = TagTypeEnum.get(tag.getTagType());

                    if (typeEnum == TagTypeEnum.LIST) {//列表标签
                        cfg.setSharedVariable(TAG_PREFIX + tag.getTagName(), new CustomTag(map, tag));
                    }



                    if (typeEnum == TagTypeEnum.SINGLE && (!systemTag1.contains(tag.getTagName())
                            //文字内容需要id参数
                            || (map.containsKey("id") && tag.getTagName().equals("field"))
                            //分页需要pageTag参数
                            || (map.containsKey("pageTag") && (tag.getTagName().equals("pre")
                            || tag.getTagName().equals("next") || tag.getTagName().equals("page")))
                    )) {

                        String sql = null;
                        try {
                            sql = rendering(map, tag.getTagSql());
                            List _list = (List) tagBiz.excuteSql(sql);
                            if (_list.size() > 0) {
                                if (_list.get(0) != null) {
                                    MapWrapper<String, Object> mw = new MapWrapper<>((HashMap<String, Object>) _list.get(0));
                                    //把Clob类型转化成string
                                    mw.forEach(x-> {
                                        if (x.getValue() instanceof Clob) {
                                            x.setValue(StringUtil.clobStr((Clob) x.getValue()));
                                        }
                                    });
                                }
                                map.put(tag.getTagName(), _list.get(0));
                            }
                        } catch (IOException e) {
                            LOG.error("", e);
                        } catch (TemplateException e) {
                            LOG.error("", e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // todo 解锁
            LOCK.unlock();
        }


        StringWriter writer = new StringWriter();
        try {
            template.process(map, writer);
            return writer.toString();
        } catch (Exception e) {
            LOG.error("渲染错误", e);
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 标签替换
     *
     * @param content 模板内容
     * @return 替换后的内容
     */
    public static String replaceTag(String content) {
        // 创建 Pattern 对象
        //替include标签 <#include "header.htm" /> 或者 <#include "header.htm">  转换为 <@ms_includeEx template=header.htm/>
        content = content.replaceAll("<#include(.*)/>", StrUtil.format("<@{}includeEx template=$1/>", TAG_PREFIX));
        content = content.replaceAll("<#include(.*)>", StrUtil.format("<@{}includeEx template=$1/>", TAG_PREFIX));

        //替换全局标签{ms:global.name/} 转换为{global.name/}
        content = content.replaceAll("\\{ms:([^\\}]+)/\\}", "\\${$1}");
        //替换全局标签 {@ms:file */} 转换为<@ms_file */>
        content = content.replaceAll("\\{@ms:([^\\}]+)/\\}", StrUtil.format("<@{}$1/>", TAG_PREFIX));

        //替换列表开头标签 {ms:arclist *} 转换为{@ms_arclist */}
        content = content.replaceAll("\\{ms:([^\\}]+)\\}", StrUtil.format("<@{}$1>", TAG_PREFIX));
        //替换列表结束标签 {/ms:arclist *} 转换为{/@ms_arclist}
        content = content.replaceAll("\\{/ms:([^\\}]+)\\}", StrUtil.format("</@{}$1>", TAG_PREFIX));
        //替换内容老的标签 [field.*/] 转换为${filed.*}
        content = content.replaceAll("\\[([^\\]]+)/\\]", "\\${$1}");
        return content;
    }

}
