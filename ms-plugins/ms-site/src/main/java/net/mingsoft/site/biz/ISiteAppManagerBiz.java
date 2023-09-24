/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.site.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.site.entity.SiteAppManagerEntity;

import java.util.List;
import java.util.Map;

/**
 * 管理员站点关联表业务
 * @author 铭飞科技
 * 创建日期：2022-1-4 10:22:11<br/>
 * 历史修订：添加分发文章方法 2022-1-7
 */
public interface ISiteAppManagerBiz extends IBaseBiz<SiteAppManagerEntity> {


    /**
     * 根据appId获取对应的分类
     * @param appId
     * @return
     */
    List<Map<String, Object>> getCategoryByAppId(String appId);

    /**
     * 根据categoryId和contentId分发文章
     * @param categoryId
     * @param contentId
     */
    String distribution(String categoryId, String contentId);

   /**
     * 创建站点时同时初始化站点数据
     * @param app App实体
     */
    void saveSiteAndMDiyModel(AppEntity app);

    /**
     * 根据AppId删除其自带创建的字典和角色以防止脏数据产生
     * @param ids AppId数组
     */
    void deleteByAppIds(List<String> ids);

    /**
     * 给对应表增加AppId列
     * @param tableName 表名
     */
    void initAppId(String tableName);

    /**
     * 删除对应表AppID列
     * @param tableName 表名
     */
    void removeAppId(String tableName);

}
