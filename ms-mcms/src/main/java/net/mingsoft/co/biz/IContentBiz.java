/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.co.biz;

import net.mingsoft.co.bean.ContentBean;

import java.util.List;


/**
 * 文章业务
 * @author 铭软开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
public interface IContentBiz extends net.mingsoft.cms.biz.IContentBiz {

    /**
     * 移动或复制文章到指定栏目
     * @param contentBean
     * @param type
     */
    List<String> removeOrCopy(ContentBean contentBean, String type);


}
