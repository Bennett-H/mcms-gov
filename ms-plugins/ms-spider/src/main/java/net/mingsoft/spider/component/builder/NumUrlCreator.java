/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.component.builder;

import net.mingsoft.spider.exception.SpiderException;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据数字分类
 */
public class NumUrlCreator implements URLCreator {

    @Override
    public boolean support(String start, String end) {
        try {
            if (Integer.parseInt(start) > Integer.parseInt(end)) {
                throw new SpiderException("起始值不能大于结束值");
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    @Override
    public List<String> build(String link, String start, String end) {
        ArrayList<String> urls = new ArrayList<>();
        for (int i = Integer.parseInt(start); i <= Integer.parseInt(end); i++) {
            //去除空格
            urls.add(String.format(link, i));
        }
        return urls;
    }
}
