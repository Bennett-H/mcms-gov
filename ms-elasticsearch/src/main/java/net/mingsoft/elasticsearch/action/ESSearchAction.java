/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */










package net.mingsoft.elasticsearch.action;


import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseAction;
import net.mingsoft.elasticsearch.service.IESService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * es搜索接口
 * @author 铭飞开发团队
 * 创建日期：2022/2/10 9:53<br/>
 * 历史修订：把gov的ESSearchAction中部分方法移动到es模块
 */
@Api(tags = {"后端-全文检索接口"})
@Controller("ESSearchAction")
@RequestMapping("/${ms.manager.path}/es")
public class ESSearchAction extends BaseAction {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private IESService esService;



    /**
     * 删除ES索引
     */
    @ApiOperation(value = "删除ES索引接口")
    @PostMapping("/deleteDoc")
    @RequiresPermissions("gov:es:delete")
    @ResponseBody
    public ResultData deleteDoc() {
        // 删除es doc
        IndexOperations index = elasticsearchRestTemplate.indexOps(esService.getBeanClass());
        try {
            if (index.delete()) {
                return ResultData.build().success().msg("成功删除索引!");
            }
            return ResultData.build().error("删除索引失败!");
        }catch (Exception e) {
            e.printStackTrace();
            return ResultData.build().error("未找到当前ES信息,请检查当前ES链接是否正常");
        }
    }

    /**
     * 创建ES索引
     */
    @ApiOperation(value = "创建ES索引接口")
    @PostMapping("/createDoc")
    @RequiresPermissions("gov:es:create")
    @ResponseBody
    public ResultData createDoc() {
        //创建es doc
        try {
            esService.createDoc(esService.getBeanClass());
        }catch (DataAccessResourceFailureException e) {
            e.printStackTrace();
            return ResultData.build().error("未找到当前ES信息,请检查当前ES链接是否正常");
        }catch (UncategorizedElasticsearchException e) {
            e.printStackTrace();
            return ResultData.build().error("未找到当前ES信息,请检查当前ES链接是否正常");
        }
        return ResultData.build().success().msg("创建成功!");
    }

    /**
     * 获取当前ES服务器信息
     */
    @ApiOperation(value = "获取当前ES服务器信息接口")
    @PostMapping("/esInfo")
    @ResponseBody
    public ResultData esInfo() {
        try {
            IndexOperations index = elasticsearchRestTemplate.indexOps(esService.getBeanClass());
            Map<String, Object> setting = index.getSettings();
            Map<String, Object> infoMap = new HashMap<>();
            IndexCoordinates coordinates = elasticsearchRestTemplate.getIndexCoordinatesFor(esService.getBeanClass());
            infoMap.put("indexName", coordinates.getIndexName());
            infoMap.put("indexCreateDate", DateUtil.date(Long.parseLong((String) setting.get("index.creation_date"))).toString());
            infoMap.put("numberOfReplicas", setting.get("index.number_of_replicas"));
            infoMap.put("numberOfShards", setting.get("index.number_of_shards"));
            infoMap.put("storeType", setting.get("index.store.type"));
            long count = elasticsearchRestTemplate.count(new NativeSearchQuery(QueryBuilders.boolQuery()), esService.getBeanClass());
            infoMap.put("count", count);
            return ResultData.build().success(infoMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultData.build().error("无当前es信息");
        }
    }

    /**
     * 获取当前ES服务器Mapping
     */
    @ApiOperation(value = "获取当前ES服务器Mapping接口")
    @PostMapping("/esMappingInfo")
    @ResponseBody
    public ResultData esMappingInfo() {
        IndexOperations index = elasticsearchRestTemplate.indexOps(esService.getBeanClass());
        Map<String, Object> mappings;
        try {
            mappings = index.getMapping();
        }catch (Exception e) {
            e.printStackTrace();
            return ResultData.build().error("未找到当前ES信息,请检查当前ES链接是否正常");
        }

        return ResultData.build().success(mappings);
    }



}
