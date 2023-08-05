/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.cms.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.cms.bean.CategoryBean;
import net.mingsoft.cms.bean.ContentBean;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.mdiy.entity.ModelEntity;

import java.util.List;
import java.util.Map;


/**
 * 文章业务
 * @author 铭飞开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：<br/>
 * 重写co
 * 2021-6-23 增加待审文章业务<br/>
 * 2021-10-15 增加回收站方法<br/>
 */
public interface IContentBiz extends IBaseBiz<ContentEntity> {



    /**
     * 根据文章属性查询
     * @param contentBean
     * @return
     */
    List<CategoryBean> queryIdsByCategoryIdForParser(ContentBean contentBean);
    /**
     * 根据文章属性查询,不包括单篇
     * @param contentBean
     * @return
     */
    List<CategoryBean> queryIdsByCategoryIdForParserAndNotCover(ContentBean contentBean);

    /**
     * 查询文章id列表,用于获取上一篇下一篇
     */
    List<CategoryBean> queryIdsByCategoryId(ContentBean contentBean);

    /**
     * 用于查询静态化标识文章,忽略站群,手动使用appID
     */
    List<ContentBean> queryContentIgnoreTenantLine(ContentBean contentBean);

    int getSearchCount(ModelEntity contentModel, List diyList, Map whereMap, int appId, String categoryIds);

    /**
     * 查询逻辑删除的文章
     */
    List<ContentBean> listForRecycle(ContentEntity content);

    /**
     * 彻底删除文章
     * @param contentIds 文章ID列表
     */
    void completeDelete(List<String> contentIds);

    void reduction(List<String> ids);

    /**
     * 根据待审文章ID获取待审文章Bean列表
     * @param contentTitle 文章标题
     * @param ids 待审文章ID数组
     * @param plStatus 审批状态
     * @param progressStatus 文章审批状态,用于筛选
     * @return 文章Bean数组
     */
    List<ContentBean> auditList(String contentTitle,List<String> ids, String plStatus, String progressStatus);


    /**
     * 根据解析标签arclist的sql获取list
     * @return
     */
    List list(Map map);
}
