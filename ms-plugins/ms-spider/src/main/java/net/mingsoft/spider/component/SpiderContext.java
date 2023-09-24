/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.component;

import net.mingsoft.spider.component.paser.AbstractParser;
import net.mingsoft.spider.component.paser.DefaultParser;
import net.mingsoft.spider.component.process.DefaultProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采集上下文 {@link DefaultProcessor}
 */
public class SpiderContext {

    /**
     * 任务主键
     */
    private String taskId;

    /**
     * 规则主键
     */
    private String regularId;

    /**
     * 字符编码
     */
    private String charset;

    /**
     * 线程数
     */
    private Integer threadNum;

    /**
     * url表达式
     */
    private String urlExpression;

    /**
     * 内容区域表达式
     */
    private String areaExpression;

    /**
     * 静态文件下载路径
     */
    private String staticFolder;

    /**
     * 扩展参数
     */
    private Map<String,Object> parameterMap = new HashMap<>();

    public String getStaticFolder() {
        return staticFolder;
    }

    public void setStaticFolder(String staticFolder) {
        this.staticFolder = staticFolder;
    }

    /**
     * 起始链接
     */
    private List<String> links;

    public SpiderContext(String taskId, String regularId,List<String> links, String urlExpression, String areaExpression, List<AbstractParser> filedParsers) {
        this.taskId = taskId;
        this.regularId = regularId;
        this.urlExpression = urlExpression;
        this.areaExpression = areaExpression;
        this.filedParsers = filedParsers;
        this.links = links;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    /**
     * 字段解析器
     */
    private List<AbstractParser> filedParsers;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRegularId() {
        return regularId;
    }

    public void setRegularId(String regularId) {
        this.regularId = regularId;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getUrlExpression() {
        return urlExpression;
    }

    public void setUrlExpression(String urlExpression) {
        this.urlExpression = urlExpression;
    }

    public String getAreaExpression() {
        return areaExpression;
    }

    public void setAreaExpression(String areaExpression) {
        this.areaExpression = areaExpression;
    }

    public List<AbstractParser> getFiledParsers() {
        return filedParsers;
    }

    public void setFiledParsers(List<AbstractParser> filedParsers) {
        this.filedParsers = filedParsers;
    }

    public Integer getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(Integer threadNum) {
        this.threadNum = threadNum;
    }


    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, Object> parameterMap) {
        this.parameterMap = parameterMap;
    }
}
