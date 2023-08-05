/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.config;

import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author by 铭飞开源团队
 * @Description TODO
 * @date 2020/1/10 11:21
 */
@Configuration
public class DruidConfig {

    /*
     * 解决druid 日志报错：discard long time none received connection:xxx
     * */
    static {
        System.setProperty("druid.mysql.usePingMethod","false");
    }


    /**
     * druid监控 配置URI拦截策略
     */
    @Bean
    @ConditionalOnProperty(prefix="spring",name = "datasource.druid.stat-view-servlet.enabled", havingValue = "true")
    public FilterRegistrationBean druidStatFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        // 添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");
        // 添加不需要忽略的格式信息.
        filterRegistrationBean.addInitParameter("exclusions",
                "/static/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid,/druid/*");
        // 用于session监控页面的用户名显示 需要登录后主动将username注入到session里
        filterRegistrationBean.addInitParameter("principalSessionName", "username");
        return filterRegistrationBean;
    }

    /**
     * druid数据库连接池监控
     */
    @Bean
    @ConditionalOnProperty(prefix="spring",name = "datasource.druid.stat-view-servlet.enabled", havingValue = "true")
    public DruidStatInterceptor druidStatInterceptor() {
        return new DruidStatInterceptor();
    }

    @Bean
    @ConditionalOnProperty(prefix="spring",name = "datasource.druid.stat-view-servlet.enabled", havingValue = "true")
    public JdkRegexpMethodPointcut druidStatPointcut() {
        JdkRegexpMethodPointcut druidStatPointcut = new JdkRegexpMethodPointcut();
        String patterns = "net.mingsoft.*.biz.*";
        // 可以set多个
        druidStatPointcut.setPatterns(patterns);
        return druidStatPointcut;
    }

    /**
     * druid 为druidStatPointcut添加拦截
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix="spring",name = "datasource.druid.stat-view-servlet.enabled", havingValue = "true")
    public Advisor druidStatAdvisor() {
        return new DefaultPointcutAdvisor(druidStatPointcut(), druidStatInterceptor());
    }


}
