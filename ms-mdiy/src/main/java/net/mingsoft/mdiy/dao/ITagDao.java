/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.dao;

import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mdiy.entity.TagEntity;
import org.apache.ibatis.annotations.CacheNamespace;
import org.mybatis.caches.ehcache.EhcacheCache;

import java.util.List;

/**
 * 标签持久层
 * @author 铭飞开发团队
 * 创建日期：2018-10-24 8:44:34<br/>
 * 历史修订：<br/>
 */
public interface ITagDao extends IBaseDao<TagEntity> {

    @Override
    List<TagEntity> queryAll();

}
