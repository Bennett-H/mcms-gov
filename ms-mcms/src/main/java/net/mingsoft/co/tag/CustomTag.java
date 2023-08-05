/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.co.tag;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.map.MapWrapper;
import cn.hutool.json.JSONUtil;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.*;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mdiy.biz.ITagBiz;
import net.mingsoft.mdiy.entity.TagEntity;
import net.mingsoft.mdiy.util.ParserUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Clob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: 铭软团队
 * @Description: 自定义标签
 * @Date: Create in 2020/06/23 9:16
 */

public class CustomTag implements TemplateDirectiveModel {


    protected static BeansWrapper build = new BeansWrapperBuilder(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).build();
    /*
     * log4j日志记录
     */
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /**
     * 解析标签用的map
     */
    private Map data;
    /**
     * 标签sql模板
     */
    private TagEntity tagSql;
    /**
     * 传出的变量名
     */
    private String variableName;


    private Map tagParams;

    private TimedCache<String, List> timedCache;


    public CustomTag(TimedCache timedCache, Map data, TagEntity tagSql) {
        if(this.data != null) {
            LOG.error("c --{}",JSONUtil.toJsonStr(this.data));
        }
        this.data = data;
        this.tagSql = tagSql;
        this.variableName = "field";
        this.timedCache = timedCache;
//        LOG.error("CustomTag-tagSql:{}------page:{}----thread:{}", tagSql.getTagName(), JSONUtil.toJsonStr(this.data.get(ParserUtil.PAGE)), Thread.currentThread().getName());
    }

    public Map getTagParams() {
        return this.tagParams;
    }

    @Override
    public  void execute(Environment environment, Map params, TemplateModel[] templateModels,
                        TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        //如果只是获取标签属性
        if (this.data == null && tagSql == null) {
            this.tagParams = params;
            return;
        }


        HashMap<Object, Object> _params = MapUtil.newHashMap();
        //历史变量
        //将外部传入的压入
        _params.putAll(this.data);
//        LOG.error("execute-tagSql:{}------page:{}--thread:{}---params:{}", tagSql.getTagName(), JSONUtil.toJsonStr(this.data.get(ParserUtil.PAGE)), Thread.currentThread().getName(),JSONUtil.toJsonStr(params));
        TemplateModel oldVar = environment.getVariable(variableName);
        //压入了栏目则传入sql模板
        TemplateModel column = environment.getVariable(ParserUtil.COLUMN);


        if (column != null) {
            _params.put(ParserUtil.COLUMN, build.unwrap(column));
        }


        //将标签传入参数逐一的压入
        params.forEach((k, v) -> {
            if (v instanceof TemplateModel) {
                try {
                    _params.put(k, build.unwrap((TemplateModel) v));
                } catch (TemplateModelException e) {
                    e.printStackTrace();
                    LOG.error("转换参数错误 key:{} -{}", k, e);
                }
            }
        });


        ITagBiz tagBiz = SpringUtil.getBean(ITagBiz.class);
        String sql = "";
        try {
             sql = ParserUtil.rendering(_params, tagSql.getTagSql());
        } catch(Exception e) {
            LOG.error("标签{}异常 {} ",tagSql.getTagName(),tagSql.getTagSql());
            e.printStackTrace();
            throw new BusinessException("标签"+tagSql.getTagName()+"异常");
        }



//            LOG.error("------{},{}", sql, JSONUtil.toJsonStr(data.get(net.mingsoft.mdiy.co.util.ParserUtil.PAGE)));


//        LOG.error("c-tag-name:{} c-tag-id:{}",tagSql.getTagName(),tagSql.getId());
        List<Map> _list = null;
        if (timedCache.get(sql) != null) {
            _list = timedCache.get(sql);
        } else {
            try {
                _list = (List) tagBiz.excuteSql(sql);//执行一条查询
                timedCache.put(sql, _list);
            } catch (Exception e) {
                LOG.error("标签{}错误",tagSql.getTagName());
                e.printStackTrace();
            }

        }
        AtomicInteger index = new AtomicInteger(1);
        //渲染标签
        _list.forEach(x -> {
            x.put("index", index.getAndIncrement());
            try {
                //把NClob类型转化成string
                MapWrapper<String, Object> mw = new MapWrapper<>((HashMap<String, Object>) x);
                mw.forEach(y -> {
                    if (y.getValue() instanceof Clob) {
                        y.setValue(StringUtil.clobStr((Clob) y.getValue()));
                    }
                });

                TemplateModel columnModel = build.wrap(x);
                //如果自定义了变量则赋值自定义变量
                if (templateModels.length > 0) {
                    templateModels[0] = columnModel;
                } else {
                    environment.setVariable(variableName, columnModel);
                }

                //压入 以便嵌套使用
                environment.setVariable(ParserUtil.COLUMN, columnModel);
                //渲染
                templateDirectiveBody.render(environment.getOut());
                //渲染完成还原变量
                environment.setVariable(ParserUtil.COLUMN, column);
                environment.setVariable(variableName, oldVar);

            } catch (TemplateModelException e) {
                e.printStackTrace();
            } catch (TemplateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


}
