/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CacheUtil {

    @Autowired
    private CacheManager cacheManager;

    private static CacheManager cm;

    @PostConstruct
    public void init() {
        cm = cacheManager;
    }

    public static void put(String cacheName, String key, Object value) {
        Cache cache = cm.getCache(cacheName);
        cache.put(key, value);
    }

    public static Object get(String cacheName, String key) {
        Cache cache = cm.getCache(cacheName);
        if (cache == null) {
            return null;
        }
        return cache.get(key).get();
    }

    public static <T> T get(String cacheName, String key, Class<T> clazz) {
        Cache cache = cm.getCache(cacheName);
        return cache.get(key, clazz);
    }

    public static void evict(String cacheName, String key) {
        Cache cache = cm.getCache(cacheName);
        cache.evict(key);
    }

}
