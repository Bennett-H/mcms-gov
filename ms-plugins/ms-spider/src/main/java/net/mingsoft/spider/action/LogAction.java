/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.action;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.spider.bean.LogBean;
import net.mingsoft.spider.biz.ILogBiz;
import net.mingsoft.spider.entity.LogEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
/**
 * 日志表管理控制层
 * @author 铭软科技
 * 创建日期：2020-9-11 10:35:05<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-采集模块接口"})
@Controller("spiderLogAction")
@RequestMapping("/${ms.manager.path}/spider/log")
public class LogAction extends BaseAction{


	/**
	 * 注入日志表业务层
	 */
	@Autowired
	private ILogBiz logBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("spider:log:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/spider/log/index";
	}

	/**
	 * 查询日志表列表
	 * @param log 日志表实体
	 */
	@ApiOperation(value = "查询日志表列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "taskId", value = "任务主键", required =false,paramType="query"),
    	@ApiImplicitParam(name = "regularId", value = "规则主键", required =false,paramType="query"),
    	@ApiImplicitParam(name = "sourceUrl", value = "内容链接", required =false,paramType="query"),
    	@ApiImplicitParam(name = "content", value = "采集内容", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@RequiresPermissions("spider:log:view")
	public ResultData list(@ModelAttribute @ApiIgnore LogBean log, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
		BasicUtil.startPage();
		List logList = logBiz.query(log);
		return ResultData.build().success(new EUListBean(logList,(int)BasicUtil.endPage(logList).getTotal()));
	}


	/**
	 * 获取日志表
	 * @param log 日志表实体
	 */
	@ApiOperation(value = "获取日志表列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions("spider:log:view")
	public ResultData get(@ModelAttribute @ApiIgnore LogEntity log,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(log.getId()==null) {
			return ResultData.build().error();
		}
		LogEntity _log = logBiz.getById(log.getId());
		return ResultData.build().success(_log);
	}


	/**
	 */
	@ApiOperation(value = "批量删除日志表列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除日志表", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("spider:log:del")
	public ResultData delete(@RequestBody List<LogEntity> logs,HttpServletResponse response, HttpServletRequest request) {
		List<String> idList = new ArrayList<>();
		logs.forEach(x->{
			idList.add(x.getId());
		});
		logBiz.removeByIds(idList);
		return ResultData.build().success();
	}


	/**
	 * @param logs 日志表实体数组
	 */
	@ApiOperation(value = "批量导入接口")
	@PostMapping("/import")
	@ResponseBody
	@LogAnn(title = "批量导入", businessType = BusinessTypeEnum.OTHER)
	@RequiresPermissions("spider:log:import")
	public ResultData batchImport(@RequestBody List<LogBean> logs) {

		logBiz.batchImport(logs);

		return ResultData.build().success();
	}


}
