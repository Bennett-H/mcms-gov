/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.elasticsearch.aop;

import cn.hutool.core.collection.CollUtil;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.elasticsearch.bean.ESBaseBean;
import net.mingsoft.elasticsearch.bean.ESSearchBean;
import net.mingsoft.elasticsearch.constant.Const;
import net.mingsoft.elasticsearch.service.IESService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 保存搜索热词的aop
 */
@Component("esHotKeyWordAop")
@Aspect
public class ESHotKeyWordAop extends BaseAop {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private IESService esService;

    //新建索引
    @Pointcut("execution(* net.mingsoft.elasticsearch.action.ESSearchAction.createDoc(..)) ")
    public void create() {
    }

    //删除索引
    @Pointcut("execution(* net.mingsoft.elasticsearch.action.ESSearchAction.deleteDoc(..))")
    public void delete() {
    }

    //保存搜索关键词
    @Pointcut("execution(* net.mingsoft.elasticsearch.service.IESService.search(..))")
    public void search() {
    }


    /**
     * 创建热词索引
     *
     * @param joinPoint
     * @param result
     */
    @AfterReturning(value = "create()", returning = "result")
    public void create(JoinPoint joinPoint, ResultData result) {
        if (!(result.getCode() == 200)) {
            return;
        }
        Class<?> beanClass = esService.getBeanClass();
        org.springframework.data.elasticsearch.annotations.Document annotation = beanClass.getAnnotation(org.springframework.data.elasticsearch.annotations.Document.class);
        if (annotation == null || StringUtils.isBlank(annotation.indexName())) {
            return;
        }
        String indexName = annotation.indexName() + "-hot";
        IndexOperations index = elasticsearchRestTemplate.indexOps(IndexCoordinates.of(indexName));
        index.create();
        Document mapping = index.createMapping(beanClass);
        /**
         * 添加热词字段
         */
        LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) mapping.get("properties");
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            LinkedHashMap<String, Object> valueMap = (LinkedHashMap<String, Object>) entry.getValue();
            if (!("text".equalsIgnoreCase(valueMap.get("type").toString()) || "keyword".equalsIgnoreCase(valueMap.get("type").toString()))) {
                continue;
            }
            LinkedHashMap<String, Object> fields = new LinkedHashMap<>();

            LinkedHashMap<String, Object> hot = new LinkedHashMap<>();
            hot.put("type", "keyword");
            fields.put("hot", hot);

            valueMap.put("fields", fields);
        }
        LinkedHashMap<String, Object> createTime = new LinkedHashMap<>();
        createTime.put("type", "date");
        createTime.put("format", "yyyy-MM-dd HH:mm:ss");
        properties.put("createTime", createTime);
        mapping.put("properties", properties);
        index.putMapping(mapping);

    }


    /**
     * 删除热词索引
     *
     * @param joinPoint
     * @param result
     */
    @AfterReturning(value = "delete()", returning = "result")
    public void delete(JoinPoint joinPoint, ResultData result) {
        if (!(result.getCode() == 200)) {
            return;
        }
        Class<?> beanClass = esService.getBeanClass();
        org.springframework.data.elasticsearch.annotations.Document annotation = beanClass.getAnnotation(org.springframework.data.elasticsearch.annotations.Document.class);
        if (annotation == null || StringUtils.isBlank(annotation.indexName())) {
            return;
        }
        String indexName = annotation.indexName() + "-hot";
        IndexOperations index = elasticsearchRestTemplate.indexOps(IndexCoordinates.of(indexName));
        try {
            index.delete();
        } catch (DataAccessResourceFailureException e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存查询关键词
     *
     * @param joinPoint
     */
    @AfterReturning(value = "search()", returning = "result")
    public void search(JoinPoint joinPoint, ResultData result) {
        EUListBean euListBean = (EUListBean) result.get("data");
        if (euListBean == null || CollUtil.isEmpty(euListBean.getRows())) {
            return;
        }
        Class<? extends ESBaseBean> docCls = getType(joinPoint, Class.class);
        ESSearchBean searchBean = getType(joinPoint, ESSearchBean.class);
        Map<String, Float> fields = getType(joinPoint, LinkedHashMap.class);

        if (CollUtil.isEmpty(fields)){
            return;
        }
        Set<String> fieldSet = fields.keySet();


        org.springframework.data.elasticsearch.annotations.Document annotation = docCls.getAnnotation(org.springframework.data.elasticsearch.annotations.Document.class);
        if (searchBean == null || StringUtils.isBlank(searchBean.getKeyword()) || annotation == null || StringUtils.isBlank(annotation.indexName())) {
            return;
        }
        String indexName = annotation.indexName() + "-hot";

        try {
            Object bean = docCls.newInstance();
            Field[] declaredFields = bean.getClass().getSuperclass().getDeclaredFields();
            for (Field field : declaredFields) {
                //记录访问权限
                boolean flag = field.isAccessible();
                //修改成员属性为可以访问
                field.setAccessible(true);

                String name = field.getName();
                if (fieldSet.contains(name)) {
                    field.set(bean, searchBean.getKeyword());
                }

                //恢复访问权限
                field.setAccessible(flag);
            }

            Field createTime = bean.getClass().getSuperclass().getDeclaredField("createTime");
            //记录访问权限
            boolean flag = createTime.isAccessible();
            //修改成员属性为可以访问
            createTime.setAccessible(true);

            createTime.set(bean, new Date());

            createTime.setAccessible(flag);

            elasticsearchRestTemplate.save(bean, IndexCoordinates.of(indexName));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
