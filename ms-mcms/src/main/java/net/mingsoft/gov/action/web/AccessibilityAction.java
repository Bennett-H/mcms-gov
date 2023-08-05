/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.gov.action.web;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
@Api(tags = {"前端-无障碍配置接口"})
@Controller("AccessibilityAction")
@RequestMapping("/gov/accessibility")
public class AccessibilityAction {

    @ApiOperation("提供前台token")
    @ResponseBody
    @GetMapping("/getToken")
    public ResultData getToken(HttpServletRequest request, HttpServletResponse response) {
        String apiKey = ConfigUtil.getString("无障碍配置", "accessibilityApiKey");
        String apiSecret = ConfigUtil.getString("无障碍配置", "accessibilityApiSecret");
        //是否启动
        String start = ConfigUtil.getString("无障碍配置","accessibilityStart");
        //背景色
        String color = ConfigUtil.getString("无障碍配置","accessibilityColor");
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + apiKey
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + apiSecret;
        String result = HttpUtil.get(getAccessTokenUrl);
        JSONObject jsonObject = new JSONObject(result);
        String accessToken = (String) jsonObject.get("access_token");
        HashMap<String, Object> map = new HashMap<>();
        map.put("accessToken",accessToken);
        map.put("start",start);
        map.put("color",color);
        return ResultData.build().success(map);
    }
}
