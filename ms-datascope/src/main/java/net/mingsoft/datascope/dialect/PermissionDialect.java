/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.datascope.dialect;

import org.apache.ibatis.mapping.BoundSql;

import java.sql.SQLException;

/**
 * 简单根据databaseId区分数据库使用对应的分页拦截
 */
public class PermissionDialect {

    public static void handel(BoundSql boundSql,String databaseId) throws SQLException {
        MySqlPermissionDialect.handel(boundSql);
    }

}
