/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.biz.impl;

import net.mingsoft.spider.biz.ITableBiz;
import net.mingsoft.spider.dao.ITableDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableBizImpl implements ITableBiz{

    @Autowired
    private ITableDao tableDao;

    @Override
    public List<String> queryDBTables(){
        return tableDao.queryDBTables();
    }

    @Override
    public List<String> queryDMTables() {
        return tableDao.queryDMTables();
    }

    @Override
    public List<String> queryFiledByTableName(String tableName) {
        return tableDao.queryDBColumnsByTable(tableName);
    }

    @Override
    public List<String> queryDMFiledByTableName(String tableName) {
        return tableDao.queryDMColumnsByTableName(tableName);
    }
}
