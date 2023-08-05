/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.datascope.utils;


import net.mingsoft.datascope.bean.DataScopeBean;

/**
 * 数据权限控制方法，通常在BasicUtil.start()调用后执行DataScopeUtil.start()方法
 *
 * @author 铭软开发团队
 * 创建日期：2020-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
public class DataScopeUtil {

    /**
     * 定义ThreadLocal,存储DataScopeBean对象
     */
    private static final ThreadLocal<DataScopeBean> LOCAL_SCOPE = new ThreadLocal<DataScopeBean>();

    public static DataScopeBean getLocalScope() {
        return LOCAL_SCOPE.get();
    }
    /**
     * 开启数据源权限,在执行第一条sql语句会被消耗，注意处理因异常导致没有被消耗的情况，最好紧接查询语句之上
     * @param createBy 创建人 用于过滤创建者可以看到自己的新增的数据
     */
    public static void start(String createBy) {
        LOCAL_SCOPE.set(new DataScopeBean(createBy));
    }

    /**
     * 开启数据源权限,在执行第一条sql语句会被消耗，注意处理因异常导致没有被消耗的情况，最好紧接查询语句之上
     * @param createBy 创建人 用于过滤创建者可以看到自己的新增的数据
     * @param ignoreManager 是否忽略超级管理员，true：忽略,false 不忽略
     */
    public static void start(String createBy,boolean ignoreManager) {
        LOCAL_SCOPE.set(new DataScopeBean(createBy,ignoreManager));
    }
    /**
     * 开启数据源权限,在执行第一条sql语句会被消耗，注意处理因异常导致没有被消耗的情况，最好紧接查询语句之上
     * @param createBy 创建人 用于过滤创建者可以看到自己的新增的数据
     * @param targetId 目标绑定用户
     * @param type 业务类型
     */
    public static void start(String createBy, String targetId,String type) {
        LOCAL_SCOPE.set(new DataScopeBean(createBy,targetId,type));
    }

    /**
     * 开启数据源权限,在执行第一条sql语句会被消耗，注意处理因异常导致没有被消耗的情况，最好紧接查询语句之上
     * @param createBy 创建人 用于过滤创建者可以看到自己的新增的数据
     * @param targetId 用户隶属角色或分类id,具体根据配置页面数据决定
     * @param type 业务类型
     * @param ignoreManager 是否忽略超级管理员，true：忽略,false 不忽略
     */
    public static void start(String createBy, String targetId,String type,boolean ignoreManager) {
        LOCAL_SCOPE.set(new DataScopeBean(createBy,targetId,type,ignoreManager));
    }

    /**
     * 开启数据源权限,在执行第一条sql语句会被消耗，注意处理因异常导致没有被消耗的情况，最好紧接查询语句之上
     * 此方法用于多个目标ID时使用
     * @param createBy 创建人 用于过滤创建者可以看到自己的新增的数据
     * @param type 业务类型
     * @param ignoreManager 是否忽略超级管理员，true：忽略,false 不忽略
     * @param targetIds 目标集合
     */
    public static void start(String createBy,String type,boolean ignoreManager, String targetIds) {
        LOCAL_SCOPE.set(new DataScopeBean(createBy,type,ignoreManager, targetIds));
    }

    /**
     * 清理
     */
    public static void clear() {
        LOCAL_SCOPE.remove();
    }

}
