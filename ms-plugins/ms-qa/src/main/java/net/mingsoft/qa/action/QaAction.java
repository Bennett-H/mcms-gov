/**
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循铭软科技《保密协议》
 */


package net.mingsoft.qa.action;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import net.mingsoft.mdiy.bean.ModelJsonBean;
import net.mingsoft.qa.biz.IQaBiz;
import net.mingsoft.qa.entity.QaEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通用模型
 */
@Api(tags={"后端-问卷调查模块接口"})
@Controller
@RequestMapping("/${ms.manager.path}/qa/qa")
public class QaAction extends BaseAction {

    @Autowired
    private IQaBiz qaBiz;

    /**
     * 主页
     * @param response
     * @param request
     * @return
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("qa:qa:view")
    public String index(HttpServletResponse response, HttpServletRequest request){
        return "/qa/qa/index";
    }
    /**
     * form
     * @param response
     * @param request
     * @return
     */
    @ApiIgnore
    @GetMapping("/form")
    @RequiresPermissions(value = {"qa:qa:save", "qa:qa:update"}, logical = Logical.OR)
    public String form(HttpServletResponse response, HttpServletRequest request){
        return "/qa/qa/form";
    }


    @ApiOperation(value = "查询自定义模型列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qaName", value = "模型名称", required =false,paramType="query"),
            @ApiImplicitParam(name = "qaTableName", value = "模型表名", required =false,paramType="query"),
            @ApiImplicitParam(name = "appId", value = "应用编号", required =false,paramType="query"),
            @ApiImplicitParam(name = "modelJson", value = "json", required =false,paramType="query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
    @GetMapping("/list")
    @ResponseBody
    @RequiresPermissions("qa:qa:view")
    public ResultData list(@ModelAttribute @ApiIgnore QaEntity qaEntity, HttpServletResponse response, HttpServletRequest request) {
        BasicUtil.startPage();
        LambdaQueryWrapper<QaEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(qaEntity.getQaName())){
            wrapper.like(QaEntity::getQaName,qaEntity.getQaName());
        }
        List modelList = qaBiz.list(wrapper);
        return ResultData.build().success(new EUListBean(modelList,(int)BasicUtil.endPage(modelList).getTotal()));
    }

    /**
     * 通用渲染表单
     * @param response
     * @param request
     * @return
     */
    @ApiOperation(value = "查询自定义模型接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qaName", value = "模型名称", required =false,paramType="query"),
            @ApiImplicitParam(name = "qaTableName", value = "模型表名", required =false,paramType="query"),
            @ApiImplicitParam(name = "appId", value = "应用编号", required =false,paramType="query"),
            @ApiImplicitParam(name = "modelJson", value = "json", required =false,paramType="query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
    @GetMapping("/get")
    @ResponseBody
    @RequiresPermissions("qa:qa:view")
    public ResultData get(QaEntity qaEntity, HttpServletResponse response, HttpServletRequest request){
        if(qaEntity==null || StringUtils.isEmpty(qaEntity.getQaName())){
            return ResultData.build().error(this.getResString("err.error",this.getResString("model.name")));
        }
        QaEntity qa = qaBiz.getOne(new QueryWrapper<>(qaEntity));
        return ResultData.build().success(qa);
    }


    @ApiOperation(value = "导入自定义模型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelJson", value = "json", required =true,paramType="query"),
    })
    @LogAnn(title = "导入",businessType= BusinessTypeEnum.INSERT)
    @PostMapping("/importJson")
    @ResponseBody
    @RequiresPermissions("qa:qa:importJson")
    public ResultData importJson(@ModelAttribute @ApiIgnore QaEntity qaEntity, HttpServletResponse response, HttpServletRequest request, BindingResult result) {
        //验证json的值是否合法
        if(StringUtils.isBlank(qaEntity.getModelJson())){
            return ResultData.build().error(getResString("err.empty", this.getResString("model.json")));
        }
        ModelJsonBean modelJsonBean = new ModelJsonBean();
        try{
            modelJsonBean = JSONUtil.toBean(qaEntity.getModelJson(), ModelJsonBean.class);
        }catch (Exception e){
            return ResultData.build().error(getResString("err.error", this.getResString("model.json")));
        }
        // 保存导入的json模型
        if(qaBiz.importConfig(modelJsonBean)){
            return ResultData.build().success();
        }else {
            return ResultData.build().error(getResString("err.exist", this.getResString("table.name")));
        }
    }

    @ApiOperation(value = "更新导入自定义模型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelJson", value = "json", required =true,paramType="query"),
    })
    @LogAnn(title = "导入",businessType= BusinessTypeEnum.INSERT)
    @PostMapping("/updateJson")
    @ResponseBody
    @RequiresPermissions("qa:qa:importJson")
    public ResultData updateJson(@ModelAttribute @ApiIgnore QaEntity qaEntity, HttpServletResponse response, HttpServletRequest request, BindingResult result) {
        //验证json的值是否合法
        if(StringUtils.isBlank(qaEntity.getModelJson())){
            return ResultData.build().error(getResString("err.empty", this.getResString("model.json")));
        }
        if(StringUtils.isBlank(qaEntity.getId())){
            return ResultData.build().error(getResString("err.empty", this.getResString("id")));
        }
        ModelJsonBean modelJsonBean = new ModelJsonBean();
        try{
            modelJsonBean = JSONUtil.toBean(qaEntity.getModelJson(), ModelJsonBean.class);
        }catch (Exception e){
            return ResultData.build().error(getResString("err.error", this.getResString("model.json")));
        }
        // 保存导入的json模型
        if(qaBiz.updateConfig(qaEntity.getId(), modelJsonBean)){
            return ResultData.build().success();
        }else {
            return ResultData.build().error(getResString("err.exist", this.getResString("table.name")));
        }
    }

    @ApiOperation("更新数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qaName", value = "模型名称", required =false,paramType="query"),
            @ApiImplicitParam(name = "qaTableName", value = "模型表名", required =false,paramType="query"),
            @ApiImplicitParam(name = "appId", value = "应用编号", required =false,paramType="query"),
            @ApiImplicitParam(name = "modelJson", value = "json", required =false,paramType="query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
    @LogAnn(title = "更新自定义业务数据",businessType= BusinessTypeEnum.UPDATE)
    @PostMapping("update")
    @ResponseBody
    @RequiresPermissions("qa:qa:update")
    public ResultData update(@ModelAttribute @ApiIgnore QaEntity qaEntity,HttpServletRequest request, HttpServletResponse response) {
        if (qaEntity.getId() == null){
            return ResultData.build().error(getResString("err.error",getResString("id")));
        }
        qaEntity.setModelJson(null);
        qaEntity.setModelField(null);
        qaEntity.setQaTableName(null);
        qaBiz.updateById(qaEntity);
        return ResultData.build().success(qaEntity);

    }


    @ApiOperation(value = "批量删除自定义模型列表接口")
    @LogAnn(title = "批量删除自定义模型列表接口",businessType= BusinessTypeEnum.DELETE)
    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("qa:qa:del")
    public ResultData delete(@RequestBody List<QaEntity> qas, HttpServletResponse response, HttpServletRequest request) {
        List<String> ids = qas.stream().map(p -> p.getId()).collect(Collectors.toList());
        if (qaBiz.delete(ids)) {
            return ResultData.build().success();
        }else {
            return ResultData.build().error(getResString("err.error",getResString("id")));
        }

    }

}
