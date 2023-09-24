/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.component;

/**
 * see spider_task_regular表的meteDate内容
 * metaDate内容的数据结构
 */
public class ParserDefinition {

    /**
     * 名称
     */
    private String name;
    /**
     * 解析类型 regular/xpath/css/default
     */
    private String type;
    /**
     * 解析表达式
     */
    private String expression;
    /**
     * 映射类型
     */
    private String fieldType;
    /**
     * 对应数据库字段
     */
    private String filed;

    /**
     * 是否匹配栏目页
     */
    private String isMatch;

    /**
     * 是否为html内容
     */
    private String isHtml;

    /**
     * 字段格式化
     */
    private String fieldFormat;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getFiled() {
        return filed;
    }

    public void setFiled(String filed) {
        this.filed = filed;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getIsMatch() {
        return isMatch;
    }

    public void setIsMatch(String isMatch) {
        this.isMatch = isMatch;
    }

    public String getFieldFormat() {
        return fieldFormat;
    }

    public void setFieldFormat(String fieldFormat) {
        this.fieldFormat = fieldFormat;
    }

    public String getIsHtml() {
        return isHtml;
    }

    public void setIsHtml(String isHtml) {
        this.isHtml = isHtml;
    }
}
