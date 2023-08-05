/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.datascope.dialect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.github.pagehelper.dialect.helper.MySqlDialect;
import net.mingsoft.basic.constant.e.ManagerAdminEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.datascope.bean.DataScopeBean;
import net.mingsoft.datascope.utils.DataScopeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.RowBounds;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据权限方言，修复权限导致的分页问题
 * @author 铭软开发团队
 * 创建日期：2020-11-28 15:12:32<br/>
 * 历史修订：<br/>
 **/
public class MySqlPermissionDialect extends MySqlDialect {

    @Override
    public String getCountSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey countKey) {

        try {
            DataScopeBean dataScopeObject = DataScopeUtil.getLocalScope();
            if (dataScopeObject != null) {
                handel(boundSql);
                dataScopeObject.setChange(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.getCountSql(ms, boundSql, parameterObject, rowBounds, countKey);
    }

    /**
     * 处理Sql
     *
     * @param boundSql mybatis提供
     * @throws SQLException
     */
    public static void handel(BoundSql boundSql) throws SQLException {
        DataScopeBean dataScopeObject = DataScopeUtil.getLocalScope();
        //获取用户信息，没有登录和超级管理员不过滤，适用于后台
        ManagerEntity managerSession = BasicUtil.getManager();
        if (managerSession != null
                && ManagerAdminEnum.SUPER.toString().equals(managerSession.getManagerAdmin())
                && !dataScopeObject.isIgnoreAdmin()) {
            return;
        }else {
            // 忽略管理员
            //如果没有数据权限目标用户就不进行数据权限过滤
            if (StrUtil.isBlank(dataScopeObject.getCreateBy())) {
                return;
            }
        }
        DataSource dataSource = SpringUtil.getBean(DataSource.class);
        //查询是否存在子查询sql
        String subSql = null;
        List<Entity> configList =  Db.use(dataSource).query("SELECT config_subsql FROM datascope_data_config WHERE config_name=?",dataScopeObject.getType());
        if (CollUtil.isNotEmpty(configList)){
            subSql = configList.get(0).getStr("config_subsql");
        }
        //获取当前业务dao的sql语句
        String sql = boundSql.getSql();
        String buildSqlStr = "SELECT datas__.* FROM ({}) datas__ WHERE create_by='{}' OR id in({})";
        //0表示不回取出任何数据
        String ids = "0";
        // 为多个目标ID时的情况
        if (StrUtil.isNotBlank(dataScopeObject.getTargetIds())) {
            //查询所有目标用户拥有的数据权限
            String selectSql = "select data_id from datascope_data where DATA_TARGET_ID in ({}) AND DATA_TYPE=?";
            selectSql = StrUtil.format(selectSql, dataScopeObject.getTargetIds());
            //若有子查询sql
            if (StringUtils.isNotBlank(subSql)){
                selectSql= StrUtil.format(subSql, selectSql);
            }
            List<Entity> list = Db.use(dataSource).query(selectSql, dataScopeObject.getType());
            List<String> idList = new ArrayList<>();
            for (Entity entity : list) {
                // id不为空
                if (StringUtils.isNotBlank(entity.getStr("data_id"))){
                    idList.add(entity.getStr("data_id"));
                }
            }
            if(CollUtil.isNotEmpty(idList)){
                ids = ArrayUtil.join(idList.toArray(), ",");
            }
        }
        if (StrUtil.isNotBlank(dataScopeObject.getTargetId())) {
            //查询目标用户拥有的数据权限
            String selectSql = "select data_id from datascope_data where DATA_TARGET_ID=? AND DATA_TYPE=?";
            if (StringUtils.isNotBlank(subSql)){
                selectSql= StrUtil.format(subSql, selectSql);
            }
            List<Entity> list = Db.use(dataSource).query(selectSql, dataScopeObject.getTargetId(), dataScopeObject.getType());
            String[] idArray = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                // id不为空
                if (StringUtils.isNotBlank(list.get(i).getStr("data_id"))){
                    idArray[i] = list.get(i).getStr("data_id");
                }
            }
            if(idArray.length>0){
                ids = ArrayUtil.join(idArray, ",");
            }
        }
        //将原始sql组合到数据权限过滤
        buildSqlStr = StrUtil.format(buildSqlStr, sql,dataScopeObject.getCreateBy(), ids);
        ReflectUtil.setFieldValue(boundSql, "sql", buildSqlStr);
    }


}

