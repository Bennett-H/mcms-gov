/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.site.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.site.entity.SiteAppManagerEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 管理员站点关联表持久层
 * @author 铭飞科技
 * 创建日期：2022-1-4 10:22:11<br/>
 * 历史修订：<br/>
 */
public interface ISiteAppManagerDao extends IBaseDao<SiteAppManagerEntity> {
    @InterceptorIgnore(tenantLine = "true")
    void insertContentByMap(@Param(value="map") Map<String,Object> map, @Param(value="table_name") String tableName);

    /**
     * 给对应表增加AppId列
     * @param tableName 表名
     */
    void initAppId(@Param(value="table_name") String tableName);

    /**
     * 删除对应表AppID列
     * @param tableName 表名
     */
    void removeAppId(@Param(value="table_name") String tableName);

}
