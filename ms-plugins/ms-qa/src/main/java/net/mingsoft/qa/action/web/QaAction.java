/**
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循铭软科技《保密协议》
 */


package net.mingsoft.qa.action.web;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.qa.action.BaseAction;
import net.mingsoft.qa.biz.IQaBiz;
import net.mingsoft.qa.entity.QaEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * 通用模型
 */
@Api(tags = {"前端-问卷调查模块接口"})
@Controller("webQaAction")
@RequestMapping("/qa/qa")
public class QaAction extends BaseAction {


    @Autowired
    private IQaBiz qaBiz;


    /**
     * 根据投票名称获取渲染表单
     *
     * @param response
     * @param request
     * @return
     */
    @ApiOperation(value = "根据投票名称获取渲染表单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qaName", value = "投票名称", required = true, paramType = "query"),
    })
    @GetMapping("/get")
    @ResponseBody
    public ResultData get(String qaName, HttpServletResponse response, HttpServletRequest request) {
        //获取表单id
        if (StringUtils.isBlank(qaName)) {
            return ResultData.build().error();
        }
        QueryWrapper<QaEntity> queryWrapper = new QueryWrapper<QaEntity>();
        queryWrapper.like("qa_name", qaName);
        QaEntity qa = qaBiz.getOne(queryWrapper);
        if (qa != null) {

            if (qa.getStartTime() == null || qa.getEndTime() == null){
                String modelJson = qa.getModelJson();
                Map<String, String> map = JSONUtil.toBean(modelJson,Map.class);
                map.put("html", "<h1 align='center' style='margin-bottom: 50px'>问卷调查没有设置时间</h2>");
                qa.setModelJson(JSONUtil.toJsonStr(map));
                return ResultData.build().success(qa);
            }
            Long startTime = qa.getStartTime().getTime();
            Long endTime = qa.getEndTime().getTime();
            Long now = new Date().getTime();
            //若不在时间范围内
            if (!(startTime <= now && now <= endTime)) {
                String modelJson = qa.getModelJson();
                Map<String, String> map = JSONUtil.toBean(modelJson,Map.class);
                map.put("html", "<h1 align='center' style='margin-bottom: 50px'>问卷调查已失效</h2>");
                qa.setModelJson(JSONUtil.toJsonStr(map));
            }

            return ResultData.build().success(qa);
        }
        return ResultData.build().error();
    }

}
