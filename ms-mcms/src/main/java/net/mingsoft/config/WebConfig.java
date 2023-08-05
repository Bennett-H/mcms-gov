/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.spring.stat.BeanTypeAutoProxyCreator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.basic.filter.XSSEscapeFilter;
import net.mingsoft.basic.interceptor.ActionInterceptor;
import net.mingsoft.co.constant.Const;
import net.mingsoft.mdiy.biz.IConfigBiz;
import net.mingsoft.mdiy.entity.ConfigEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 历史修订
 * 2021-12-25: basic重写，修改addResourceHandlers方法，文件配置使用自定义配置管理 -
 * 2021-12-27: 修改addResourceHandlers方法，舍弃uploadMapping配置
 * 2022-14-27: mappingJackson2HttpMessageConverter 单独抽出配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private IConfigBiz configBiz;

    @Autowired(required = false)
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;


    @Bean
    public ActionInterceptor actionInterceptor() {
        return new ActionInterceptor();
    }


//    @Bean
//    public ConfigurationCustomizer configurationCustomizer() {
//        return configuration -> configuration.setUseDeprecatedExecutor(false);
//    }
////     最新版
////     MybatisPlus使用的拦截器分页,如果需要启用额外的方法都需要加上page参数，改动太大，目前不修改
//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
//        interceptor.addInnerInterceptor(paginationInnerInterceptor);
//        return interceptor;
//    }


    /**
     * 增加对rest api鉴权的spring mvc拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 会员拦截器,依赖会员模块时启用
        registry.addInterceptor(new net.mingsoft.people.interceptor.ActionInterceptor(MSProperties.people.loginUrl))
                .addPathPatterns("/people/**");
        // 排除配置
        registry.addInterceptor(actionInterceptor()).excludePathPatterns("/static/**", "/app/**", "/webjars/**",
                "/*.html", "/*.htm");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 上传路径
        String uploadFolderPath = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadPath", MSProperties.upload.path);
        // 上传路径映射 这里的映射不能使用File.separator Windows会存在映射问题
        String uploadMapping = "/" + uploadFolderPath + "/**";
        String template = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadTemplate", MSProperties.upload.template);
        registry.addResourceHandler(uploadMapping).addResourceLocations("/" + uploadFolderPath + "/", "file:" + uploadFolderPath + "/");
        registry.addResourceHandler("/" + template + "/**").addResourceLocations("/" + template + "/", "file:" + template + "/");
        registry.addResourceHandler("/swagger-ui/**").addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
        registry.addResourceHandler("/**/*.html").addResourceLocations("/" + MSProperties.diy.htmlDir + "/", "file:" + MSProperties.diy.htmlDir + "/");
        //三种映射方式 webapp下、当前目录下、jar内
        registry.addResourceHandler("/app/**").addResourceLocations("/app/", "file:app/", "classpath:/app/");
        registry.addResourceHandler("/static/**").addResourceLocations("/static/", "file:static/", "classpath:/static/", "classpath:/META-INF/resources/");
        registry.addResourceHandler("/api/**").addResourceLocations("/api/", "file:api/", "classpath:/api/");

        if (new File(uploadFolderPath).isAbsolute()) {
            //如果指定了绝对路径，上传的文件都映射到uploadMapping下
            registry.addResourceHandler(uploadMapping).addResourceLocations("file:" + uploadFolderPath + "/"
                    //映射其他路径文件
                    //,file:F://images
            );
        }
    }

    /**
     * druid数据库连接池监控
     */
    @Bean
    public BeanTypeAutoProxyCreator beanTypeAutoProxyCreator() {
        BeanTypeAutoProxyCreator beanTypeAutoProxyCreator = new BeanTypeAutoProxyCreator();
        beanTypeAutoProxyCreator.setTargetBeanType(DruidDataSource.class);
        beanTypeAutoProxyCreator.setInterceptorNames("druidStatInterceptor");
        return beanTypeAutoProxyCreator;
    }

    //XSS过滤器
    @Bean
    public FilterRegistrationBean xssFilterRegistration() {

        XSSEscapeFilter xssFilter = new XSSEscapeFilter();
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Map map = this.getMap("XSS过滤器配置");
        registration.setName("XSSFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        Map<String, String> initParameters = new HashMap();
        boolean enable = true;
        if (map != null) {
            //使用自定义配置
            Object xssEnable = map.get("xssEnable");
            Object filterUrl = map.get("filterUrl");
            Object excludeUrl = map.get("excludeUrl");
            if (xssEnable != null) {
                enable = new Boolean(xssEnable.toString());
            }
            //includes : 需要过滤的路径
            if (filterUrl != null && StrUtil.isNotBlank(filterUrl.toString())) {
                xssFilter.includes.addAll(Arrays.asList(filterUrl.toString().split(",")));
            }
            //excludes : 需要排除过滤的路径
            if (excludeUrl != null && StrUtil.isNotBlank(excludeUrl.toString())) {
                xssFilter.excludes.addAll(Arrays.asList(excludeUrl.toString().split(",")));
            }
            //isIncludeRichText主要用于设置富文本内容是否需要过滤
        }
        initParameters.put("isIncludeRichText", "false");
        registration.setInitParameters(initParameters);
        registration.setFilter(xssFilter);
        registration.setEnabled(enable);
        return registration;
    }

    /**
     * RequestContextListener注册
     */
    @Bean
    public ServletListenerRegistrationBean<RequestContextListener> requestContextListenerRegistration() {
        return new ServletListenerRegistrationBean<>(new RequestContextListener());
    }

    /**
     * 设置默认首页
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/msIndex.do");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        WebMvcConfigurer.super.addViewControllers(registry);
    }


    /**
     * 解决com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException 问题，提交实体不存在的字段异常
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // TODO Auto-generated method stub
        converters.add(mappingJackson2HttpMessageConverter);
        WebMvcConfigurer.super.configureMessageConverters(converters);

    }

    @Bean
    public ExecutorService crawlExecutorPool() {
        // 创建线程池
        ExecutorService pool =
                new ThreadPoolExecutor(20, 20,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());
        return pool;
    }


    /**
     * 获取configName完整配置数据，通过一次性获取所有配置，避免重复传递 configName
     *
     * @param configName 配置名称  对应自定义配置列表上的 配置名称 字段
     * @return map
     */
    public Map getMap(String configName) {
        if (StringUtils.isEmpty(configName) || StringUtils.isEmpty(configName)) {
            return null;
        }
        //根据配置名称获取data
        ConfigEntity configEntity = new ConfigEntity();
        configEntity.setConfigName(configName);
        configEntity = configBiz.getOne(new QueryWrapper<>(configEntity));
        if (configEntity == null || StringUtils.isEmpty(configEntity.getConfigData())) {
            return null;
        }
        return JSONUtil.toBean(configEntity.getConfigData(), HashMap.class);
    }


}
