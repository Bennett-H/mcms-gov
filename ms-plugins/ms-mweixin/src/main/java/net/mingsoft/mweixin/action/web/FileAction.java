/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.action.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mweixin.biz.IFileBiz;
import net.mingsoft.mweixin.entity.FileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 * 微信文件表管理控制层
 * @author 铭飞开发团队
 * 创建日期：2018-12-29 9:18:11<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"前端-微信模块接口"})
@Controller("webWxFileAction")
@RequestMapping("/mweixin/file")
public class FileAction extends net.mingsoft.mweixin.action.BaseAction{
	
	
	/**
	 * 注入微信文件表业务层
	 */
	@Autowired
	private IFileBiz fileBiz;

	
	/**
	 * 查询微信文件表列表
	 * @param file 微信文件表实体
	 * <i>file参数包含字段信息参考：</i><br/>
	 * fileId 文件编号<br/>
	 * weixinId 微信编号<br/>
	 * isSync 是否同步至微信<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>[<br/>
	 * { <br/>
	 * fileId: 文件编号<br/>
	 * weixinId: 微信编号<br/>
	 * isSync: 是否同步至微信<br/>
	 * }<br/>
	 * ]</dd><br/>	 
	 */
	@ApiOperation(value = "查询微信文件表列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fileId", value = "文件编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "weixinId", value = "微信编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileMediaId", value = "文件mediaid", required =false,paramType="query"),
			@ApiImplicitParam(name = "categoryId", value = "文件分类编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileName", value = "文件名称", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileUrl", value = "文件链接", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileSize", value = "文件大小", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileJson", value = "文件详情Json数据", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileType", value = "文件类型", required =false,paramType="query"),
			@ApiImplicitParam(name = "isChild", value = "子业务", required =false,paramType="query"),
			@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
			@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@GetMapping("/list")
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore FileEntity file,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model) {
		BasicUtil.startPage();
		List fileList = fileBiz.query(file);
		BasicUtil.endPage(fileList);
		return ResultData.build().success(fileList);
	}
	
	
	
	/**
	 * 保存微信文件表实体
	 * @param file 微信文件表实体
	 * <i>file参数包含字段信息参考：</i><br/>
	 * fileId 文件编号<br/>
	 * weixinId 微信编号<br/>
	 * isSync 是否同步至微信<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * fileId: 文件编号<br/>
	 * weixinId: 微信编号<br/>
	 * isSync: 是否同步至微信<br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value = "保存微信文件表实体接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fileId", value = "文件编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "weixinId", value = "微信编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileMediaId", value = "文件mediaid", required =false,paramType="query"),
			@ApiImplicitParam(name = "categoryId", value = "文件分类编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileName", value = "文件名称", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileUrl", value = "文件链接", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileSize", value = "文件大小", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileJson", value = "文件详情Json数据", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileType", value = "文件类型", required =false,paramType="query"),
			@ApiImplicitParam(name = "isChild", value = "子业务", required =false,paramType="query"),
			@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
			@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
			@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
	})
	@PostMapping("/save")
	@ResponseBody
	public ResultData save(@ModelAttribute @ApiIgnore FileEntity file, HttpServletResponse response, HttpServletRequest request) {
		//验证文件编号的值是否合法			
		if(StringUtil.isBlank(file.getFileId())){
			return ResultData.build().error(getResString("err.empty", this.getResString("file.id")));
		}
		if(!StringUtil.checkLength(file.getFileId()+"", 1, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("file.id"), "1", "11"));
		}//验证微信编号的值是否合法
		if(StringUtil.isBlank(file.getWeixinId())){
			return ResultData.build().error(getResString("err.empty", this.getResString("weixin.id")));
		}
		if(!StringUtil.checkLength(file.getWeixinId()+"", 1, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("weixin.id"), "1", "11"));
		}
		fileBiz.saveEntity(file);
		return ResultData.build().success(file);
	}
	
}
