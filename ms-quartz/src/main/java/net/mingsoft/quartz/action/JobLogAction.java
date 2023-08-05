/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.quartz.action;

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
import net.mingsoft.quartz.biz.IJobLogBiz;
import net.mingsoft.quartz.entity.JobLogEntity;
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
 * 任务调度日志管理控制层
 * @author 铭飞开源团队
 * 创建日期：2019-11-21 16:47:19<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-定时调度模块接口"})
@Controller
@RequestMapping("/${ms.manager.path}/quartz/jobLog")
public class JobLogAction extends BaseAction{
	
	
	/**
	 * 注入任务调度日志业务层
	 */	
	@Autowired
	private IJobLogBiz jobLogBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("quartz:jobLog:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/quartz/job-log/index";
	}
	
	/**
	 * 查询任务调度日志列表
	 * @param jobLog 任务调度日志实体
	 */
	@ApiOperation(value = "查询任务调度日志列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "qjlName", value = "任务名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "qjlGroup", value = "任务组", required =false,paramType="query"),
    	@ApiImplicitParam(name = "qjlTarget", value = "调用目标", required =false,paramType="query"),
    	@ApiImplicitParam(name = "qjlStatus", value = "执行状态", required =false,paramType="query"),
    	@ApiImplicitParam(name = "qjlMsg", value = "日志信息", required =false,paramType="query"),
    	@ApiImplicitParam(name = "qjlErrorMsg", value = "错误信息", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore JobLogEntity jobLog,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		List jobLogList = jobLogBiz.query(jobLog);
		return ResultData.build().success(new EUListBean(jobLogList,(int)BasicUtil.endPage(jobLogList).getTotal()));
	}
	
	/**
	 * 返回编辑界面jobLog_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions("quartz:jobLog:view")
	public String form(@ModelAttribute JobLogEntity jobLog,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		if(jobLog.getId()!=null){
			LambdaQueryWrapper<JobLogEntity> wrapper = new LambdaQueryWrapper<>();
			wrapper.eq(JobLogEntity::getId,jobLog.getId());
			BaseEntity jobLogEntity = jobLogBiz.getOne(wrapper,false);
			model.addAttribute("jobLogEntity",jobLogEntity);
		}
		return "/quartz/job-log/form";
	}

	/**
	 * 获取任务调度日志
	 * @param jobLog 任务调度日志实体
	 */
	@ApiOperation(value = "获取任务调度日志列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore JobLogEntity jobLog,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(jobLog.getId()==null) {
			return ResultData.build().error();
		}
		LambdaQueryWrapper<JobLogEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(JobLogEntity::getId,jobLog.getId());
		JobLogEntity _jobLog = jobLogBiz.getOne(wrapper,false);
		return ResultData.build().success(_jobLog);
	}
	


	/**
	* 保存任务调度日志
	* @param jobLog 任务调度日志实体
	*/
	@ApiOperation(value = "保存任务调度日志列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "qjlName", value = "任务名称", required =false,paramType="query"),
			@ApiImplicitParam(name = "qjlGroup", value = "任务组", required =false,paramType="query"),
			@ApiImplicitParam(name = "qjlTarget", value = "调用目标", required =false,paramType="query"),
			@ApiImplicitParam(name = "qjlStatus", value = "执行状态", required =false,paramType="query"),
			@ApiImplicitParam(name = "qjlMsg", value = "日志信息", required =false,paramType="query"),
			@ApiImplicitParam(name = "qjlErrorMsg", value = "错误信息", required =false,paramType="query"),
			@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
			@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存任务调度日志", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("quartz:jobLog:save")
	public ResultData save(@ModelAttribute @ApiIgnore JobLogEntity jobLog, HttpServletResponse response, HttpServletRequest request) {
		if(!StringUtil.checkLength(jobLog.getQjlName()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qjl.name"), "1", "255"));
		}
		if(!StringUtil.checkLength(jobLog.getQjlGroup()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qjl.group"), "1", "255"));
		}
		if(!StringUtil.checkLength(jobLog.getQjlTarget()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qjl.target"), "1", "255"));
		}
		jobLogBiz.save(jobLog);
		return ResultData.build().success(jobLog);
	}
	
	/**
	 * @param jobLogs 任务调度日志实体
	 */
	@ApiOperation(value = "批量删除任务调度日志列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除任务调度日志", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("quartz:jobLog:del")
	public ResultData delete(@RequestBody List<JobLogEntity> jobLogs,HttpServletResponse response, HttpServletRequest request) {
		int[] ids = new int[jobLogs.size()];
		for(int i = 0;i<jobLogs.size();i++){
			ids[i] =Integer.parseInt(jobLogs.get(i).getId()) ;
		}
		jobLogBiz.delete(ids);
		return ResultData.build().success();
	}
	/**
	*	更新任务调度日志列表
	* @param jobLog 任务调度日志实体
	*/
	 @ApiOperation(value = "更新任务调度日志列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
		@ApiImplicitParam(name = "qjlName", value = "任务名称", required =false,paramType="query"),
		@ApiImplicitParam(name = "qjlGroup", value = "任务组", required =false,paramType="query"),
		@ApiImplicitParam(name = "qjlTarget", value = "调用目标", required =false,paramType="query"),
		@ApiImplicitParam(name = "qjlStatus", value = "执行状态", required =false,paramType="query"),
		@ApiImplicitParam(name = "qjlMsg", value = "日志信息", required =false,paramType="query"),
		@ApiImplicitParam(name = "qjlErrorMsg", value = "错误信息", required =false,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新任务调度日志", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("quartz:jobLog:update")
	public ResultData update(@ModelAttribute @ApiIgnore JobLogEntity jobLog, HttpServletResponse response,
			HttpServletRequest request) {
		if(!StringUtil.checkLength(jobLog.getQjlName()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qjl.name"), "1", "255"));
		}
		if(!StringUtil.checkLength(jobLog.getQjlGroup()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qjl.group"), "1", "255"));
		}
		if(!StringUtil.checkLength(jobLog.getQjlTarget()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qjl.target"), "1", "255"));
		}
		jobLogBiz.updateById(jobLog);
		return ResultData.build().success(jobLog);
	}


		
}
