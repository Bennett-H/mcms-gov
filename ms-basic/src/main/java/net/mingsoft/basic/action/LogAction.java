/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.basic.action;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.bean.LogBean;
import net.mingsoft.basic.biz.ILogBiz;
import net.mingsoft.basic.entity.LogEntity;
import net.mingsoft.basic.util.BasicUtil;
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
 * 系统日志管理控制层
 * @author 铭飞开发团队
 * 创建日期：2020-11-21 9:41:34<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-基础接口"})
@Controller("basicLogAction")
@RequestMapping("/${ms.manager.path}/basic/log")
public class LogAction extends BaseAction{


	/**
	 * 注入系统日志业务层
	 */
	@Autowired
	private ILogBiz logBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("basic:log:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/basic/log/index";
	}

	/**
	 * 返回编辑界面log_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions("basic:log:view")
	public String form(@ModelAttribute LogEntity log,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		return "/basic/log/form";
	}

	/**
	 * 查询系统日志列表
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
    	@ApiImplicitParam(name = "del", value = "删除标记", required =false,paramType="query"),
    	@ApiImplicitParam(name = "id", value = "编号", required =false,paramType="query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@RequiresPermissions("basic:log:view")
	public ResultData list(@ModelAttribute @ApiIgnore LogBean log, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
		BasicUtil.startPage();
		List<LogEntity> logList = logBiz.query(log);
		return ResultData.build().success(new EUListBean(logList,(int)BasicUtil.endPage(logList).getTotal()));
	}

	/**
	 * 获取系统日志
	 * @param log 系统日志实体
	 */
	@ApiOperation(value = "获取系统日志列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions("basic:log:view")
	public ResultData get(@ModelAttribute @ApiIgnore LogEntity log,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(log.getId()==null) {
			return ResultData.build().error("ID不能为空!");
		}
		LogEntity _log = logBiz.getById(log.getId());
		return ResultData.build().success(_log);
	}
}
