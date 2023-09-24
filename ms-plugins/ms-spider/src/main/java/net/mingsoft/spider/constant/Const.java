/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.constant;

import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * spider定义
 *
 * @author 铭软科技
 * 创建日期：2020-9-12 10:51:17<br/>
 * 历史修订：<br/>
 */
public final class Const {

    /**
     * 资源文件
     */
    public final static String RESOURCES = "net.mingsoft.spider.resources.resources";

    /**
     * 存放爬取图片的文件夹  /upload/1/content/*
     */
    public final static String MODEL = "content";

    /**
     * 匹配类型
     */
    public final static String REGULAR = "regular";
    public final static String XPATH = "xpath";
    public final static String CSS = "css";
    public final static String DEFAULT = "default";


    /**
     * 存放在map中的数据的key
     * see net.mingsoft.spider.component.process.DefaultProcessor
     */

    public final static String IS_HTML = "isHtml";
    public final static String FILED = "filed";
    public final static String FILED_TYPE = "filedType";
    public final static String DATA = "data";
    public final static String SOURCE = "source";

    /**
     * see net.mingsoft.spider.component.process.DefaultProcessor
     * 存放在page.putField中的key, 可以在pipeline 中获取到
     */
    public final static String TASK_ID = "taskId";
    public final static String REGULAR_ID = "regularId";
    public final static String DESTINATION = "destination";
    public final static String STATIC_FOLDER = "staticFolder";

    /**
     * 用于匹配内容区域
     */
    public final static String MIDDLE = "([\\s\\S]*)";

    /**
     * 全局通用
     */
    public final static String YES = "yes";
    public final static String NO = "no";
    public final static String EXCEPTION = "exception";

    /**
     * 匹配img链接的src属性
     */
    public final static Pattern IMAGE_PATTERN = Pattern.compile("<img.*?src=['|\"](?<url>.+?)[\"|'].*?>", Pattern.CASE_INSENSITIVE);

    public final static Pattern RESOURCE_PATTERN = Pattern.compile(".*?(png|gif|bmp|pdf|mp4|avi|jpg|zip)", Pattern.CASE_INSENSITIVE);

    /**
     * 服务器给客户端发送消息的地址 : 目前用于爬虫测试功能
     */
    public final static String SPIDER_PUBLISH_DESTINATION = "/user/spider/publish";

}
