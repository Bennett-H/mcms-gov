/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.msend.action;

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
import net.mingsoft.msend.biz.ITemplateBiz;
import net.mingsoft.msend.entity.TemplateEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 发送消息模板表管理控制层
 * @author 铭飞开发团队
 * @version
 * 版本号：0.0<br/>
 * 创建日期：2017-8-24 17:52:29<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-发送模块接口"})
@Controller
@RequestMapping("/${ms.manager.path}/msend/template")
public class TemplateAction extends net.mingsoft.msend.action.BaseAction{

	/**
	 * 注入发送消息模板表业务层
	 */
	@Autowired
	private ITemplateBiz templateBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("sendTemplate:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/msend/template/index";
	}

	/**
	 * 查询发送消息模板表列表
	 * @param template 发送消息模板表实体
	 * <i>template参数包含字段信息参考：</i><br/>
	 * templateId 编号<br/>
	 * modelId 模块编号<br/>
	 * appId 应用编号<br/>
	 * templateTitle 标题<br/>
	 * templateMail <br/>
	 * templateSms <br/>
	 * templateCode 邮件模板代码<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>[<br/>
	 * { <br/>
	 * templateId: 编号<br/>
	 * modelId: 模块编号<br/>
	 * appId: 应用编号<br/>
	 * templateTitle: 标题<br/>
	 * templateMail: <br/>
	 * templateSms: <br/>
	 * templateCode: 邮件模板代码<br/>
	 * }<br/>
	 * ]</dd><br/>
	 */
	@ApiOperation(value = "查询发送消息模板表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "templateId", value = "编号", required = false,paramType="query"),
			@ApiImplicitParam(name = "modelId", value = "模块编号", required = false,paramType="query"),
			@ApiImplicitParam(name = "appId", value = "应用编号", required = false,paramType="query"),
			@ApiImplicitParam(name = "templateTitle", value = "标题", required = false,paramType="query"),
			@ApiImplicitParam(name = "templateMail", value = "邮件模板", required = false,paramType="query"),
			@ApiImplicitParam(name = "templateSms", value = "短信模板", required = false,paramType="query"),
			@ApiImplicitParam(name = "templateCode", value = "邮件模板代码", required = false,paramType="query"),
	})
	@GetMapping("/list")
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore TemplateEntity template) {
		if(template == null){
			template = new TemplateEntity();
		}
		BasicUtil.startPage();
		List templateList= templateBiz.query(template);
		return ResultData.build().success(new EUListBean(templateList,(int)BasicUtil.endPage(templateList).getTotal()));
	}

	/**
	 * 返回编辑界面template_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions(value = {"sendTemplate:save", "sendTemplate:update"}, logical = Logical.OR)
	public String form(@ModelAttribute @ApiIgnore TemplateEntity template,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		if(!StringUtils.isEmpty(template.getId())){
			LambdaQueryWrapper<TemplateEntity> wrapper = new LambdaQueryWrapper<>();
			wrapper.eq(TemplateEntity::getId,template.getIntId());
			BaseEntity templateEntity = templateBiz.getOne(wrapper, false);
			model.addAttribute("templateEntity",templateEntity);
		}
		return "/msend/template/form";
	}

	/**
	 * 获取发送消息模板表
	 * @param template 发送消息模板表实体
	 * <i>template参数包含字段信息参考：</i><br/>
	 * templateId 编号<br/>
	 * modelId 模块编号<br/>
	 * appId 应用编号<br/>
	 * templateTitle 标题<br/>
	 * templateMail <br/>
	 * templateSms <br/>
	 * templateCode 邮件模板代码<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * templateId: 编号<br/>
	 * modelId: 模块编号<br/>
	 * appId: 应用编号<br/>
	 * templateTitle: 标题<br/>
	 * templateMail: <br/>
	 * templateSms: <br/>
	 * templateCode: 邮件模板代码<br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value = "获取发送消息模板表接口")
	@ApiImplicitParam(name = "templateId", value = "编号", required = true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore TemplateEntity template,HttpServletResponse response, HttpServletRequest request,ModelMap model){
		if(template.getIntId() <= 0) {
            return ResultData.build().error(getResString("err.error", this.getResString("template.id")));
		}
		LambdaQueryWrapper<TemplateEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(TemplateEntity::getId,template.getIntId());
		TemplateEntity _template = templateBiz.getOne(wrapper);
		return ResultData.build().success(_template);
	}

	/**
	 * 保存发送消息模板表实体
	 * @param template 发送消息模板表实体
	 * <i>template参数包含字段信息参考：</i><br/>
	 * templateId 编号<br/>
	 * modelId 模块编号<br/>
	 * appId 应用编号<br/>
	 * templateTitle 标题<br/>
	 * templateMail <br/>
	 * templateSms <br/>
	 * templateCode 邮件模板代码<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * templateId: 编号<br/>
	 * modelId: 模块编号<br/>
	 * appId: 应用编号<br/>
	 * templateTitle: 标题<br/>
	 * templateMail: <br/>
	 * templateSms: <br/>
	 * templateCode: 邮件模板代码<br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value = "保存发送消息模板表接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "templateTitle", value = "标题", required = true,paramType="query"),
		@ApiImplicitParam(name = "templateCode", value = "邮件模板代码", required = true,paramType="query"),
    	@ApiImplicitParam(name = "templateId", value = "编号", required = false,paramType="query"),
    	@ApiImplicitParam(name = "modelId", value = "模块编号", required = false,paramType="query"),
    	@ApiImplicitParam(name = "templateMail", value = "邮件模板", required = false,paramType="query"),
    	@ApiImplicitParam(name = "templateSms", value = "短信模板", required = false,paramType="query"),
    })
	@LogAnn(title = "保存发送消息模板表接口",businessType= BusinessTypeEnum.INSERT)
	@PostMapping("/save")
	@ResponseBody
	@RequiresPermissions("sendTemplate:save")
	public ResultData save(@ModelAttribute @ApiIgnore TemplateEntity template) {
		//验证标题是否合法
		if(StringUtils.isBlank(template.getTemplateTitle())){
			return ResultData.build().error(getResString("err.empty", this.getResString("template.title")));
		}
		templateBiz.save(template);
		return ResultData.build().success(template);
	}

	/**
	 * 批量删除消息模板
	 *            <dt><span class="strong">返回</span></dt><br/>
	 *            <dd>{code:"错误编码",<br/>
	 *            result:"true｜false",<br/>
	 *            resultMsg:"错误信息"<br/>
	 *            }</dd>
	 */
	@ApiOperation(value = "批量删除发送消息接口")
	@LogAnn(title = "批量删除发送消息接口",businessType= BusinessTypeEnum.DELETE)
	@PostMapping("/delete")
	@ResponseBody
	@RequiresPermissions("sendTemplate:del")
	public ResultData delete(@RequestBody List<TemplateEntity> templates,HttpServletResponse response, HttpServletRequest request) {
		int[] ids = new int[templates.size()];
		for(int i = 0;i<templates.size();i++){
			ids[i] = templates.get(i).getIntId();
		}
		templateBiz.delete(ids);
		return ResultData.build().success();
	}

	/**
	 * 更新发送消息模板表信息发送消息模板表
	 * @param template 发送消息模板表实体
	 * <i>template参数包含字段信息参考：</i><br/>
	 * templateId 编号<br/>
	 * modelId 模块编号<br/>
	 * appId 应用编号<br/>
	 * templateTitle 标题<br/>
	 * templateMail <br/>
	 * templateSms <br/>
	 * templateCode 邮件模块代码<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * templateId: 编号<br/>
	 * modelId: 模块编号<br/>
	 * appId: 应用编号<br/>
	 * templateTitle: 标题<br/>
	 * templateMail: <br/>
	 * templateSms: <br/>
	 * templateCode: 邮件模块代码<br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value = "更新发送消息模板表信息发送消息模板表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "templateId", value = "编号", required = true,paramType="query"),
    	@ApiImplicitParam(name = "modelId", value = "模块编号", required = false,paramType="query"),
    	@ApiImplicitParam(name = "appId", value = "应用编号", required = false,paramType="query"),
    	@ApiImplicitParam(name = "templateTitle", value = "标题", required = true,paramType="query"),
    	@ApiImplicitParam(name = "templateMail", value = "邮件模板", required = false,paramType="query"),
    	@ApiImplicitParam(name = "templateSms", value = "短信模板", required = false,paramType="query"),
    	@ApiImplicitParam(name = "templateCode", value = "邮件模板代码", required = false,paramType="query"),
    })
	@LogAnn(title = "更新发送消息模板表信息发送消息模板表接口",businessType= BusinessTypeEnum.UPDATE)
	@PostMapping("/update")
	@ResponseBody
	@RequiresPermissions("sendTemplate:update")
	public ResultData update(@ModelAttribute @ApiIgnore TemplateEntity template, HttpServletResponse response,
			HttpServletRequest request) {
		//验证标题是否合法
		if(StringUtils.isBlank(template.getTemplateTitle())){
			return ResultData.build().error(getResString("err.empty", this.getResString("template.title")));
		}
		templateBiz.updateById(template);
		return ResultData.build().success(template);
	}


	/**
	 * 校验参数*/
	@ApiOperation(value = "校验参数接口")
	@GetMapping("/verify")
	@ResponseBody
	public ResultData verify(String fieldName, String fieldValue, String id, String idName){
		boolean verify = false;
		if(StringUtils.isBlank(id)){
			verify = super.validated("msend_template",fieldName,fieldValue);
		}else{
			verify = super.validated("msend_template",fieldName,fieldValue,id,idName);
		}
		if(verify){
			return ResultData.build().success(false);
		}else {
			return ResultData.build().success(true);
		}
	}

}
