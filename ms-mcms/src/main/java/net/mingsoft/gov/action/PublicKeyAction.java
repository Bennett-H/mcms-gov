/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.gov.action;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseAction;
import net.mingsoft.co.util.RSAUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

/**
 * 公钥管理控制层
 * @author 铭飞开发团队
 * @version
 * 版本号：100<br/>
 * 创建日期：2021-03-12 11:40:29<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-公钥控制器"})
@Controller("KeyAction")
@RequestMapping("/${ms.manager.path}/gov/publicKey")
public class PublicKeyAction extends BaseAction {

    public PublicKeyAction() {
        LOG.debug("PublicKeyAction init");
    }

    @ApiIgnore
    @GetMapping("index")
    @RequiresPermissions("gov:publicKey:view")
    public String index(){
        return "/gov/key/index";
    }

    /**
     * 获取公钥
     * @param response
     * 响应
     */
    @ApiOperation(value = "初始化公钥私钥")
    @GetMapping(value = "/initKey")
    @ResponseBody
    @RequiresPermissions("gov:publicKey:initKey")
    public ResultData initKey(HttpServletRequest request, HttpServletResponse response) throws Exception{

        Map<String, Key> stringKeyMap = RSAUtils.initKey();
        String publicKey = RSAUtils.getPublicKey(stringKeyMap);
        String privateKey = RSAUtils.getPrivateKey(stringKeyMap);
        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put("publicKey",publicKey);
        resultMap.put("privateKey",privateKey);

        return ResultData.build().success(resultMap);
    }
}
