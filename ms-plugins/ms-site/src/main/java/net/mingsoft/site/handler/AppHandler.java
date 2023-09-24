/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.site.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mdiy.biz.IConfigBiz;
import net.mingsoft.mdiy.entity.ConfigEntity;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AppHandler implements TenantLineHandler {
    @Autowired
    IConfigBiz configBiz;

    private Map map = null;


    @Override
    public Expression getTenantId() {
        //分页插件处理,会被执行两次,一次查询数量，一次查询结果，当查询到了数量之后当前线程变量就会被赋值，再次startPage就会丢失数量，所以判断拿到了数量就不再startPage
        Page<Object> localPage = PageHelper.getLocalPage();
        if (localPage != null && localPage.getTotal() == 0) {
            PageHelper.clearPage();
        }
        int appId = BasicUtil.getApp().getAppId();
        if (localPage != null && localPage.getTotal() == 0) {
            PageHelper.startPage(localPage.getPageNum(), localPage.getPageSize(), localPage.isCount());
            PageHelper.orderBy(localPage.getOrderBy());
        }
        return new LongValue(appId);
    }

    @Override
    public String getTenantIdColumn() {
        return "app_id";
    }

    @Override
    public boolean ignoreTable(String tableName) {
        if (this.map == null) {
            this.map = getConfigMap("站群配置");
        }
        if (CollUtil.isEmpty(this.map)) {
            return true;
        }
        Object table = map.get("siteTables");
        boolean siteEnable = false;
        if (table != null) {
            siteEnable = new Boolean(map.get("siteEnable").toString());
        }
        if (!siteEnable) {
            return true;
        }
        String[] tables = null;
        if (table != null) {
            tables = table.toString().split(",");
        }

        return ArrayUtil.isEmpty(tables) || !ArrayUtil.containsIgnoreCase(tables, tableName);
    }

    /**
     * 获取configName完整配置数据，通过一次性获取所有配置，避免重复传递 configName
     *
     * @param configName 配置名称  对应自定义配置列表上的 配置名称 字段
     * @return map
     */
    public Map getConfigMap(String configName) {
        this.map = new HashMap();
        if (StringUtils.isEmpty(configName) || StringUtils.isEmpty(configName)) {
            return null;
        }
        //根据配置名称获取data
        ConfigEntity configEntity = new ConfigEntity();
        configEntity.setConfigName(configName);
        configEntity = configBiz.getOne(new QueryWrapper<>(configEntity));
        if (configEntity == null || StringUtils.isEmpty(configEntity.getConfigData())) {
            return null;
        }
        this.map = JSONUtil.toBean(configEntity.getConfigData(), HashMap.class);

        return this.map;
    }
}
