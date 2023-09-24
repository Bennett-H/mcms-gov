/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.dao;

import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mweixin.bean.DraftArticleBean;
import net.mingsoft.mweixin.entity.DraftEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 草稿持久层
 * @author 陈思鹏
 * 创建日期：2022-3-1 11:23:09<br/>
 * 历史修订：<br/>
 */
@Component("WxIDraftDao")
public interface IWxDraftDao extends IBaseDao<DraftEntity> {
    /**
     * 查询草稿列表
     * @param weixinId 微信ID
     * @param page 分页参数
     * @return 图文草稿列表
     */
    List queryDraftList(DraftArticleBean draftArticleBean);


}
