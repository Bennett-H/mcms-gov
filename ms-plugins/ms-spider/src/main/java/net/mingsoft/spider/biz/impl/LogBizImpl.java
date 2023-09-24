/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.spider.biz.impl;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.spider.bean.LogBean;
import net.mingsoft.spider.biz.ILogBiz;
import net.mingsoft.spider.dao.ILogDao;
import net.mingsoft.spider.entity.LogEntity;
import net.mingsoft.spider.util.LogBatchImportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.mingsoft.spider.constant.Const.NO;

/**
 * 日志表管理持久化层
 * @author 铭软科技
 * 创建日期：2020-9-10 14:12:40<br/>
 * 历史修订：<br/>
 */
@Service("spiderlogBizImpl")
@Transactional
public class LogBizImpl extends BaseBizImpl<ILogDao, LogEntity> implements ILogBiz {


    @Autowired
    private ILogDao logDao;

    @Autowired
	private LogBatchImportUtil batchImportUtil;

    @Override
    protected IBaseDao getDao() {
        // TODO Auto-generated method stub
        return logDao;
    }


    @Override
    public void batchImport(List<LogBean> logs) {
        //分组
        Map<String, List<LogBean>> collect = logs.stream().collect(Collectors.groupingBy(LogBean::getImportTable));
        collect.forEach((k,v)->{
            batchImportUtil.batchInsert(k,v);
        });
    }
}
