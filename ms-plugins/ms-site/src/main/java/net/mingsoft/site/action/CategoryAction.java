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
import net.mingsoft.site.biz.ISiteAppManagerBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 分类管理控制层
 * @author 铭飞开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：
 * 20210722 增加管理员栏目权限处理<br/>
 */
@Api(tags = {"后端-栏目接口"})
@Controller("siteCategoryAction")
@RequestMapping("/${ms.manager.path}/site/cms/category")
public class CategoryAction extends BaseAction {
    @Autowired
    ISiteAppManagerBiz siteAppManagerBiz;

    /**
     * 查询指定站点的分类列表
     * @param appId 站点Id
     */
    @ApiOperation(value = "查询分类列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "站点Id", required =false,paramType="query"),
    })
    @RequestMapping(value = "/getCategoryByAppId", method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResultData list(String appId) {
        List<Map<String, Object>> categoryByAppId = siteAppManagerBiz.getCategoryByAppId(appId);
        return ResultData.build().success(categoryByAppId);

    }



}
