/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.attention.biz;

import net.mingsoft.attention.bean.CollectionBean;
import net.mingsoft.attention.entity.CollectionLogEntity;
import net.mingsoft.base.biz.IBaseBiz;

import java.util.List;

/**
 * 关注业务
 * @author 铭飞开发团队
 * 创建日期：2022-1-21 16:28:31<br/>
 * 历史修订：<br/>
 */
public interface ICollectionLogBiz extends IBaseBiz<CollectionLogEntity> {


    /**
     * 批量查询业务数据,如业务点赞数或者关注数
     * 如果携带peopleId,会检测该会员是否点赞或者关注某条数据
     * @param dataIds dataId集合数据(必填)
     * @param dataType 业务类型(必填)
     * @param peopleId 用户编号(选填) 但需注意：若peopleId没有值，则是否点赞字段是不生效的，无法用于判断游客状态下点赞情况
     * @return CollectionBean集合
     */
    List<CollectionBean> queryCollectionCount(List<String> dataIds, String dataType, String peopleId);

}
