/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.component.downloader;

import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.downloader.HttpClientDownloader;


/**
 * webmagic框架预留HttpClientDownloader组件的拓展类
 * 当请求获取并解析完当前页面得到Page对象后的回调方法
 * TODO 可拓展
 */
@Component
public class ExtraHttpClientDownloader extends HttpClientDownloader {

    /**
     * 请求页面成功并解析失败后处理回调
     * @param request request请求
     */
    @Override
    protected void onSuccess(Request request) {
        super.onSuccess(request);
    }

    /**
     * 请求页面失败或者解析异常处理回调
     * @param request request对象
     */
    @Override
    protected void onError(Request request) {
        super.onError(request);
    }
}

