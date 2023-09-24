/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.component.builder;

import java.util.List;

/**
 * 构建url的顶级接口
 */
public interface URLCreator {

    /**
     * 是否支持处理当前url
     * @param start 起始页
     * @param end 结束页
     * @return
     */
    public boolean support(String start,String end);

    /**
     * 构建url
     * @param link 目标链接
     * @param start 起始页
     * @param end   结束页
     * @return  构建的链接集合
     */
    public List<String> build(String link,String start,String end);
}
