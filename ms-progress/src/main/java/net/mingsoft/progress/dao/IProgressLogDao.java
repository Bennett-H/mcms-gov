/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.dao;

import net.mingsoft.base.dao.IBaseDao;

import java.util.*;

import net.mingsoft.progress.entity.ProgressLogEntity;

/**
 * 进度日志持久层
 *
 * @author 铭飞科技
 * 创建日期：2020-5-28 15:12:02<br/>
 * 历史修订：<br/>
 */
public interface IProgressLogDao extends IBaseDao<ProgressLogEntity> {

    /**
     * 根据方案和节点id获取进度日志的dataId集合
     * @param schemeName  方案名称
     * @param progressIds 进度节点ids  非必填值
     * @param plStatus    进度日志节点状态
     * @return 业务数据id集合
     */
    public List<String> queryDataIdBySchemeNameAndNodeNames(String schemeName, List progressIds, String plStatus);

    /**
     * 获取进度总数
     * @param progressLogEntity 进度日志实体
     * @return 总进度数
     */
    Integer getSumProgress(ProgressLogEntity progressLogEntity);


}
