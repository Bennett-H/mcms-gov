/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.util;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.co.cache.ConfigCache;
import net.mingsoft.mdiy.biz.IConfigBiz;
import net.mingsoft.mdiy.entity.ConfigEntity;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义配置参数获取
 * 2022-01-27 重写mdiy 缓存获取配置不一致所以重写，方法名称、形参等不可修改
 */
public class ConfigUtil {

    /**
     * 返回字符串类型的数据
     * @param configName 配置名称  对应自定义配置列表上的 配置名称 字段
     * @param key 对应代码生成器中的字段名称 注意：名称是驼峰式
     * @return 无匹配返回空
     */
    public static String getString(String configName,String key) {
        Object object = getObject(configName, key);
        if(object==null){
            return "";
        }
        return object.toString();
    }

    /**
     * 返回字符串类型的数据
     * @param configName 配置名称  对应自定义配置列表上的 配置名称 字段
     * @param key 对应代码生成器中的字段名称 注意：名称是驼峰式
     * @param defaultValue 默认值,如果配置中没有值，会返回默认值
     * @return 无匹配返回默认值
     */
    public static String getString(String configName,String key, String defaultValue) {
        Object object = getObject(configName, key);
        if(object==null){
            return defaultValue;
        }
        return object.toString();
    }

    /**
     * 返回布尔类型的数据
     * @param configName 配置名称  对应自定义配置列表上的 配置名称 字段
     * @param key 对应代码生成器中的字段名称 注意：名称是驼峰式
     * @return 无匹配返回FALSE
     */
    public static Boolean getBoolean(String configName,String key) {
        Object object = getObject(configName, key);
        if(object == null){
            return Boolean.FALSE;
        }
        return BooleanUtils.toBoolean(object.toString());
    }

    /**
     * 返回布尔类型的数据
     * @param configName 配置名称  对应自定义配置列表上的 配置名称 字段
     * @param key 对应代码生成器中的字段名称 注意：名称是驼峰式
     * @param defaultValue 默认值,如果配置中没有值，会返回默认值
     * @return 无匹配返回默认值
     */
    public static Boolean getBoolean(String configName,String key, Boolean defaultValue) {
        Object object = getObject(configName, key);
        if(object == null){
            return defaultValue;
        }
        return BooleanUtils.toBoolean(object.toString());
    }



    /**
     * 返回整型类型的数据
     * @param configName 配置名称  对应自定义配置列表上的 配置名称 字段
     * @param key 对应代码生成器中的字段名称 注意：名称是驼峰式
     * @return 无匹配返回0
     */
    public static int getInt(String configName,String key) {
        Object object = getObject(configName, key);
        if(object==null){
            return 0;
        }
        return Integer.parseInt(object.toString());
    }

    /**
     * 返回整型类型的数据
     * @param configName 配置名称  对应自定义配置列表上的 配置名称 字段
     * @param key 对应代码生成器中的字段名称 注意：名称是驼峰式
     * @param defaultValue 默认值,如果配置中没有值，会返回默认值
     * @return 无匹配返回默认值
     */
    public static int getInt(String configName,String key,int defaultValue) {
        Object object = getObject(configName, key);
        if(object==null){
            return defaultValue;
        }
        return Integer.parseInt(object.toString());
    }

    /**
     * 如果不确定返回类型，可以使用 getObject
     * @param configName 配置名称  对应自定义配置列表上的 配置名称 字段
     * @param key 对应代码生成器中的字段名称 注意：名称是驼峰式
     * @return 无匹配返回null
     */
    public static Object getObject(String configName,String key) {
        ConfigEntity configEntity = getEntity(configName);
        if (configEntity == null){
            return null;
        }
        //将data转换成map
        HashMap map = JSONUtil.toBean(configEntity.getConfigData(), HashMap.class);
        if(map!=null){
            return map.get(key);
        }
        return null;
    }

    /**
     * 获取configName完整配置数据，通过一次性获取所有配置，避免重复传递 configName
     * @param configName 配置名称  对应自定义配置列表上的 配置名称 字段
     * @return map
     */
    public static Map getMap(String configName) {
        ConfigEntity configEntity = getEntity(configName);
        if (configEntity == null){
            return new HashMap();
        }
        return JSONUtil.toBean(configEntity.getConfigData(), HashMap.class);
    }

    /**
     * 通过configName查询实体
     * @param configName 配置名称  对应自定义配置列表上的 配置名称 字段
     * @return ConfigEntity
     */
    public static ConfigEntity getEntity(String configName){
        if (StringUtils.isEmpty(configName) || StringUtils.isEmpty(configName)) {
            return null;
        }
        IConfigBiz configBiz = SpringUtil.getBean(IConfigBiz.class);
        //根据配置名称获取data
        ConfigCache configCache = SpringUtil.getBean(ConfigCache.class);
        ConfigEntity configEntity = configCache.get(configName);
        //先从缓存中拿,没有再去查数据库
        if (configEntity == null){
            configEntity = new ConfigEntity();
            configEntity.setConfigName(configName);
            configEntity = configBiz.getOne(new QueryWrapper<>(configEntity));
            configCache.saveOrUpdate(configName,configEntity);
        }
        return configEntity;
    }

    /**
     * 通过configName 删除缓存
     * 方法名称不能更改，底层使用到了这个方法，改名会导致重写不成功找不到方法
     * @param configName 配置名称  对应自定义配置列表上的 配置名称 字段
     */
    public static void removeEntity(String configName){
        if (StringUtils.isNotEmpty(configName)) {
            //根据配置名称获取data
            ConfigCache configCache = SpringUtil.getBean(ConfigCache.class);
            if (configCache.get(configName)!=null){
                configCache.delete(configName);
            }
        }
    }
}

