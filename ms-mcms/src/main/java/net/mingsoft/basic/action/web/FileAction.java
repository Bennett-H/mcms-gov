/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.basic.action.web;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseFileAction;
import net.mingsoft.basic.bean.UploadConfigBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.co.biz.IFileBiz;
import net.mingsoft.co.constant.Const;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * 上传文件
 * 历史修订:basic重写-文件配置使用自定义配置管理 -2021-12-17
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
	@PostMapping("/upload")
	@ResponseBody
	public ResultData upload(@ApiIgnore UploadConfigBean bean, HttpServletRequest req, HttpServletResponse res) throws IOException {
		boolean enable = BooleanUtils.toBoolean(ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadEnable", "true"));
		if(enable){
//			FileTypeUtil.getType(bean.getFile().getInputStream())
			//检查文件类型
			String uploadFileName = FileNameUtil.cleanInvalid(bean.getFile().getOriginalFilename());
			if (StringUtils.isBlank(uploadFileName)) {
				return ResultData.build().error("文件名不能为空!");
			}
			//非法路径过滤
			if(bean.getUploadPath()!=null&&(bean.getUploadPath().contains("../")||bean.getUploadPath().contains("..\\"))){
				return ResultData.build().error(getResString("err.error", getResString("file.path")));
			}
			// 是否需要拼接appId
			if(bean.isAppId()){
				bean.setUploadPath(BasicUtil.getApp().getAppId()+ File.separator+ bean.getUploadPath()) ;
			}
			// 加一层日期文件夹区分文件
			String date = DateUtil.format(new Date(), "yyyyMMdd");
			bean.setUploadPath(bean.getUploadPath() +(bean.isUploadDatePath()?File.separator + date:""));
			String uploadFileType = FileUtil.getSuffix(bean.getFile().getOriginalFilename());
			// 切片为blob类型，做容错处理
			uploadFileType = StringUtils.isNotBlank(uploadFileType) ? uploadFileType : FileUtil.getSuffix(bean.getFileName());
			// 禁止的文件类型
			String[] errorType = ConfigUtil.getString(net.mingsoft.co.constant.Const.CONFIG_UPLOAD, "uploadDenied", "exe,jsp").split(",");
			for (String errType : errorType) {
				if((uploadFileType).equalsIgnoreCase(errType)){
					LOG.info("文件类型被拒绝:{}",uploadFileType);
					return ResultData.build().error(getResString("err.error", getResString("file.type")));
				}
			}
			// 单个文件上传计算下各个参数值避免重复上传
			if (StringUtils.isBlank(bean.getFileIdentifier())){
				try {
					bean.setFileIdentifier(String.valueOf(Hex.encodeHex(MessageDigest.getInstance("MD5").digest(bean.getFile().getBytes()))));
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
			if (StringUtils.isBlank(bean.getFileName())){
				bean.setFileName(bean.getFile().getOriginalFilename());
			}
			if (bean.getFileSize()==0){
				bean.setFileSize(bean.getFile().getSize());
			}

			// 检测文件是否存在记录
			String fileUploadedPath = bean.getFileUploadedPath(SpringUtil.getBean(IFileBiz.class));
			String fileRealPath = "";
			if (StringUtils.isNotBlank(fileUploadedPath)){
				// 上传路径
				String uploadFolderPath = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadPath", "upload");
				boolean isReal = new File(uploadFolderPath).isAbsolute();
				String uploadMapping = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadMapping", "/upload/**");
				String realPath = isReal? uploadFolderPath:false? BasicUtil.getRealPath(""):BasicUtil.getRealPath(uploadFolderPath) ;
				fileRealPath = fileUploadedPath.replace(uploadMapping.replace("/**",""),realPath);
			}
			//文件上传类型限制
			String[] fileTypes = ConfigUtil.getString(Const.CONFIG_UPLOAD, "webFileType", "zip").split(",");
			for (String fileType : fileTypes) {
				if (uploadFileType.equalsIgnoreCase(fileType)) {
					long fileSize = ConfigUtil.getInt(Const.CONFIG_UPLOAD, "webFileSize", 20);
					// 检测文件是否过大
					// 进行大小转换 单位MB 1024*1024 = 1048576
					if (bean.getFile().getSize() > (fileSize * 1048576)) {
						return ResultData.build().error("上传文件大于"+fileSize+"MB");
					}
					return new File(fileRealPath).exists() ? ResultData.build().success(fileUploadedPath) : this.upload(bean);
				}
			}
			return ResultData.build().error("文件类型被拒绝:"+uploadFileType);
		}else {
			return ResultData.build().error(getResString("insufficient.permissions"));
		}

	}
}
