/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.config;

import freemarker.template.TemplateException;
import net.mingsoft.co.resolver.MSLocaleResolver;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.IOException;

/**
 * 读取国际化配置，传递给freemker
 * 历史修订：basic重写 因为需要添加国际化所以重写  2021/12/28
 *          新增onApplicationEvent方法,监听spring容器启动
 *          把MSLocaleResolver从webconfig移动到这里
 */
@Configuration
public class I18NConfig implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    protected freemarker.template.Configuration configuration;

    public void init() throws IOException, TemplateException {
        String defaultLocal = ConfigUtil.getString("国际化配置", "defaultLanguage");
        String localLanguage = ConfigUtil.getString("国际化配置", "internationalLanguage");

        configuration.setSharedVariable("localDefault", defaultLocal);
        configuration.setSharedVariable("localLanguage", localLanguage.split(","));
    }

    /**
     * 国际化配置拦截
     * @return
     */
    @Bean
    public MSLocaleResolver localeResolver(){
        return new MSLocaleResolver();
    }

    /**
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event){
        if(event.getApplicationContext().getParent() == null) {
            try {
                init();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TemplateException e) {
                e.printStackTrace();
            }
        }
    }
}
