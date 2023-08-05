/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.gov.interceptor;

import net.mingsoft.config.MSProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ip拦截器配置类
 * @author by 铭飞开源团队
 * @Description TODO
 * @date 2022/1/6 15:44
 */
@Configuration
public class LoginInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(MSProperties.manager.path + "/updatePassword.do",MSProperties.manager.path + "/basic/manager/info.do",MSProperties.manager.path +"/password.do",MSProperties.manager.path +"/gov/code/**",MSProperties.manager.path + MSProperties.manager.loginPath)
                .addPathPatterns("/"+MSProperties.manager.path+"/**");
    }

}
