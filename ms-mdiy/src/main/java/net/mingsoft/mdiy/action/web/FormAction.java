/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.action.web;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.mdiy.action.BaseAction;
import net.mingsoft.mdiy.biz.IModelBiz;
import net.mingsoft.mdiy.entity.ModelEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 通用模型
 */
@Api(tags={"前端-自定义模块接口"})
@Controller("webFormAction")
@RequestMapping("/mdiy/form")
public class FormAction extends BaseAction {

    /**
     * 注入自定义模型业务层
     */
    @Autowired
    private IModelBiz modelBiz;

    /**
     * 根据模型名称获取渲染表单
     *
     * @param response
     * @param request
     * @return
     */
    @ApiOperation(value = "根据模型名称获取渲染表单")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "modelName", value = "模型名称", required = true, paramType="query"),
    })
    @GetMapping("/get")
    @ResponseBody
    public ResultData get(String modelName, HttpServletResponse response, HttpServletRequest request) {
        //获取表单id
        if (StringUtils.isBlank(modelName)) {
            return ResultData.build().error();
        }
        QueryWrapper<ModelEntity> queryWrapper = new QueryWrapper<ModelEntity>();
        queryWrapper.like("model_name", modelName);
        ModelEntity model = modelBiz.getOne(queryWrapper);
        if(model!=null) {
            //判断是否允许外部提交
            if(Boolean.parseBoolean(JSONUtil.toBean(model.getModelJson(), Map.class).get("isWebSubmit").toString())) {
                return ResultData.build().success(model);
            }
        }
        return ResultData.build().error("此业务不允许外部提交");
    }

}
