/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.progress.action.people;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.progress.action.BaseAction;
import net.mingsoft.progress.biz.IProgressLogBiz;
import net.mingsoft.progress.entity.ProgressLogEntity;
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
import java.util.stream.Collectors;

/**
 * 进度日志管理控制层
 * @author 铭飞科技
 * 创建日期：2021-3-5 14:29:00<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"前端-用户-进度模块接口"})
@Controller("peopleProgressProgressLogAction")
@RequestMapping("/people/progress/progressLog")
public class ProgressLogAction extends BaseAction {


	/**
	 * 注入进度日志业务层
	 */
	@Autowired
	private IProgressLogBiz progressLogBiz;


	/**
	 * 查询进度日志列表
	 * @param progressLog 进度日志实体
	 */
	@ApiOperation(value = "查询进度日志列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "schemeId", value = "进度方案关联id", required =false,paramType="query"),
    	@ApiImplicitParam(name = "progressId", value = "进度关联id", required =false,paramType="query"),
    	@ApiImplicitParam(name = "plNodeName", value = "进度名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "dataId", value = "业务编号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "plOperator", value = "操作人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "plNumber", value = "进度数", required =false,paramType="query"),
    	@ApiImplicitParam(name = "plStatus", value = "状态", required =false,paramType="query"),
    	@ApiImplicitParam(name = "plFinished", value = "是否处于完成状态", required =false,paramType="query"),
    	@ApiImplicitParam(name = "plContent", value = "操作内容", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "del", value = "删除标记", required =false,paramType="query"),
    	@ApiImplicitParam(name = "id", value = "编号", required =false,paramType="query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore ProgressLogEntity progressLog,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		List progressLogList = progressLogBiz.list(new QueryWrapper<>(progressLog));
		return ResultData.build().success(new EUListBean(progressLogList,(int)BasicUtil.endPage(progressLogList).getTotal()));
	}


	/**
	 * 获取进度日志
	 * @param progressLog 进度日志实体
	 */
	@ApiOperation(value = "获取进度日志列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore ProgressLogEntity progressLog,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(progressLog.getId()==null) {
			return ResultData.build().error();
		}
		ProgressLogEntity _progressLog = (ProgressLogEntity)progressLogBiz.getById(progressLog.getId());
		return ResultData.build().success(_progressLog);
	}



}
