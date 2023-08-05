/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.bean;

/**
 * 自定义模型json bean
 *
 * @author elims
 * @version 创建日期：2021/3/25 12:18<br/>
 * 历史修订：<br/>
 */
public class ModelJsonBean {

    /**
     * html代码片段
     */
    private String html;
    /**
     * 字段属性
     */
    private String field;
    /**
     * 模型名称
     */
    private String title;
    /**
     * vue 逻辑部分代码片段
     */
    private String script;
    /**
     * 模型名称
     */
    private String tableName;  
    /**
     * 列表筛选json
     */
    private String searchJson;
    /**
     * 模型sql
     */
    private String sql;

    /**
     * 是否允许前端提交，用作自定义业务是否可以前端提交保存控制
     */
    private boolean isWebSubmit;

    /**
     * 是否开启验证码校验，用作自定义业务前端提交时验证码校验
     */
    private boolean isWebCode;

    public boolean isWebSubmit() {
        return isWebSubmit;
    }

    public void setWebSubmit(boolean webSubmit) {
        isWebSubmit = webSubmit;
    }

    public boolean isWebCode() {
        return isWebCode;
    }

    public void setWebCode(boolean webCode) {
        isWebCode = webCode;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSearchJson() {
        return searchJson;
    }

    public void setSearchJson(String searchJson) {
        this.searchJson = searchJson;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
