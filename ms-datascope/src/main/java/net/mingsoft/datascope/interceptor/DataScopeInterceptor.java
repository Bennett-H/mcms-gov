/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.datascope.interceptor;

import net.mingsoft.datascope.bean.DataScopeBean;
import net.mingsoft.datascope.dialect.PermissionDialect;
import net.mingsoft.datascope.utils.DataScopeUtil;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Properties;

/**
 * 数据权限拦截器
 *
 * @author 铭软开发团队
 * 创建日期：2020-11-28 15:12:32<br/>
 * 历史修订：<br/>
 **/
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Component
public class DataScopeInterceptor implements Interceptor {


    @Value("${mybatis-plus.configuration.database-id}")
    private  String databaseId;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            StatementHandler statementHandler = realTarget(invocation.getTarget());
            MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
            MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
            //只过滤搜索
            if (!SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
                return invocation.proceed();
            }
            BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
            DataScopeBean dataScopeObject = DataScopeUtil.getLocalScope();
            if (dataScopeObject != null && !dataScopeObject.isChange()) {
                PermissionDialect.handel(boundSql,databaseId);
            }
        } finally {
            //清理
            DataScopeUtil.clear();
        }
        return invocation.proceed();
    }


    /**
     * 获取真实对象
     *
     * @param target 代理对象
     * @param <T>
     * @return
     */
    private static <T> T realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        }
        return (T) target;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
