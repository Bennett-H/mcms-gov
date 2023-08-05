/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.dao;

import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.progress.entity.SchemeEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 进度方案持久层
 * @author 铭飞科技
 * 创建日期：2020-5-28 15:12:02<br/>
 * 历史修订：<br/>
 */
public interface ISchemeDao extends IBaseDao<SchemeEntity> {

    void updateSchemeTableStatus(
            @Param("tableName") String tableName,
            @Param("progressStatus") String progressStatus,
            @Param("updateDate") String updateDate,
            @Param("id") String id);

}
