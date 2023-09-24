/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mweixin.biz.ITemplateBiz;
import net.mingsoft.mweixin.biz.IWeixinBiz;
import net.mingsoft.mweixin.constant.SessionConst;
import net.mingsoft.mweixin.entity.TemplateEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.service.PortalService;
import net.mingsoft.mweixin.utils.TemplateMessageUtil;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Map;

/**
 * 微信消息模板管理控制层
 * @author 铭飞开发团队
 * 创建日期：2023-6-8 15:43:33<br/>
 * 历史修订：<br/>
 */
@Api(tags = "后台-微信消息模板接口")
@Controller("mweixinTemplateAction")
@RequestMapping("/${ms.manager.path}/mweixin/template")
public class TemplateAction extends net.mingsoft.mweixin.action.BaseAction{

	/**
	 * 注入微信消息模板业务层
	 */
	@Autowired
	private ITemplateBiz templateBiz;

	@Autowired
	private IWeixinBiz weixinBiz;

	/**
	 * 返回主界面index
	 */
	@GetMapping("/index")
	@RequiresPermissions("mweixin:template:view")
	public String index(HttpServletResponse response,HttpServletRequest request) {
		return "/mweixin/template/index";
	}

	/**
	 * 查询微信消息模板列表
	 * @param template 微信消息模板实体
	 */
	@ApiOperation(value = "查询微信消息模板列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "templateId", value = "模板编号", paramType = "query"),
			@ApiImplicitParam(name = "title", value = "模板名称", paramType = "query"),
			@ApiImplicitParam(name = "primaryIndustry", value = "主属行业", paramType = "query"),
			@ApiImplicitParam(name = "deputyIndustry", value = "副属行业", paramType = "query"),
			@ApiImplicitParam(name = "content", value = "内容", paramType = "query"),
			@ApiImplicitParam(name = "example", value = "样例", paramType = "query"),
	})
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore TemplateEntity template,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		//取出微信实体
		WeixinEntity weixin = (WeixinEntity) BasicUtil.getSession(SessionConst.WEIXIN_SESSION);
		if (weixin == null) {
			return ResultData.build().error(getResString("weixin.not.found"));
		}
		// 设置微信id
		template.setWeixinId(weixin.getId());
		List templateList = null;
		if ( template.getSqlWhere() != null){
			templateList = templateBiz.query(template);
		} else {
			LambdaQueryWrapper<TemplateEntity> wrapper = new LambdaQueryWrapper<>(template).orderByDesc(TemplateEntity::getCreateDate);
			templateList = templateBiz.list(wrapper);
		}
		return ResultData.build().success(new EUListBean(templateList,templateList.size()));
	}

	/**
	 * 返回编辑界面template的form
	 */
	@GetMapping("/form")
	public String form(@ModelAttribute TemplateEntity template,HttpServletResponse response,HttpServletRequest request,ModelMap model) {
		return "/mweixin/template/form";
	}


	/**
	 * 获取微信消息模板
	 * @param template 微信消息模板实体
	 */
	@ApiOperation(value = "获取微信消息模板列表接口")
	@ApiImplicitParam(name = "id", value = "主键ID", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore TemplateEntity template,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model) {
		if (template.getId()==null) {
			return ResultData.build().error();
		}
		TemplateEntity _template = (TemplateEntity)templateBiz.getById(template.getId());
		return ResultData.build().success(_template);
	}

	/**
	 *	更新微信消息模板列表
	 * @param template 微信消息模板实体
	 */
	@ApiOperation(value = "更新微信消息模板列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "title", value = "模板名称", required =true, paramType = "query"),
			@ApiImplicitParam(name = "templateKeyword", value = "模板关键词", required =true, paramType = "query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新微信消息模板", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("mweixin:template:update")
	public ResultData update(@ModelAttribute @ApiIgnore TemplateEntity template, HttpServletResponse response,
							 HttpServletRequest request) {
		//先查询数据是否存在
		TemplateEntity _template = templateBiz.getById(template.getId());
		if(_template == null) {
			return ResultData.build().error(getResString("err.not.exist",template.getId() ));
		}
		// 判断更改是否是当前微信公众号
		//取出微信实体
		WeixinEntity weixin = (WeixinEntity) BasicUtil.getSession(SessionConst.WEIXIN_SESSION);
		if (weixin == null) {
			return ResultData.build().error(getResString("weixin.not.found"));
		}
		if (!_template.getWeixinId().equals(weixin.getId())) {
			return ResultData.build().error(getResString("err.error", (getResString("weixin.no"))));
		}
		if (!StringUtil.checkLength(template.getTemplateTitle()+"", 0, 255)) {
			return ResultData.build().error(getResString("err.length", this.getResString("title"), "0", "255"));
		}
		List<TemplateEntity> list = templateBiz.list(new LambdaQueryWrapper<TemplateEntity>().eq(TemplateEntity::getTemplateCode, template.getTemplateCode()).ne(TemplateEntity::getId,template.getId()));
		if (StringUtils.isNotBlank(template.getTemplateCode()) && CollectionUtil.isNotEmpty(list)){
			return ResultData.build().error(getResString("err.exist",this.getResString("template.code")));
		}
		// 只允许修改标题和关键词
		_template.setTemplateCode(template.getTemplateCode());
		_template.setTemplateKeyword(template.getTemplateKeyword());
		templateBiz.updateById(_template);
		return ResultData.build().success(template);
	}


	/**
	 * 同步微信消息模板
	 */
	@ApiOperation(value = "同步微信消息模板到")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "weixinId", value = "微信编号", required =true, paramType = "query"),
	})
	@PostMapping("/sync")
	@ResponseBody
	@LogAnn(title = "同步微信消息模板到", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("mweixin:template:sync")
	public ResultData sync(HttpServletResponse response, HttpServletRequest request) {
		WeixinEntity weixin = (WeixinEntity) BasicUtil.getSession(SessionConst.WEIXIN_SESSION);
		if (templateBiz.syncTemplate(weixin)) {
			return ResultData.build().success();
		}
		return ResultData.build().error("同步失败");
	}


	/**
	 * 指定用户发送微信消息模板,
	 * 参数openIds需要长字符串，每个openId之间用逗号隔开
	 * keywords是json结构
	 */
	@ApiOperation(value = "指定用户发送微信消息模板")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "模板编号", required =true, paramType = "query"),
			@ApiImplicitParam(name = "weixinId", value = "微信编号", required =true, paramType = "query"),
			@ApiImplicitParam(name = "openIds", value = "用户公众号唯一标识", required =true, paramType = "query"),
			@ApiImplicitParam(name = "keywords", value = "关键字参数，json结构", required =true, paramType = "query"),
	})
	@PostMapping("/sendByOpenId")
	@ResponseBody
	@LogAnn(title = "指定用户发送微信消息模板", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("mweixin:template:update")
	public ResultData sendByOpenId(HttpServletResponse response, HttpServletRequest request) {
		String openIds = BasicUtil.getString("openIds");
		String keywords = BasicUtil.getString("keywords");
		String weixinId = BasicUtil.getString("weixinId");
		Map wordList = null;
		try {
			wordList = JSONUtil.toBean(keywords, Map.class);
		} catch (Exception e) {
			LOG.debug("参数不符合json规范");
			e.printStackTrace();
			return ResultData.build().error(getResString("err.error","keywords"));
		}
		if (StringUtils.isBlank(openIds)){
			return ResultData.build().error(getResString("err.error","openIds"));
		}
		WeixinEntity weixin = weixinBiz.getById(weixinId);
		if (weixin==null){
			return ResultData.build().error(getResString("err.error","weixin"));
		}
		PortalService portalService = new PortalService().build(weixin);
		TemplateEntity template = templateBiz.getById(BasicUtil.getString("id"));
		if (template==null){
			return ResultData.build().error(getResString("err.error",this.getResString("id")));
		}
		List<WxMpTemplateData> wxMpTemplateData = TemplateMessageUtil.buildParams(template.getTemplateContent(), template.getTemplateKeyword(), wordList);
		TemplateMessageUtil.send(portalService,wxMpTemplateData,"",template.getTemplateId(),openIds.split(","));


		return ResultData.build().success("发送成功");
	}

	@GetMapping("verify")
	@ResponseBody
	public ResultData verify(String fieldName, String fieldValue, String id,String idName) {
		boolean verify = false;
		if (StringUtils.isBlank(id)) {
			verify = super.validated("WX_TEMPLATE",fieldName,fieldValue);
		} else {
			verify = super.validated("WX_TEMPLATE",fieldName,fieldValue,id,idName);
		}
		if (verify) {
			return ResultData.build().success(false);
		}else {
			return ResultData.build().success(true);
		}
	}

}
