/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.config;

import com.jagregory.shiro.freemarker.ShiroTags;
import freemarker.core.TemplateClassResolver;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FreemarkerConfig {

	@Autowired
    protected freemarker.template.Configuration configuration;
	@Autowired
	protected FreeMarkerConfigurer configurer;
    @Autowired
    protected org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver resolver;
    @Autowired
    protected org.springframework.web.servlet.view.InternalResourceViewResolver springResolver;

    @PostConstruct
	public void init() throws IOException, TemplateException {
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);
		configuration.setSharedVariable("shiro", new ShiroTags());
	}

}
