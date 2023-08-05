/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.basic.cache;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import net.sf.ehcache.Ehcache;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * SpringCache的包装类,整合ehcache 和 redis 缓存方法
 */
public class CacheWrapper implements Cache {

    private StringRedisTemplate redisTemplate;

    private Cache cache;

    public CacheWrapper(Cache cache) {
        this.cache = cache;
        this.redisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
    }

    @Override
    public String getName() {
        return cache.getName();
    }

    @Override
    public Object getNativeCache() {
        return cache.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object o) {
        return cache.get(o);
    }

    @Override
    public <T> T get(Object o, Class<T> aClass) {
        return cache.get(o,aClass);
    }

    @Override
    public <T> T get(Object o, Callable<T> callable) {
        return cache.get(o,callable);
    }

    @Override
    public void put(Object o, Object o1) {
        cache.put(o, o1);
    }

    @Override
    public void evict(Object o) {
        cache.evict(o);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    /**
     * 获取缓存数量
     * @return
     */
    public Integer size(){
        Object nativeCache = cache.getNativeCache();
        int size = 0;
        if (nativeCache instanceof Ehcache){
            size =  ((Ehcache) nativeCache).getSize();
        }else {
            //查询redis key的数量
            Set<String> keys = redisTemplate.keys(cache.getName() + "::*");
            size = keys.size();
        }
        return size;
    }

    /**
     * 返回全部缓存
     * @param cacheManager
     * @param cacheName
     * @return
     */
    public List list(CacheManager cacheManager,String cacheName){
        Object nativeCache = cache.getNativeCache();
        List list = CollUtil.newArrayList();
        if (nativeCache instanceof Ehcache){ //如果是Ehcache
            ((Ehcache) nativeCache).getKeys().forEach(key ->
                    list.add(cacheManager.getCache(cacheName).get(key).get())
            );
        }else {
            //redis缓存
            Set<String> keys = redisTemplate.keys(cacheName + "::*").stream().map(s -> s.substring(s.lastIndexOf(":") + 1)).collect(Collectors.toSet());
            keys.forEach(key ->
                    list.add(cacheManager.getCache(cacheName).get(key).get())
            );
        }

        return list;
    }

    /**
     * 把缓存刷新到硬盘,只用ehcache会用到
     */
    public void flush() {
        Object nativeCache = cache.getNativeCache();
        if (nativeCache instanceof Ehcache){
            ((Ehcache) nativeCache).flush();
        }
    }
}
