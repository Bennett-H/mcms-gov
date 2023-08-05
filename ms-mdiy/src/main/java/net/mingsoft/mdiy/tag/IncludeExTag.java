/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.tag;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.core._MiscTemplateException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.*;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.config.MSProperties;
import net.mingsoft.mdiy.util.ParserUtil;

import java.io.IOException;
import java.util.Map;

/**
 * @Author: huise
 * @Description: 自定义引入模板标签
 * @Date: Create in 2020/06/24 15:37
 */
public class IncludeExTag implements TemplateDirectiveModel {

    /**
     * 外部key值
     */
    private static final String TEMPLATE_KEY = "template";

    protected static BeansWrapper build = new BeansWrapperBuilder(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).build();

    /**
     * 模板根目录
     */
    private String basePath;
    /**
     * 字符串加载器
     */
    private StringTemplateLoader stringLoader;

    public IncludeExTag(String basePath, StringTemplateLoader stringLoader) {
        this.basePath = basePath;
        this.stringLoader = stringLoader;
    }

    @Override
    public void execute(Environment environment, Map params, TemplateModel[] templateModel,
                        TemplateDirectiveBody directiveBody) throws TemplateException, IOException {

        TemplateLoader templateLoader = environment.getConfiguration().getTemplateLoader();
        String uploadTemplatePath = MSProperties.upload.template;
        String path = build.unwrap((TemplateModel) params.get(uploadTemplatePath)).toString();

        if (!params.containsKey(uploadTemplatePath)) {
            throw new MalformedTemplateNameException("missing required parameter '" + uploadTemplatePath, "'");
        }
        if (templateLoader.findTemplateSource(path) == null) {
            throw new _MiscTemplateException(environment, "Missing template file path:" + path);
        }
        //替换模板
        String temp = FileUtil.readString(FileUtil.file(basePath, path).getPath(), CharsetUtil.CHARSET_UTF_8);
        //正则替换标签
        temp = ParserUtil.replaceTag(temp);
        stringLoader.putTemplate("ms:custom:" + path, temp);
        Template template = environment.getTemplateForInclusion("ms:custom:" + path, null, true);
        //引入模板
        environment.include(template);


    }
}
