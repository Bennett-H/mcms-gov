/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.action.web;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mdiy.action.BaseAction;
import net.mingsoft.mdiy.biz.IModelBiz;
import net.mingsoft.mdiy.biz.IModelDataBiz;
import net.mingsoft.mdiy.constant.e.ModelCustomTypeEnum;
import net.mingsoft.mdiy.entity.ModelEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 通用业务数据
 */
@Api(tags={"前端-自定义模块接口"})
@Controller("webFormDataAction")
@RequestMapping("/mdiy/form/data")
public class FormDataAction extends BaseAction {

    /**
     * 注入自定义配置业务层
     */
    @Autowired
    private IModelDataBiz modelDataBiz;

    /**
     * 注入自定义模型
     */
    @Autowired
    private IModelBiz modelBiz;


    @ApiOperation("保存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelName", value = "业务模型名称", required = true, paramType="query"),
            @ApiImplicitParam(name = "modelId", value = "业务模型Id", required = false, paramType="query"),
            @ApiImplicitParam(name = "rand_code", value = "验证码", required = false, paramType = "query"),
    })
    @PostMapping("save")
    @ResponseBody
    public ResultData save(HttpServletRequest request, HttpServletResponse response) {
        // 做modelId和modelName兼容
        String modelName = BasicUtil.getString("modelName");
        String modelId = BasicUtil.getString("modelId");
        if(StringUtils.isEmpty(modelName) && StringUtils.isEmpty(modelId)){
            return ResultData.build().error(getResString("err.empty",getResString("model.name")));
        }
        LOG.debug("保存表单");
        // 根据自定义模型Id和自定义模型名称查询
        LambdaQueryWrapper<ModelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(modelName),ModelEntity::getModelName, modelName)
                .eq(StringUtils.isNotEmpty(modelId), ModelEntity::getId, modelId)
                .eq(ModelEntity::getModelCustomType, ModelCustomTypeEnum.FORM.toString());
        ModelEntity model = modelBiz.getOne(wrapper, true);
        if (model == null) {
            return ResultData.build().error(getResString("err.not.exist", this.getResString("model.name")));
        }
        //判断是否允许外部提交
        if (!Boolean.parseBoolean(JSONUtil.toBean(model.getModelJson(), Map.class).get("isWebSubmit").toString())) {
            ResultData.build().error(getResString("err.error", this.getResString("model.name")));
        }
        // 判断验证码是否校验, isWebCode如果为空，默认要验证(以防之前的没有isWebCode,做一个兼容)
        if (ObjectUtil.isNull(JSONUtil.toBean(model.getModelJson(), Map.class).get("isWebCode")) || Boolean.parseBoolean(JSONUtil.toBean(model.getModelJson(), Map.class).get("isWebCode").toString())) {
            //验证码
            if (!checkRandCode()) {
                LOG.debug("验证码错误");
                return ResultData.build().error(getResString("err.error", this.getResString("rand.code")));
            }
        }
        Map<String, Object> map = BasicUtil.assemblyRequestMap();
        if (modelDataBiz.saveDiyFormData(model.getId(), map)) {
            return ResultData.build().success();
        } else {
            return ResultData.build().error(getResString("fail", this.getResString("save")));
        }

    }

    /**
     * 提供前端查询自定义表单提交数据
     *
     * @param modelName 业务名称
     * @param request
     * @param response
     */
    @ApiOperation(value = "提供前端查询自定义表单提交数据")
    @ApiImplicitParam(name = "modelName", value = "业务名称", required = true,paramType = "query")
    @GetMapping("list")
    @ResponseBody
    public ResultData list(@RequestParam("modelName")String modelName, HttpServletRequest request, HttpServletResponse response) {
        //判断传入的加密数字是否能转换成整形
        QueryWrapper<ModelEntity> queryWrapper = new QueryWrapper<ModelEntity>();
        queryWrapper.like("model_name", modelName);
        ModelEntity model = modelBiz.getOne(queryWrapper);
        if (model != null) {
            //判断是否允许外部提交
            if (!Boolean.parseBoolean(JSONUtil.toBean(model.getModelJson(), Map.class).get("isWebSubmit").toString())) {
                ResultData.build().error();
            }
            List list = modelDataBiz.queryDiyFormData(model.getId(), BasicUtil.assemblyRequestMap());
            if (ObjectUtil.isNotNull(list)) {
                return ResultData.build().success(new EUListBean(list, (int) BasicUtil.endPage(list).getTotal()));
            }
            return ResultData.build().error();
        }
        return ResultData.build().error();
    }


}
