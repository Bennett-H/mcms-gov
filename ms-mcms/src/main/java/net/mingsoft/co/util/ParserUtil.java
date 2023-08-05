/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.co.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.core.TemplateClassResolver;
import freemarker.template.*;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.co.bean.TemplateBean;
import net.mingsoft.co.constant.Const;
import net.mingsoft.co.job.GeneraterServiceJob;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.mdiy.util.DictUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 解析相关工具类
 */
public class ParserUtil {
    /*
     * log4j日志记录
     */
    protected final static Logger LOG = LoggerFactory.getLogger(ParserUtil.class);


    /**
     * 静态文件生成路径;例如：mcms/html/1
     */
    public static final String HTML = "html";

    /**
     * index
     */
    public static final String INDEX = "index";


    public static final String DATE_TIME = "dateTime";

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
     * 模版文件后缀名;例如：index.htm
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
     * 上下文路径
     */
    public static final String CONTEXT_PATH = "contextPath";

    /**
     * 栏目实体;
     */
    public static final String COLUMN = "column";

    /**
     * 模板名称
     */
    public static final String TEMPLATE_NAME = "templateName";

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
     * 是否开启短链
     */
    public static final String SHORT_SWITCH = "shortSwitch";

    /**
     * 站点目录
     */
    public static final String APP_TEMPLATE = "appTemplate";

    /**
     * 返回当前应用模版路径
     *
     * @return ms.upload.template + 应用编号
     */
    public static String buildTemplatePath() {
        return ParserUtil.buildTemplatePath(new HashMap<>());
    }

    public static String buildTemplatePath(Map<String, Object> parserParams){
        return ParserUtil.buildTemplatePath(parserParams,"");
    }

    public static String buildTemplatePath(String... path){
        return ParserUtil.buildTemplatePath(null,path);
    }




    /**
     * 组装并返回模版路径
     * @param parserParams 参数map集合，主要用于拼接appId
     * @param path 可变参数，主要用于接收模板名称及文件名拼接
     * @return ms.upload.template + 应用编号 / path/path
     */
    public static String buildTemplatePath(Map<String, Object> parserParams,String... path) {
        AppEntity app = BasicUtil.getWebsiteApp()!=null?BasicUtil.getWebsiteApp():BasicUtil.getApp();
        return buildTemplatePath(app,parserParams,path);
    }

    /**
     * 组装并返回模板路径，用于指定站点
     * @param app 站点实体
     * @param parserParams 参数map集合，主要用于拼接appId
     * @param path 可变参数，主要用于接收模板名称及文件名拼接
     * @return ms.upload.template + 应用编号 / path/path
     */
    public static String buildTemplatePath(AppEntity app,Map<String, Object> parserParams,String... path){
        String uploadTemplatePath = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadTemplate", "template");

        StringBuffer _path = new StringBuffer();
        if (new File(uploadTemplatePath).isAbsolute()) {
            _path.append(uploadTemplatePath + File.separator);
        } else {
            _path.append(BasicUtil.getRealPath(uploadTemplatePath)+File.separator);
        }
        if (CollUtil.isNotEmpty(parserParams) && parserParams.get("appId") != null) {
            _path.append(parserParams.get("appId"));
        } else {
            _path.append(app.getAppId());
        }
        Arrays.stream(path).forEach((p) -> {
            _path.append(File.separator).append(p);
        });
        return _path.toString();
    }

    /**
     * 拼接静态化生成的文件路径 如/html/web/out/index.html
     * 带html层级
     * @param path    当前业务路径
     * @return 文件生成路径(绝对路径)
     */
    public static String buildGenerateHtmlPath(String htmlDir, String appDir, String style, String path) {
        return BasicUtil.getRealPath(buildHtmlPath(htmlDir,appDir,style,path));
    }

    /**
     * 拼接静态化生成的文件路径 如/html/web/out/index.html
     * 带html层级
     * @param path    当前业务路径
     * @return 文件生成路径(相对路径)
     */
    public static String buildHtmlPath(String htmlDir, String appDir, String style, String path) {
        String dir = (StringUtils.isNotBlank(htmlDir) ? htmlDir + File.separator : "")  + (StringUtils.isNotBlank(appDir) ? appDir + File.separator : "");
        return buildViewHtmlPath(dir,style,path);
    }

    /**
     * 拼接静态化生成的文件路径 如/web/out/index.html
     * 不带html层级
     * @param path    当前业务路径
     * @return 文件预览路径(相对路径)
     */
    public static String buildViewHtmlPath(String appDir, String style, String path) {
        String htmlPath = (StringUtils.isNotBlank(appDir) ? appDir+File.separator : "") + (StringUtils.isNotBlank(style) ? style+File.separator : "");
        htmlPath = htmlPath + (StringUtils.isNotBlank(path) ? path.concat(path.indexOf(".") > -1 ? "" : HTML_SUFFIX) : "");
        return htmlPath;

    }

    public static boolean isDisableList(String style, String templatePath) {
        //组织模板路径
        String buildTemplatePath = ParserUtil.buildTemplatePath(style);
        // 读取模板文件
        String content = FileUtil.readString(FileUtil.file(buildTemplatePath, templatePath), CharsetUtil.CHARSET_UTF_8);

        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile("\\{(.*?)disablelist(.*?)\\}");
        // 现在创建 matcher 对象
        Matcher m = pattern.matcher(content);

        if (m.find()) {
            return true;
        }

        return false;
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
    public static int getPageSize(String templateFolder, String templatePath, int defaultSize) {
        // 读取模板文件
        String content = FileUtil.readString(FileUtil.file(templateFolder, templatePath), CharsetUtil.CHARSET_UTF_8);

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
        } else {
            return -1;
        }

        return defaultSize;
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


    /**
     * 设置基本的参数, 自动获取App实体
     * @param parserParams map
     */
    public static void putBaseParams(Map<String, Object> parserParams,String style) {
        //是否开启短链
        boolean shortSwitch = ConfigUtil.getBoolean("静态化配置", "shortSwitch", false);
        parserParams.put(ParserUtil.SHORT_SWITCH,Boolean.valueOf(shortSwitch));
        parserParams.putIfAbsent(ParserUtil.IS_DO, false);
        //对项目名预处理
        String contextPath = BasicUtil.getContextPath();
        if (StringUtils.isNotBlank(contextPath) && "/".equalsIgnoreCase(contextPath) ){
            contextPath = "";
        }
        parserParams.putIfAbsent(ParserUtil.CONTEXT_PATH, contextPath);
        if (BasicUtil.getWebsiteApp() == null && shortSwitch) {
            parserParams.put(ParserUtil.URL, BasicUtil.getApp().getAppHostUrl());
            // 设置为空目录层级
            parserParams.put(ParserUtil.APP_DIR, "");
        } else if (BasicUtil.getWebsiteApp() != null){
            //外部可以自定义url地址，非http请求时候需要外部设置url
            if(parserParams.get(ParserUtil.URL)==null) {
                parserParams.put(ParserUtil.URL, BasicUtil.getUrl());
            }
            parserParams.put(ParserUtil.APP_ID, BasicUtil.getWebsiteApp().getAppId());
            //设置默认
            parserParams.put(ParserUtil.APP_DIR, BasicUtil.getWebsiteApp().getAppDir());
        } else {
            //外部可以自定义url地址，非http请求时候需要外部设置url
            if(parserParams.get(ParserUtil.URL)==null) {
                parserParams.put(ParserUtil.URL, BasicUtil.getUrl());
            }
            //设置默认
            parserParams.put(ParserUtil.APP_DIR, BasicUtil.getApp().getAppDir());
        }

        // 当前站点只绑定一套模板并且开启了短链，不拼接模板名称
        List<Map> styles = JSONUtil.toList(BasicUtil.getApp().getAppStyles(), Map.class);
        List<Map> list = styles.stream().filter(s -> {
            return StringUtils.isNotEmpty(s.get("template").toString());
        }).collect(Collectors.toList());
        boolean single = list.size()-1==0;
        String template = (single && shortSwitch) ? "" : style;

        parserParams.put(ParserUtil.APP_TEMPLATE, template);
        parserParams.put(ParserUtil.TEMPLATE_NAME, style);


    }

    /**
     * 设置基本的参数, 手动指定App实体, 一般用于多线程无法获取session的情况
     * @param parserParams map
     * @param app App实体
     */
    public static void putBaseParams(Map<String, Object> parserParams, AppEntity app,String style) {
        //是否开启短链
        boolean shortSwitch = ConfigUtil.getBoolean("静态化配置", "shortSwitch", false);
        boolean siteEnable = Boolean.parseBoolean(ConfigUtil.getString("站群配置", "siteEnable", "false"));

        parserParams.put(ParserUtil.SHORT_SWITCH,Boolean.valueOf(shortSwitch));
        parserParams.putIfAbsent(ParserUtil.IS_DO, false);
        //对项目名预先处理
        String contextPath = BasicUtil.getContextPath();
        if (StringUtils.isNotBlank(contextPath) && "/".equalsIgnoreCase(contextPath) ){
            contextPath = "";
        }
        parserParams.putIfAbsent(ParserUtil.CONTEXT_PATH, contextPath);
        //外部可以自定义url地址，非http请求时候需要外部设置url
        parserParams.putIfAbsent(ParserUtil.URL, BasicUtil.getUrl());
        if (!siteEnable && shortSwitch) {
            // 设置为空目录层级
            parserParams.put(ParserUtil.APP_DIR, "");
        } else if (siteEnable){
            parserParams.put(ParserUtil.APP_ID, BasicUtil.getWebsiteApp().getAppId());
            //设置默认
            parserParams.put(ParserUtil.APP_DIR, app==null?BasicUtil.getApp().getAppDir():app.getAppDir());
        } else {
            //设置默认
            parserParams.put(ParserUtil.APP_DIR, app==null?BasicUtil.getApp().getAppDir():app.getAppDir());
        }

        // 当前站点只绑定一套模板并且开启了短链，不拼接模板名称
        List<Map> styles = JSONUtil.toList(BasicUtil.getApp().getAppStyles(), Map.class);
        List<Map> list = styles.stream().filter(s -> {
            return StringUtils.isNotEmpty(s.get("template").toString());
        }).collect(Collectors.toList());
        boolean single = list.size()-1==0;
        String template = (single && shortSwitch) ? "" : style;

        parserParams.put(ParserUtil.APP_TEMPLATE, template);
        parserParams.put(ParserUtil.TEMPLATE_NAME, style);
    }

    /**
     * 根据模板字典值查询模板名
     * @param dictValue 模板字典值
     * @return 模板名称字符串
     */
    public static String getTemplateName(String dictValue){
        if (StringUtils.isBlank(dictValue)){
            return null;
        }
        String dictLabel = DictUtil.getDictLabel("模板类型", dictValue);
        List<TemplateBean> templateList;
        if (BasicUtil.getWebsiteApp() != null) {
            templateList = JSONUtil.toList(BasicUtil.getWebsiteApp().getAppStyles(), TemplateBean.class);
        }else {
            templateList = JSONUtil.toList(BasicUtil.getApp().getAppStyles(), TemplateBean.class);
        }
        List<TemplateBean> result = templateList.stream().filter(templateBean -> templateBean.getName().equals(dictLabel)).collect(Collectors.toList());
        if(result.size()>0){
            return result.get(0).getTemplate();
        }
        return null;
    }

    /**
     * 根据模板字典值,站点实体查询模板名
     * @param dictValue 模板字典值
     * @return 模板名称字符串
     */
    public static String getTemplateName(String dictValue, AppEntity app){
        if (StringUtils.isBlank(dictValue)){
            return null;
        }
        String dictLabel = DictUtil.getDictLabel("模板类型", dictValue);
        List<TemplateBean> templateList = JSONUtil.toList(app.getAppStyles(), TemplateBean.class);
        List<TemplateBean> result = templateList.stream().filter(templateBean -> templateBean.getName().equals(dictLabel)).collect(Collectors.toList());
        if(result.size()>0){
            return result.get(0).getTemplate();
        }
        return null;
    }

    /**
     * 根据模板名查询模板字典值
     * @param templateName 模板名称
     * @return 模板字典值,如outsite
     */
    public static String getDictValue(String templateName){
        return getDictValue(templateName,BasicUtil.getApp());
    }

    /**
     * 根据模板名、站点实体查询模板字典值
     * @param templateName 模板名称
     * @param app 站点实体
     * @return 模板字典值，如outsite
     */
    public static String getDictValue(String templateName,AppEntity app){
        if (StringUtils.isBlank(templateName)){
            return null;
        }
        List<TemplateBean> templateList = JSONUtil.toList(app.getAppStyles(), TemplateBean.class);
        List<TemplateBean> result = templateList.stream().filter(templateBean -> templateBean.getTemplate().equals(templateName)).collect(Collectors.toList());
        if(result.size()>0){
            return DictUtil.getDictValue("模板类型", result.get(0).getName());
        }
        return null;
    }

    /**
     * 提供业务调用自动静态化
     * @param categoryIds 父亲栏目编号
     */
    public static void generate(List categoryIds) {
        generate(categoryIds,BasicUtil.getApp());
    }

    /**
     * 提供业务调用自动静态化,用于区分不同站点
     * @param categoryIds 父亲栏目编号
     * @param app 站点实体
     */
    public static void generate(List categoryIds, AppEntity app){
        try {
            LOG.debug("appId：{}，styles：{}",app.getAppId(),app.getAppStyles());
            List<Map> maps = JSONUtil.toList(app.getAppStyles(), Map.class);
            GeneraterServiceJob generaterServiceJob = SpringUtil.getBean(GeneraterServiceJob.class);
            for (Map style : maps) {
                String key = style.get("name") + "";
                String value = DictUtil.getDictValue("模板类型", key);
                generaterServiceJob.index("index.htm","index.html",value,app.getAppUrl());
                generaterServiceJob.content(value,app.getAppUrl());
                generaterServiceJob.category(value, app.getAppUrl(), categoryIds);
            }
        } catch (Exception e) {
            LOG.error("自动静态化失败");
            e.printStackTrace();

        }
    }
}
