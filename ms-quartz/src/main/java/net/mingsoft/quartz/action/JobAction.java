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
import net.mingsoft.quartz.biz.IJobBiz;
import net.mingsoft.quartz.biz.IJobLogBiz;
import net.mingsoft.quartz.entity.JobEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.CronScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;







/**
 * 任务实体表管理控制层
 * @author 铭飞开源团队
 * 创建日期：2019-11-21 16:47:19<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-定时调度模块接口"})
@Controller
@RequestMapping("/${ms.manager.path}/quartz/job")
public class JobAction extends BaseAction{


	/**
	 * 注入任务实体表业务层
	 */
	@Autowired
	private IJobBiz jobBiz;

	/**
	 * 注入任务实体表业务层
	 */
	@Autowired
	private IJobLogBiz jobLogBiz;


	@Autowired
	private Scheduler scheduler;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("quartz:job:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/quartz/job/index";
	}

	/**
	 * 查询任务实体表列表
	 * @param job 任务实体表实体
	 */
	@ApiOperation(value = "查询任务实体表列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "qjName", value = "任务名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "qjGroup", value = "任务组", required =false,paramType="query"),
    	@ApiImplicitParam(name = "qjStatus", value = "状态", required =false,paramType="query"),
    	@ApiImplicitParam(name = "qjAsync", value = "开启并发", required =false,paramType="query"),
    	@ApiImplicitParam(name = "qjTarget", value = "调用目标", required =false,paramType="query"),
    	@ApiImplicitParam(name = "qjCron", value = "执行表达式", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore JobEntity job,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		List jobList = jobBiz.query(job);
		return ResultData.build().success(new EUListBean(jobList,(int)BasicUtil.endPage(jobList).getTotal()));
	}

	/**
	 * 返回编辑界面job_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions(value = {"quartz:job:save", "quartz:job:update"}, logical = Logical.OR)
	public String form(@ModelAttribute JobEntity job,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		if(job.getId()!=null){
			LambdaQueryWrapper<JobEntity> wrapper = new LambdaQueryWrapper<>();
			wrapper.eq(JobEntity::getId,job.getId());
			BaseEntity jobEntity = jobBiz.getOne(wrapper,false);
			model.addAttribute("jobEntity",jobEntity);
		}
		return "/quartz/job/form";
	}

	/**
	 * 获取任务实体表
	 * @param job 任务实体表实体
	 */
	@ApiOperation(value = "获取任务实体表列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore JobEntity job,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(job.getId()==null) {
			return ResultData.build().error();
		}
		LambdaQueryWrapper<JobEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(JobEntity::getId,job.getId());
		JobEntity _job = jobBiz.getOne(wrapper,false);
		return ResultData.build().success(_job);
	}

	/**
	* 保存任务实体表
	* @param job 任务实体表实体
	*/
	@ApiOperation(value = "保存任务实体表列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "qjName", value = "任务名称", required =true,paramType="query"),
			@ApiImplicitParam(name = "qjGroup", value = "任务组", required =false,paramType="query"),
			@ApiImplicitParam(name = "qjStatus", value = "状态", required =false,paramType="query"),
			@ApiImplicitParam(name = "qjAsync", value = "开启并发", required =false,paramType="query"),
			@ApiImplicitParam(name = "qjTarget", value = "调用目标", required =true,paramType="query"),
			@ApiImplicitParam(name = "qjCron", value = "执行表达式", required =true,paramType="query"),
			@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
			@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存任务实体表", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("quartz:job:save")
	public ResultData save(@ModelAttribute @ApiIgnore JobEntity job, HttpServletResponse response, HttpServletRequest request) throws SchedulerException {
		//验证任务名称的值是否合法
		if(StringUtil.isBlank(job.getQjName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("qj.name")));
		}
		if(!StringUtil.checkLength(job.getQjName()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qj.name"), "1", "255"));
		}
		if(!StringUtil.checkLength(job.getQjGroup()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qj.group"), "1", "255"));
		}
		//验证调用目标的值是否合法
		if(StringUtil.isBlank(job.getQjTarget())){
			return ResultData.build().error(getResString("err.empty", this.getResString("qj.target")));
		}
		if(!StringUtil.checkLength(job.getQjTarget()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qj.target"), "1", "255"));
		}
		//验证执行表达式的值是否合法
		if(StringUtil.isBlank(job.getQjCron())){
			return ResultData.build().error(getResString("err.empty", this.getResString("qj.cron")));
		}
		if(!StringUtil.checkLength(job.getQjCron()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qj.cron"), "1", "255"));
		}
		// 表达式调度构建器
		try {
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getQjCron());
		} catch(Exception e) {
			return ResultData.build().error(getResString("err.error", this.getResString("qj.cron")));
		}
		jobBiz.save(job);
		return ResultData.build().success(job);
	}

	/**
	 * @param jobs 任务实体表实体数组
	 */
	@ApiOperation(value = "批量删除任务实体表列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除任务实体表", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("quartz:job:del")
	public ResultData delete(@RequestBody List<JobEntity> jobs,HttpServletResponse response, HttpServletRequest request) {
		int[] ids = new int[jobs.size()];
		for(int i = 0;i<jobs.size();i++){
			ids[i] =Integer.parseInt(jobs.get(i).getId()) ;
		}
		jobBiz.delete(ids);
		return ResultData.build().success();
	}
	/**
	*	更新任务实体表列表
	* @param job 任务实体表实体
	*/
	 @ApiOperation(value = "更新任务实体表列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
    	@ApiImplicitParam(name = "qjName", value = "任务名称", required =true,paramType="query"),
		@ApiImplicitParam(name = "qjGroup", value = "任务组", required =false,paramType="query"),
		@ApiImplicitParam(name = "qjStatus", value = "状态", required =false,paramType="query"),
		@ApiImplicitParam(name = "qjAsync", value = "开启并发", required =false,paramType="query"),
    	@ApiImplicitParam(name = "qjTarget", value = "调用目标", required =true,paramType="query"),
    	@ApiImplicitParam(name = "qjCron", value = "执行表达式", required =true,paramType="query"),
		@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
		@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
		@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新任务实体表", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("quartz:job:update")
	public ResultData update(@ModelAttribute @ApiIgnore JobEntity job, HttpServletResponse response,
			HttpServletRequest request) throws SchedulerException {
		//验证任务名称的值是否合法
		if(StringUtil.isBlank(job.getQjName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("qj.name")));
		}
		if(!StringUtil.checkLength(job.getQjName()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qj.name"), "1", "255"));
		}
		if(!StringUtil.checkLength(job.getQjGroup()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qj.group"), "1", "255"));
		}
		//验证调用目标的值是否合法
		if(StringUtil.isBlank(job.getQjTarget())){
			return ResultData.build().error(getResString("err.empty", this.getResString("qj.target")));
		}
		if(!StringUtil.checkLength(job.getQjTarget()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qj.target"), "1", "255"));
		}
		//验证执行表达式的值是否合法
		if(StringUtil.isBlank(job.getQjCron())){
			return ResultData.build().error(getResString("err.empty", this.getResString("qj.cron")));
		}
		if(!StringUtil.checkLength(job.getQjCron()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qj.cron"), "1", "255"));
		}
		 // 表达式调度构建器
		 try {
			 CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getQjCron());
		 } catch(Exception e) {
			 return ResultData.build().error(getResString("err.error", this.getResString("qj.cron")));
		 }
		jobBiz.updateById(job);
		return ResultData.build().success(job);
	}



	@ApiOperation(value = "检查任务接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fieldName", value = "字段", required = false, paramType = "query"),
	})
	@GetMapping("verify")
	@ResponseBody
	public ResultData verify(String fieldName, String fieldValue, String id,String idName){
		boolean verify = false;
		if(StringUtils.isBlank(id)){
			verify = super.validated("quartz_job",fieldName,fieldValue);
		}else{
			verify = super.validated("quartz_job",fieldName,fieldValue,id,idName);
		}
		if(verify){
			return ResultData.build().success(false);
		}else {
			return ResultData.build().success(true);
		}
	}


	/*
	 * 立即运行
	 */
	@ApiOperation(value = "立即运行任务接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
			@ApiImplicitParam(name = "qjName", value = "任务名称", required =true,paramType="query"),
			@ApiImplicitParam(name = "qjGroup", value = "任务组", required =false,paramType="query"),
			@ApiImplicitParam(name = "qjStatus", value = "状态", required =false,paramType="query"),
			@ApiImplicitParam(name = "qjAsync", value = "开启并发", required =false,paramType="query"),
			@ApiImplicitParam(name = "qjTarget", value = "调用目标", required =true,paramType="query"),
			@ApiImplicitParam(name = "qjCron", value = "执行表达式", required =true,paramType="query"),
			@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
			@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@ResponseBody
	@GetMapping("/runNow")
	@RequiresPermissions("quartz:job:update")
	public ResultData runNow(@ModelAttribute @ApiIgnore JobEntity job) {
		//验证任务名称的值是否合法
		if(StringUtil.isBlank(job.getQjName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("qj.name")));
		}
		if(!StringUtil.checkLength(job.getQjName()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qj.name"), "1", "255"));
		}
		if(!StringUtil.checkLength(job.getQjGroup()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qj.group"), "1", "255"));
		}
		//验证调用目标的值是否合法
		if(StringUtil.isBlank(job.getQjTarget())){
			return ResultData.build().error(getResString("err.empty", this.getResString("qj.target")));
		}
		if(!StringUtil.checkLength(job.getQjTarget()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qj.target"), "1", "255"));
		}
		//验证执行表达式的值是否合法
		if(StringUtil.isBlank(job.getQjCron())){
			return ResultData.build().error(getResString("err.empty", this.getResString("qj.cron")));
		}
		if(!StringUtil.checkLength(job.getQjCron()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("qj.cron"), "1", "255"));
		}
		try {
			jobBiz.run(job);
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ResultData.build().error(getResString("err.error", this.getResString("qj.target")));
		}
		return ResultData.build().success();
	}


	@ApiOperation(value = "查看当前站点ID接口")
	@ResponseBody
	@GetMapping("/getWebsiteId")
	public ResultData getWebsiteId() {
		return ResultData.build().success(BasicUtil.getApp().getAppId());
	}

}
