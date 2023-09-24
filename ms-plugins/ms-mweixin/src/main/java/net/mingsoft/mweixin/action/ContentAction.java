/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.action;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信文章导入
 * @author 铭软
 * 创建日期：2022-2-24 11:51:43<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-文章导入草稿接口"})
@Controller("mweixinWxContentAction")
@RequestMapping("/${ms.manager.path}/mweixin/content")
public class ContentAction extends BaseAction {


    /**
     * 返回ifream框架页面
     */
    @GetMapping("/main")
    public String main(HttpServletResponse response, HttpServletRequest request) {
        return "/mweixin/content/main";
    }

    /**
     * 返回文章列表
     */
    @GetMapping("/index")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/mweixin/content/index";
    }

}
