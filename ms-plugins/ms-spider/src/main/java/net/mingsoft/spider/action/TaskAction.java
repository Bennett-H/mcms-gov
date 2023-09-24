/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.action;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
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
import net.mingsoft.spider.bean.TaskRegularBean;
import net.mingsoft.spider.biz.ITableBiz;
import net.mingsoft.spider.biz.ITaskBiz;
import net.mingsoft.spider.entity.TaskEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
/**
 * 采集任务管理控制层
 * @author 铭软科技
 * 创建日期：2020-9-12 9:29:01<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-采集模块接口"})
@Controller("spiderTaskAction")
@RequestMapping("/${ms.manager.path}/spider/task")
public class TaskAction extends BaseAction{

	/**
	 * 注入采集任务业务层
	 */
	@Autowired
	private ITaskBiz taskBiz;
	/**
	 * 注入数据库业务层
	 */
	@Autowired
	private ITableBiz tableBiz;

	@Value("${spring.datasource.url}")
	private String jdbcUrl;


	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("spider:task:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/spider/task/index";
	}

	/**
	 * 查询采集任务列表
	 * @param task 采集任务实体
	 */
	@ApiOperation(value = "查询采集任务列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "taskName", value = "采集名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "importTable", value = "导入表", required =false,paramType="query"),
    	@ApiImplicitParam(name = "isAutoImport", value = "自动导入", required =false,paramType="query"),
    	@ApiImplicitParam(name = "isRepeat", value = "是否去重", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore TaskEntity task,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		List taskList = taskBiz.query(task);
		return ResultData.build().success(new EUListBean(taskList,(int)BasicUtil.endPage(taskList).getTotal()));
	}

	/**
	 * 返回编辑界面task_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions(value = {"spider:task:save", "spider:task:update"}, logical = Logical.OR)
	public String form(@ModelAttribute TaskEntity task,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		if(task.getId()!=null){
			BaseEntity taskEntity = taskBiz.getById(task.getId());
			model.addAttribute("taskEntity",taskEntity);
		}
		return "/spider/task/form";
	}
	/**
	 * 获取采集任务
	 * @param task 采集任务实体
	 */
	@ApiOperation(value = "获取采集任务列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore TaskEntity task,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(task.getId()==null) {
			return ResultData.build().error();
		}
		TaskEntity _task = taskBiz.getById(task.getId());
		return ResultData.build().success(_task);
	}

	@ApiOperation(value = "保存采集任务列表接口")
	 @ApiImplicitParams({
    	@ApiImplicitParam(name = "taskName", value = "采集名称", required =true,paramType="query"),
		@ApiImplicitParam(name = "importTable", value = "导入表", required =false,paramType="query"),
		@ApiImplicitParam(name = "isAutoImport", value = "自动导入", required =false,paramType="query"),
		@ApiImplicitParam(name = "isRepeat", value = "是否去重", required =false,paramType="query"),
	})

	/**
	* 保存采集任务
	* @param task 采集任务实体
	*/
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存采集任务", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("spider:task:save")
	public ResultData save(@ModelAttribute @ApiIgnore TaskEntity task, HttpServletResponse response, HttpServletRequest request) {
		if(super.validated("spider_task","task_name",task.getTaskName())){
			return ResultData.build().error(getResString("err.exist", this.getResString("task.name")));
		}
		//验证采集名称的值是否合法
		if(StringUtil.isBlank(task.getTaskName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("task.name")));
		}
		//验证采集名称的值是否合法
		if(!StringUtil.checkLength(task.getTaskName(),1,20)){
			return ResultData.build().error(getResString("err.length", this.getResString("task.name"),"0","20"));
		}

		taskBiz.save(task);
		return ResultData.build().success(task);
	}

	/**
	 * @param tasks 采集任务实体
	 */
	@ApiOperation(value = "批量删除采集任务列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除采集任务", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("spider:task:del")
	public ResultData delete(@RequestBody List<TaskEntity> tasks,HttpServletResponse response, HttpServletRequest request) {
		String[] ids = new String[tasks.size()];
		for(int i = 0;i<tasks.size();i++){
			ids[i] =tasks.get(i).getId();
		}
		taskBiz.delete(ids);
		return ResultData.build().success();
	}

	/**
	*	更新采集任务列表
	* @param task 采集任务实体
	*/
	 @ApiOperation(value = "更新采集任务列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
    	@ApiImplicitParam(name = "taskName", value = "采集名称", required =true,paramType="query"),
		@ApiImplicitParam(name = "importTable", value = "导入表", required =false,paramType="query"),
		@ApiImplicitParam(name = "isAutoImport", value = "自动导入", required =false,paramType="query"),
		@ApiImplicitParam(name = "isRepeat", value = "是否去重", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新采集任务", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("spider:task:update")
	public ResultData update(@ModelAttribute @ApiIgnore TaskEntity task, HttpServletResponse response,
			HttpServletRequest request) {
		if(super.validated("spider_task","task_name","id",task.getId(),task.getTaskName())){
			return ResultData.build().error(getResString("err.exist", this.getResString("task.name")));
		}
		//验证采集名称的值是否合法
		if(StringUtil.isBlank(task.getTaskName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("task.name")));
		}
		taskBiz.updateEntity(task);
		return ResultData.build().success(task);
	}

	@ApiOperation(value = "校验采集任务字段接口")
	@GetMapping("verify")
	@ResponseBody
	public ResultData verify(String fieldName, String fieldValue, String id,String idName){
		boolean verify = false;
		if(StringUtils.isBlank(id)){
			verify = super.validated("spider_task",fieldName,fieldValue);
		}else{
			verify = super.validated("spider_task",fieldName,fieldValue,id,idName);
		}
		if(verify){
			return ResultData.build().success(false);
		}else {
			return ResultData.build().success(true);
		}
	}

	/**
	 * 获取当前数据库所有字段的
	 * @return
	 */
	@ApiOperation(value = "获取当前数据库所有字段的接口")
	@GetMapping("/tables")
	@ResponseBody
	public ResultData queryTables(){
		if (JdbcUtils.getDbType(jdbcUrl).getDb().equals(DbType.DM.getDb())) {
			// 如果jdbcUrl为达梦
			return ResultData.build().success(tableBiz.queryDMTables());
		}else {
			return ResultData.build().success(tableBiz.queryDBTables());
		}
	}

	/**
	 * 启动爬虫
	 * @param taskId 采集任务id
	 * @param taskRegulars 采集规则内容
	 * @return
	 */
	@ApiOperation(value = "启动爬虫接口")
	@PostMapping("/start/{taskId}")
	@ResponseBody
	@RequiresPermissions("spider:taskRegular:up")
	public ResultData start(@PathVariable String taskId,
								   @RequestBody List<TaskRegularBean> taskRegulars){
		if (taskBiz.isCollection()) {
			taskBiz.startCollect(taskId,taskRegulars);
			return ResultData.build().success();
		}else {
			return ResultData.build().error("后台正在采集,请等待上一个采集任务完成后继续采集!");
		}

	}

}
