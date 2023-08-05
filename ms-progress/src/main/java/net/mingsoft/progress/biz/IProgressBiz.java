/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.progress.bean.ProgressBean;
import net.mingsoft.progress.entity.ProgressEntity;

import java.util.List;


/**
 * 进度表业务
 * @author 铭飞科技
 * 创建日期：2020-5-28 15:12:02<br/>
 * 历史修订：<br/>
 */
public interface IProgressBiz extends IBaseBiz<ProgressEntity> {


    /**
     * 根据方案名称查询进度列表
     * 获取的列表已经根据时间排序
     * @param schemeName 方案名称
     * @return
     */
    List<ProgressEntity> queryProgress(String schemeName);

    /**
     * 下一进度节点
     * @param schemeId
     * @param plNodeName
     * @return
     */
    ProgressEntity nextProgress(int schemeId, String plNodeName);

    List<ProgressEntity> queryNotAddLog(ProgressBean progressBean);

    /**
     * 查询子叶节点
     */
    List<ProgressEntity> queryChildren(ProgressEntity progressEntity);

    /**
     * 更新父级及子集
     */
    void updateProgress(ProgressEntity progressEntity);

    /**
     * 添加进度节点
     */
    void saveProgress(ProgressEntity progressEntity);

}

