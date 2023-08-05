/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */










package net.mingsoft.cms.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.injector.methods.SelectById;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.cms.entity.CategoryEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * 分类持久层
 * @author 铭飞开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
@Component("cmsCategoryDao")
public interface ICategoryDao extends IBaseDao<CategoryEntity> {

    /**
     * 查询当前分类下面的所有子分类
     * @param category 必须存在categoryId categoryParentId
     * @return
     */
    public List<CategoryEntity> queryChildren(CategoryEntity category);

    @InterceptorIgnore(tenantLine = "true")
    CategoryEntity getEntityById(String id);


    /**
     * 查询栏目列表忽略多租户
     * @param ids 查询指定的栏目ids
     * @return 栏目列表集合
     */
    @InterceptorIgnore(tenantLine = "true")
    List<CategoryEntity> queryCategoryIgnoreSite(@Param("ids") String[] ids);;

}
