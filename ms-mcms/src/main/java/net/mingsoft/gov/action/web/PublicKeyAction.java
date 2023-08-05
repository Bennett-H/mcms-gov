/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.gov.action.web;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseAction;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 公钥管理控制层
 * @author 铭飞开发团队
 * @version
 * 版本号：100<br/>
 * 创建日期：2021-03-12 11:40:29<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"前端-政务版接口"})
@Controller("publicKeyAction")
@RequestMapping("/gov/publicKey")
public class PublicKeyAction extends BaseAction {

    public PublicKeyAction() {
        LOG.debug("PublicKeyAction init");
    }

    /**
     * 获取公钥
     * @param response
     * 响应
     */
    @ApiOperation(value = "获取公钥")
    @PostMapping(value = "/getPublicKey")
    @ResponseBody
    public ResultData getPublicKey(HttpServletRequest request, HttpServletResponse response) {
        String publicKey = ConfigUtil.getString("公私钥配置", "publicKey");
        if (StringUtils.isBlank(publicKey)) {
            return ResultData.build().error("公钥内容为空,请先初始化公钥!");
        }
        return ResultData.build().success(publicKey);
    }
}
