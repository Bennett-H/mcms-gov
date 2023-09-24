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
import net.mingsoft.spider.biz.ITaskRegularBiz;
import net.mingsoft.spider.entity.TaskRegularEntity;
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
import java.util.List;

import static net.mingsoft.spider.constant.Const.YES;

/**
 * 采集规则管理控制层
 * @author 铭软科技
 * 创建日期：2020-9-11 10:35:05<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-采集模块接口"})
@Controller("spiderTaskRegularAction")
@RequestMapping("/${ms.manager.path}/spider/taskRegular")
public class TaskRegularAction extends BaseAction{

	@Value("${spring.datasource.url}")
	private String jdbcUrl;


	/**
	 * 注入采集规则业务层
	 */
	@Autowired
	private ITaskRegularBiz taskRegularBiz;
	/**
	 * 注入数据库业务层
	 */
	@Autowired
	private ITableBiz tableBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("spider:taskRegular:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/spider/task-regular/index";
	}

	/**
	 * 查询采集规则列表
	 * @param taskRegular 采集规则实体
	 */
	@ApiOperation(value = "查询采集规则列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "taskId", value = "任务主键", required =false,paramType="query"),
    	@ApiImplicitParam(name = "regularName", value = "规则名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "threadNum", value = "线程数", required =false,paramType="query"),
    	@ApiImplicitParam(name = "charset", value = "字符编码", required =false,paramType="query"),
    	@ApiImplicitParam(name = "linkUrl", value = "被采集页面", required =false,paramType="query"),
    	@ApiImplicitParam(name = "isPage", value = "是否分页", required =false,paramType="query"),
    	@ApiImplicitParam(name = "startPage", value = "起始页", required =false,paramType="query"),
    	@ApiImplicitParam(name = "endPage", value = "结束页", required =false,paramType="query"),
    	@ApiImplicitParam(name = "startArea", value = "列表开始区域", required =false,paramType="query"),
    	@ApiImplicitParam(name = "endArea", value = "列表结束区域", required =false,paramType="query"),
    	@ApiImplicitParam(name = "articleUrl", value = "内容链接匹配", required =false,paramType="query"),
    	@ApiImplicitParam(name = "filedName", value = "字段匹配名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "filedRegula", value = "字段匹配规则", required =false,paramType="query"),
    	@ApiImplicitParam(name = "filedText", value = "字段匹配", required =false,paramType="query"),
    	@ApiImplicitParam(name = "filedDfiled", value = "关联表列名", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore TaskRegularBean taskRegular, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
		BasicUtil.startPage();
		List taskRegularList = taskRegularBiz.query(taskRegular);
		return ResultData.build().success(new EUListBean(taskRegularList,(int)BasicUtil.endPage(taskRegularList).getTotal()));
	}

	/**
	 * 返回编辑界面taskRegular_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions(value = {"spider:taskRegular:save", "spider:taskRegular:update"}, logical = Logical.OR)
	public String form(@ModelAttribute TaskRegularEntity taskRegular,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		if(taskRegular.getId()!=null){
			BaseEntity taskRegularEntity = taskRegularBiz.getById(taskRegular.getId());
			model.addAttribute("taskRegularEntity",taskRegularEntity);
		}
		return "/spider/task-regular/form";
	}
	/**
	 * 获取采集规则
	 * @param taskRegular 采集规则实体
	 */
	@ApiOperation(value = "获取采集规则列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore TaskRegularEntity taskRegular,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(taskRegular.getId()==null) {
			return ResultData.build().error();
		}
		TaskRegularEntity _taskRegular = taskRegularBiz.getById(taskRegular.getId());;
		return ResultData.build().success(_taskRegular);
	}

	@ApiOperation(value = "保存采集规则列表接口")
	 @ApiImplicitParams({
		@ApiImplicitParam(name = "taskId", value = "任务主键", required =false,paramType="query"),
    	@ApiImplicitParam(name = "regularName", value = "规则名称", required =true,paramType="query"),
		@ApiImplicitParam(name = "threadNum", value = "线程数", required =false,paramType="query"),
		@ApiImplicitParam(name = "charset", value = "字符编码", required =false,paramType="query"),
    	@ApiImplicitParam(name = "linkUrl", value = "被采集页面", required =true,paramType="query"),
		@ApiImplicitParam(name = "isPage", value = "是否分页", required =false,paramType="query"),
		@ApiImplicitParam(name = "startPage", value = "起始页", required =false,paramType="query"),
		@ApiImplicitParam(name = "endPage", value = "结束页", required =false,paramType="query"),
		@ApiImplicitParam(name = "startArea", value = "列表开始区域", required =false,paramType="query"),
		@ApiImplicitParam(name = "endArea", value = "列表结束区域", required =false,paramType="query"),
    	@ApiImplicitParam(name = "articleUrl", value = "内容链接匹配", required =true,paramType="query"),
    	@ApiImplicitParam(name = "filedName", value = "字段匹配名称", required =true,paramType="query"),
		@ApiImplicitParam(name = "filedRegula", value = "字段匹配规则", required =false,paramType="query"),
    	@ApiImplicitParam(name = "filedText", value = "字段匹配", required =true,paramType="query"),
		@ApiImplicitParam(name = "filedDfiled", value = "关联表列名", required =false,paramType="query"),
	})

	/**
	* 保存采集规则
	* @param taskRegular 采集规则实体
	*/
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存采集规则", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("spider:taskRegular:save")
	public ResultData save(@ModelAttribute @ApiIgnore TaskRegularEntity taskRegular, HttpServletResponse response, HttpServletRequest request) {
		if(super.validated("spider_task_regular","regular_name",taskRegular.getRegularName())){
			return ResultData.build().error(getResString("err.exist", this.getResString("regular.name")));
		}
		//验证规则名称的值是否合法
		if(StringUtil.isBlank(taskRegular.getRegularName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("regular.name")));
		}
		if(!StringUtil.checkLength(taskRegular.getThreadNum()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("thread.num"), "0", "11"));
		}
		//验证被采集页面的值是否合法
		if(StringUtil.isBlank(taskRegular.getLinkUrl())){
			return ResultData.build().error(getResString("err.empty", this.getResString("link.url")));
		}
		if (YES.equals(taskRegular.getIsPage())){
			if(!StringUtil.checkLength(taskRegular.getStartPage()+"", 1, 10)){
				return ResultData.build().error(getResString("err.length", this.getResString("start.page"), "1", "10"));
			}
			if(!StringUtil.checkLength(taskRegular.getEndPage()+"", 1, 10)){
				return ResultData.build().error(getResString("err.length", this.getResString("end.page"), "1", "10"));
			}
		}
		//验证内容链接匹配的值是否合法
		if(StringUtil.isBlank(taskRegular.getArticleUrl())){
			return ResultData.build().error(getResString("err.empty", this.getResString("article.url")));
		}
		//校验metaDate数据? TODO

		taskRegularBiz.save(taskRegular);
		return ResultData.build().success(taskRegular);
	}

	/**
	 * @param taskRegular 采集规则实体
	 */
	@ApiOperation(value = "批量删除采集规则列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除采集规则", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("spider:taskRegular:del")
	public ResultData delete(@RequestBody List<TaskRegularEntity> taskRegulars,HttpServletResponse response, HttpServletRequest request) {
		String[] ids = new String[taskRegulars.size()];
		for(int i = 0;i<taskRegulars.size();i++){
			ids[i] =taskRegulars.get(i).getId() ;
		}
		taskRegularBiz.delete(ids);
		return ResultData.build().success();
	}

	/**
	*	更新采集规则列表
	* @param taskRegular 采集规则实体
	*/
	 @ApiOperation(value = "更新采集规则列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
		@ApiImplicitParam(name = "taskId", value = "任务主键", required =false,paramType="query"),
    	@ApiImplicitParam(name = "regularName", value = "规则名称", required =true,paramType="query"),
		@ApiImplicitParam(name = "threadNum", value = "线程数", required =false,paramType="query"),
		@ApiImplicitParam(name = "charset", value = "字符编码", required =false,paramType="query"),
    	@ApiImplicitParam(name = "linkUrl", value = "被采集页面", required =true,paramType="query"),
		@ApiImplicitParam(name = "isPage", value = "是否分页", required =false,paramType="query"),
		@ApiImplicitParam(name = "startPage", value = "起始页", required =false,paramType="query"),
		@ApiImplicitParam(name = "endPage", value = "结束页", required =false,paramType="query"),
		@ApiImplicitParam(name = "startArea", value = "列表开始区域", required =false,paramType="query"),
		@ApiImplicitParam(name = "endArea", value = "列表结束区域", required =false,paramType="query"),
    	@ApiImplicitParam(name = "articleUrl", value = "内容链接匹配", required =true,paramType="query"),
    	@ApiImplicitParam(name = "filedName", value = "字段匹配名称", required =true,paramType="query"),
		@ApiImplicitParam(name = "filedRegula", value = "字段匹配规则", required =false,paramType="query"),
    	@ApiImplicitParam(name = "filedText", value = "字段匹配", required =true,paramType="query"),
		@ApiImplicitParam(name = "filedDfiled", value = "关联表列名", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新采集规则", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("spider:taskRegular:update")
	public ResultData update(@ModelAttribute @ApiIgnore TaskRegularEntity taskRegular, HttpServletResponse response,
			HttpServletRequest request) {
		if(super.validated("spider_task_regular","regular_name","id",taskRegular.getId(),taskRegular.getRegularName())){
			return ResultData.build().error(getResString("err.exist", this.getResString("regular.name")));
		}
		//验证规则名称的值是否合法
		if(StringUtil.isBlank(taskRegular.getRegularName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("regular.name")));
		}
		if(!StringUtil.checkLength(taskRegular.getThreadNum()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("thread.num"), "0", "11"));
		}
		//验证被采集页面的值是否合法
		if(StringUtil.isBlank(taskRegular.getLinkUrl())){
			return ResultData.build().error(getResString("err.empty", this.getResString("link.url")));
		}
		 if (YES.equals(taskRegular.getIsPage())){
			 if(!StringUtil.checkLength(taskRegular.getStartPage()+"", 1, 10)){
				 return ResultData.build().error(getResString("err.length", this.getResString("start.page"), "1", "10"));
			 }
			 if(!StringUtil.checkLength(taskRegular.getEndPage()+"", 1, 10)){
				 return ResultData.build().error(getResString("err.length", this.getResString("end.page"), "1", "10"));
			 }
		 }
		//验证内容链接匹配的值是否合法
		if(StringUtil.isBlank(taskRegular.getArticleUrl())){
			return ResultData.build().error(getResString("err.empty", this.getResString("article.url")));
		}
		taskRegularBiz.updateEntity(taskRegular);
		return ResultData.build().success(taskRegular);
	}

	/**
	 * 校验数据库字段
	 * @param fieldName 字段名
	 * @param fieldValue 字段值
	 * @param id
	 * @param idName
	 * @return
	 */
	@ApiOperation(value = "检验采集规则字段接口")
	@GetMapping("verify")
	@ResponseBody
	public ResultData verify(String fieldName, String fieldValue, String id,String idName){
		boolean verify = false;
		if(StringUtils.isBlank(id)){
			verify = super.validated("spider_task_regular",fieldName,fieldValue);
		}else{
			verify = super.validated("spider_task_regular",fieldName,fieldValue,id,idName);
		}
		if(verify){
			return ResultData.build().success(false);
		}else {
			return ResultData.build().success(true);
		}
	}

	/**
	 * 根据表名获取当前表的所有字段
	 * @param tableName 表名
	 * @return
	 */
	@ApiOperation(value = "根据表名获取当前表的所有字段接口")
	@GetMapping("/fileds/{tableName}")
	@ResponseBody
	public ResultData queryTables(@PathVariable String tableName){
		if (JdbcUtils.getDbType(jdbcUrl).getDb().equals(DbType.DM.getDb())) {
			// 如果jdbcUrl为达梦
			return ResultData.build().success(tableBiz.queryDMFiledByTableName(tableName));
		}else {
			return ResultData.build().success(tableBiz.queryFiledByTableName(tableName));
		}
	}

}
