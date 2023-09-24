/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.action.web;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import net.mingsoft.base.constant.Const;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.config.MSProperties;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.mweixin.action.BaseAction;
import net.mingsoft.mweixin.service.PortalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * 微信js-sdk接口
 * @author 铭飞开发团队
 * @version 
 * 版本号：100-000-000<br/>
 * 创建日期：2017年10月26日<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"前端-微信模块接口"})
@Controller("jsSdkActionWeb")
@RequestMapping("/mweixin/jsSdk")
public class JsSdkAction extends BaseAction {
	
	/**
	 * 构建微信js
	 * @param url 调用的页面url
	 * @param weixinNo 微信号
	 * @return
	 */
	@ApiOperation(value = "构建微信js接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "url", value = "调用的页面url", required =true,paramType="query"),
			@ApiImplicitParam(name = "weixinNo", value = "微信号", required =true,paramType="query"),
	})
	@ResponseBody
	@PostMapping("/createJsapiSignature")
	public ResultData createJsapiSignature(String url,String weixinNo, HttpServletResponse response, HttpServletRequest request) {
		//验证微信编号是否合法			
		if(StringUtils.isBlank(weixinNo)){
			return ResultData.build().error(getResString("err.empty", this.getResString("weixin.no")));
		}
		//验证微信分享链接是否合法			
		if(StringUtils.isBlank(url)){
			return ResultData.build().error(getResString("err.empty", this.getResString("weixin.share.url")));
		}
		PortalService weixinService = super.builderWeixinService(weixinNo);
		try {
			WxJsapiSignature wxJsapiSign = weixinService.createJsapiSignature(url);
			return ResultData.build().success(wxJsapiSign) ;
		} catch (WxErrorException e) {
			e.printStackTrace();
		}
		return ResultData.build().error();
	}
	
	/**
	 * 
	 * 下载资源文件 返回数字
	 * @param weixinNo 微信号
	 * @param mediaIds 媒体编号 多个用逗号隔开
	 * @param path 路径
	 * @param response
	 * @param request
	 */
	@ApiOperation(value = "下载资源文件接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "mediaIds", value = "媒体编号 多个用逗号隔开", required =true,paramType="query"),
			@ApiImplicitParam(name = "weixinNo", value = "微信号", required =true,paramType="query"),
			@ApiImplicitParam(name = "path", value = "路径", required =true,paramType="query"),
	})
	@ResponseBody
	@PostMapping("/downloadMedia")
	public ResultData downloadMedia(String weixinNo,String mediaIds,String path, HttpServletResponse response, HttpServletRequest request) {
		//验证微信编号是否合法			
		if(StringUtils.isBlank(weixinNo)){
			return ResultData.build().error(getResString("err.empty", this.getResString("weixin.no")));
		}
		//验证微信媒体编号是否合法			
		if(StringUtils.isBlank(mediaIds)){
			return ResultData.build().error(getResString("err.empty", this.getResString("weixin.media.id")));
		}
		String[] _mediaIds  = mediaIds.split(",");
		String[] mediaIdPaths = new String[_mediaIds.length];
		//tomcat路径
		String serverPath = BasicUtil.getRealPath("");
		String uploadFolderPath = ConfigUtil.getString("文件上传配置", "uploadPath", MSProperties.upload.path);
		String src = uploadFolderPath+ Const.SEPARATOR + BasicUtil.getApp().getAppId();
		if(StringUtils.isNotEmpty(path)){
			src = uploadFolderPath+ Const.SEPARATOR + path;
		}
		File filePath = new File(serverPath+Const.SEPARATOR +src);
		if(!filePath.exists()){
			filePath.mkdirs();
		}
		PortalService weixinService = super.builderWeixinService(weixinNo);
		for(int i=0;i<_mediaIds.length;i++ ){
			try {
				File file = weixinService.getMaterialService().mediaDownload(_mediaIds[i]);
				if(ObjectUtil.isNull(file)){
					mediaIdPaths[i] = null;
					continue ;
				}
				//文件移动到指定位置
				FileUtil.move(file, filePath, true);
				mediaIdPaths[i] = src + Const.SEPARATOR + file.getName();
			} catch (WxErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ResultData.build().success(mediaIdPaths);
	}
}
