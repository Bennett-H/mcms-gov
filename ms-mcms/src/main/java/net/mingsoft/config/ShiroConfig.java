/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.basic.filter.ShiroLoginFilter;
import net.mingsoft.basic.realm.BaseAuthRealm;
import net.mingsoft.co.realm.ManagerAuthRealm;
import net.mingsoft.basic.strategy.ILoginStrategy;
import net.mingsoft.basic.strategy.IModelStrategy;
import net.mingsoft.co.filter.SingleSessionControlFilter;
import net.mingsoft.co.listener.ManagerOnlineListener;
import net.mingsoft.co.strategy.CoManagerModelStrategy;
import net.mingsoft.co.strategy.ManagerLoginStrategy;
import net.mingsoft.gov.listener.ShiroSessionListener;
import net.mingsoft.gov.realm.ManagerLoginMD5CredentialsMatcher;
import net.mingsoft.gov.realm.ManagerLoginSM4CredentialsMatcher;
import net.mingsoft.mdiy.biz.IConfigBiz;
import net.mingsoft.mdiy.entity.ConfigEntity;
import net.sf.ehcache.CacheManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 政务版本登录
 * @author Administrator
 * @version 创建日期：2020/11/18 18:12<br/>
 * 历史修订：<br/>
 * 20210712  managerLoginCredentialsMatcher 增加安全配置选项、managerLoginCredentialsMatcher增加国密
 * 20210712 customRealm 修改形参数 SimpleCredentialsMatcher
 * 2022/1/13 DefaultWebSessionManager 添加session监听
 * 2022-1-21 DefaultWebSessionManager 设置session过期扫描时间间隔
 * 2022-2-22 customRealm 站点身份认证通过自定义配置决定
 */
@Configuration
public class ShiroConfig {

    // 系统启动或停止时防止ehcache缓存被清除
    @PostConstruct
    private void init() {
        System.setProperty(CacheManager.ENABLE_SHUTDOWN_HOOK_PROPERTY, "true");
    }
    // 系统启动或停止时防止ehcache缓存被清除
    @PreDestroy
    private void destroy() {
        CacheManager.getInstance().shutdown();
    }

    @Resource
    MSProperties msProperties;

    @Resource
    IConfigBiz configBiz;

    // 集群环境下使用redis共享session用
    @Resource
    RedisSessionDao redisSessionDao;

    // 集群环境下使用redis共享session用
    @Resource
    ShiroRedisCacheManager shiroRedisCacheManager;

    /**
     * 开启Shiro的注解(如@RequiresRoles , @RequiresPermissions),需借助SspringAOP扫描使用Sshiro注解的类，并在必要时进行安全逻辑验证
     * 配置以下两个bean(Defaul tAdvisorAutoProxyCreator和uthorizat ionAttributeSourceAdvisor)即可实现此功能
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 开启shiro aop注解支持
     * 使用代理方式;所以需要开启代码支持
     *
     * @param securityManager
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(
            @Autowired(required = false) DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setProxyTargetClass(true);
        return autoProxyCreator;
    }

    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager, SingleSessionControlFilter singleSessionControlFilter) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // setLoginUrl 如果不设置值，默认会自动寻找Web工程根目录下的"/login.jsp"页面 或 "/login" 映射
        shiroFilterFactoryBean.setLoginUrl(msProperties.getManager().path + msProperties.getManager().loginPath);
        // 设置无权限时跳转的 url;
        shiroFilterFactoryBean.setUnauthorizedUrl(msProperties.getManager().path + "/404.do");
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        // 并发登入拦截注入 自定义拦截器
        filters.put("single-session", singleSessionControlFilter);
        filters.put("authc",new ShiroLoginFilter());

        // 设置拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 游客，开发权限
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/html/**", "anon");
        // 开放登录接口
        filterChainDefinitionMap.put(msProperties.getManager().path + msProperties.getManager().loginPath, "anon");
        filterChainDefinitionMap.put(msProperties.getManager().path + "/checkLogin.do", "anon");
        // 其余接口一律拦截
        // 主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截
        filterChainDefinitionMap.put(msProperties.getManager().path + "/**", "authc,single-session");


        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 注入 securityManager
     */
    @Bean
    public SecurityManager securityManager(BaseAuthRealm authRealm, EhCacheManager ehCacheManager, DefaultWebSessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(sessionManager);
        securityManager.setCacheManager(ehCacheManager);
        //集群环境下使用redis共享session用下行代码
//        securityManager.setCacheManager(shiroRedisCacheManager);
        //cookie管理配置对象
        Map<String,String> map = getConfigMap("安全设置");
        // 配置为禁用时不设置rememberme
        if (CollUtil.isNotEmpty(map) && !Boolean.parseBoolean(map.get("rememberMeEnable"))){
            securityManager.setRememberMeManager(rememberMeManager());
        }
        // 设置realm
        securityManager.setRealm(authRealm);
        return securityManager;
    }

    /**
     * cookie对象
     * @return
     */
    public SimpleCookie rememberMeCookie() {
        // 设置cookie名称，对应login.html页面的<input type="checkbox" name="rememberMe"/>
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        // 设置cookie的过期时间，单位为秒，这里为一天
        cookie.setMaxAge(86400);
        return cookie;
    }

    /**
     * cookie管理对象
     * @return
     */
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        // rememberMe cookie加密的密钥
        String shiroKey = msProperties.getShiroKey();
        cookieRememberMeManager.setCipherKey(shiroKey.getBytes());
        return cookieRememberMeManager;
    }


    @Bean
    public MemorySessionDAO getMemorySessionDAO() {
        return new MemorySessionDAO();
    }

    @Bean
    public SimpleCookie getSimpleCookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName(msProperties.getCookieName());
        return simpleCookie;

    }

    /**
     * 解决springboot下 shiro使用ehcache和@cacheable冲突的处理
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheManager ehCacheCacheManager() {
        return CacheManager.create();
    }

    @Bean
    public EhCacheManager ehCacheManager(CacheManager cacheManager) {
        EhCacheManager ehCacheManager = new EhCacheManager();
        System.setProperty(CacheManager.ENABLE_SHUTDOWN_HOOK_PROPERTY, "true");
        ehCacheManager.setCacheManager(cacheManager);
        return ehCacheManager;
    }

    @Bean
    public DefaultWebSessionManager defaultWebSessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        //添加session监听
        HashSet<SessionListener> sessionListeners = new HashSet<>();
        sessionListeners.add(new ManagerOnlineListener());
        sessionManager.setSessionListeners(sessionListeners);
        sessionManager.setSessionDAO(getMemorySessionDAO());
        //集群环境下使用redis共享session用下行代码
//        sessionManager.setSessionDAO(redisSessionDao);
        sessionManager.setSessionIdCookie(getSimpleCookie());
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        //设置session过期扫描时间间隔,单位为毫秒
        sessionManager.setSessionValidationInterval(1000 * 60);
        return sessionManager;
    }


    /**
     * 并发登录控制
     *
     * @return
     */
    @Bean
    public SingleSessionControlFilter singleSessionControlFilter(EhCacheManager cacheManager,DefaultWebSessionManager defaultWebSessionManager) {
        SingleSessionControlFilter singleSessionControlFilter = new SingleSessionControlFilter();
        //用于根据会话ID，获取会话进行踢出操作的；
        singleSessionControlFilter.setSessionManager(defaultWebSessionManager);
        // 系统清空在线管理员缓存,防止登录时遇到上次系统登录缓存问题
        cacheManager.getCache("shiro-kickout-session").clear();
        //使用cacheManager获取相应的cache来缓存用户登录的会话；用于保存用户—会话之间的关系的；
        singleSessionControlFilter.setCacheManager(cacheManager);
        //集群环境下使用redis共享session用下行代码
//        singleSessionControlFilter.setCacheManager(shiroRedisCacheManager);
        //是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；
        singleSessionControlFilter.setKickAfter(false);
        //被踢出后重定向到的地址；
        singleSessionControlFilter.setLoginPath(msProperties.getManager().path + msProperties.getManager().loginPath);
        return singleSessionControlFilter;
    }

    /**
     * 配置密码比较器
     *
     * @return
     */
    @Bean
    public SimpleCredentialsMatcher managerLoginCredentialsMatcher(@Autowired(required = false) IConfigBiz configBiz) {
        ConfigEntity configEntity = new ConfigEntity();
        configEntity.setConfigName("安全设置");
        configEntity = configBiz.getOne(new QueryWrapper(configEntity));
        HashMap map = null;
        if (configEntity != null) {
             map = JSONUtil.toBean(configEntity.getConfigData(), HashMap.class);
        }


        if(String.valueOf(map.get("algorithm")).equalsIgnoreCase("MD5")) {
            ManagerLoginMD5CredentialsMatcher managerLoginCredentialsMatcher = new ManagerLoginMD5CredentialsMatcher();
        //如果密码加密,可以打开下面配置
        //加密算法的名称
            managerLoginCredentialsMatcher.setHashAlgorithmName(String.valueOf(map.get("algorithm")));
        //配置加密的次数
            managerLoginCredentialsMatcher.setHashIterations(Integer.valueOf(map.get("hashIterations")+""));
        //是否存储为16进制
        managerLoginCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return managerLoginCredentialsMatcher;
        } else {
            return new ManagerLoginSM4CredentialsMatcher();
        }
    }

    /**
     * 自定义身份认证 realm;
     * <p>
     * 必须写这个类，并加上 @Bean 注解，目的是注入 CustomRealm， 否则会影响 CustomRealm类 中其他类的依赖注入
     * @return ManagerAuthRealm
     */
    @Bean
    public ManagerAuthRealm customRealm(SimpleCredentialsMatcher managerLoginCredentialsMatcher) {
        ManagerAuthRealm managerAuthRealm = new ManagerAuthRealm();
        managerAuthRealm.setCredentialsMatcher(managerLoginCredentialsMatcher);
        return managerAuthRealm;
    }

    /**
     * 站点身份认证 realm;
     * <p>
     * 必须写这个类，并加上 @Bean 注解，目的是注入 CustomRealm， 否则会影响 CustomRealm类 中其他类的依赖注入
     * @return siteManagerAuthRealm
     */
//    @Bean
//    public BaseAuthRealm customRealm(SimpleCredentialsMatcher managerLoginCredentialsMatcher) {
//        Map<String,String> map = getConfigMap("站群配置");
//        //若开启了站群则用站群的身份认证,否则用自定义身份认证
//        if (CollUtil.isNotEmpty(map) && "true".equals(map.get("siteEnable"))){
//            SiteManagerAuthRealm siteManagerAuthRealm = new SiteManagerAuthRealm();
//            siteManagerAuthRealm.setCredentialsMatcher(managerLoginCredentialsMatcher);
//            return siteManagerAuthRealm;
//        }
//        ManagerAuthRealm managerAuthRealm = new ManagerAuthRealm();
//        managerAuthRealm.setCredentialsMatcher(managerLoginCredentialsMatcher);
//        return managerAuthRealm;
//    }

    /**
     * 配置session监听
     * @return
     */
    @Bean("sessionListener")
    public ShiroSessionListener sessionListener() {
        ShiroSessionListener sessionListener = new ShiroSessionListener();
        return sessionListener;
    }

    /**
     * 管理员菜单策略
     *
     * @return
     */
    @Bean
    public IModelStrategy modelStrategy() {
        // 开源菜单策略
        //return  new ManagerModelStrategy();
        // 企业多角色菜单策略
        return  new CoManagerModelStrategy();
    }

    /**
     * 管理登录策略
     *
     * @return
     */
    @Bean
    public ILoginStrategy loginStrategy() {
        //企业登录策略
        return new ManagerLoginStrategy();
    }

    /**
     * 获取configName完整配置数据，通过一次性获取所有配置，避免重复传递 configName
     *
     * @param configName 配置名称  对应自定义配置列表上的 配置名称 字段
     * @return map
     */
    private Map getConfigMap(String configName) {
        Map map = new HashMap();
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
        map = JSONUtil.toBean(configEntity.getConfigData(), HashMap.class);

        return map;
    }

}
