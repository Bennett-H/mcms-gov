/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.cache;

import net.mingsoft.basic.cache.BaseCache;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "generater-progress")
public class GeneraterProgressCache extends BaseCache<List> {

    public static final String CACHE_NAME = "generater-progress";

    @Cacheable(key = "#key", unless = "#result == null")
    @Override
    public List get(String key) {
        return null;
    }

    @Cacheable(key = "#key")
    public int getInt(String key) {
        return 0;
    }

    @Override
    public int count() {
        return super.count(CACHE_NAME);
    }

    @Override
    public List list() {
        return super.list(CACHE_NAME);
    }

    @CacheEvict(key = "#key")
    public void delete(String key) {
    }

    @CacheEvict(allEntries = true)
    public void deleteAll() {
    }

    @CachePut(key = "#key")
    public List saveOrUpdate(String key, List progress) {
        super.flush(CACHE_NAME);
        return progress;
    }

    @CachePut(key = "#key")
    public int saveOrUpdate(String key, int count) {
        super.flush(CACHE_NAME);
        return count;
    }

    @Cacheable(key = "#key",sync = true)
    public List save(String key, List progress) {
        return progress;
    }

    @Override
    public void flush() {
        super.flush(CACHE_NAME);
    }

}
