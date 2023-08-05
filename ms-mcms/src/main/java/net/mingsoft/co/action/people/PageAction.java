/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.co.action.people;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.co.bean.TemplateBean;
import net.mingsoft.co.service.RenderingService;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.mdiy.action.BaseAction;
import net.mingsoft.mdiy.biz.IPageBiz;
import net.mingsoft.mdiy.entity.PageEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @ClassName: PageAction
 * @Description:TODO(自定义页面)
 * @author: 铭软开发团队
 * @date: 2018年12月17日 下午6:10:12
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 */
@Api(tags={"前端-用户-企业模块接口"})
@Controller("webCoPeoplemdiyPage")
@RequestMapping(value = {"/people"})
public class PageAction extends BaseAction {

    /**
     * 自定义页面业务层
     */
    @Autowired
    private IPageBiz pageBiz;


    @Autowired
    private RenderingService renderingService;

    @ApiOperation(value = "自定义页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateName", value = "皮肤", required = true, paramType = "path"),
            @ApiImplicitParam(name = "diy", value = "请求地址", required = true, paramType = "path")
    })
    @GetMapping(value = "/{templateName}/{diy}")
    public void diy(@PathVariable(value = "templateName") String templateName, @PathVariable(value = "diy") String diy, HttpServletRequest req, HttpServletResponse resp) {
        if (StringUtils.isBlank(templateName)) {
            throw new BusinessException(getResString("err.empty", "templateName"));
        }
        Map<String, Object> params = BasicUtil.assemblyRequestMap();
        params.forEach((k, v) -> {
            params.put(k, v.toString().replaceAll("('|\"|\\\\)", "\\\\$1"));
        });
        // 开启isDo需要传modelName参数,参数是用于区分哪个模块的
        params.put(ParserUtil.IS_DO, Boolean.parseBoolean(BasicUtil.getString(ParserUtil.IS_DO, "false")));
        ParserUtil.putBaseParams(params,templateName);

        PageEntity page = new PageEntity();
        page.setPageKey("people/".concat(diy));
        PageEntity _page = pageBiz.getEntity(page);
        // 页面已经关闭
        if (!_page.getPageEnable()){
            throw new BusinessException("当前模板已被关闭");
        }
        String pageTmpl = "";
        if (StringUtils.isNotBlank(_page.getPagePath())) {
            List<TemplateBean> tmpl = JSONUtil.toList(_page.getPagePath(), TemplateBean.class);
            List<TemplateBean> result = tmpl.stream().filter(templateBean -> templateBean.getTemplate().equals(templateName)).collect(Collectors.toList());
            if (result.size() > 0) {
                pageTmpl = result.get(0).getFile();
            }
        }

        String buildTemplatePath = ParserUtil.buildTemplatePath();
        String templateFolder = buildTemplatePath.concat(templateName);
        if(!new File(templateFolder.concat(File.separator).concat(pageTmpl)).exists()) {
            LOG.debug("模板页面:{}",pageTmpl);
            throw new BusinessException(getResString("err.empty", "template"));
        }
        String templateContent = FileUtil.readString(FileUtil.file(templateFolder, pageTmpl), CharsetUtil.CHARSET_UTF_8);
        templateContent = ParserUtil.replaceTag(templateContent);
        Future<String> future = renderingService.rendering(params, templateFolder, pageTmpl, templateContent, null);
        String result = null;
        try {
            result = future.get();
            BasicUtil.outString(resp, result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }


}
