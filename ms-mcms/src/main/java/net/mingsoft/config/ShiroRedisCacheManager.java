/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */
package net.mingsoft.config;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * @Author csp
 * @create 2022/4/12 10:47
 * @Description 实现缓存管理manager，所有缓存从redis中取数据
 */
@Component
public class ShiroRedisCacheManager implements CacheManager {
    /**
     * 注入自己的redisCache
     */
    @Resource
    private Cache shiroRedisCache;

    /**
     * 覆写方法
     */
    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return shiroRedisCache;
    }
}