/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.bean;

import net.mingsoft.datascope.bean.DataBean;

import java.util.List;
import java.util.Map;

/**
 * 栏目数据权限
 * @author elims
 * 创建日期：2021-5-26 21:34:32<br/>
 * 历史修订：增加栏目相关字段,增加是否选中字段<br/>
 */
public class CategoryDataBean extends DataBean {

    /**
     * 分类主键
     */
    private String categoryId;

    /**
     * 父分类主键
     */
    private String categoryCategoryId;
    /**
     * 栏目名称
     */
    private String categoryTitle;

    /**
     * 栏目管理属性
     */
    private String categoryType;

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    /**
     * true 叶节点
     */
    private boolean leaf;

    /**
     * 栏目是否选中
     */
    private boolean check;

    /**
     * 菜单功能集合
     */
    private List<Map<String,Object>> dataIdModelList;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCategoryId() {
        return categoryCategoryId;
    }

    public void setCategoryCategoryId(String categoryCategoryId) {
        this.categoryCategoryId = categoryCategoryId;
    }

    public List<Map<String, Object>> getDataIdModelList() {
        return dataIdModelList;
    }

    public void setDataIdModelList(List<Map<String, Object>> dataIdModelList) {
        this.dataIdModelList = dataIdModelList;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }
}
