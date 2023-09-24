/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mweixin.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpFreePublishService;
import me.chanjar.weixin.mp.bean.draft.WxMpDraftList;
import me.chanjar.weixin.mp.bean.freepublish.WxMpFreePublishArticles;
import me.chanjar.weixin.mp.bean.freepublish.WxMpFreePublishItem;
import me.chanjar.weixin.mp.bean.freepublish.WxMpFreePublishList;
import me.chanjar.weixin.mp.bean.freepublish.WxMpFreePublishStatus;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mweixin.bean.DraftArticleBean;
import net.mingsoft.mweixin.biz.IDraftBiz;
import net.mingsoft.mweixin.constant.SessionConst;
import net.mingsoft.mweixin.constant.e.DraftPublishStateEnum;
import net.mingsoft.mweixin.entity.ArticleEntity;
import net.mingsoft.mweixin.entity.DraftEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.service.PortalService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 草稿管理控制层
 *
 * @author 陈思鹏
 * 创建日期：2022-3-1 11:23:09<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-微信模块接口"})
@Controller("mweixinWxDraftAction")
@RequestMapping("/${ms.manager.path}/mweixin/draft")
public class DraftAction extends BaseAction {


    /**
     * 注入草稿业务层
     */
    @Autowired
    private IDraftBiz draftBiz;

    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("wxDraft:view")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/mweixin/draft/index";
    }

    /**
     * 返回编辑界面form
     */
    @ApiIgnore
    @GetMapping("/form")
    @RequiresPermissions(value = {"wxDraft:save", "wxDraft:edit"}, logical = Logical.OR)
    public String form(HttpServletResponse response, HttpServletRequest request) {
        return "/mweixin/draft/form";
    }


    /**
     * 图文素材列表
     *
     * @param request
     * @param response
     * @return 图文素材列表界面
     */
    @ApiOperation(value = "图文素材列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleList", value = "文章列表", dataTypeClass = ArticleEntity.class, allowMultiple = true, required = true, paramType = "query"),
            @ApiImplicitParam(name = "articles", value = "文章列表字符串", required = false, paramType = "query"),
            @ApiImplicitParam(name = "basicTitle", value = "图文标题", required = false, paramType = "query"),
            @ApiImplicitParam(name = "masterArticle", value = "文章字符串", required = false, paramType = "query"),
    })
    @GetMapping("/list")
    @ResponseBody
    @RequiresPermissions("wxDraft:view")
    public ResultData list(@ModelAttribute @ApiIgnore DraftArticleBean draftArticleBean, HttpServletRequest request, HttpServletResponse response, @ApiIgnore ModelMap model) {
        //取出微信实体
        WeixinEntity weixin = (WeixinEntity) BasicUtil.getSession(SessionConst.WEIXIN_SESSION);
        //获取微信ID
        draftArticleBean.setWeixinId(weixin.getIntId());
        //搜索的数据
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setArticleTitle(draftArticleBean.getBasicTitle());
        draftArticleBean.setMasterArticle(articleEntity);
        //查询图文素材
        BasicUtil.startPage();
        List<DraftArticleBean> draftList = draftBiz.queryDraft(draftArticleBean);
        //结果排序
        draftList.forEach(news -> {
            if (CollUtil.isEmpty(news.getArticleList())) {
                return;
            }
            news.getArticleList().sort((o1, o2) -> {
                if (o1.getArticleSort() == null) {
                    return 1;
                } else if (o2.getArticleSort() == null) {
                    return -1;
                }
                return o1.getArticleSort() - o2.getArticleSort();
            });
        });
        return ResultData.build().success(new EUListBean(draftList, (int) BasicUtil.endPage(draftList).getTotal()));
    }

    /**
     * 同步微信草稿
     *
     * @param request
     * @return 返回news_form.ftl
     */
    @ApiOperation(value = "同步微信素材")
    @ApiImplicitParam(name = "weixinNo", value = "微信号", required = true, paramType = "query")
    @GetMapping("/sync")
    @ResponseBody
    @RequiresPermissions("wxDraft:sync")
    public ResultData weiXinNewsSyncLocal(@ApiIgnore String weixinNo, HttpServletRequest request, HttpServletResponse response) {
        try {
            //取出微信实体
            WeixinEntity weixin = this.getWeixinSession(request);
            //若微信或者微信相关数据不存在
            if (weixin == null || weixin.getIntId() <= 0) {
                return ResultData.build().error(this.getResString("weixin.not.found"));
            }
            //同步未发布的
            WxMpDraftList wxMpDraftList = this.builderWeixinService(weixin.getWeixinNo()).getDraftService().listDraft(0, 20);
            draftBiz.weiXinDraftSyncLocal(wxMpDraftList, weixin.getIntId());
            //同步已发布的
            WxMpFreePublishList publishList = this.builderWeixinService(weixin.getWeixinNo()).getFreePublishService().getPublicationRecords(0, 20);
            draftBiz.weiXinPublishedSyncLocal(publishList, weixin.getIntId());
        } catch (WxErrorException e) {
            e.printStackTrace();
            return ResultData.build().error(e.getMessage());
        }
        return ResultData.build().success();
    }

    /**
     * 查询微信草稿发布状态并更新发布状态和返回url链接数据
     *
     * @param request
     * @return 返回news_form.ftl
     */
    @ApiOperation(value = "查询微信草稿发布状态")
    @PostMapping("/checkPushStatus")
    @ResponseBody
    @RequiresPermissions("wxDraft:sync")
    public ResultData weiXinGetPushStatus(@ModelAttribute @ApiIgnore DraftArticleBean draftArticleBean, HttpServletRequest request, HttpServletResponse response, @ApiIgnore ModelMap model) {
        if (StringUtils.isEmpty(draftArticleBean.getId())) {
            return ResultData.build().error(this.getResString("err.empty", this.getResString("weixin.draft.id")));
        }
        // 如果有Url就直接返回url
        if (StringUtils.isNotEmpty(draftArticleBean.getDraftUrl())) {
            return ResultData.build().success(draftArticleBean.getDraftUrl());
        }
        DraftEntity draftEntity = draftBiz.weiXinGetPushStatus(draftArticleBean);
        if (StringUtils.isEmpty(draftEntity.getDraftUrl())) {
            return ResultData.build().error("该图文还未通过微信审核，请耐心等待审核通过");
        }
        return ResultData.build().success(draftEntity.getDraftUrl());
    }


    /**
     * 根据草稿Id获取
     *
     * @param response
     * @param mode
     * @param id       草稿id
     */
    @ApiOperation(value = "根据草稿Id获取草稿实体")
    @ApiImplicitParam(name = "id", value = "草稿编号", required = true, dataType = "Integer", paramType = "query")
    @GetMapping("/{id}/get")
    @ResponseBody
    @RequiresPermissions("wxDraft:view")
    public ResultData get(@PathVariable String id, HttpServletResponse response, @ApiIgnore ModelMap mode) {
        if (StringUtils.isBlank(id)) {
            return ResultData.build().error();
        }
        //根据ID获取相应草稿
        DraftEntity draftEntity = draftBiz.getById(id);
        //如果该草稿不存在
        if (draftEntity == null) {
            return ResultData.build().error();
        }
        DraftArticleBean draftArticleBean = new DraftArticleBean();
        BeanUtil.copyProperties(draftEntity, draftArticleBean);
        //填充图文
        draftBiz.setDraftBean(draftArticleBean);
        //结果排序

        if (CollUtil.isNotEmpty(draftArticleBean.getArticleList())) {
            draftArticleBean.getArticleList().sort((o1, o2) -> {
                if (o1.getArticleSort() == null) {
                    return 1;
                } else if (o2.getArticleSort() == null) {
                    return -1;
                }
                return o1.getArticleSort() - o2.getArticleSort();
            });
        }


        return ResultData.build().success(draftArticleBean);
    }


    /**
     * 保存草稿
     *
     * @param wxDraft 草稿实体
     */
    @ApiOperation(value = "保存草稿列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mediaId", value = "微信媒体id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "childArticleIds", value = "子图文ids", required = true, paramType = "query"),
            @ApiImplicitParam(name = "masterArticleId", value = "主图文id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "weixinId", value = "微信id", required = true, paramType = "query"),
    })
    @PostMapping("/save")
    @ResponseBody
    @LogAnn(title = "保存草稿", businessType = BusinessTypeEnum.INSERT)
    @RequiresPermissions("wxDraft:save")
    public ResultData save(@ModelAttribute @ApiIgnore DraftEntity wxDraft, HttpServletResponse response, HttpServletRequest request) {
        if (!StringUtil.checkLength(wxDraft.getMediaId() + "", 0, 255)) {
            return ResultData.build().error(getResString("err.length", this.getResString("media.id"), "0", "255"));
        }
        if (!StringUtil.checkLength(wxDraft.getChildArticleIds() + "", 0, 255)) {
            return ResultData.build().error(getResString("err.length", this.getResString("child.article.ids"), "0", "255"));
        }
        if (!StringUtil.checkLength(wxDraft.getMasterArticleId() + "", 0, 11)) {
            return ResultData.build().error(getResString("err.length", this.getResString("master.article.id"), "0", "11"));
        }
        if (!StringUtil.checkLength(wxDraft.getWeixinId() + "", 0, 11)) {
            return ResultData.build().error(getResString("err.length", this.getResString("weixin.id"), "0", "11"));
        }
        draftBiz.save(wxDraft);
        return ResultData.build().success(wxDraft);
    }

    /**
     * 根据Id删除指定草稿
     *
     * @param id       ID
     * @param request
     * @param response
     */
    @ApiOperation(value = "根据Id删除指定草稿")
    @ApiImplicitParam(name = "id", value = "草稿编号", required = true, paramType = "path")
    @LogAnn(title = "根据Id删除指定草稿", businessType = BusinessTypeEnum.DELETE)
    @PostMapping("/{id}/delete")
    @ResponseBody
    @RequiresPermissions("wxDraft:del")
    public ResultData delete(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(id)) {
            return ResultData.build().error();
        }
        WeixinEntity weixin = this.getWeixinSession(request);
        //若微信或者微信相关数据不存在
        if (weixin == null || weixin.getIntId() <= 0) {
            return ResultData.build().error(this.getResString("weixin.not.found"));
        }
        DraftEntity draftEntity = draftBiz.getById(id);
        if (draftEntity != null) {
            PortalService service = SpringUtil.getBean(PortalService.class);
            PortalService wxService = service.build(weixin);
            Integer publishState = draftEntity.getPublishState();
            if (publishState == DraftPublishStateEnum.PUBLISHING_FAILED.toInt() || publishState == DraftPublishStateEnum.UNPUBLISHED.toInt()) {
                //未发布或发布失败
                try {
                    wxService.getDraftService().delDraft(draftEntity.getMediaId());
                } catch (WxErrorException e) {
                    e.printStackTrace();
                }
            } else if (publishState == DraftPublishStateEnum.PUBLISHED.toInt()) {
                //若发布成功
                try {
                    wxService.getFreePublishService().deletePush(draftEntity.getArticleId(), 0);
                } catch (WxErrorException e) {
                    e.printStackTrace();
                    return ResultData.build().error(e.getMessage());

                }
            }
        }
        draftBiz.removeById(id);
        return ResultData.build().success();
    }

    /**
     * 更新草稿列表
     *
     * @param wxDraft 草稿实体
     */
    @ApiOperation(value = "更新草稿列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mediaId", value = "微信媒体id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "childArticleIds", value = "子图文ids", required = true, paramType = "query"),
            @ApiImplicitParam(name = "masterArticleId", value = "主图文id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "weixinId", value = "微信id", required = true, paramType = "query"),
    })
    @PostMapping("/update")
    @ResponseBody
    @LogAnn(title = "更新草稿", businessType = BusinessTypeEnum.UPDATE)
    @RequiresPermissions("wxDraft:update")
    public ResultData update(@ModelAttribute @ApiIgnore DraftEntity wxDraft, HttpServletResponse response,
                             HttpServletRequest request) {
        if (!StringUtil.checkLength(wxDraft.getMediaId() + "", 0, 255)) {
            return ResultData.build().error(getResString("err.length", this.getResString("media.id"), "0", "255"));
        }
        if (!StringUtil.checkLength(wxDraft.getChildArticleIds() + "", 0, 255)) {
            return ResultData.build().error(getResString("err.length", this.getResString("child.article.ids"), "0", "255"));
        }
        if (!StringUtil.checkLength(wxDraft.getMasterArticleId() + "", 0, 11)) {
            return ResultData.build().error(getResString("err.length", this.getResString("master.article.id"), "0", "11"));
        }
        if (!StringUtil.checkLength(wxDraft.getWeixinId() + "", 0, 11)) {
            return ResultData.build().error(getResString("err.length", this.getResString("weixin.id"), "0", "11"));
        }
        draftBiz.updateById(wxDraft);
        return ResultData.build().success(wxDraft);
    }


    /**
     * 保存更新素材
     *
     * @param draftArticleBean 素材实体
     * @param request
     * @param response
     */
    @ApiOperation(value = "微信保存和更新素材接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newsType", value = "微信素材类别编号 imageText.图文 text.文本 image.图片", required = true, paramType = "query"),
            @ApiImplicitParam(name = "newsCategoryId", value = "微信分类编号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "articles", value = "多个微信素材使用json格式", required = false, paramType = "query"),
            @ApiImplicitParam(name = "masterArticle", value = "微信主图文素材", required = true, paramType = "query")
    })
    @LogAnn(title = "微信保存和更新素材接口", businessType = BusinessTypeEnum.UPDATE)
    @PostMapping("/saveOrUpdate")
    @ResponseBody
    @RequiresPermissions("wxDraft:save")
    public ResultData saveOrUpdate(@RequestBody @ApiIgnore DraftArticleBean draftArticleBean, HttpServletRequest request, HttpServletResponse response) {
        if (ObjectUtil.isNull(draftArticleBean.getMasterArticle())) {
            return ResultData.build().error();
        }
        ArrayList<ArticleEntity> articleEntities = new ArrayList<>(draftArticleBean.getArticleList());
        articleEntities.add(draftArticleBean.getMasterArticle());
        for (ArticleEntity articleEntity : articleEntities) {
            if (StringUtils.isBlank(articleEntity.getArticleTitle())) {
                return ResultData.build().error("标题不能为空");
            }
            if (StringUtils.isBlank(articleEntity.getArticleAuthor())) {
                return ResultData.build().error("作者不能为空");
            }
            if (StringUtils.isBlank(articleEntity.getArticleDescription())) {
                return ResultData.build().error("摘要不能为空");
            }
            if (StringUtils.isBlank(articleEntity.getArticleContent())) {
                return ResultData.build().error("内容不能为空");
            }
            if (StringUtils.isBlank(articleEntity.getArticleThumbnails())) {
                return ResultData.build().error("封面不能为空");
            } else {
                File file = new File(BasicUtil.getRealPath("") + File.separator + articleEntity.getArticleThumbnails());
                if (!file.exists()) {
                    return ResultData.build().error("封面不存在");
                }
            }
            if (articleEntity.getArticleContent().length() > 20000) {
                articleEntity.setArticleContent(articleEntity.getArticleContent().substring(0, 20000));
            }
        }
        //取出微信实体
        WeixinEntity weixin = this.getWeixinSession(request);
        //若微信或者微信相关数据不存在
        if (weixin == null || weixin.getIntId() <= 0) {
            return ResultData.build().error(this.getResString("weixin.not.found"));
        }
        //获取微信ID
        int weixinId = weixin.getIntId();
        //微信ID
        draftArticleBean.setWeixinId(weixinId);
        //持久化新增素材
        try {
            draftBiz.saveOrUpdateDraftArticleBean(draftArticleBean);
        } catch (WxErrorException e) {
            e.printStackTrace();
            return ResultData.build().error(e.getMessage());
        }
        return ResultData.build().success();
    }


    /**
     * 发布草稿
     *
     * @param draftArticleBean 草稿实体
     * @param request
     * @param response
     */
    @ApiOperation(value = "发布草稿")
    @LogAnn(title = "发布草稿接口", businessType = BusinessTypeEnum.UPDATE)
    @PostMapping("/submit")
    @ResponseBody
    @RequiresPermissions("wxDraft:submit")
    public ResultData submit(@ModelAttribute DraftArticleBean draftArticleBean, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(draftArticleBean.getId())) {
            return ResultData.build().error();
        }
        if (StringUtils.isNotEmpty(draftArticleBean.getPublishId())) {
            return ResultData.build().error("已经发布了");
        }
        //取出微信实体
        WeixinEntity weixin = this.getWeixinSession(request);
        //若微信或者微信相关数据不存在
        if (weixin == null || weixin.getIntId() <= 0) {
            return ResultData.build().error(this.getResString("weixin.not.found"));
        }
        //获取微信ID
        int weixinId = weixin.getIntId();
        //微信ID
        draftArticleBean.setWeixinId(weixinId);
        //发布素材
        draftBiz.submit(draftArticleBean);
        return ResultData.build().success();
    }


    /**
     * 同步微信已发布草稿
     *
     * @param request
     * @return 返回news_form.ftl
     */
    @ApiOperation(value = "同步微信已发布草稿")
    @ApiImplicitParam(name = "weixinNo", value = "微信号", required = true, paramType = "query")
    @GetMapping("/syncPublished")
    @ResponseBody
    @RequiresPermissions("wxDraft:sync")
    public ResultData weiXinPublishedSyncLocal(@ApiIgnore String weixinNo, HttpServletRequest request, HttpServletResponse response) {
        try {
            //取出微信实体
            WeixinEntity weixin = this.getWeixinSession(request);
            //若微信或者微信相关数据不存在
            if (weixin == null || weixin.getIntId() <= 0) {
                return ResultData.build().error(this.getResString("weixin.not.found"));
            }
            WxMpFreePublishList publishList = this.builderWeixinService(weixin.getWeixinNo()).getFreePublishService().getPublicationRecords(0, 20);
            draftBiz.weiXinPublishedSyncLocal(publishList, weixin.getIntId());
        } catch (WxErrorException e) {
            e.printStackTrace();
            return ResultData.build().error(e.getMessage());

        }
        return ResultData.build().success();
    }


}
