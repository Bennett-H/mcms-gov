/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.co.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.biz.ILogBiz;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.entity.LogEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.co.bean.SocketMsgBean;
import net.mingsoft.co.constant.e.MessageEnum;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * co日志管理控制层
 * 历史修订：增加socket实时输出日志info<br/>
 * 增加查询异常日志list<br/>
 */
@Api(tags={"后端-企业模块接口"})
@Controller("coLogAction")
@RequestMapping("/${ms.manager.path}/co/log")
public class LogAction extends BaseAction{

    private static final String LOG_CONFIG_NAME = "监控日志配置";

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ILogBiz logBiz;

    /**
     * 连接集合
     */
    private static final Map<String, Tailer> tailerMap = new ConcurrentHashMap<>();
    /**
     * 返回文章日志界面
     */
    @ApiIgnore
    @GetMapping("/content/index")
    @RequiresPermissions("log:content:view")
    public String contentIndex(HttpServletResponse response,HttpServletRequest request){
        return "/co/log/content/index";
    }
    /**
     * 返回文章日志界面
     */
    @ApiIgnore
    @GetMapping("/content/form")
    @RequiresPermissions("log:content:view")
    public String contentForm(HttpServletResponse response,HttpServletRequest request){
        return "/co/log/content/form";

    }


    /**
     * 返回监控日志界面
     */
    @ApiIgnore
    @GetMapping("/console")
    @RequiresPermissions("co:log:console")
    public String console(HttpServletResponse response,HttpServletRequest request){
        return "/co/log/console";
    }

    /**
     * 返回异常日志主界面
     */
    @ApiIgnore
    @GetMapping("/index")
    @RequiresPermissions("co:log:view")
    public String index(HttpServletResponse response,HttpServletRequest request){
        return "/co/log/index";
    }

    /**
     * 返回异常日志内容界面
     */
    @ApiIgnore
    @GetMapping("/form")
    @RequiresPermissions("co:log:view")
    public String form(HttpServletResponse response,HttpServletRequest request){
        return "/co/log/form";
    }

    /**
     * 查询系统日志列表
     * 此接口为复用接口，不可做权限控制，已对页面添加权限控制
     * @param log 系统日志实体
     */
    @ApiOperation(value = "查询系统日志列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logTitle", value = "标题", required =false,paramType="query"),
            @ApiImplicitParam(name = "logIp", value = "IP", required =false,paramType="query"),
            @ApiImplicitParam(name = "logMethod", value = "请求方法", required =false,paramType="query"),
            @ApiImplicitParam(name = "logRequestMethod", value = "请求方式", required =false,paramType="query"),
            @ApiImplicitParam(name = "logUrl", value = "请求地址", required =false,paramType="query"),
            @ApiImplicitParam(name = "logStatus", value = "请求状态", required =false,paramType="query"),
            @ApiImplicitParam(name = "logBusinessType", value = "业务类型", required =false,paramType="query"),
            @ApiImplicitParam(name = "logUserType", value = "用户类型", required =false,paramType="query"),
            @ApiImplicitParam(name = "logUser", value = "操作人员", required =false,paramType="query"),
            @ApiImplicitParam(name = "logLocation", value = "所在地区", required =false,paramType="query"),
            @ApiImplicitParam(name = "logParam", value = "请求参数", required =false,paramType="query"),
            @ApiImplicitParam(name = "logResult", value = "返回参数", required =false,paramType="query"),
            @ApiImplicitParam(name = "logErrorMsg", value = "错误消息", required =false,paramType="query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
    @RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResultData list(@ModelAttribute @ApiIgnore LogEntity log, HttpServletResponse response, HttpServletRequest request) {
        BasicUtil.startPage();
        String startTime = BasicUtil.getString("startTime");
        String endTime = BasicUtil.getString("endTime");
        LambdaQueryWrapper<LogEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LogEntity::getLogBusinessType, "error");
        wrapper.between(StringUtils.isNotBlank(startTime)&&StringUtils.isNotBlank(endTime), LogEntity::getCreateDate, DateUtil.parse(startTime), DateUtil.parse(endTime));
        wrapper.like(StringUtils.isNotBlank(log.getLogTitle()), LogEntity::getLogTitle, log.getLogTitle());
        wrapper.like(StringUtils.isNotBlank(log.getLogUrl()), LogEntity::getLogUrl, log.getLogUrl());
        wrapper.orderByDesc(LogEntity::getCreateDate);
        List<LogEntity> logList = logBiz.list(wrapper);
        return ResultData.build().success(new EUListBean(logList,(int)BasicUtil.endPage(logList).getTotal()));
    }


    /**
     * 获取监控日志接口
     */
    @ApiOperation(value = "获取监控日志接口")
    @MessageMapping(value = "/control")
    @ResponseBody
    public void control(@RequestBody SocketMsgBean msgBean) {
        MessageEnum messageEnum = MessageEnum.findMessageEnumByStr(msgBean.getAction());
        if (null == messageEnum) {
            return;
        }
        switch (messageEnum) {
            case START: {
                LOG.debug("LoggingWebSocketServer 任务开始");
                Tailer tailer = this.getTailer();
                if (CollUtil.isNotEmpty(tailerMap) || tailer == null) {
                    return;
                }
                tailerMap.put("tailer", this.getTailer());
                tailerMap.get("tailer").run();
                break;
            }
            case STOP:{
                LOG.debug("LoggingWebSocketServer 任务结束");
                if (CollUtil.isEmpty(tailerMap)) {
                    return;
                }
                tailerMap.get("tailer").stop();
                tailerMap.remove("tailer");
                break;
            }
            default:{
                // ignore
                break;
            }
        }
    }



    /**
     * 查询文章日志接口
     */
    @ApiOperation(value = "获取文章日志接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logTitle", value = "标题", required =false,paramType="query"),
            @ApiImplicitParam(name = "logIp", value = "IP", required =false,paramType="query"),
            @ApiImplicitParam(name = "logMethod", value = "请求方法", required =false,paramType="query"),
            @ApiImplicitParam(name = "logRequestMethod", value = "请求方式", required =false,paramType="query"),
            @ApiImplicitParam(name = "logUrl", value = "请求地址", required =false,paramType="query"),
            @ApiImplicitParam(name = "logStatus", value = "请求状态", required =false,paramType="query"),
            @ApiImplicitParam(name = "logBusinessType", value = "业务类型", required =false,paramType="query"),
            @ApiImplicitParam(name = "logUserType", value = "用户类型", required =false,paramType="query"),
            @ApiImplicitParam(name = "logUser", value = "操作人员", required =false,paramType="query"),
            @ApiImplicitParam(name = "logLocation", value = "所在地区", required =false,paramType="query"),
            @ApiImplicitParam(name = "logParam", value = "请求参数", required =false,paramType="query"),
            @ApiImplicitParam(name = "logResult", value = "返回参数", required =false,paramType="query"),
            @ApiImplicitParam(name = "logErrorMsg", value = "错误消息", required =false,paramType="query"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
            @ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
            @ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
            @ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
    @PostMapping(value = "/content/list")
    @RequiresPermissions("log:content:view")
    @ResponseBody
    public ResultData contentList(@ModelAttribute @ApiIgnore LogEntity log) {
        BasicUtil.startPage();
        String startTime = BasicUtil.getString("startTime");
        String endTime = BasicUtil.getString("endTime");
        LambdaQueryWrapper<LogEntity> wrapper = Wrappers.lambdaQuery();
        List<String> businessTypes = new ArrayList<>();
        if (StringUtils.isBlank(log.getLogBusinessType())){
            businessTypes.add(BusinessTypeEnum.CONTENT_UPDATE.toString().toLowerCase());
            businessTypes.add(BusinessTypeEnum.CONTENT_DELETE.toString().toLowerCase());
            businessTypes.add(BusinessTypeEnum.CONTENT_INSERT.toString().toLowerCase());
        }else {
            businessTypes.add(log.getLogBusinessType());
        }
        wrapper.in(LogEntity::getLogBusinessType, businessTypes);
        wrapper.between(StringUtils.isNotBlank(startTime)&&StringUtils.isNotBlank(endTime), LogEntity::getCreateDate,  DateUtil.parse(startTime), DateUtil.parse(endTime));
        wrapper.eq(StringUtils.isNotBlank(log.getBusinessId()), LogEntity::getBusinessId, log.getBusinessId());
        wrapper.like(StringUtils.isNotBlank(log.getLogUrl()), LogEntity::getLogUrl, log.getLogUrl());
        wrapper.orderByDesc(LogEntity::getCreateDate);
        List<LogEntity> logList = logBiz.list(wrapper);
        return ResultData.build().success(new EUListBean(logList,(int)BasicUtil.endPage(logList).getTotal()));
    }


    private Tailer getTailer() {
        String logSocket = ConfigUtil.getString(LOG_CONFIG_NAME, "logSocket");
        TailerListener listener = new TailerListenerAdapter() {
            public void handle(String line) {
                line = line.replace("\u001B[2m", "");
                line = line.replace("\u001B[0;39m", "");
                line = line.replace("\u001B[33m", "");
                line = line.replace("\u001B[31m", "");
                line = line.replace("\u001B[32m", "");
                line = line.replace("\u001B[35m", "");
                line = line.replace("\u001B[36m", "");
                messagingTemplate.convertAndSend(logSocket, line);
            }
        };

        String pathname = ConfigUtil.getString(LOG_CONFIG_NAME, "logPath");
        File file = new File(pathname);
        if (file.exists()) {
            long delayMillis = Long.parseLong(ConfigUtil.getString(LOG_CONFIG_NAME, "logTime", "1000"));
            return new Tailer(file, listener, delayMillis, true);
        }
        return null;
    }

    /**
     * 检查日志文件是否存在
     */
    @ApiOperation(value = "检查日志文件是否存在接口")
    @GetMapping(value ="/checkLog")
    @ResponseBody
    public ResultData checkLog() {
        String pathname = ConfigUtil.getString("监控日志配置", "logPath");
        File file = new File(pathname);
        if (file.exists()) {
            return ResultData.build().success();
        }
        return ResultData.build().error("当前日志文件不存在,请检查监控日志配置!");
    }

    /**
     * 检测服务是否开启
     */
    @ApiOperation(value = "检测服务是否开启")
    @GetMapping(value ="/isRun")
    @ResponseBody
    public ResultData isRun() {
        if (CollUtil.isNotEmpty(tailerMap) || tailerMap.size()!=0) {
            return ResultData.build().success();
        }
        return ResultData.build().error();
    }


}
