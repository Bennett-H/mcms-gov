/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.msend.service;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.TemplateClassResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.msend.entity.TemplateEntity;
import net.mingsoft.msend.util.SendUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * 发送基础类
 * @author 铭飞团队
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2021-05-15<br/>
 * 历史修订：<br/>
 */
public abstract class BaseSendService {
    protected static String MAIL = "mail", SMS = "sms";
    protected static final Logger LOG = LoggerFactory.getLogger(SendUtil.class);

    /**
     * 发送
     *
     * @param config 配置的类型
     * @param toUser     接收用户
     * @param template   模板内容
     * @param values     根据values.key值替换替换模版里面内容的${key}，
     * @return
     */
    public abstract boolean send(Map<String,String> config, String[] toUser, TemplateEntity template, Map values);

    /**
     * 根据文本内容渲染模板
     *
     * @param root    参数值
     * @param content 模板内容
     * @return 渲染后的内容
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
            throw new BusinessException("消息模板不存在");
        } catch (TemplateException e) {
            e.printStackTrace();
            throw new BusinessException("消息模板解析错误");
        }

    }
}
