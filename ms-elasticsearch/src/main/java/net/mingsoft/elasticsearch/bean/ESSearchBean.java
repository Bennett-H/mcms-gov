/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.elasticsearch.bean;


import java.util.*;

/**
 * ES搜索bean，提供最基础的搜索条件
 *
 * @author 铭软科技
 * 创建日期：2021-1-6 15:13:32<br/>
 * 历史修订：<br/>
 */
public class ESSearchBean {


    /**
     * 查找字段,需要配合的es库字段
     */
    private String[] searchFields = {};

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 排序
     */
    private String order;

    /**
     * 排序方式
     */
    private String orderBy;


    /**
     * 附加业务条件，例如：分类=1
     */
    private Map filterWhere;

    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * 一页显示数量
     */
    private Integer pageSize;

    /**
     * 范围字段
     * 所需格式：     [
     *                 {
     *                     type: 'date',
     *                     field: 'date',
     *                     start: '2022-09-09 14:06:55',
     *                     end: '2023-02-10 14:06:55'
     *                 }
     *             ]
     */
    private List<Map> rangeFields;




    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Map getFilterWhere() {
        return filterWhere;
    }

    public void setFilterWhere(Map filterWhere) {
        this.filterWhere = filterWhere;
    }

    public String[] getSearchFields() {
        return searchFields;
    }

    public void setSearchFields(String[] searchFields) {
        this.searchFields = searchFields;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public List<Map> getRangeFields() {
        return rangeFields;
    }

    public void setRangeFields(List<Map> rangeFields) {
        this.rangeFields = rangeFields;
    }

}
