/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.action.web;

import cn.hutool.http.HttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 解决防盗链的问题
 * @Author: 铭飞开源团队--huise
 * @Date: 2019/6/4 15:55
 */
@Api(tags = {"前端-微信模块接口"})
@Controller("fileForwardAction")
@RequestMapping("/mweixin/forward")
public class FileForwardAction {
    /**
     * 传入一个src 返回原图片
     * @param src  图片链接
     * @param response
     * @param request
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "微信素材重定向接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "src", value = "图片链接", required =true,paramType="query"),
    })
    @GetMapping(value = "/image",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] image(String src, HttpServletResponse response, HttpServletRequest request) throws IOException {
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        HttpUtil.download(src,outputStream,true);
       return  outputStream.toByteArray();
    }

}
