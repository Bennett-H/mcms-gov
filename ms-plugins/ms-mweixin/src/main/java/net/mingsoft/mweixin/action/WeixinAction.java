/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.action;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mweixin.biz.IWeixinBiz;
import net.mingsoft.mweixin.constant.SessionConst;
import net.mingsoft.mweixin.entity.WeixinEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @ClassName:  WeixinAction
 * @Description:TODO(微信管理)
 * @author: 铭飞开发团队
 * @date:   2018年4月3日 上午9:28:02
 *
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 */
@Api(tags = {"后端-微信模块接口"})
@Controller
@RequestMapping("/${ms.manager.path}/mweixin")
public class WeixinAction extends BaseAction {

	/**
	 * 注入微信基础业务层
	 */
	@Autowired
	private IWeixinBiz weixinBiz;


	/**
	 * 微信公众号列表界面
	 * @param request
	 * @param mode
	 * @return manager/weixin/weixin_list.ftl的界面
	 */
	@GetMapping("/index")
	@ApiIgnore
	@RequiresPermissions("weixin:view")
	public String index(HttpServletRequest request, ModelMap mode,HttpServletResponse response) {
		return "mweixin/index";
	}
	/**
	 *
	 * @param request
	 * @param mode
	 * @param response
	 * @return 微信实体数据outjson形式
	 */
	@ApiOperation(value="微信菜单列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "weixinNo", value = "微信号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "weixinOriginId", value = "微信原始ID", required = false,paramType="query"),
    	@ApiImplicitParam(name = "weixinName", value = "公众号名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "weixinType", value = "微信类型", required =false,paramType="query"),
    	@ApiImplicitParam(name = "weixinToken", value = "微信token", required = false,paramType="query"),
    	@ApiImplicitParam(name = "weixinPayMchId", value = "微信支付mchid", required = false,paramType="query"),
    	@ApiImplicitParam(name = "weixinPayKey", value = "微信支付key", required = false,paramType="query"),
    })
	@GetMapping("/list")
	@ResponseBody
	@RequiresPermissions("weixin:view")
	public ResultData list(@ModelAttribute WeixinEntity weiXinEntity, HttpServletRequest request, ModelMap mode, HttpServletResponse response) {
		//开始分页
		BasicUtil.startPage();
		List weixinList = weixinBiz.query(weiXinEntity);
		EUListBean _list = new EUListBean(weixinList,(int) BasicUtil.endPage(weixinList).getTotal());
		return ResultData.build().success(_list);
	}
	/**
	 * 返回新增页面
	 *
	 * @param mode
	 * @param request
	 * @return 新增微信页面
	 */
	@GetMapping("/form")
	@ApiIgnore
	@RequiresPermissions(value = {"weixin:save", "weixin:update"}, logical = Logical.OR)
	public String form(@ApiIgnore ModelMap mode, HttpServletRequest request) {
		return "/mweixin/form";
	}

	/**
	 * 微信功能列表页
	 *
	 * @param weixinId
	 *            微信ID
	 * @param mode
	 * @param request
	 * @return 微信功能列表页
	 */
	@ApiOperation(value = "微信功能列表页")
	@ApiImplicitParam(name = "id", value = "素材编号", required = true,paramType="path")
	@GetMapping("/{weixinId}/weixin")
	@RequiresPermissions("weixin:edit")
	public String weixin(@PathVariable int weixinId,@ApiIgnore ModelMap mode, HttpServletRequest request) {
		if (weixinId <= 0) {
			// 非法
			return this.redirectBack(true);
		}
		// 根据微信ID获取微信实体
		WeixinEntity weixin = (WeixinEntity)this.weixinBiz.getEntity(weixinId);
		// 获取公众号名称
		String weixinName = weixin.getWeixinName();
		// 压入微信公众号名称
		request.setAttribute("weixinId", weixinId);
		request.setAttribute("weixinName", weixinName);
		// 微信实体压进session
		this.setWeixinSession(request, SessionConst.WEIXIN_SESSION, weixin);
		return "/mweixin/weixin";
	}

	/**
	 * 持久化新增微信帐号
	 *
	 * @param weixin
	 *            微信帐号信息
	 * @param request
	 * @param response
	 */
	@ApiOperation(value="微信实体保存接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "weixinNo", value = "微信号", required =true,paramType="query"),
    	@ApiImplicitParam(name = "weixinOriginId", value = "微信原始ID", required = true,paramType="query"),
    	@ApiImplicitParam(name = "weixinName", value = "公众号名称", required =true,paramType="query"),
    	@ApiImplicitParam(name = "weixinType", value = "微信类型", required =true,paramType="query"),
    	@ApiImplicitParam(name = "weixinToken", value = "微信token", required = true,paramType="query"),
    	@ApiImplicitParam(name = "weixinPayMchId", value = "微信支付mchid", required = true,paramType="query"),
    	@ApiImplicitParam(name = "weixinPayKey", value = "微信支付key", required = true,paramType="query"),
    	@ApiImplicitParam(name = "weixinAppSecret", value = "应用授权码", required = true,paramType="query"),
    	@ApiImplicitParam(name = "weixinAesKey", value = "消息加解密密钥", required = true,paramType="query"),
    })
	@LogAnn(title = "微信实体保存接口",businessType= BusinessTypeEnum.INSERT)
	@PostMapping("/save")
	@ResponseBody
	@RequiresPermissions("weixin:save")
	public ResultData save(@ModelAttribute WeixinEntity weixin, HttpServletRequest request, HttpServletResponse response) {
		if (weixin == null) {
			return ResultData.build().error();
		}
		// 问题：去掉图片中的"|"
		if (!StringUtils.isBlank(weixin.getWeixinHeadImg())) {
			weixin.setWeixinHeadImg(weixin.getWeixinHeadImg().replace("|", ""));
		}
		if (!StringUtils.isBlank(weixin.getWeixinImage())) {
			weixin.setWeixinImage(weixin.getWeixinImage().replace("|", ""));
		}
		// 保存微信
		weixinBiz.saveEntity(weixin);
		return ResultData.build().success();
	}

	/**
	 * 批量删除微信
	 *
	 * @param response
	 * @param response
	 */
	@ApiOperation(value="批量删除微信")
	@LogAnn(title = "批量删除微信",businessType= BusinessTypeEnum.DELETE)
	@PostMapping("/delete")
	@ResponseBody
	@RequiresPermissions("weixin:del")
	public ResultData delete(@RequestBody List<WeixinEntity> weixins,HttpServletResponse response, HttpServletRequest request) {
		//声明数组接收要删除的id
		int[] ids = new int[weixins.size()];
		//循环的到要删除的ID
		for(int i=0;i<weixins.size();i++){
			ids[i] = weixins.get(i).getIntId();
		}
		// 根据ID批量删除微信
		weixinBiz.deleteByIds(ids);
		// 返回json数据
		return ResultData.build().success();
	}
	/**
	 * 获取微信实体
	 *
	 * @param weixin 微信帐号信息
	 * @param response
	 * @param request
	 * @return
	 */
	@ApiOperation(value="返回编辑界面接口")
    @ApiImplicitParam(name = "pmId", value = "微信编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions("weixin:view")
	public ResultData get(@ModelAttribute WeixinEntity weixin,HttpServletResponse response, HttpServletRequest request){
		if(weixin.getIntId()<=0) {
			return null;
		}
		WeixinEntity _weixin = (WeixinEntity) weixinBiz.getEntity(weixin.getIntId());
		return ResultData.build().success(_weixin);
	}
	/**
	 * 更新微信实体
	 *
	 * @param weixin
	 *            微信实体
	 * @param response
	 */
	@ApiOperation(value="微信实体保存接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "微信自增长编号", required =true,paramType="query"),
    	@ApiImplicitParam(name = "weixinNo", value = "微信号", required =true,paramType="query"),
    	@ApiImplicitParam(name = "weixinOriginId", value = "微信原始ID", required = true,paramType="query"),
    	@ApiImplicitParam(name = "weixinName", value = "公众号名称", required =true,paramType="query"),
    	@ApiImplicitParam(name = "weixinType", value = "微信类型", required =true,paramType="query"),
    	@ApiImplicitParam(name = "weixinToken", value = "微信token", required = true,paramType="query"),
    	@ApiImplicitParam(name = "weixinPayMchId", value = "微信支付mchid", required = true,paramType="query"),
    	@ApiImplicitParam(name = "weixinPayKey", value = "微信支付key", required = true,paramType="query"),
    	@ApiImplicitParam(name = "weixinAppSecret", value = "应用授权码", required = true,paramType="query"),
    	@ApiImplicitParam(name = "weixinAesKey", value = "消息加解密密钥", required = true,paramType="query"),
    })
	@LogAnn(title = "根据id删除自定义表单接口",businessType= BusinessTypeEnum.UPDATE)
	@PostMapping("/update")
	@ResponseBody
	@RequiresPermissions("weixin:update")
	public ResultData update(@ModelAttribute WeixinEntity weixin, HttpServletResponse response) {
		if (weixin.getIntId() <= 0) {
			return ResultData.build().error();
		}
		// 问题：去掉图片中的"|"
		if (!StringUtils.isBlank(weixin.getWeixinHeadImg())) {
			weixin.setWeixinHeadImg(weixin.getWeixinHeadImg().replace("|", ""));
		}
		if (!StringUtils.isBlank(weixin.getWeixinImage())) {
			weixin.setWeixinImage(weixin.getWeixinImage().replace("|", ""));
		}
		// 更新微信
		weixinBiz.updateEntity(weixin);
		return ResultData.build().success();
	}

}
