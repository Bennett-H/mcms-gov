/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.mdiy.action;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mdiy.biz.IModelBiz;
import net.mingsoft.mdiy.biz.IModelDataBiz;
import net.mingsoft.mdiy.constant.e.ModelCustomTypeEnum;
import net.mingsoft.mdiy.entity.ModelEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 通用业务数据
 */
@Api(tags={"后端-自定义模块接口"})
@Controller
@RequestMapping("/${ms.manager.path}/mdiy/form/data")
public class FormDataAction extends BaseAction {


    /**
     * 注入自定义配置业务层
     */
    @Autowired
    private IModelDataBiz modelDataBiz;

    @Autowired
    private IModelBiz modelBiz;

    /**
     * 扩展模型表单
     */
    @ApiIgnore
    @GetMapping("/index")
    public String index(HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model){
        String modelName = BasicUtil.getString("modelName");
        ModelEntity modelEntity = modelBiz.getOne(new LambdaQueryWrapper<ModelEntity>()
                .eq(ModelEntity::getModelName, modelName)
                .eq(ModelEntity::getModelCustomType, ModelCustomTypeEnum.FORM.getLabel()));
        if (modelEntity != null){
            request.setAttribute("modelId",modelEntity.getId());
        }
        return "/mdiy/form/data/index";
    }

    /**
     * 扩展模型表单
     */
    @ApiIgnore
    @GetMapping("/form")
    public String form(HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model){
        return "/mdiy/form/data/form";
    }

    /**
     * 提供前端查询自定义表单提交数据
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "提供后台查询自定义表单提交数据")
    @ApiImplicitParam(name = "modelId", value = "模型编号", required = true, paramType = "query")
    @RequestMapping(value = "/queryData", method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResultData queryData( HttpServletRequest request, HttpServletResponse response) {
        //获取表单id
        Map<String, Object> map = BasicUtil.assemblyRequestMap();
        if(map.get("modelId") == null || StringUtils.isBlank(map.get("modelId").toString())){
            return ResultData.build().error(getResString("err.empty",getResString("model.id")));
        }
        ModelEntity modelEntity = modelBiz.getById(map.get("modelId").toString());

        if(!getPermissions("mdiy:formData:view","mdiy:formData:" + modelEntity.getModelName() + ":view")){
            return ResultData.build().error("没有权限!");
        }
        // 默认排序
        map.putIfAbsent("order", "desc");
        map.putIfAbsent("orderBy", "id");
        List list = modelDataBiz.queryDiyFormData(map.get("modelId").toString(),map);
        return ResultData.build().success(new EUListBean(list,(int)BasicUtil.endPage(list).getTotal()));
    }

    /**
     * 提供前端查询自定义表单提交数据
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "提供后台查询自定义表单提交数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型编号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "主键编号", required = true, paramType = "query")
    })
    @GetMapping("/getData")
    @ResponseBody
    public ResultData getData( HttpServletRequest request, HttpServletResponse response) {
        //获取表单id
        String modelId = BasicUtil.getString("modelId");
        String id = BasicUtil.getString("id");
        if(StringUtils.isEmpty(modelId)){
            return ResultData.build().error(getResString("err.empty",getResString("model.id")));
        }
        if(StringUtils.isEmpty(id)){
            return ResultData.build().error(getResString("err.empty",getResString("id")));
        }
        Object object = modelDataBiz.getFormData(modelId,id);
        if (ObjectUtil.isNotNull(object) ) {
            return ResultData.build().success(object);
        }
        return ResultData.build().error();
    }


    @ApiOperation("保存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelName", value = "业务模型名称", required = true, paramType="query"),
            @ApiImplicitParam(name = "modelId", value = "业务模型Id", required = false, paramType="query"),
    })
    @LogAnn(title = "更新自定义业务数据",businessType= BusinessTypeEnum.INSERT)
    @PostMapping("save")
    @ResponseBody
    public ResultData save(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = BasicUtil.assemblyRequestMap();
        String modelName = BasicUtil.getString("modelName");
        String modelId = BasicUtil.getString("modelId");
        if(StringUtils.isBlank(modelName) && StringUtils.isBlank(modelId)){
            return ResultData.build().error(getResString("err.empty",getResString("model.id")));
        }

        LambdaQueryWrapper<ModelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(modelName),ModelEntity::getModelName, modelName)
                .eq(StringUtils.isNotEmpty(modelId), ModelEntity::getId, modelId)
                .eq(ModelEntity::getModelCustomType, ModelCustomTypeEnum.FORM.toString());
        ModelEntity modelEntity = modelBiz.getOne(wrapper, true);
        if (modelEntity == null) {
            return ResultData.build().error(getResString("err.not.exist", this.getResString("model.name")));
        }
        if(!getPermissions("mdiy:formData:save","mdiy:formData:" + modelEntity.getModelName() + ":save")){
            return ResultData.build().error("没有权限!");
        }
        if (modelDataBiz.saveDiyFormData(modelEntity.getId(),map)) {
            return ResultData.build().success();
        }else {
            return ResultData.build().error(getResString("err.error",getResString("model.id")));
        }
    }

    @ApiOperation("更新自定义业务数据")
    @ApiImplicitParam(name = "modelId", value = "模型编号", required = true, paramType = "query")
    @LogAnn(title = "更新自定义业务数据",businessType= BusinessTypeEnum.UPDATE)
    @PostMapping("update")
    @ResponseBody
    public ResultData update(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = BasicUtil.assemblyRequestMap();
        String modelId = map.get("modelId").toString();
        if(StringUtils.isBlank(modelId)){
            return ResultData.build().error(getResString("err.empty",getResString("model.id")));
        }
        ModelEntity modelEntity = modelBiz.getById(modelId);
        if(!getPermissions("mdiy:formData:update","mdiy:formData:" + modelEntity.getModelName() + ":update")){
            return ResultData.build().error("没有权限!");
        }
        if (modelDataBiz.updateDiyFormData(modelId,map)) {
            return ResultData.build().success();
        }else {
            return ResultData.build().error(getResString("err.error",getResString("model.id")));
        }
    }

    @ApiOperation(value = "批量删除自定义业务数据接口")
    @LogAnn(title = "批量删除自定义业务数据接口",businessType= BusinessTypeEnum.DELETE)
    @PostMapping("delete")
    @ResponseBody
    public ResultData delete(@RequestParam("modelId") String modelId, HttpServletResponse response, HttpServletRequest request) {
        int[] ids = BasicUtil.getInts("ids",",");

        if (StringUtils.isEmpty(modelId)) {
            return ResultData.build().error(getResString("err.empty",getResString("model.id")));
        }
        ModelEntity modelEntity = modelBiz.getById(modelId);

        if(!getPermissions("mdiy:formData:del","mdiy:formData:" + modelEntity.getModelName() + ":del")){
            return ResultData.build().error("没有权限!");
        }
        for (int id : ids) {
            modelDataBiz.deleteQueryDiyFormData(id,modelId);
        }
        return ResultData.build().success();
    }

}
