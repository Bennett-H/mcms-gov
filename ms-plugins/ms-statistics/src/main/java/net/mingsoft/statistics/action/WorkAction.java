/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.statistics.action;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 工作量统计页面
 */
@ApiIgnore
@Controller
@RequestMapping("/${ms.manager.path}/statistics/work")
public class WorkAction extends BaseAction{


    /**
     * 管理员工作量统计
     */
    @GetMapping("/manager")
    @RequiresPermissions("manager:statistics:view")
    public String manager(HttpServletResponse response, HttpServletRequest request){
        return "/statistics/work/manager";
    }


}
