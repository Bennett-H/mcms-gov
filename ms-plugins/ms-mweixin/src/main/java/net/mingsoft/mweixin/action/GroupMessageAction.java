/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.action;

import cn.hutool.core.util.ObjectUtil;
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
import net.mingsoft.mweixin.biz.IGroupMessageBiz;
import net.mingsoft.mweixin.entity.GroupMessageEntity;
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
/**
 * 群发消息管理控制层
 * @author 铭飞开发团队
 * 创建日期：2019-6-6 12:03:07<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-微信模块接口"})
@Controller
@RequestMapping("/${ms.manager.path}/mweixin/wxGroupMessage")
public class GroupMessageAction extends BaseAction{
	
	
	/**
	 * 注入群发消息业务层
	 */	
	@Autowired
	private IGroupMessageBiz wxGroupMessageBiz;

	/**
	 * 返回群发记录主界面keyword
	 */
	@ApiIgnore
	@GetMapping("/reply/index")
	@RequiresPermissions("reply:view")
	public String index(HttpServletResponse response, HttpServletRequest request) {
		return "/mweixin/reply/index";
	}

	/**
	 * 返回一键群发主界面form
	 */
	@ApiIgnore
	@GetMapping("/reply/form")
	@RequiresPermissions("reply:view")
	public String form(HttpServletResponse response, HttpServletRequest request) {
		return "/mweixin/reply/form";
	}

	/**
	 * 查询群发消息列表
	 * @param wxGroupMessage 群发消息实体
	 * <i>wxGroupMessage参数包含字段信息参考：</i><br/>
	 * id 关联主键<br/>
	 * gmSendTime 发送时间<br/>
	 * gmSendGroup 发送对象组<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>[<br/>
	 * { <br/>
	 * id: 关联主键<br/>
	 * gmSendTime: 发送时间<br/>
	 * gmSendGroup: 发送对象组<br/>
	 * }<br/>
	 * ]</dd><br/>	 
	 */
	@ApiOperation(value = "查询群发消息列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "id", value = "关联主键", required =false,paramType="query"),
    	@ApiImplicitParam(name = "gmSendTime", value = "发送时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "gmSendGroup", value = "发送对象组", required =false,paramType="query"),
    })
	@GetMapping("/list")
	@ResponseBody
	@RequiresPermissions("reply:view")
	public ResultData list(@ModelAttribute @ApiIgnore GroupMessageEntity wxGroupMessage, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
		BasicUtil.startPage();
		List wxGroupMessageList = wxGroupMessageBiz.query(wxGroupMessage);
		return ResultData.build().success(new EUListBean(wxGroupMessageList,(int)BasicUtil.endPage(wxGroupMessageList).getTotal()));
	}
	

	
	/**
	 * 获取群发消息
	 * @param wxGroupMessage 群发消息实体
	 * <i>wxGroupMessage参数包含字段信息参考：</i><br/>
	 * id 关联主键<br/>
	 * gmSendTime 发送时间<br/>
	 * gmSendGroup 发送对象组<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * id: 关联主键<br/>
	 * gmSendTime: 发送时间<br/>
	 * gmSendGroup: 发送对象组<br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value = "获取群发消息列表接口")
    @ApiImplicitParam(name = "id", value = "关联主键", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions("reply:view")
	public ResultData get(@ModelAttribute @ApiIgnore GroupMessageEntity wxGroupMessage, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model){
		if(StringUtils.isEmpty(wxGroupMessage.getId())) {
			return null;
		}
		GroupMessageEntity _wxGroupMessage = (GroupMessageEntity)wxGroupMessageBiz.getEntity(wxGroupMessage.getIntId());
		return ResultData.build().success(_wxGroupMessage);
	}

	/**
	 * 保存群发消息实体
	 * @param wxGroupMessage 群发消息实体
	 * <i>wxGroupMessage参数包含字段信息参考：</i><br/>
	 * id 关联主键<br/>
	 * gmSendTime 发送时间<br/>
	 * gmSendGroup 发送对象组<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * id: 关联主键<br/>
	 * gmSendTime: 发送时间<br/>
	 * gmSendGroup: 发送对象组<br/>
	 * }</dd><br/>
	 * @deprecated 未被使用
	 */
	@ApiOperation(value = "保存群发消息列表接口")
	 @ApiImplicitParams({
    	@ApiImplicitParam(name = "id", value = "关联主键", required =true,paramType="query"),
    	@ApiImplicitParam(name = "gmSendTime", value = "发送时间", required =true,paramType="query"),
    	@ApiImplicitParam(name = "gmSendGroup", value = "发送对象组", required =true,paramType="query"),
	})
	@LogAnn(title = "保存群发消息列表接口",businessType= BusinessTypeEnum.INSERT)
	@PostMapping("/save")
	@ResponseBody
	@RequiresPermissions("wxGroupMessage:save")
	public ResultData save(@ModelAttribute @ApiIgnore GroupMessageEntity wxGroupMessage, HttpServletResponse response, HttpServletRequest request, BindingResult result) {
		//验证发送时间的值是否合法
		if(ObjectUtil.isEmpty(wxGroupMessage.getGmSendTime())){
			return ResultData.build().error(getResString("err.empty", this.getResString("gm.send.time")));
		}
		if(!StringUtil.checkLength(wxGroupMessage.getGmSendTime()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("gm.send.time"), "1", "255"));
		}
		//验证发送对象组的值是否合法
		if(StringUtils.isBlank(wxGroupMessage.getGmSendGroup())){
			return ResultData.build().error(getResString("err.empty", this.getResString("gm.send.group")));
		}
		if(!StringUtil.checkLength(wxGroupMessage.getGmSendGroup()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("gm.send.group"), "1", "255"));
		}
		wxGroupMessageBiz.saveEntity(wxGroupMessage);
		return ResultData.build().success(wxGroupMessage);
	}


	/**
	 * @deprecated 未被使用
	 * @param wxGroupMessages
	 * @param response
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "批量删除群发消息列表接口")
	@LogAnn(title = "批量删除群发消息列表接口",businessType= BusinessTypeEnum.DELETE)
	@PostMapping("/delete")
	@ResponseBody
	@RequiresPermissions("wxGroupMessage:del")
	public ResultData delete(@RequestBody List<GroupMessageEntity> wxGroupMessages, HttpServletResponse response, HttpServletRequest request) {
		int[] ids = new int[wxGroupMessages.size()];
		for(int i = 0;i<wxGroupMessages.size();i++){
			ids[i] =wxGroupMessages.get(i).getIntId() ;
		}
		wxGroupMessageBiz.delete(ids);
		return ResultData.build().success();
	}

	/**
	 * 更新群发消息信息群发消息
	 * @param wxGroupMessage 群发消息实体
	 * <i>wxGroupMessage参数包含字段信息参考：</i><br/>
	 * id 关联主键<br/>
	 * gmSendTime 发送时间<br/>
	 * gmSendGroup 发送对象组<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * id: 关联主键<br/>
	 * gmSendTime: 发送时间<br/>
	 * gmSendGroup: 发送对象组<br/>
	 * }</dd><br/>
	 * @deprecated 未被使用
	 */
	 @ApiOperation(value = "更新群发消息列表接口")
	 @ApiImplicitParams({
    	@ApiImplicitParam(name = "id", value = "关联主键", required =true,paramType="query"),
    	@ApiImplicitParam(name = "gmSendTime", value = "发送时间", required =true,paramType="query"),
    	@ApiImplicitParam(name = "gmSendGroup", value = "发送对象组", required =true,paramType="query"),
	})
	 @LogAnn(title = "更新群发消息列表接口",businessType= BusinessTypeEnum.UPDATE)
	@PostMapping("/update")
	@ResponseBody
	@RequiresPermissions("wxGroupMessage:update")
	public ResultData update(@ModelAttribute @ApiIgnore GroupMessageEntity wxGroupMessage, HttpServletResponse response,
					   HttpServletRequest request) {
		//验证关联主键的值是否合法
		if(StringUtils.isBlank(wxGroupMessage.getId())){
			return ResultData.build().error(getResString("err.empty", this.getResString("pm.id")));
		}
		if(!StringUtil.checkLength(wxGroupMessage.getIntId()+"", 1, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("pm.id"), "1", "11"));
		}
		//验证发送时间的值是否合法
		if(ObjectUtil.isEmpty(wxGroupMessage.getGmSendTime())){
			return ResultData.build().error(getResString("err.empty", this.getResString("gm.send.time")));
		}
		if(!StringUtil.checkLength(wxGroupMessage.getGmSendTime()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("gm.send.time"), "1", "255"));
		}
		//验证发送对象组的值是否合法
		if(StringUtils.isBlank(wxGroupMessage.getGmSendGroup())){
			return ResultData.build().error(getResString("err.empty", this.getResString("gm.send.group")));
		}
		if(!StringUtil.checkLength(wxGroupMessage.getGmSendGroup()+"", 1, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("gm.send.group"), "1", "255"));
		}
		wxGroupMessageBiz.updateEntity(wxGroupMessage);
		return ResultData.build().success(wxGroupMessage);
	}

}
