/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.biz;

import java.util.List;

public interface ITableBiz {

    /**
     * 查询指定数据的所有表
     * @return
     */
    public List<String> queryDBTables();

    /**
     * 查询达梦数据库所有表
     * @return
     */
    public List<String> queryDMTables();

    /**
     * 查询指定数据指定表的所有字段
     * @return
     */
    public List<String> queryFiledByTableName(String tableName);


    /**
     * 查询达梦数据库指定数据指定表的所有字段
     * @return 表字段列表
     */
    public List<String> queryDMFiledByTableName(String tableName);
}
