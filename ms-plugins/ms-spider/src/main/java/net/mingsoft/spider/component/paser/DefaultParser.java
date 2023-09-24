/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.component.paser;

import net.mingsoft.spider.component.ParserDefinition;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;

import java.util.Collections;

/**
 * 常量选择器，将数据原封不动返回
 */
public class DefaultParser extends AbstractParser {


    public DefaultParser(ParserDefinition pd) {
        super(pd);
    }

    @Override
    public Selectable parser(Page page, Selectable selectable) {
        return new PlainText(Collections.singletonList(content.getExpression()));
    }

}
