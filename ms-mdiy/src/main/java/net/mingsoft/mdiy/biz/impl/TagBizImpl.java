/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.biz.impl;

import net.mingsoft.mdiy.entity.TagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mdiy.biz.ITagBiz;
import net.mingsoft.mdiy.dao.ITagDao;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2018-10-24 8:44:34<br/>
 * 历史修订：<br/>
 */
@Service("tagBizImpl")
@Transactional(rollbackFor = RuntimeException.class)
public class TagBizImpl extends BaseBizImpl<ITagDao, TagEntity> implements ITagBiz {

    @Autowired
    private ITagDao tagDao;

    @Override
    protected IBaseDao getDao() {
        // TODO Auto-generated method stub
        return tagDao;
    }

    @Override
    public List<TagEntity> queryAll() {
        return tagDao.queryAll();
    }
}
