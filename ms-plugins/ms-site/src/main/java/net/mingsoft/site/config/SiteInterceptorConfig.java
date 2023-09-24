/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.site.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

/**
 * 站群拦截器
 * @author by 铭飞开源团队
 * @Description TODO
 * @date 2020/5/23 9:44
 */
@Configuration
public class SiteInterceptorConfig implements WebMvcConfigurer {

    @Value("${ms.manager.path}")
    private String managerPath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 排除登录接口,及静态资源
        registry.addInterceptor(new net.mingsoft.site.interceptor.AppInterceptor()).excludePathPatterns(
                managerPath + "/login.do",
                "/error",
                "/favicon.ico",
                "/code.do",
                "/mdiy/config/get.do",
                "/gov/publicKey/getPublicKey.do",
                "/static/**",
                "/temp/**",
                "/template/**",
                "/upload/**",
                "/mweixin/portal**",
                "/miniapp/miniApp/**",
                "/miniapp/portal**",
                "/**/*.html"
        );
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusAppHandlerInterceptor(@Autowired(required = false) DataSource dataSource, @Autowired(required = false) net.mingsoft.site.handler.AppHandler appHandler) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //多租户
        if (appHandler != null) {
            interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(appHandler));
        }
        return interceptor;
    }
}
