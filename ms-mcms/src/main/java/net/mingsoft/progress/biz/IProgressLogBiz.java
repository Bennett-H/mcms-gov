/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.progress.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.progress.bean.ProgressLogBean;
import net.mingsoft.progress.entity.ProgressLogEntity;

import java.util.List;


/**
 * 进度日志业务
 * @author 铭飞科技
 * 创建日期：2020-5-28 15:12:02<br/>
 * 历史修订：<br/>
 */
public interface IProgressLogBiz extends IBaseBiz<ProgressLogEntity> {

    /**
     * 根据方案和进度节点获取所有审核中的dataIds
     * @param schemeName 方案名称
     * @param progressIds 进度节点id 多个节点逗号隔开
     * @param plStatus 进度日志节点状态   状态为空查询所有日志为null的记录
     * @return
     */
    List<String> queryDataIdBySchemeNameAndNodeNames(String schemeName,List progressIds, String plStatus);

    /**
     * 方案名称和业务编号查询进度日志
     * @param schemeName 方案名称
     * @param dataId 业务编号
     * @return
     */
    ProgressLogEntity getProgressLogBySchemeNameAndDataId(String schemeName,String dataId);

    /**
     * 根据方案名称获取进度的第一条作为进度日志
     * @param schemeName 方案名称
     * @param dataId 业务编号
     * @param createBy 创建人
     * @return
     */
    boolean insertProgressLog(String schemeName, String dataId,String createBy);

    List<ProgressLogBean> queryDataByCategoryId(List<String> categoryIds, String plStatus);

    /**
     * 获取进度总数
     * @param progressLogEntity 进度日志实体
     * @return 总进度数
     */
    Integer getSumProgress(ProgressLogEntity progressLogEntity);

    /**
     * 获取最大进度数
     * @param progressLogEntity 进度日志实体
     * @return 最大进度数
     */
    Integer getMaxProgress(ProgressLogEntity progressLogEntity);

}

