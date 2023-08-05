/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.basic.cache;

import net.mingsoft.basic.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.util.List;

/**
 * 基础缓存,
 *
 * @author 铭飞团队
 * @version 版本号：<br/>
 * 创建日期：2016年6月2日<br/>
 * 历史修订：2022-04-14 gov 重写,添加redis缓存方案
 */
public abstract class BaseCache<T> {

    @Autowired
    protected CacheManager cacheManager;


    /**
     * 保存时候需要指定key
     * @param t
     * @return
     */
    public abstract T saveOrUpdate(String key,T t);

    public abstract void delete(String key);

    public abstract void deleteAll();

    public abstract T get(String key);

    public abstract void flush();
    public void flush(String cacheName) {
        CacheWrapper wrapper = new CacheWrapper(SpringUtil.getBean(CacheManager.class).getCache(cacheName));
        wrapper.flush();
    }

    public abstract int count();

    public int count(String cacheName) {
        CacheWrapper wrapper = new CacheWrapper(SpringUtil.getBean(CacheManager.class).getCache(cacheName));
        return wrapper.size();
    }

    public abstract List list();

    /**
     *
     * @param cacheName
     * @return
     */
    public List list(String cacheName) {
        CacheWrapper wrapper = new CacheWrapper(SpringUtil.getBean(CacheManager.class).getCache(cacheName));
        return wrapper.list(cacheManager,cacheName);
    }

}
