/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.action;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.strategy.IModelStrategy;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mdiy.biz.IConfigBiz;
import net.mingsoft.mdiy.biz.IModelBiz;
import net.mingsoft.mdiy.entity.ConfigEntity;
import net.mingsoft.mdiy.entity.ModelEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 通用模型配置数据
 * 历史修订: 2022-1-27 get() update() 从使用configBiz改为configUtil
 */
@Api(tags={"后端-自定义模块接口"})
@Controller
@RequestMapping("/${ms.manager.path}/mdiy/config/data")
public class ConfigDataAction extends BaseAction {

    /**
     * 注入自定义配置业务层
     */
    @Autowired
    private IConfigBiz configBiz;

    /**
     * 注入自定义模型业务层
     */
    @Autowired
    private IModelBiz modelBiz;


    @Autowired
    private IModelStrategy modelStrategy;



    /**
     * 自定义配置
     */
    @ApiIgnore
    @GetMapping("/form")
    public String form(HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        return "/mdiy/config/data/form";
    }

    /**
     * 配置数据获取
     *
     * @param response
     * @param request
     * @return
     */
    @ApiOperation(value = "配置数据获取接口")
    @GetMapping("/get")
    @ResponseBody
    public ResultData get(HttpServletResponse response, HttpServletRequest request) {
        String modelId = BasicUtil.getString("modelId");
        if (StringUtils.isEmpty(modelId)) {
            return ResultData.build().error(getResString("err.empty",getResString("model.id")));
        }
        ModelEntity modelEntity = modelBiz.getById(modelId);
        if (modelEntity == null) {
            return ResultData.build().error(getResString("err.error", this.getResString("model.id")));
        }
        if(!getPermissions("mdiy:configData:view","mdiy:configData:" + modelEntity.getModelName() + ":view")){
            return ResultData.build().error("没有权限!");
        }
        ConfigEntity configEntity = ConfigUtil.getEntity(modelEntity.getModelName());
        if (configEntity == null) {
            return ResultData.build().error(getResString("err.error", getResString("config.name")));
        }
        return ResultData.build().success(JSONUtil.parseObj(configEntity.getConfigData()));
    }

    @ApiOperation(value = "更新自定义配置")
    @LogAnn(title = "更新自定义配置", businessType = BusinessTypeEnum.UPDATE)
    @PostMapping("/update")
    @ResponseBody
    public ResultData update(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> map = BasicUtil.assemblyRequestMap();
        String modelId = map.get("modelId").toString();
        if (StringUtils.isEmpty(modelId)) {
            return ResultData.build().error(getResString("err.empty",getResString("model.id")));
        }
        ModelEntity modelEntity = modelBiz.getById(modelId);
        if (modelEntity == null) {
            return ResultData.build().error(getResString("err.error", this.getResString("model.id")));
        }
        if(!getPermissions("mdiy:configData:update","mdiy:configData:" + modelEntity.getModelName() + ":update")){
            return ResultData.build().error("没有权限!");
        }
        ConfigEntity configEntity = ConfigUtil.getEntity(modelEntity.getModelName());
        if (configEntity == null) {
            return ResultData.build().error(getResString("err.empty", this.getResString("config.name")));
        }

        configEntity.setConfigData(JSONUtil.toJsonStr(map));
        configBiz.updateById(configEntity);
        return ResultData.build().success(configEntity);
    }

    /**
     *  获取配置中的key指定value值
     * @param configName 配置名称
     * @param key 配置的key值
     * @param response
     * @param request
     * @return
     */
    @ApiOperation(value = "获取自定义配置中的key指定value值接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "configName", value = "配置名称", required =true,paramType="query"),
            @ApiImplicitParam(name = "key", value = "配置的key值", required =true,paramType="query"),
    })
    @GetMapping("/getMap")
    @ResponseBody
    public ResultData getMap(String configName,String key, HttpServletResponse response, HttpServletRequest request){
        if(StringUtils.isBlank(key))  {
            return ResultData.build().success(ConfigUtil.getMap(configName));
        } else {
            return ResultData.build().success(ConfigUtil.getString(configName,key));
        }

    }

}
