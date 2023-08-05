/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.cms.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.cms.bean.CategoryBean;
import net.mingsoft.cms.bean.ContentBean;
import net.mingsoft.cms.entity.ContentEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 文章持久层
 * @author 铭飞开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：
 * 重写co
 * 2021-6-23 增加待审文章业务<br/>
 * 2021-10-15 增加回收站方法
 */
public interface IContentDao extends IBaseDao<ContentEntity> {


    /**
     * 查询文章编号集合
     * @contentBean
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    public List<CategoryBean> queryIdsByCategoryIdForParser(ContentBean contentBean);
    /**
     * 查询文章编号集合,不包含单篇
     * @contentBean
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    public List<CategoryBean> queryIdsByCategoryIdForParserAndNotCover(ContentBean contentBean);

    @InterceptorIgnore(tenantLine = "true")
    List<ContentBean> queryContentIgnoreTenantLine(ContentBean contentBean);

    /**
     * 查询文章id列表,用于获取上一篇下一篇,需要忽略站群,appId由后台控制
     */
    @InterceptorIgnore(tenantLine = "true")
    List<CategoryBean> queryIdsByCategoryId(ContentBean contentBean);

    /**
     * 根据查询文章实体总数
     *
     * @param tableName
     *            :自定义生成的表名
     * @param map
     *            key:字段名 value:List 字段的各种判断值 list[0]:是否为自定义字段 list[1]:是否为整形
     *            list[2]:是否是等值查询 list[3]:字段的值
     * @return 文章实体总数
     */
    int getSearchCount(@Param("tableName") String tableName, @Param("diyList") List diyList, @Param("map") Map<String, Object> map,
                       @Param("appId") int appId, @Param("ids") String ids, @Param("progressStatus") String progressStatus);

    /**
     * 分类编号删除文章
     * @param ids
     */
    void deleteEntityByCategoryIds(@Param("ids") String[] ids);

    /**
     * 查询逻辑删除的文章
     */
    List<ContentBean> listForRecycle(ContentEntity content);

    /**
     * 彻底删除文章
     * @param contentIds 文章ID列表
     */
    void completeDelete(@Param("contentIds") List<String> contentIds);

    /**
     * 还原已删除的文章
     * @param ids 文章列表
     * @param updateDate 更新时间，提供给静态化生成使用，考虑适配，从外部传递到xml
     */
    void reduction(@Param("ids") List<String> ids,@Param("updateDate") Date updateDate);


    /**
     * 根据待审文章ID获取待审文章Bean列表
     * @param contentTitle 文章标题
     * @param ids 待审文章ID数组
     * @param plStatus 审批状态
     * @return 文章Bean数组
     */
    List<ContentBean> auditList(@Param("contentTitle") String contentTitle, @Param("ids") List<String> ids, @Param("plStatus") String plStatus, @Param("progressStatus") String progressStatus);

}
