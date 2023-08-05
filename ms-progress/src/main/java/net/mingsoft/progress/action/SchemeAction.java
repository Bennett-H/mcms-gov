/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.action;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.progress.biz.ISchemeBiz;
import net.mingsoft.progress.entity.SchemeEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 进度方案管理控制层
 *
 * @author 铭飞科技
 * 创建日期：2021-3-8 8:53:25<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-进度模块接口"})
@Controller("progressSchemeAction")
@RequestMapping("/${ms.manager.path}/progress/scheme")
public class SchemeAction extends BaseAction {


    /**
     * 注入进度方案业务层
     */
    @Autowired
    private ISchemeBiz schemeBiz;

    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("progress:scheme:view")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/progress/scheme/index";
    }

    /**
     * 查询进度方案列表
     *
     * @param scheme 进度方案实体
     */
    @ApiOperation(value = "查询进度方案列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schemeName", value = "方案名称", required = false, paramType = "query"),
            @ApiImplicitParam(name = "schemeType", value = "类型", required = false, paramType = "query"),
            @ApiImplicitParam(name = "schemeTable", value = "回调表名", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required = false, paramType = "query"),
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResultData list(@ModelAttribute @ApiIgnore SchemeEntity scheme, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
        BasicUtil.startPage();
        List schemeList = schemeBiz.list(new QueryWrapper<>(scheme));
        return ResultData.build().success(new EUListBean(schemeList, (int) BasicUtil.endPage(schemeList).getTotal()));
    }

    /**
     * 返回编辑界面scheme_form
     */
    @ApiIgnore
    @GetMapping("/form")
    @RequiresPermissions(value = {"progress:scheme:save", "progress:scheme:update"}, logical = Logical.OR)
    public String form(@ModelAttribute SchemeEntity scheme, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
        return "/progress/scheme/form";
    }

    /**
     * 获取进度方案
     *
     * @param scheme 进度方案实体
     */
    @ApiOperation(value = "获取进度方案列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required = true, paramType = "query")
    @GetMapping("/get")
    @ResponseBody
    public ResultData get(@ModelAttribute @ApiIgnore SchemeEntity scheme, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        if (scheme.getId() == null) {
            return ResultData.build().error();
        }
        SchemeEntity _scheme = (SchemeEntity) schemeBiz.getById(scheme.getId());
        return ResultData.build().success(_scheme);
    }


    /**
     * 保存进度方案
     *
     * @param scheme 进度方案实体
     */
    @ApiOperation(value = "保存进度方案列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schemeName", value = "方案名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "schemeType", value = "类型", required = true, paramType = "query"),
            @ApiImplicitParam(name = "schemeTable", value = "回调表名", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required = false, paramType = "query"),
    })
    @PostMapping("/save")
    @ResponseBody
    @LogAnn(title = "保存进度方案", businessType = BusinessTypeEnum.INSERT)
    @RequiresPermissions("progress:scheme:save")
    public ResultData save(@ModelAttribute @ApiIgnore SchemeEntity scheme, HttpServletResponse response, HttpServletRequest request) {
        if (super.validated("progress_scheme", "scheme_name", scheme.getSchemeName())) {
            return ResultData.build().error(getResString("err.exist", this.getResString("scheme.name")));
        }
        //验证方案名称的值是否合法
        if (StringUtils.isBlank(scheme.getSchemeName())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("scheme.name")));
        }
        //验证类型的值是否合法
        if (StringUtils.isBlank(scheme.getSchemeType())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("scheme.type")));
        }
        if (!StringUtil.checkLength(scheme.getSchemeTable() + "", 0, 20)) {
            return ResultData.build().error(getResString("err.length", this.getResString("scheme.table"), "0", "20"));
        }
        schemeBiz.saveOrUpdate(scheme);
        return ResultData.build().success(scheme);
    }

    /**
     * @param schemes 进度方案实体
     */
    @ApiOperation(value = "批量删除进度方案列表接口")
    @PostMapping("/delete")
    @ResponseBody
    @LogAnn(title = "删除进度方案", businessType = BusinessTypeEnum.DELETE)
    @RequiresPermissions("progress:scheme:del")
    public ResultData delete(@RequestBody List<SchemeEntity> schemes, HttpServletResponse response, HttpServletRequest request) {
        List<String> ids = schemes.stream().map(p -> p.getId()).collect(Collectors.toList());
        if (schemeBiz.deleteBySchemeIds(ids)) {
            return ResultData.build().success();
        } else {
            return ResultData.build().error(getResString("err.error", getResString("id")));
        }
    }

    /**
     * 更新进度方案列表
     *
     * @param scheme 进度方案实体
     */
    @ApiOperation(value = "更新进度方案列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "schemeName", value = "方案名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "schemeType", value = "类型", required = true, paramType = "query"),
            @ApiImplicitParam(name = "schemeTable", value = "回调表名", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required = false, paramType = "query"),
    })
    @PostMapping("/update")
    @ResponseBody
    @LogAnn(title = "更新进度方案", businessType = BusinessTypeEnum.UPDATE)
    @RequiresPermissions("progress:scheme:update")
    public ResultData update(@ModelAttribute @ApiIgnore SchemeEntity scheme, HttpServletResponse response,
                             HttpServletRequest request) {
        if (super.validated("progress_scheme", "scheme_name", "id", scheme.getId(), scheme.getSchemeName())) {
            return ResultData.build().error(getResString("err.exist", this.getResString("scheme.name")));
        }
        //验证方案名称的值是否合法
        if (StringUtils.isBlank(scheme.getSchemeName())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("scheme.name")));
        }
        //验证类型的值是否合法
        if (StringUtils.isBlank(scheme.getSchemeType())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("scheme.type")));
        }
        if (!StringUtil.checkLength(scheme.getSchemeTable() + "", 0, 20)) {
            return ResultData.build().error(getResString("err.length", this.getResString("scheme.table"), "0", "20"));
        }
        schemeBiz.saveOrUpdate(scheme);
        return ResultData.build().success(scheme);
    }

    @ApiOperation(value = "检查方案接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fieldName", value = "字段", required = false, paramType = "query"),
    })
    @GetMapping("verify")
    @ResponseBody
    public ResultData verify(String fieldName, String fieldValue, String id, String idName) {
        boolean verify = false;
        if (StringUtils.isBlank(id)) {
            verify = super.validated("progress_scheme", fieldName, fieldValue);
        } else {
            verify = super.validated("progress_scheme", fieldName, fieldValue, id, idName);
        }
        if (verify) {
            return ResultData.build().success(false);
        } else {
            return ResultData.build().success(true);
        }
    }

}
