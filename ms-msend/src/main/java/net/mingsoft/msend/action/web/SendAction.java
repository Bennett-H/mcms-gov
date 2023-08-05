/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.msend.action.web;

import cn.hutool.json.JSONUtil;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.msend.bean.SendBean;
import net.mingsoft.msend.util.SendUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 邮件管理控制层
 *
 * @author 铭飞开发团队
 * @version 版本号：0.0<br/>
 *          创建日期：2017-8-24 14:41:18<br/>
 *          历史修订：<br/>
 */
@Api(tags={"前端-发送模块接口"})
@Controller("sendAction")
@RequestMapping("/msend")
public class SendAction extends net.mingsoft.msend.action.BaseAction {


    @ApiOperation(value = "自由调用邮箱接口")
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "receive", value = "接收者（邮箱或手机号）", required =true,paramType="query"),
            @ApiImplicitParam(name = "modelCode", value = "模板编码（后台AES加密过的）", required = true,paramType="query"),
            @ApiImplicitParam(name = "content", value = "消息内容map，格式:{key1:value1,key2:value2}", required = true,paramType="query"),
            @ApiImplicitParam(name = "type", value = "消息类型  邮件配置 |SendClound邮件配置  | SendClound短信配置", required = true,paramType="query"),
            @ApiImplicitParam(name = "tokenSession", value = "认证", required = true,paramType="query"),

    })
    @PostMapping("/send")
    @ResponseBody
    public ResultData send(SendBean send, HttpServletRequest request, HttpServletResponse response) throws TemplateException, IOException {

        Object obj = BasicUtil.getSession("tokenSession");
        if(StringUtils.isBlank(send.getTokenSession()) || obj==null || !send.getTokenSession().equals(obj.toString())) {
            return ResultData.build().error("tokenSession验证失败");
        }
        // 验证模板编码是否为空
        if (StringUtils.isBlank(send.getModelCode())) {
            return ResultData.build().error(
                    this.getResString("err.error", this.getResString("model.code")));
        }

        boolean status = SendUtil.send(send.getConfigType(),send.getModelCode(), send.getReceive(), JSONUtil.toBean(send.getContent(),Map.class));

        if(status){
            return ResultData.build().success();
        }else {
            return ResultData.build().error("发送失败");
        }
    }

}
