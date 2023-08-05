/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.cache;

import net.mingsoft.basic.cache.BaseCache;
import net.mingsoft.mdiy.entity.ConfigEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "config")
public class ConfigCache extends BaseCache<ConfigEntity> {

    public static final String CACHE_NAME="config";


    @Cacheable(key = "#key", unless = "#result == null")
    @Override
    public ConfigEntity get(String key) {
        return null;
    }

    @Override
    public void flush() {
        super.flush(CACHE_NAME);
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
    public ConfigEntity saveOrUpdate(String key, ConfigEntity configEntity) {
        return configEntity;
    }
}
