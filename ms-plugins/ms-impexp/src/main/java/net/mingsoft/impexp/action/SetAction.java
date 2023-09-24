/**
 * 版权所有  铭软科技(mingsoft.net)
 * 未经授权，严禁通过任何介质未经授权复制此文件
 */
package net.mingsoft.impexp.action;

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
import net.mingsoft.impexp.biz.ISetBiz;
import net.mingsoft.impexp.entity.SetEntity;
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
 * 导入导出配置管理控制层
 *
 * @author 铭软科技
 * 创建日期：2021-2-2 17:35:57<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-导入导出模块接口"})
@Controller("impexpSetAction")
@RequestMapping("/${ms.manager.path}/impexp/set")
public class SetAction extends net.mingsoft.impexp.action.BaseAction {


    /**
     * 注入导入导出配置业务层
     */
    @Autowired
    private ISetBiz setBiz;

    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("impexp:set:view")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/impexp/set/index";
    }

    /**
     * 查询导入导出配置列表
     *
     * @param set 导入导出配置实体
     */
    @ApiOperation(value = "查询导入导出配置列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "导入导出标识", required = false, paramType = "query"),
            @ApiImplicitParam(name = "exportSql", value = "导出sql配置", required = false, paramType = "query"),
            @ApiImplicitParam(name = "importJson", value = "导入主表json", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required = false, paramType = "query"),
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @RequiresPermissions("impexp:set:view")//此处添加会导致仅有文章导入导出权限的管理员不能正常使用功能
    public ResultData list(@ModelAttribute @ApiIgnore SetEntity set, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
        BasicUtil.startPage();
        List setList = setBiz.list(new QueryWrapper<>(set));
        return ResultData.build().success(new EUListBean(setList, (int) BasicUtil.endPage(setList).getTotal()));
    }

    /**
     * 返回编辑界面set_form
     */
    @ApiIgnore
    @GetMapping("/form")
    @RequiresPermissions(value = {"impexp:set:save", "impexp:set:update"}, logical = Logical.OR)
    public String form(@ModelAttribute SetEntity set, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
        return "/impexp/set/form";
    }

    /**
     * 获取导入导出配置
     *
     * @param set 导入导出配置实体
     */
    @ApiOperation(value = "获取导入导出配置列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required = true, paramType = "query")
    @GetMapping("/get")
    @ResponseBody
    public ResultData get(@ModelAttribute @ApiIgnore SetEntity set, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        if (set.getId() == null) {
            return ResultData.build().error();
        }
        SetEntity _set = (SetEntity) setBiz.getById(set.getId());
        return ResultData.build().success(_set);
    }


    /**
     * 保存导入导出配置
     *
     * @param set 导入导出配置实体
     */
    @ApiOperation(value = "保存导入导出配置列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "导入导出标识", required = true, paramType = "query"),
            @ApiImplicitParam(name = "exportSql", value = "导出sql配置", required = true, paramType = "query"),
            @ApiImplicitParam(name = "importJson", value = "导入主表json", required = true, paramType = "query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required = false, paramType = "query"),
    })
    @PostMapping("/save")
    @ResponseBody
    @LogAnn(title = "保存导入导出配置", businessType = BusinessTypeEnum.INSERT)
    @RequiresPermissions("impexp:set:save")
    public ResultData save(@ModelAttribute @ApiIgnore SetEntity set, HttpServletResponse response, HttpServletRequest request) {
        if (super.validated("impexp_set", "name", set.getName())) {
            return ResultData.build().error(getResString("err.exist", this.getResString("name")));
        }
        //验证导入导出标识的值是否合法
        if (StringUtils.isBlank(set.getName())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("name")));
        }
        if (!StringUtil.checkLength(set.getName() + "", 0, 20)) {
            return ResultData.build().error(getResString("err.length", this.getResString("name"), "0", "20"));
        }
        //验证导出sql配置的值是否合法
        if (StringUtils.isBlank(set.getExportSql())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("export.sql")));
        }
        //验证导入主表json的值是否合法
        if (StringUtils.isBlank(set.getImportJson())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("import.json")));
        }
        setBiz.save(set);
        return ResultData.build().success(set);
    }

    /**
     * @param sets 导入导出配置实体
     */
    @ApiOperation(value = "批量删除导入导出配置列表接口")
    @PostMapping("/delete")
    @ResponseBody
    @LogAnn(title = "删除导入导出配置", businessType = BusinessTypeEnum.DELETE)
    @RequiresPermissions("impexp:set:del")
    public ResultData delete(@RequestBody List<SetEntity> sets, HttpServletResponse response, HttpServletRequest request) {
        List<String> ids = sets.stream().map(p -> p.getId()).collect(Collectors.toList());
        setBiz.removeByIds(ids);
        return ResultData.build().success();
    }

    /**
     * 更新导入导出配置列表
     *
     * @param set 导入导出配置实体
     */
    @ApiOperation(value = "更新导入导出配置列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "导入导出标识", required = true, paramType = "query"),
            @ApiImplicitParam(name = "exportSql", value = "导出sql配置", required = true, paramType = "query"),
            @ApiImplicitParam(name = "importJson", value = "导入主表json", required = true, paramType = "query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required = false, paramType = "query"),
    })
    @PostMapping("/update")
    @ResponseBody
    @LogAnn(title = "更新导入导出配置", businessType = BusinessTypeEnum.UPDATE)
    @RequiresPermissions("impexp:set:update")
    public ResultData update(@ModelAttribute @ApiIgnore SetEntity set, HttpServletResponse response,
                             HttpServletRequest request) {
        if (super.validated("impexp_set", "name", set.getName(), set.getId(), "id")) {
            return ResultData.build().error(getResString("err.exist", this.getResString("name")));
        }
        //验证导入导出标识的值是否合法
        if (StringUtils.isBlank(set.getName())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("name")));
        }
        if (!StringUtil.checkLength(set.getName() + "", 0, 20)) {
            return ResultData.build().error(getResString("err.length", this.getResString("name"), "0", "20"));
        }
        //验证导出sql配置的值是否合法
        if (StringUtils.isBlank(set.getExportSql())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("export.sql")));
        }
        //验证导入主表json的值是否合法
        if (StringUtils.isBlank(set.getImportJson())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("import.json")));
        }
        setBiz.updateById(set);
        return ResultData.build().success(set);
    }

    @ApiOperation(value = "校验配置是否重复")
    @GetMapping("verify")
    @ResponseBody
    public ResultData verify(String fieldName, String fieldValue, String id, String idName) {
        boolean verify = false;
        if (StringUtils.isBlank(id)) {
            verify = super.validated("impexp_set", fieldName, fieldValue);
        } else {
            verify = super.validated("impexp_set", fieldName, fieldValue, id, idName);
        }
        if (verify) {
            return ResultData.build().success(false);
        } else {
            return ResultData.build().success(true);
        }
    }

}
