/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.attention.dao;

import net.mingsoft.attention.bean.CollectionBean;
import net.mingsoft.attention.entity.CollectionEntity;
import net.mingsoft.attention.entity.CollectionLogEntity;
import net.mingsoft.base.dao.IBaseDao;

import java.util.List;

/**
 * 关注持久层
 * @author 铭飞开发团队
 * 创建日期：2022-1-21 16:28:31<br/>
 * 历史修订：<br/>
 */
public interface ICollectionLogDao extends IBaseDao<CollectionLogEntity> {

    /**
     * 批量查询业务关注或者点赞总数
     * @param dataIds dataId集合
     * @param dataType 业务类型
     * @param peopleId 用户编号
     * @return CollectionBean集合
     */
    List<CollectionBean> queryCollectionCount(List<String> dataIds, String dataType, String peopleId);

}
