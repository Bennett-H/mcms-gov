/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.mdiy.entity.TagEntity;

import java.util.List;

/**
 * 标签业务
 * @author 铭飞开发团队
 * 创建日期：2018-10-24 8:44:34<br/>
 * 历史修订：<br/>
 */
public interface ITagBiz extends IBaseBiz<TagEntity> {

    /**
     * 用于获取缓存,增加效率
     */
    @Override
    List<TagEntity> queryAll();
}
