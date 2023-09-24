/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.site.action;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseAction;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.site.biz.ISiteAppManagerBiz;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加分发文章接口
 */
@Api(tags = {"后端-文章接口"})
@Controller("siteContentAction")
@RequestMapping("/${ms.manager.path}/site/cms/content")
public class ContentAction extends BaseAction {


    @Autowired
    ISiteAppManagerBiz SiteAppManagerBiz;

    /**
     * 返回distribute页面
     */
    @ApiIgnore
    @GetMapping("/distributeIndex")
    @RequiresPermissions("cms:content:distribution")
    public String main(HttpServletResponse response,HttpServletRequest request){
        if (!ConfigUtil.getBoolean("站群配置", "siteEnable")) {
            throw new BusinessException("需要开启站群才能使用");
        }
        return "/site/cms/content/distribution-index";
    }



    /**
     *	分发文章给其他站点
     * @param categoryId 需要分发的栏目
     * @param contentIds 需要分发的文章
     */
    @ApiOperation(value = "分发文章的文章接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "需要分发的栏目", required = true, paramType = "query"),
            @ApiImplicitParam(name = "contentId", value = "需要分发的文章", required = true, paramType = "query"),
    })
    @PostMapping("/distribute")
    @ResponseBody
    @LogAnn(title = "分发文章", businessType = BusinessTypeEnum.UPDATE)
    @RequiresPermissions("cms:content:distribution")
    public ResultData distribution(String categoryId, String contentIds, HttpServletResponse response, HttpServletRequest request) {
        //需文章和栏目都不为空，否则return
        if (StringUtils.isBlank(categoryId) || StringUtils.isBlank(contentIds)){
            return ResultData.build().error();
        }
        String[] contentIdsString = contentIds.split(",");
        List<String> ids = new ArrayList<>();
        for (String contentId : contentIdsString) {
            ids.add(SiteAppManagerBiz.distribution(categoryId,contentId));
        }
        return ResultData.build().success(ids);
    }



}
