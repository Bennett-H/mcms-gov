/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.elasticsearch.action.web;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.constant.Const;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.elasticsearch.action.BaseAction;
import net.mingsoft.elasticsearch.bean.ESSearchBean;
import net.mingsoft.elasticsearch.constant.e.SearchMethodEnum;
import net.mingsoft.elasticsearch.service.IESService;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;


import java.util.*;
import java.util.stream.Collectors;

/**
 * es搜索接口
 *
 * @author 铭飞开发团队
 * 创建日期：2020/12/29 9:53<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"前端-全文检索接口"})
@Controller("webESSearchAction")
@RequestMapping("/es")
public class ESSearchAction extends BaseAction {

    @Autowired
    private IESService esService;


    /**
     * 通用搜索接口,保留搜索字段keyword、order、orderBy、pageNo、pageSize,其余字段根据实际业务调整
     *
     * @param keyword 关键字
     * @return 内容
     */
    @ApiOperation(value = "通用搜索接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "docName", value = "ES索引名称(填写值:自定义字典-ES索引-字典名称)", required = true, paramType = "query"),
            @ApiImplicitParam(name = "keyword", value = "搜索关键字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "order", value = "排序方式", required = false, paramType = "query"),
            @ApiImplicitParam(name = "orderBy", value = "排序的字段", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "页数", required = false, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "页数大小", required = false, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "fields", value = "搜索关键字字段,值输入ES索引字段值,多个用逗号隔开 例:content,title", required = false, paramType = "query"),
            @ApiImplicitParam(name = "rangeField", value = "范围字段,需要传开始范围和结束范围才能生效,可以对时间进行范围筛选,值填写对应es索引的索引字段", required = false, paramType = "query"),
            @ApiImplicitParam(name = "start", value = "开始范围 时间格式:yyyy-MM-dd HH:mm:ss 例:2021-08-06 12:25:36", required = false, paramType = "query"),
            @ApiImplicitParam(name = "end", value = "结束范围 时间格式:yyyy-MM-dd HH:mm:ss 例:2021-08-06 12:25:36", required = false, paramType = "query"),
            @ApiImplicitParam(name = "searchMethod", value = "搜索方式(传值参考SearchMethodEnum)", required = false, paramType = "query"),
    })
    @PostMapping("/search")
    @ResponseBody
    public ResultData search(String keyword) {
        //是否开启es
        if (!BooleanUtil.toBoolean(ConfigUtil.getString("ES搜索配置", "isEnable"))){
            LOG.debug("es is closed");
            return ResultData.build().error(this.getResString("error.es.close"));
        }
        //关键词不能为空
        if (StringUtils.isBlank(keyword)){
            return ResultData.build().error(getResString("err.empty",this.getResString("keyword")));
        }
        ESSearchBean esSearchBean = new ESSearchBean();
        esSearchBean.setKeyword(keyword);

        // 设置排序
        String order = BasicUtil.getString("order", "desc");
        String orderBy = BasicUtil.getString("orderBy", "createTime");
        esSearchBean.setOrder(order);
        esSearchBean.setOrderBy(orderBy);
        // 排除条件
        String noflag = BasicUtil.getString("noflag");
        Map<String, Object> notEquals = new HashMap<>();
        if (StringUtils.isNotBlank(noflag)) {
            List<String> collect = Arrays.stream(noflag.split(",")).collect(Collectors.toList());
            // es flag字段
            notEquals.put("flag", collect);
        }
        // 过滤条件
        String filter = BasicUtil.getString("filter");
        Map<Object,Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(filter)) {
            try {
                map = JSONUtil.toBean(filter, Map.class);
            } catch (Exception e) {
                LOG.debug("过滤条件转json失败");
                e.printStackTrace();
            }
        }

        //设置区分站点
        if (ConfigUtil.getBoolean("站群配置", "siteEnable") && BasicUtil.getSession(Const.APP)!=null){
            //esSearchBean
            AppEntity appEntity = (AppEntity) BasicUtil.getSession(Const.APP);
            map.put("appId",appEntity.getId());
            esSearchBean.setFilterWhere(map);
        }
        esSearchBean.setFilterWhere(map);
        //设置分页
        Integer pageNo = BasicUtil.getInt("pageNo", 1);
        Integer pageSize = BasicUtil.getInt("pageSize", 10);
        esSearchBean.setPageNo(pageNo);
        esSearchBean.setPageSize(pageSize);

        // 获取范围
        String rangeFields = BasicUtil.getString("rangeFields");
        if (StringUtils.isNotBlank(rangeFields)){
            List<Map> fieldList = new ArrayList<>();
            try {
                fieldList = JSONUtil.toList(rangeFields, Map.class);
            } catch (Exception e) {
                LOG.debug("范围查询转json失败");
                e.printStackTrace();
            }
            esSearchBean.setRangeFields(fieldList);
        }

        //设置搜索字段
        String fields = BasicUtil.getString("fields");
        LinkedHashMap<String, Float> searchFields = new LinkedHashMap<>();

        //设置搜索字段，数值f标识用于权重排序,一般多字段搜索使用,数值越大该字段的权重越大,当searchMethod等于geo_function_search生效
        if (StringUtils.isNotBlank(fields)) {
            String[] fieldList = fields.split(",");
            // 权重字段 越靠后权重越大
            float weight = 5f;
            for (String field : fieldList) {
                if (StringUtils.isBlank(field)) {
                    return ResultData.build().error("fields非法参数!");
                }
                searchFields.put(field, weight);
                weight += 5f;
            }
        }else {
            // 默认设置搜索标题
            searchFields.put("title", 10f);
        }

        // 设置搜索方式,默认使用分词搜索
        String searchMethod = BasicUtil.getString("searchMethod", SearchMethodEnum.MULTI_MATCH_SEARCH.toString());

        return esService.search(esService.getBeanClass(), searchFields, null,
                notEquals, esSearchBean, searchMethod);

    }

    @ApiOperation(value = "联想搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "docName", value = "ES索引名称(填写值:自定义字典-ES索引-字典名称)", required = true, paramType = "query"),
            @ApiImplicitParam(name = "field", value = "搜索字段,对应ES索引内的字段", required = true, paramType = "query"),
            @ApiImplicitParam(name = "keyword", value = "搜索关键字", required = true, paramType = "query"),
    })
    @PostMapping("/suggestSearch")
    @ResponseBody
    public ResultData suggestSearch(@ApiIgnore String keyword, @ApiIgnore String field) {
        //是否开启es
        if (!BooleanUtil.toBoolean(ConfigUtil.getString("ES搜索配置", "isEnable"))){
            LOG.debug("es is closed");
            return ResultData.build().error(this.getResString("error.es.close"));
        }
        return esService.suggestInput(field, keyword, esService.getBeanClass());
    }


    @ApiOperation(value = "热词搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "docName", value = "ES索引名称(填写值:自定义字典-ES索引-字典名称)", required = true, paramType = "query"),
            @ApiImplicitParam(name = "field", value = "搜索字段,对应ES索引内的字段", required = true, paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间 格式:yyyy-MM-dd HH:mm:ss 例:2021-08-06 12:25:36", dataType = "date", required = false, paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间 格式:yyyy-MM-dd HH:mm:ss 例:2021-08-06 12:25:36", dataType = "date", required = false, paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "热词数量,用于控制热词返回的数量,默认不填为10", dataType = "int", required = false, paramType = "query"),
    })
    @PostMapping("/hotSearch")
    @ResponseBody
    public ResultData hotSearch(@ApiIgnore String field,
                                @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        Date startTime,
                                @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        Date endTime) {
        //是否开启es
        if (!BooleanUtil.toBoolean(ConfigUtil.getString("ES搜索配置", "isEnable"))){
            LOG.debug("es is closed");
            return ResultData.build().error(this.getResString("error.es.close"));
        }
        int limit = BasicUtil.getInt("limit", 10);
        return esService.hotSearch(field, startTime, endTime, limit, esService.getBeanClass());
    }


    /**
     *
     * @return 聚合内容
     */
    @ApiOperation(value = "聚合搜索接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "docName", value = "ES索引名称(填写值:自定义字典-ES索引-字典名称)", required = true, paramType = "query"),
            @ApiImplicitParam(name = "field", value = "聚合字段,对应ES索引内的字段,默认根据栏目id", required = false, paramType = "query"),

    })
    @PostMapping("/aggregationSearch")
    @ResponseBody
    public ResultData aggregationSearch() {
        //是否开启es
        if (!BooleanUtil.toBoolean(ConfigUtil.getString("ES搜索配置", "isEnable"))){
            LOG.debug("es is closed");
            return ResultData.build().error(this.getResString("error.es.close"));
        }
        Map<String, Object> fields = new HashMap<>();
        fields.put("field", BasicUtil.getString("field", "typeId"));
        return esService.aggregationSearch(fields, esService.getBeanClass());
    }

}
