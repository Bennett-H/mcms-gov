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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "content")
public class ContentCache extends BaseCache<Map> {

    public static final String CACHE_NAME="content";


    @Cacheable(key ="#key", unless = "#result == null")
    @Override
    public Map get(String key) {
        return null;
    }
    @Override
    public int count() {
        return super.count(CACHE_NAME);
    }

    @Override
    public List list() {
        return super.list(CACHE_NAME);
    }

    /**
     * 此方法用于获取站群的数据
     * @return
     */
    public List listByAppId(int appId) {
        List<Map<String, Object>> list = super.list(CACHE_NAME);
        return list.stream().filter(map -> map.get("appId").toString().equals(String.valueOf(appId))).collect(Collectors.toList());
    }

    @CacheEvict(key ="#key")
    public void delete(String key) {
    }

    /**
     * 此方法会删除所有缓存，请谨慎使用
     */
    @CacheEvict(allEntries = true)
    public void deleteAll() {
    }

    @CachePut(key = "#key")
    public Map saveOrUpdate(String key,Map content) {
        return content;
    }

    @Override
    public void flush() {
        super.flush(CACHE_NAME);
    }

}
