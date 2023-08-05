/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.base.util;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.TemplateClassResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class FtlUtil {
    /**
     * 根据文本内容渲染模板
     *
     * @param root    参数值
     * @param content 模板内容
     * @return 渲染后的内容，如果解析有问题直接返回原始内容
     */
    public static String rendering(Map root, String content) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("template", content);
        cfg.setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);
        cfg.setNumberFormat("#");
        cfg.setTemplateLoader(stringLoader);

        Template template = null;
        try {
            template = cfg.getTemplate("template", "utf-8");
            StringWriter writer = new StringWriter();
            template.process(root, writer);
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return content;
    }
}
