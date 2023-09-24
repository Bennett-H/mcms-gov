/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.action;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.mingsoft.base.entity.ResultData;
import net.mingsoft.mweixin.bean.QrLogBean;
import net.mingsoft.mweixin.constant.SessionConst;
import net.mingsoft.mweixin.entity.WeixinEntity;
import org.springframework.validation.BindingResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.ModelMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;
import net.mingsoft.mweixin.biz.IQrLogBiz;
import net.mingsoft.mweixin.entity.QrLogEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;
/**
 * 场景二维码日志管理控制层
 * @author 铭飞开发团队
 * 创建日期：2023-6-5 14:21:08<br/>
 * 历史修订：<br/>
 */
@Api(tags = "后台-场景二维码日志接口")
@Controller("mweixinQrLogAction")
@RequestMapping("/${ms.manager.path}/mweixin/qrLog")
public class QrLogAction extends BaseAction{


	/**
	 * 注入场景二维码日志业务层
	 */
	@Autowired
	private IQrLogBiz qrLogBiz;

	/**
	 * 返回主界面index
	 */
	@GetMapping("/index")
	@RequiresPermissions("mweixin:qrLog:view")
	public String index(HttpServletResponse response,HttpServletRequest request) {
		return "/mweixin/qr-log/index";
	}

	/**
	 * 查询场景二维码日志列表
	 * @param qrLog 场景二维码日志实体
	 */
	@ApiOperation(value = "查询场景二维码日志列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "peopleId", value = "会员编号", paramType = "query"),
			@ApiImplicitParam(name = "qrIp", value = "ip地址", paramType = "query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore QrLogBean qrLog, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
		BasicUtil.startPage();
		//取出微信实体
		WeixinEntity weixin = (WeixinEntity) BasicUtil.getSession(SessionConst.WEIXIN_SESSION);
		if (weixin == null) {
			return ResultData.build().error(getResString("weixin.not.found"));
		}
		// 设置微信id
		qrLog.setWeixinId(weixin.getId());
		List qrLogList = qrLogBiz.query(qrLog);
		return ResultData.build().success(new EUListBean(qrLogList, (int) BasicUtil.endPage(qrLogList).getTotal()));
	}

	/**
	 * 返回编辑界面qrLog的form
	 */
	@GetMapping("/form")
	public String form(@ModelAttribute QrLogEntity qrLog,HttpServletResponse response,HttpServletRequest request,ModelMap model) {
		return "/mweixin/qr-log/form";
	}


	/**
	 * 获取场景二维码日志
	 * @param qrLog 场景二维码日志实体
	 */
	@ApiOperation(value = "获取场景二维码日志列表接口")
    @ApiImplicitParam(name = "id", value = "主键ID", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore QrLogEntity qrLog,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model) {
		if (qrLog.getId()==null) {
			return ResultData.build().error();
		}
		QrLogEntity _qrLog = (QrLogEntity)qrLogBiz.getById(qrLog.getId());
		return ResultData.build().success(_qrLog);
	}


	/**
	* 保存场景二维码日志
	* @param qrLog 场景二维码日志实体
	*/
	@ApiOperation(value = "保存场景二维码日志列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "peopleId", value = "会员编号", required =true, paramType = "query"),
			@ApiImplicitParam(name = "qrIp", value = "ip地址", required =true, paramType = "query"),
	})
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存场景二维码日志", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("mweixin:qrLog:save")
	public ResultData save(@ModelAttribute @ApiIgnore QrLogEntity qrLog, HttpServletResponse response, HttpServletRequest request) {
		//取出微信实体
		WeixinEntity weixin = (WeixinEntity) BasicUtil.getSession(SessionConst.WEIXIN_SESSION);
		if (weixin == null) {
			return ResultData.build().error(getResString("weixin.not.found"));
		}
		// 设置微信id
		qrLog.setWeixinId(weixin.getId());
		if (!StringUtil.checkLength(qrLog.getOpenId()+"", 0, 255)) {
			return ResultData.build().error(getResString("err.length", this.getResString("people.id"), "0", "255"));
		}
		if (!StringUtil.checkLength(qrLog.getQcId()+"", 0, 255)) {
			return ResultData.build().error(getResString("err.length", this.getResString("qr.ip"), "0", "255"));
		}
		qrLogBiz.save(qrLog);
		return ResultData.build().success(qrLog);
	}

	/**
     *  删除场景二维码日志
     *
	 * @param qrLogs 场景二维码日志实体
	 */
	@ApiOperation(value = "批量删除场景二维码日志列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除场景二维码日志", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("mweixin:qrLog:del")
	public ResultData delete(@RequestBody List<QrLogEntity> qrLogs,HttpServletResponse response, HttpServletRequest request) {
	    List<String> ids = (List)qrLogs.stream().map((p) -> {return p.getId();}).collect(Collectors.toList());
		return this.qrLogBiz.removeByIds(ids) ? ResultData.build().success() : ResultData.build().error(this.getResString("err.error", new String[]{this.getResString("id")}));
		}

	/**
	 *	更新场景二维码日志列表
	 *
	 * @param qrLog 场景二维码日志实体
	 */
	 @ApiOperation(value = "更新场景二维码日志列表接口")
	 @ApiImplicitParams({
    		@ApiImplicitParam(name = "peopleId", value = "会员编号", required =true, paramType = "query"),
    		@ApiImplicitParam(name = "qrIp", value = "ip地址", required =true, paramType = "query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新场景二维码日志", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("mweixin:qrLog:update")
	public ResultData update(@ModelAttribute @ApiIgnore QrLogEntity qrLog, HttpServletResponse response,
			HttpServletRequest request) {
		 //取出微信实体
		 WeixinEntity weixin = (WeixinEntity) BasicUtil.getSession(SessionConst.WEIXIN_SESSION);
		 if (weixin == null) {
			 return ResultData.build().error(getResString("weixin.not.found"));
		 }
		 // 设置微信id
		 qrLog.setWeixinId(weixin.getId());
		 //先查询数据是否存在
		QrLogEntity _qrLog = (QrLogEntity)qrLogBiz.getById(qrLog.getId());
		if(_qrLog == null) {
			return ResultData.build().error(getResString("err.not.exist",qrLog.getId() ));
		}
		if (!StringUtil.checkLength(qrLog.getOpenId()+"", 0, 255)) {
			return ResultData.build().error(getResString("err.length", this.getResString("people.id"), "0", "255"));
		}
		if (!StringUtil.checkLength(qrLog.getQcId()+"", 0, 255)) {
			return ResultData.build().error(getResString("err.length", this.getResString("qr.ip"), "0", "255"));
		}
		qrLogBiz.updateById(qrLog);
		return ResultData.build().success(qrLog);
	}


}
