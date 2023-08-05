/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.elasticsearch.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.elasticsearch.bean.ESBaseBean;
import net.mingsoft.elasticsearch.bean.ESSearchBean;
import net.mingsoft.elasticsearch.constant.Const;
import net.mingsoft.elasticsearch.constant.e.SearchMethodEnum;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 增加精确搜索注释,增加高斯函数使用注释
 */
public interface IESService {

    /**
     * 多条件搜索
     * @param docCls 文档对象类
     * @param fields 搜索字段
     * @param notEquals 不等于字段
     * @param ESSearchBean es分页对象
     * @param highlightConfig 高亮配置，如果为null则不使用高亮
     *  highlightConfig.preTags 高亮前缀如：<span style="color=red">
     *  highlightConfig.postTags 高亮后缀如：</span>
     * @return
     */
    ResultData search(Class<? extends ESBaseBean> docCls,
                                    LinkedHashMap<String, Float> fields,
                                    Map<String, Object> highlightConfig,
                                    Map<String, Object> notEquals,
                                    ESSearchBean ESSearchBean,
                                    String searchMethod);

    /**
     * 创建es mapping
     * @param cls doc 对象
     */
    void createDoc(Class<?> cls);

    /**
     * 判断索引是否存在
     * @param cls doc 对象
     * @return boolean
     */
    boolean existDoc(Class<?> cls);



    /**
     * 联想词搜索
     * @param suggestField 联想词字段
     * @param suggestValue 词
     * @param cls 文档模型
     * @return
     */
    ResultData suggestInput(String suggestField, String suggestValue,Class<? extends ESBaseBean> cls);

    /**
     * 热词搜索
     * @param cls 文档模型
     * @return
     */
    ResultData hotSearch(String field, Date startTime,Date endTime,int limit,Class<? extends ESBaseBean> cls);

    /**
     * 聚合搜索
     * @param cls 文档模型
     * @return
     */
    ResultData aggregationSearch(Map<String, Object> field,Class<? extends ESBaseBean> cls);



    /**
     * 用于组装高亮结果集
     * @param hits 搜索返回结果
     * @param fields 高亮字段数组
     * @return 结果集
     */
    List<Map<String, Object>> highlightSearchHits (SearchHits<? extends ESBaseBean> hits, String[] fields);

    /**
     * 根据前端传递的索引名称返回对应的bean
     */
    Class<? extends ESBaseBean> getBeanClass();
}

