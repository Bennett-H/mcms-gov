/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.progress.entity.SchemeEntity;

import java.util.List;


/**
 * 进度方案业务
 * @author 铭飞科技
 * 创建日期：2020-5-28 15:12:02<br/>
 * 历史修订：<br/>
 */
public interface ISchemeBiz extends IBaseBiz<SchemeEntity> {


    /**
     * 更新业务表的状态
     * @param schemeName 方案名
     * @param status 状态名
     * @param dataId 业务表id
     * @return
     */
    boolean updateBusinessTableStatus(String schemeName,String status,String dataId);

    /**
     * 批量删除进度方案及其所有节点
     * @param schemeIds 进度方案id集合
     * @return 删除标记
     */
    boolean deleteBySchemeIds(List<String> schemeIds);
}
