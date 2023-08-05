/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.people.action;

import cn.hutool.core.util.EnumUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.people.bean.PeopleLogBean;
import net.mingsoft.people.biz.IPeopleLogBiz;
import net.mingsoft.people.constant.e.PeopleLogTypeEnum;
import net.mingsoft.people.entity.PeopleLogEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 会员日志管理控制层
 *
 * @author 铭软科技
 * 创建日期：2023-6-5 17:34:20<br/>
 * 历史修订：<br/>
 */
@Api(tags = "后台-会员日志接口")
@Controller("mpeoplePeopleLogAction")
@RequestMapping("/${ms.manager.path}/people/peopleLog")
public class PeopleLogAction extends net.mingsoft.people.action.BaseAction {


    /**
     * 注入会员日志业务层
     */
    @Autowired
    private IPeopleLogBiz peopleLogBiz;

    /**
     * 返回主界面index
     */
    @GetMapping("/index")
    @RequiresPermissions("people:peopleLog:view")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/people/people-log/index";
    }

    /**
     * 查询会员日志列表
     *
     * @param peopleLog 会员日志实体
     */
    @ApiOperation(value = "查询会员日志列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "peopleId", value = "会员编号", paramType = "query"),
            @ApiImplicitParam(name = "logIp", value = "IP", paramType = "query"),
            @ApiImplicitParam(name = "logAddr", value = "所在地区", paramType = "query"),
            @ApiImplicitParam(name = "logType", value = "日志类型", paramType = "query"),
            @ApiImplicitParam(name = "logInfo", value = "日志信息", paramType = "query"),
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResultData list(@ModelAttribute @ApiIgnore PeopleLogBean peopleLog, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
        BasicUtil.startPage();
        List<PeopleLogEntity> logList = peopleLogBiz.query(peopleLog);
        return ResultData.build().success(new EUListBean(logList,(int)BasicUtil.endPage(logList).getTotal()));
    }

    /**
     * 返回编辑界面peopleLog的form
     */
    @GetMapping("/form")
    public String form(@ModelAttribute PeopleLogEntity peopleLog, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
        return "/people/people-log/form";
    }


    /**
     * 获取会员日志
     *
     * @param peopleLog 会员日志实体
     */
    @ApiOperation(value = "获取会员日志列表接口")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, paramType = "query")
    @GetMapping("/get")
    @ResponseBody
    public ResultData get(@ModelAttribute @ApiIgnore PeopleLogEntity peopleLog, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
        if (peopleLog.getId() == null) {
            return ResultData.build().error();
        }
        PeopleLogEntity _peopleLog = (PeopleLogEntity) peopleLogBiz.getById(peopleLog.getId());
        return ResultData.build().success(_peopleLog);
    }

    /**
     * 获取日志类型枚举类
     */
    @ApiOperation(value = "获取日志类型枚举类")
    @PostMapping("/queryLogType")
    @ResponseBody
    public ResultData queryLogType(HttpServletResponse response, HttpServletRequest request) {
        List<Object> label = EnumUtil.getFieldValues(PeopleLogTypeEnum.class, "label");
        return ResultData.build().success(label);
    }

}
