/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.action.web;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.mweixin.action.BaseAction;
import net.mingsoft.mweixin.bean.DraftArticleBean;
import net.mingsoft.mweixin.biz.IDraftBiz;
import net.mingsoft.mweixin.entity.ArticleEntity;
import net.mingsoft.mweixin.entity.DraftEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

@Api(tags = {"前端-微信模块接口"})
@Controller("DraftActionWeb")
@RequestMapping("/mweixin/draft")
public class DraftAction extends BaseAction {

    @Autowired
    IDraftBiz draftBiz;
    @ApiOperation(value = "根据素材draftId获取素材实体")
    @ApiImplicitParam(name = "draftId", value = "草稿编号", required = true, dataType = "Integer", paramType = "query")
    @GetMapping(value = "/get" )
    public String  getNews(@RequestParam("draftId") String draftId, @ApiIgnore ModelMap mode, HttpServletRequest request){
        DraftEntity draftEntity = draftBiz.getById(draftId);
        DraftArticleBean draftArticleBean = new DraftArticleBean();
        BeanUtil.copyProperties(draftEntity,draftArticleBean);
        draftBiz.setDraftBean(draftArticleBean);
        ArticleEntity masterArticle = draftArticleBean.getMasterArticle();
        request.setAttribute("articleEntity",masterArticle);
        mode.addAttribute("articleEntity",masterArticle);
        return "/mweixin/news-content";
    }

}
