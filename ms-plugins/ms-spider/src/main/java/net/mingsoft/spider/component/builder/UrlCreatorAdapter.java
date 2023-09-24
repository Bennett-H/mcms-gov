/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.component.builder;

import net.mingsoft.spider.exception.SpiderException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建分页帮助类
 */
@Component
public class UrlCreatorAdapter implements InitializingBean,URLCreator {

    private List<URLCreator> urlCreators;

    @Override
    public void afterPropertiesSet() throws Exception {
        urlCreators = new ArrayList<>();
        urlCreators.add(new NumUrlCreator());
    }

    @Override
    public boolean support(String start, String end) {
        return true;
    }

    @Override
    public List<String> build(String link, String start, String end) {
        for (URLCreator creator : urlCreators) {
            if (creator.support(start, end)){
                return creator.build(link, start, end);
            }
        }
        throw new SpiderException("无法找到合适的url构建器");
    }
}
