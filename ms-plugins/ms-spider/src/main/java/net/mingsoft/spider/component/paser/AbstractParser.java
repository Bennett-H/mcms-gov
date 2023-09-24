/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.component.paser;

import net.mingsoft.base.util.BundleUtil;
import net.mingsoft.spider.constant.Const;
import net.mingsoft.spider.exception.SpiderException;
import net.mingsoft.spider.component.ParserDefinition;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 顶级解析器,根据 ParserDefinition的type属性转换成对应的解析器
 */
public abstract class AbstractParser {

    /**
     * 一旦创建,禁止修改里面内容,否则出现线程不安全问题
     */
    protected final ParserDefinition content;

    public AbstractParser(ParserDefinition content) {
        this.content = content;
    }

    public ParserDefinition getContent() {
        return content;
    }

    /**
     * 解析页面
     * @param page 当前页面
     * @param selectable 选择器
     */
    public abstract Selectable parser(Page page, Selectable selectable);


    /**
     * 根据 {@link ParserDefinition} 的类型转换成对应的解析器
     */
    public static AbstractParser convertToParser(ParserDefinition pd){
        String type = pd.getType();
        //根据类型解析成对应的解析器
        switch (type){
            case Const.REGULAR:
                return new RegularParser(pd);
            case Const.XPATH:
                return new XpathParser(pd);
            case Const.CSS:
                return new CssParser(pd);
            case Const.DEFAULT:
                return new DefaultParser(pd);
            default:
                throw new SpiderException(BundleUtil.
                    getResString("err.type",
                            Const.RESOURCES));
        }
    }
}
