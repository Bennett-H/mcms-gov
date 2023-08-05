/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */










package net.mingsoft.cms.biz;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.cms.entity.CategoryEntity;

import java.util.List;


/**
 * 分类业务
 * @author 铭飞开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
public interface ICategoryBiz extends IBaseBiz<CategoryEntity> {

    /**
     * 查询当前分类下的所有子分类,包含自身
     * @param category 通过setId指定栏目id
     * @return
     */
    List<CategoryEntity> queryChildren(CategoryEntity category);

    void saveEntity(CategoryEntity entity);

    /**更新父级及子集
     * @param entity
     */
    void updateEntity(CategoryEntity entity);

    /**只更新自身
     * @param entity
     */
    void update(CategoryEntity entity);

    void delete(String categoryId);

    void copyCategory(CategoryEntity entity);

    CategoryEntity getEntityById(String id);

    /**
     * 查询分类,忽略站群
     */
    /**
     * 查询栏目列表忽略多租户
     * @return 栏目列表集合
     */
    List<CategoryEntity> queryCategoryIgnoreSite();

    /**
     * 查询栏目列表忽略多租户
     * @param ids 查询指定的栏目ids
     * @return 栏目列表集合
     */
    List<CategoryEntity> queryCategoryIgnoreSite(String[] ids);

    /**
     * 强转栏目类型
     * @param categoryEntity 栏目实体
     * @param targetCategoryType 目标栏目类型
     */
    void changeType(CategoryEntity categoryEntity,String targetCategoryType);
}
