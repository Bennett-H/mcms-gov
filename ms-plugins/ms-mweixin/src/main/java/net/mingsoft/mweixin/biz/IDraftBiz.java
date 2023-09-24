/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.biz;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.draft.WxMpDraftList;
import me.chanjar.weixin.mp.bean.freepublish.WxMpFreePublishList;
import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.mweixin.bean.DraftArticleBean;
import net.mingsoft.mweixin.entity.DraftEntity;

import java.util.List;

/**
 * 草稿业务
 * @author 陈思鹏
 * 创建日期：2022-3-1 11:23:09<br/>
 * 历史修订：<br/>
 */
public interface IDraftBiz extends IBaseBiz<DraftEntity> {

    /**
     * 保存或更新文章素材表
     * @param draftArticleBean
     */
    void saveOrUpdateDraftArticleBean(DraftArticleBean draftArticleBean) throws WxErrorException;

    /**
     * 依据传入的草稿bean</br>
     * 填充草稿中文章列表,一般为微信发送图文消息时的主图文和主图文</br>
     *
     * @param draftArticleBean 需要转换的草稿bean
     * @return 转换后的素材信息
     */
    DraftArticleBean setDraftBean(DraftArticleBean draftArticleBean);


    /**
     * 根据微信编号查询草稿列表
     * @param id 微信编号
     * @return 草稿列表
     */
    List<DraftArticleBean> queryDraft(DraftArticleBean draftArticleBean);



    /**
     * 上传草稿到微信服务器 返回MediaId
     * @param draftEntity
     * @return
     */
    String uploadDraft(DraftEntity draftEntity) throws WxErrorException;

    /**
     * 微信同步未发布草稿至本地
     * @param wxMpDraftList
     * @return
     */
    void weiXinDraftSyncLocal(WxMpDraftList wxMpDraftList, int weixinId);

    /**
     * 发布素材
     * @param draftArticleBean
     */
    void submit(DraftArticleBean draftArticleBean);

    /**
     * 微信同步已发布草稿至本地
     * @param publishList
     * @return
     */
    void weiXinPublishedSyncLocal(WxMpFreePublishList publishList, int weixinId);

    /**
     * 根据publishId来查询发布状态并更新状态和返回一个更新后实体类
     * @param draftArticleBean 文章草稿实体类
     * @return 草稿实体类
     */
    DraftEntity weiXinGetPushStatus(DraftArticleBean draftArticleBean);
}
