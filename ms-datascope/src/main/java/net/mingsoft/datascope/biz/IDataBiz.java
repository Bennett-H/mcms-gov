/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.datascope.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.datascope.bean.DataBatchBean;
import net.mingsoft.datascope.entity.DataEntity;

import java.util.List;


/**
 * 数据权限业务
 * @author 铭软开发团队
 * 创建日期：2020-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
public interface IDataBiz extends IBaseBiz<DataEntity> {
    /**
     * 根据成员id获取所有权限
     * @param id 成员id
     * @return
     */
    List<String> queryProjectList(String id, String dataType);


    /**
     * 权限绑定
     * @param dataType 类型
     * @param dataTargetId 用户或用户组id
     * @param dataIdList 关联ids
     * @param createBy 创建人
     */
    void saveBatchByDataTargetIdAndDataType(String dataType, String dataTargetId, String createBy,
                                           List<String> dataIdList);

    void saveDataBatch(List<DataBatchBean> dates);

    /**
     *
     * @param dataList
     */
    DataEntity mergeDataBatch(List<DataEntity> dataList);

}
