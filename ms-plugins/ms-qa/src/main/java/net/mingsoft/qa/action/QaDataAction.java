/**
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循铭软科技《保密协议》
 */


package net.mingsoft.qa.action;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.IpUtils;
import net.mingsoft.qa.biz.IQaBiz;
import net.mingsoft.qa.biz.IQaDataBiz;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 通用问卷调查数据
 */
@Api(tags={"后端-问卷调查模块接口"})
@Controller
@RequestMapping("/${ms.manager.path}/qa/qaData")
public class QaDataAction extends BaseAction {


    @Autowired
    private IQaDataBiz qaDataBiz;

    @Autowired
    private IQaBiz qaBiz;

    /**
     * 扩展模型表单
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("qa:qaData:view")
    public String list(HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model){
        return "/qa/qa-data/index";
    }

    /**
     * 扩展模型表单
     */
    @ApiIgnore
    @GetMapping("/form")
    @RequiresPermissions(value = {"qa:qaData:save", "qa:qaData:update"}, logical = Logical.OR)
    public String form(HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model){
        return "/qa/qa-data/form";
    }

    /**
     * 提供前端查询自定义表单提交数据
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "提供前端查询自定义表单提交数据")
    @ApiImplicitParam(name = "modelId", value = "模型编号", required = true, paramType = "query")
    @RequestMapping(value = "/queryData", method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    @RequiresPermissions("qa:qaData:view")
    public ResultData queryData( HttpServletRequest request, HttpServletResponse response) {
        //获取表单id
        Map<String, Object> map = BasicUtil.assemblyRequestMap();
        int modelId = Integer.parseInt(map.get("modelId").toString());
        if(modelId <= 0){
            return ResultData.build().error(getResString("err.empty",getResString("model.id")));
        }
        List list = qaDataBiz.queryDiyFormData(modelId,BasicUtil.assemblyRequestMap());
        if (ObjectUtil.isNotNull(list) ) {
            return ResultData.build().success(new EUListBean(list,(int)BasicUtil.endPage(list).getTotal()));
        }
        return ResultData.build().error();
    }

    /**
     * 提供前端查询自定义表单提交数据
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "提供前端查询自定义表单提交数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型编号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "主键编号", required = true, paramType = "query")
    })
    @GetMapping("/getData")
    @ResponseBody
    @RequiresPermissions("qa:qaData:view")
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
        Object object = qaDataBiz.getFormData(modelId,id);
        if (ObjectUtil.isNotNull(object) ) {
            return ResultData.build().success(object);
        }
        return ResultData.build().error();
    }


    @ApiOperation("保存")
    @ApiImplicitParam(name = "modelId", value = "模型编号", required = true, paramType = "query")
    @LogAnn(title = "更新自定义业务数据",businessType= BusinessTypeEnum.INSERT)
    @PostMapping("save")
    @ResponseBody
    @RequiresPermissions("qa:qaData:save")
    public ResultData save(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = BasicUtil.assemblyRequestMap();
        //访问设备
        String deviceType = BasicUtil.isMobileDevice() ? "phone" : "pc";
        //根据ip查询用户地址
        String address = IpUtils.getRealAddressByIp(BasicUtil.getIp());
        map.put("qa_IP", BasicUtil.getIp());
        map.put("qa_device_type", deviceType);
        map.put("qa_address", address);
        map.put("create_date", new Date());
        map.put("update_date", new Date());
        int modelId = Integer.parseInt(map.get("modelId").toString());
        if(modelId <= 0){
            return ResultData.build().error(getResString("err.empty",getResString("model.id")));
        }
        if (qaDataBiz.saveDiyFormData(modelId,map)) {
            return ResultData.build().success();
        } else {
            return ResultData.build().error(getResString("err.error",getResString("model.id")));
        }
    }

    @ApiOperation("更新自定义业务数据")
    @ApiImplicitParam(name = "modelId", value = "模型编号", required = true, paramType = "query")
    @LogAnn(title = "更新自定义业务数据",businessType= BusinessTypeEnum.UPDATE)
    @PostMapping("update")
    @ResponseBody
    @RequiresPermissions("qa:qaData:update")
    public ResultData update(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = BasicUtil.assemblyRequestMap();
        int modelId = Integer.parseInt(map.get("modelId").toString());
        if(modelId <= 0){
            return ResultData.build().error(getResString("err.empty",getResString("model.id")));
        }
        if (qaDataBiz.updateDiyFormData(modelId,map)) {
            return ResultData.build().success();
        }else {
            return ResultData.build().error(getResString("err.error",getResString("model.id")));
        }
    }

    @ApiOperation(value = "批量删除自定义业务数据接口")
    @LogAnn(title = "批量删除自定义业务数据接口",businessType= BusinessTypeEnum.DELETE)
    @PostMapping("delete")
    @ResponseBody
    @RequiresPermissions("qa:qaData:del")
    public ResultData delete(@RequestParam("modelId") String modelId, HttpServletResponse response, HttpServletRequest request) {
        int[] ids = BasicUtil.getInts("ids",",");
        if (StringUtils.isEmpty(modelId)) {
            return ResultData.build().error(getResString("err.empty",getResString("model.id")));
        }
        for (int id : ids) {
            qaDataBiz.deleteQueryDiyFormData(id,modelId);
        }
        return ResultData.build().success();
    }

}
