/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.component.paser;

import net.mingsoft.spider.component.ParserDefinition;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Selectable;

/**
 * xpath选择器
 */
public class XpathParser extends AbstractParser{

    public XpathParser(ParserDefinition content) {
        super(content);
    }

    @Override
    public Selectable parser(Page page, Selectable selectable) {
        return selectable.xpath(content.getExpression());
    }


}
