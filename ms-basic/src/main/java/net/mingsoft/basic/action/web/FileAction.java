/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.action.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseFileAction;
import net.mingsoft.basic.bean.UploadConfigBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.config.MSProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * 上传文件
 */
@Api(tags={"前端-基础接口"})
@Controller
@RequestMapping("/file")
public class FileAction extends BaseFileAction {

	@ApiOperation(value = "处理post请求上传文件")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "uploadPath", value = "上传文件夹地址", required =false,paramType="form"),
			@ApiImplicitParam(name = "file", value = "文件流", dataType="__file",required =false,paramType="form"),
			@ApiImplicitParam(name = "rename", value = "是否重命名", required =false,paramType="form",defaultValue="true"),
			@ApiImplicitParam(name = "appId", value = "上传路径是否需要拼接appId", required =false,paramType="form",defaultValue="false"),
	})
	@PostMapping(value = "/upload")
	@ResponseBody
	public ResultData upload(@ApiIgnore UploadConfigBean bean, HttpServletRequest req, HttpServletResponse res) throws IOException {
		boolean uploadEnable = MSProperties.upload.enableWeb;

		if(uploadEnable){
			//非法路径过滤
			if(bean.getUploadPath()!=null&&(bean.getUploadPath().contains("../")||bean.getUploadPath().contains("..\\"))){
				return ResultData.build().error(getResString("err.error", new String[]{getResString("file.path")}));
			}
			// 是否需要拼接appId
			if(bean.isAppId()){
				bean.setUploadPath(BasicUtil.getApp().getAppId()+ File.separator+ bean.getUploadPath()) ;
			}
			UploadConfigBean config = new UploadConfigBean(bean.getUploadPath(),bean.getFile(),null,false,bean.isRename());
			return this.upload(config);
		}else {
			return ResultData.build().error(getResString("insufficient.permissions"));
		}

	}

}
