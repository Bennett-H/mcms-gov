/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.config;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author csp
 * @create 2022/4/12 10:47
 * @Description 缓存的实现类 shiro所有的缓存数据，都会存到redis中
 */
@Component
public class ShiroRedisCache<K,V> implements Cache<K,V>{

    /**
     * redis操作类
     */
    @Autowired
    private RedisTemplate<K,V> redisTemplate;

    /**
     * 定义缓存生效时间 为半个小时
     */
    private long expireTime = 1800;

    /**
     * 查询 操作
     */
    @Override
    public V get(K k) throws CacheException {
        return redisTemplate.opsForValue().get(k);
    }

    /**
     * 新增 操作
     */
    @Override
    public V put(K k, V v) throws CacheException {
        redisTemplate.opsForValue().set(k,v,expireTime, TimeUnit.SECONDS);
        return null;
    }

    /**
     * 删除 操作
     */
    @Override
    public V remove(K k) throws CacheException {
        V v = redisTemplate.opsForValue().get(k);
        redisTemplate.opsForValue().getOperations().delete(k);
        return v;
    }

    @Override
    public void clear() throws CacheException {
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }
}