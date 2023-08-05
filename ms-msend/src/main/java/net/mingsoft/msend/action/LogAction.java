/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.msend.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.msend.biz.ILogBiz;
import net.mingsoft.msend.entity.LogEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 发送日志管理控制层
 * @author 铭飞开发团队
 * @version
 * 版本号：0.0<br/>
 * 创建日期：2017-8-24 14:41:18<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-发送模块接口"})
@Controller("sendLogAction")
@RequestMapping("/${ms.manager.path}/msend/log")
public class LogAction extends net.mingsoft.msend.action.BaseAction {

    /**
     * 注入发送日志业务层
     */
    @Autowired
    private ILogBiz logBiz;

    /**
     * 返回主界面index
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("log:view")
    public String index(HttpServletResponse response, HttpServletRequest request) {
        return "/msend/log/index";
    }

    /**
     * 查询发送日志列表
     * @param log 发送日志实体
     * <i>log参数包含字段信息参考：</i><br/>
     * logId <br/>
     * appId 应用编号<br/>
     * logDatetime 时间<br/>
     * logContent 接收内容<br/>
     * logReceive 接收人<br/>
     * logType 日志类型0邮件1短信<br/>
     * <dt><span class="strong">返回</span></dt><br/>
     * <dd>[<br/>
     * { <br/>
     * logId: <br/>
     * appId: 应用编号<br/>
     * logDatetime: 时间<br/>
     * logContent: 接收内容<br/>
     * logReceive: 接收人<br/>
     * logType: 日志类型0邮件1短信<br/>
     * }<br/>
     * ]</dd><br/>
     */
    @ApiOperation(value = "查询发送日志列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logDatetime", value = "时间", required = false, paramType = "query"),
            @ApiImplicitParam(name = "logContent", value = "接收内容", required = false, paramType = "query"),
            @ApiImplicitParam(name = "logReceive", value = "接收人", required = false, paramType = "query"),
            @ApiImplicitParam(name = "logType", value = "日志类型0邮件1短信", required = false, paramType = "query")
    })
    @GetMapping("/list")
    @ResponseBody
    public ResultData list(@ModelAttribute @ApiIgnore LogEntity log, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
        BasicUtil.startPage();
        List logList = logBiz.query(log);
        return ResultData.build().success(new EUListBean(logList, (int) BasicUtil.endPage(logList).getTotal()));
    }

    /**
     * 返回编辑界面log_form
     */
    @ApiIgnore
    @GetMapping("/form")
    @RequiresPermissions("log:view")
    public String form(@ModelAttribute @ApiIgnore LogEntity log, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
        if (!StringUtils.isEmpty(log.getId())) {
            LambdaQueryWrapper<LogEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LogEntity::getIntId,log.getIntId());
            BaseEntity logEntity = logBiz.getOne(wrapper,false);
            model.addAttribute("logEntity", logEntity);
        }

        return "/msend/log/form";
    }

    /**
     * 获取发送日志
     * @param log 发送日志实体
     * <i>log参数包含字段信息参考：</i><br/>
     * logId <br/>
     * appId 应用编号<br/>
     * logDatetime 时间<br/>
     * logContent 接收内容<br/>
     * logReceive 接收人<br/>
     * logType 日志类型0邮件1短信<br/>
     * <dt><span class="strong">返回</span></dt><br/>
     * <dd>{ <br/>
     * logId: <br/>
     * appId: 应用编号<br/>
     * logDatetime: 时间<br/>
     * logContent: 接收内容<br/>
     * logReceive: 接收人<br/>
     * logType: 日志类型0邮件1短信<br/>
     * }</dd><br/>
     */
    @ApiOperation(value = "获取发送日志接口")
    @ApiImplicitParam(name = "logId", value = "日志编号", required = true, paramType = "query")
    @GetMapping("/get")
    @ResponseBody
    public ResultData get(@ModelAttribute @ApiIgnore LogEntity log, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
        if (log.getIntId() <= 0) {
            return ResultData.build().error(getResString("err.error", this.getResString("log.id")));
        }
        LambdaQueryWrapper<LogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LogEntity::getIntId,log.getIntId());
        LogEntity _log = logBiz.getOne(wrapper,false);
        return ResultData.build().success(_log);
    }

    /**
     * 保存发送日志实体
     * @param log 发送日志实体
     * <i>log参数包含字段信息参考：</i><br/>
     * logId <br/>
     * appId 应用编号<br/>
     * logDatetime 时间<br/>
     * logContent 接收内容<br/>
     * logReceive 接收人<br/>
     * logType 日志类型0邮件1短信<br/>
     * <dt><span class="strong">返回</span></dt><br/>
     * <dd>{ <br/>
     * logId: <br/>
     * appId: 应用编号<br/>
     * logDatetime: 时间<br/>
     * logContent: 接收内容<br/>
     * logReceive: 接收人<br/>
     * logType: 日志类型0邮件1短信<br/>
     * }</dd><br/>
     */
    @ApiOperation(value = "保存发送日志实体接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logDatetime", value = "时间", required = true, paramType = "query"),
            @ApiImplicitParam(name = "logContent", value = "接收内容", required = true, paramType = "query"),
            @ApiImplicitParam(name = "logReceive", value = "接收人", required = true, paramType = "query"),
            @ApiImplicitParam(name = "logType", value = "日志类型0邮件1短信", required = true, paramType = "query")
    })
    @LogAnn(title = "保存发送日志实体接口",businessType= BusinessTypeEnum.INSERT)
    @PostMapping("/save")
    @ResponseBody
    public ResultData save(@ModelAttribute @ApiIgnore LogEntity log, HttpServletResponse response, HttpServletRequest request) {
        //验证时间的值是否合法
        if (StringUtil.isBlank(log.getLogDatetime())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("log.datetime")));
        }
        if (!StringUtil.checkLength(log.getLogDatetime() + "", 1, 19)) {
            return ResultData.build().error(getResString("err.length", this.getResString("log.datetime"), "1", "19"));
        }
        //验证接收内容的值是否合法
        if (StringUtil.isBlank(log.getLogContent())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("log.content")));
        }
        if (!StringUtil.checkLength(log.getLogContent() + "", 1, 255)) {
            return ResultData.build().error(getResString("err.length", this.getResString("log.content"), "1", "255"));
        }
        //验证接收人的值是否合法
        if (StringUtil.isBlank(log.getLogReceive())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("log.receive")));
        }
        if (!StringUtil.checkLength(log.getLogReceive() + "", 1, 0)) {
            return ResultData.build().error(getResString("err.length", this.getResString("log.receive"), "1", "0"));
        }
        //验证日志类型0邮件1短信的值是否合法
        if (StringUtil.isBlank(log.getLogType())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("log.type")));
        }
        if (!StringUtil.checkLength(log.getLogType() + "", 1, 10)) {
            return ResultData.build().error(getResString("err.length", this.getResString("log.type"), "1", "10"));
        }
        logBiz.save(log);
        return ResultData.build().success(log);
    }

    /**
     * @param logs 发送日志实体
     * <i>log参数包含字段信息参考：</i><br/>
     * logId:多个logId直接用逗号隔开,例如logId=1,2,3,4
     * 批量删除发送日志
     *            <dt><span class="strong">返回</span></dt><br/>
     *            <dd>{code:"错误编码",<br/>
     *            result:"true｜false",<br/>
     *            resultMsg:"错误信息"<br/>
     *            }</dd>
     */
    @ApiOperation(value = "批量删除日志接口")
    @LogAnn(title = "批量删除日志接口",businessType= BusinessTypeEnum.DELETE)
    @PostMapping("/delete")
    @ResponseBody
    public ResultData delete(@RequestBody List<LogEntity> logs, HttpServletResponse response, HttpServletRequest request) {
        int[] ids = new int[logs.size()];
        for (int i = 0; i < logs.size(); i++) {
            ids[i] = logs.get(i).getIntId();
        }
        logBiz.delete(ids);
        return ResultData.build().success();
    }

    /**
     * 更新发送日志信息发送日志
     * @param log 发送日志实体
     * <i>log参数包含字段信息参考：</i><br/>
     * logId <br/>
     * appId 应用编号<br/>
     * logDatetime 时间<br/>
     * logContent 接收内容<br/>
     * logReceive 接收人<br/>
     * logType 日志类型0邮件1短信<br/>
     * <dt><span class="strong">返回</span></dt><br/>
     * <dd>{ <br/>
     * logId: <br/>
     * appId: 应用编号<br/>
     * logDatetime: 时间<br/>
     * logContent: 接收内容<br/>
     * logReceive: 接收人<br/>
     * logType: 日志类型0邮件1短信<br/>
     * }</dd><br/>
     */
    @ApiOperation(value = "更新发送日志信息发送日志接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logId", value = "日志编号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "logDatetime", value = "时间", required = true, paramType = "query"),
            @ApiImplicitParam(name = "logContent", value = "接收内容", required = true, paramType = "query"),
            @ApiImplicitParam(name = "logReceive", value = "接收人", required = true, paramType = "query"),
            @ApiImplicitParam(name = "logType", value = "日志类型0邮件1短信", required = true, paramType = "query")
    })
    @LogAnn(title = "更新发送日志信息发送日志接口",businessType= BusinessTypeEnum.UPDATE)
    @PostMapping("/update")
    @ResponseBody
    public ResultData update(@ModelAttribute @ApiIgnore LogEntity log, HttpServletResponse response,
                             HttpServletRequest request) {
        //验证时间的值是否合法
        if (StringUtil.isBlank(log.getLogDatetime())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("log.datetime")));
        }
        if (!StringUtil.checkLength(log.getLogDatetime() + "", 1, 19)) {
            return ResultData.build().error(getResString("err.length", this.getResString("log.datetime"), "1", "19"));
        }
        //验证接收内容的值是否合法
        if (StringUtil.isBlank(log.getLogContent())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("log.content")));
        }
        if (!StringUtil.checkLength(log.getLogContent() + "", 1, 255)) {
            return ResultData.build().error(getResString("err.length", this.getResString("log.content"), "1", "255"));
        }
        //验证接收人的值是否合法
        if (StringUtil.isBlank(log.getLogReceive())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("log.receive")));
        }
        if (!StringUtil.checkLength(log.getLogReceive() + "", 1, 0)) {
            return ResultData.build().error(getResString("err.length", this.getResString("log.receive"), "1", "0"));
        }
        //验证日志类型0邮件1短信的值是否合法
        if (StringUtil.isBlank(log.getLogType())) {
            return ResultData.build().error(getResString("err.empty", this.getResString("log.type")));
        }
        if (!StringUtil.checkLength(log.getLogType() + "", 1, 10)) {
            return ResultData.build().error(getResString("err.length", this.getResString("log.type"), "1", "10"));
        }
        logBiz.updateById(log);
        return ResultData.build().success(log);
    }

}
