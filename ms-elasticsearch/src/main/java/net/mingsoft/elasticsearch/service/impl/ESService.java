/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.elasticsearch.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.base.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.elasticsearch.bean.ESBaseBean;
import net.mingsoft.elasticsearch.bean.ESSearchBean;
import net.mingsoft.elasticsearch.constant.Const;
import net.mingsoft.elasticsearch.constant.e.SearchMethodEnum;
import net.mingsoft.elasticsearch.service.IESService;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.mdiy.util.DictUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 增加精确搜索注释,增加高斯函数使用注释
 */
@Service
public class ESService implements IESService {
    protected static final Logger LOG = LoggerFactory.getLogger(ESService.class);

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    RestHighLevelClient restHighLevelClient;


    /**
     * 多条件搜索
     *
     * @param docCls          文档对象类
     * @param fields          搜索字段
     * @param notEquals       不等于字段
     * @param esSearchBean    es分页对象
     * @param highlightConfig 高亮配置，如果为null则不使用高亮
     *                        highlightConfig.preTags 高亮前缀如：<span style="color=red">
     *                        highlightConfig.postTags 高亮后缀如：</span>
     * @return
     */
    @Override
    public ResultData search(
            Class<? extends ESBaseBean> docCls,
            LinkedHashMap<String, Float> fields,
            Map<String, Object> highlightConfig,
            Map<String, Object> notEquals,
            ESSearchBean esSearchBean,
            String searchMethod) {
        if (esSearchBean == null) {
            return ResultData.build().error("参数异常!");
        }
        Map<String, String> filterWhere = esSearchBean.getFilterWhere();
        String keyword = esSearchBean.getKeyword();

        //设置搜索内容
        BoolQueryBuilder bqb = new BoolQueryBuilder();

        if (searchMethod.equalsIgnoreCase(SearchMethodEnum.MULTI_MATCH_SEARCH.toString())) {
            // 精确搜索
            Set<String> fieldSets = fields.keySet();
            for (String field : fieldSets) {
                bqb.should(QueryBuilders.matchPhraseQuery(field, keyword).slop(2));
                // 防止filter条件导致should失效
                bqb.minimumShouldMatch(1);
            }
        } else if (searchMethod.equalsIgnoreCase(SearchMethodEnum.GEO_FUNCTION_SEARCH.toString())) {
            // 高斯函数,用于某一数值加权,例如时间范围优先,价格范围优先
            // 原点
            Object origin = ConfigUtil.getString("ES搜索配置", "origin", "now/d");
            // 偏移量(offset)：与原点相差在偏移量之内的值，也可以得到满分
            String offset = ConfigUtil.getString("ES搜索配置", "offSet", "30d");
            // 衰减规模(scale)：当值超出了原点到偏移量折叠范围，他所得的分数开始进行衰减
            String scale = ConfigUtil.getString("ES搜索配置", "scale", "1095d");
            // 衰减值(decay)：该字段可以被接受的值(默认0.5)，相当于一个分阶段，具体效果与衰减的模式有关
            double decay = Double.parseDouble(ConfigUtil.getString("ES搜索配置", "decay", "0.5"));
            // 加权字段
            String weightingField = ConfigUtil.getString("ES搜索配置", "weightingField", "date");
            bqb.must(QueryBuilders.functionScoreQuery(
                    QueryBuilders.multiMatchQuery(keyword).fields(fields),
                    ScoreFunctionBuilders.gaussDecayFunction(weightingField, origin, scale, offset, decay))
            );
        } else if (searchMethod.equalsIgnoreCase(SearchMethodEnum.SHOULD_SEARCH.toString())) {
            // 类似 || 只要有一个条件满足就匹配
            bqb.should(QueryBuilders.multiMatchQuery(keyword).fields(fields));
            // 防止filter条件导致should失效
            bqb.minimumShouldMatch(1);
        } else {
            //全文搜索 类似&&必须全部条件匹配进行返回
            bqb.must(QueryBuilders.multiMatchQuery(keyword).fields(fields));
        }

        // 范围筛选
        List<Map> rangeFields = esSearchBean.getRangeFields();
        if (CollUtil.isNotEmpty(rangeFields)){
            // 循环处理数据
            for (Map rangeField : rangeFields) {
                // 如果一组数据不完整则直接跳过该次循环
                if (null==rangeField.get("field") || null==rangeField.get("start") || null==rangeField.get("end")){
                    continue;
                }
                // 将查询字段及起止值赋值给查询器
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders
                    .rangeQuery(rangeField.get("field").toString())
                    .gte(rangeField.get("start"))
                    .lte(rangeField.get("end"));
                // 如果是时间类型还需要做格式和时区处理
                if (null!=rangeField.get("type") && "date".equalsIgnoreCase(rangeField.get("type").toString())){
                    rangeQueryBuilder = rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss").timeZone("Asia/Shanghai");
                }
                // 将查询器添加至搜索内容
                bqb.must(rangeQueryBuilder);

            }
        }

        //条件过滤
        if (filterWhere != null) {
            //添加条件
            for (String field : filterWhere.keySet()) {
                //老版本会有keyword
//                bqb.filter(QueryBuilders.matchPhraseQuery(field.concat(".keyword"), filterWhere.get(field)));
                bqb.filter(QueryBuilders.termQuery(field, filterWhere.get(field)));
            }
        }
        //不等于条件筛选
        if (CollUtil.isNotEmpty(notEquals)) {
            for (String field : notEquals.keySet()) {

                if (notEquals.get(field) instanceof List) {
                    List list = ((List) notEquals.get(field));
                    list.forEach(o -> {
                        bqb.mustNot(QueryBuilders.multiMatchQuery(o, field));
                    });
                } else {
                    bqb.mustNot(QueryBuilders.multiMatchQuery(notEquals.get(field), field));
                }
            }
        }
        //创建高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        if (highlightConfig != null) {
            // 如果出现高亮搜索结果不全的情况可以把大小调大
            highlightBuilder.fragmentSize(500);
            highlightBuilder.preTags(highlightConfig.get("preTags").toString());//设置前缀
            highlightBuilder.postTags(highlightConfig.get("postTags").toString());//设置后缀
        }
        //添加搜索列和设置高亮字段
        for (String s : fields.keySet()) {
            //添加条件
            highlightBuilder.field(s);//设置高亮字段
        }

        // 防止分页越界
        if (esSearchBean.getPageNo() == null || esSearchBean.getPageNo() < 1) {
            esSearchBean.setPageNo(1);
        }
        if (esSearchBean.getPageNo() == null || esSearchBean.getPageSize() < 1) {
            esSearchBean.setPageSize(1);
        }
        Pageable pageable = PageRequest.of(esSearchBean.getPageNo() - 1, esSearchBean.getPageSize());

        //添加条件
        NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder()
                .withQuery(bqb)
                .withPageable(pageable);

        //添加排序
        if (StringUtils.isNotEmpty(esSearchBean.getOrder())) {
            SortOrder sortOrder = esSearchBean.getOrder().equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            if (searchMethod.equalsIgnoreCase(SearchMethodEnum.GEO_FUNCTION_SEARCH.toString())) {
                // 权重排序使用
                nsqb.withSort(SortBuilders.scoreSort().order(sortOrder));
            }
            nsqb.withSort(SortBuilders.fieldSort(esSearchBean.getOrderBy()).order(sortOrder));
        }

        LOG.debug("es请求参数:\n{} \nSort参数:{}", bqb, Objects.requireNonNull(nsqb.build().getElasticsearchSorts()));

        SearchHits<? extends ESBaseBean> searchHits;
        //判断是否高亮搜索
        if (highlightConfig != null) {
            nsqb.withHighlightBuilder(highlightBuilder);
        }
        // 高亮字段数组
        String[] _fields = fields.keySet().toArray(new String[fields.keySet().toArray().length]);
        try {
            searchHits = elasticsearchRestTemplate.search(nsqb.build(), docCls);
            List<Map<String, Object>> resultList = highlightSearchHits(searchHits, _fields);
            EUListBean euListBean = new EUListBean(resultList, (int) searchHits.getTotalHits());
            return ResultData.build().success().data(euListBean);
        } catch (Exception e) {
            LOG.error("es 库异常");
            e.printStackTrace();
            return ResultData.build().error("参数异常!");
        }
    }

    /**
     * 创建es mapping
     *
     * @param cls doc 对象
     */
    @Override
    public void createDoc(Class<?> cls) {
        IndexOperations index = elasticsearchRestTemplate.indexOps(cls);
        index.create();
        Document mapping = index.createMapping(cls);
        LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) mapping.get("properties");
        /**
         * 给所有text类型字段添加联想词搜索
         */
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            LinkedHashMap<String, Object> valueMap = (LinkedHashMap<String, Object>) entry.getValue();
            if (!("text".equalsIgnoreCase(valueMap.get("type").toString()) || "keyword".equalsIgnoreCase(valueMap.get("type").toString()))) {
                continue;
            }
            LinkedHashMap<String, Object> fields = new LinkedHashMap<>();
            LinkedHashMap<String, Object> suggest = new LinkedHashMap<>();
            /**
             * 此处新增keyword定义text字段内嵌为keyword类型，解决text默认分词导致全称命中搜索结果失败问题
             */
            LinkedHashMap<String, Object> keyword = new LinkedHashMap<>();
            keyword.put("type","keyword");
            keyword.put("ignore_above",256);
            fields.put("keyword", keyword);
            suggest.put("type", "completion");
            suggest.put("analyzer", "ik_max_word");
            fields.put(Const.SUGGEST_FIELD_NAME, suggest);
            valueMap.put("fields", fields);
        }
        mapping.put("properties", properties);
        index.putMapping(mapping);
    }

    @Override
    public boolean existDoc(Class<?> cls) {
        IndexOperations index = elasticsearchRestTemplate.indexOps(cls);
        return index.exists();
    }

    /**
     * 联想词搜索
     *
     * @param suggestField 联想词字段
     * @param suggestValue 词
     * @param cls          文档模型
     * @return
     */
    @Override
    public ResultData suggestInput(String suggestField, String suggestValue, Class<? extends ESBaseBean> cls) {
        IndexCoordinates coordinates = elasticsearchRestTemplate.getIndexCoordinatesFor(cls);
        CompletionSuggestionBuilder completionSuggestionBuilder = SuggestBuilders.completionSuggestion(suggestField + "." + Const.SUGGEST_FIELD_NAME)
                .size(10)
                .skipDuplicates(true).prefix(suggestValue);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("suggest_text", completionSuggestionBuilder);
        Suggest suggest = elasticsearchRestTemplate.suggest(suggestBuilder, coordinates).getSuggest();
        CompletionSuggestion result = suggest.getSuggestion("suggest_text");
        List<String> suggests = result.getEntries().stream().map(x -> x.getOptions().stream().map(y -> y.getText().toString()).collect(Collectors.toList())).findFirst().get();
        return ResultData.build().success().data(suggests);
    }

    @Override
    public ResultData aggregationSearch(Map<String, Object> fields, Class<? extends ESBaseBean> cls) {
        if (CollUtil.isEmpty(fields) || fields.get("field") == null) {
            return ResultData.build().success();
        }
        // 聚合
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders
                .terms("group_by_field")
                .field(fields.get("field").toString());
//               .subAggregation(AggregationBuilders.terms("")); // 子聚合,有需要可以增加
        BoolQueryBuilder bqb = new BoolQueryBuilder();
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(bqb)
                .addAggregation(termsAggregationBuilder);
        SearchHits<? extends ESBaseBean> searchHits = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), cls);
        AggregationsContainer<?> aggregations = searchHits.getAggregations();
        Aggregations _aggregations = (Aggregations) aggregations.aggregations();
        Terms terms =  _aggregations.get("group_by_field");
        List<Map<String, Object>> resultList = new ArrayList<>();
        // 组装数据
        for (Terms.Bucket bucket : terms.getBuckets()) {
            Map<String, Object> map = new HashMap<>();
            map.put(fields.get("field").toString(), bucket.getKeyAsString());
            map.put("count", bucket.getDocCount());
//            map.put("value", bucket.getAggregations());
            resultList.add(map);
        }

        return ResultData.build().success(resultList);
    }

    /**
     * 查询指定索引，时间范围内，指定字段被查询的关键字及总量，倒叙返回
     * @param field 指定字段
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 分页数
     * @param cls 文档模型
     * @return 热词集合
     */
    @Override
    public ResultData hotSearch(String field, Date startTime,Date endTime, int limit, Class<? extends ESBaseBean> cls) {

        org.springframework.data.elasticsearch.annotations.Document annotation = cls.getAnnotation(org.springframework.data.elasticsearch.annotations.Document.class);
        if (annotation == null || StringUtils.isBlank(annotation.indexName())) {
            return ResultData.build().error("索引不存在!");
        }
        String indexName = annotation.indexName() + "-hot";

        SearchRequest searchRequest = new SearchRequest(indexName);
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 分组
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("HOT_COUNT").field(field+"."+"hot");
        //不需要解释
        sourceBuilder.explain(false);
        //不需要原始数据
        sourceBuilder.fetchSource(false);
        //不需要版本号
        sourceBuilder.version(false);
        sourceBuilder.aggregation(aggregationBuilder);

        //模糊查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders
                .boolQuery();
        if (endTime != null && startTime != null){
            //时间范围搜索
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders
                    .rangeQuery("createTime")
                    .gte(DateUtil.date(startTime).toString())
                    .lte(DateUtil.date(endTime).toString())
                    // 这里可以通过部署ES容器去优化掉,ES默认UTC,这里做容错 UTC+8
                    .timeZone("Asia/Shanghai");
            boolQueryBuilder.must(rangeQueryBuilder);
        }
        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println(searchResponse);
            Terms terms = searchResponse.getAggregations().get("HOT_COUNT");
            List<Map<String,Object>> result = new ArrayList<>();
            // 限制数量
            List<Terms.Bucket> buckets = terms.getBuckets().stream().limit(limit).collect(Collectors.toList());
            // 存放热词的关键字及搜索量
            Map<String, Object> hotWordMap = new HashMap<>();
            for (Terms.Bucket bucket : buckets) {
                hotWordMap = new HashMap<>();
                hotWordMap.put("key", bucket.getKeyAsString());
                hotWordMap.put("count",bucket.getDocCount());
                result.add(hotWordMap);
            }
            return ResultData.build().success(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultData.build().success();
    }


    /**
     * 用于组装高亮结果集
     *
     * @param hits   搜索返回结果
     * @param fields 高亮字段数组
     * @return 结果集
     */
    @Override
    public List<Map<String, Object>> highlightSearchHits(SearchHits<? extends ESBaseBean> hits, String[] fields) {
        List<? extends SearchHit<? extends ESBaseBean>> searchHits = hits.getSearchHits();
        List<Map<String, Object>> contents = new ArrayList<>();
        // 组织返回结果高亮
        for (SearchHit<? extends ESBaseBean> searchHit : searchHits) {
            // 将搜索结果转为MAP对象
            Map<String, Object> source = BeanUtil.beanToMap(searchHit.getContent(),false, false);
            for (String filed : fields) {
                //高亮结果集合，注意，是结果集
                List<String> result = searchHit.getHighlightFields().get(filed);
                //将高亮结果替换搜索结果
                if (CollUtil.isNotEmpty(result)) {
                    source.put(filed, StringUtils.strip(ArrayUtil.toString(result), "[]"));
                }
            }
            contents.add(source);
        }
        return contents;
    }

    /**
     * 此方法只允许在控制层调用，
     * 参数： docName 索引名称
     * @return 获取指定索引的bean对象
     */
    @Override
    public Class<? extends ESBaseBean> getBeanClass() {
        String docName = BasicUtil.getString("docName", "文章");
        String dictValue = DictUtil.getDictValue("ES索引", docName);
        if (StringUtils.isBlank(dictValue)) {
            throw new BusinessException("当前索引不存在,请检查字典");
        }
        String beanName = (String) JSONUtil.toBean(dictValue, Map.class).get("name");
        Class<? extends ESBaseBean> clz;
        try {
            clz = (Class<? extends ESBaseBean>) SpringUtil.getBean(beanName).getClass();
        } catch (NoSuchBeanDefinitionException e) {
            throw new BusinessException("当前索引不存在,请检查字典");
        }
        return clz;
    }
}

