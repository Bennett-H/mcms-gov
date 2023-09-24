/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 查询当前数据库所有表持久层
 * @author 铭软科技
 * 创建日期：2020-9-9 15:55:47<br/>
 * 历史修订：<br/>
 */
public interface ITableDao {

    /**
     * 查询数据中所有表
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    public List<String> queryDBTables();

    /**
     * 查询达梦数据库所有表
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    public List<String> queryDMTables();

    /**
     * 查询数据中指定表的所有字段
     * @param tableName 数据表
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    public List<String> queryDBColumnsByTable(@Param("tableName") String tableName);

    /**
     * 查询达梦数据中指定表的所有字段
     * @param tableName 数据表
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    public List<String> queryDMColumnsByTableName(@Param("tableName") String tableName);
}
