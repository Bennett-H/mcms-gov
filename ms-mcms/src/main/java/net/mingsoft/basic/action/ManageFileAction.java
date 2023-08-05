/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.action;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.UploadConfigBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.service.IUploadBaseService;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.co.biz.IFileBiz;
import net.mingsoft.co.constant.Const;
import net.mingsoft.config.MSProperties;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 上传文件
 * 历史修订:
 * 2021-12-25 basic重写-文件配置使用自定义配置管理
 * 2023-04-10 优化文件上传业务
 */
@Api(tags={"后端-基础接口"})
@Controller("ManageFileAction")
@RequestMapping("${ms.manager.path}/file")
public class ManageFileAction extends BaseFileAction {

	@Autowired
	private IFileBiz fileBiz;
	/**
	 * 处理post请求上传文件
	 * 可以自定义项目路径下任意文件夹
	 * @param req
	 *            HttpServletRequest对象
	 * @param res
	 *            HttpServletResponse 对象
	 * @throws ServletException
	 *             异常处理
	 * @throws IOException
	 *             异常处理
	 */
	@ApiOperation(value = "处理post请求上传文件")
	@LogAnn(title = "处理post请求上传文件",businessType= BusinessTypeEnum.OTHER)
	@PostMapping("/upload")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "uploadPath", value = "上传文件夹地址", required =false,paramType="form"),
			@ApiImplicitParam(name = "file", value = "文件流", dataType="__file",required =false,paramType="form"),
			@ApiImplicitParam(name = "rename", value = "是否重命名", required =false,paramType="form",defaultValue="true"),
			@ApiImplicitParam(name = "appId", value = "上传路径是否需要拼接appId", required =false,paramType="form",defaultValue="false"),
			@ApiImplicitParam(name = "uploadFolderPath", value = "是否修改上传目录", required =false,paramType="form",defaultValue="false"),
			@ApiImplicitParam(name = "fileFolderId", value = "是否修改上传目录", required =false,paramType="form",defaultValue="false"),
	})
	@ResponseBody
	public ResultData upload(@ApiIgnore UploadConfigBean bean, HttpServletRequest req, HttpServletResponse res) throws IOException {
		//非法路径过滤
		if(checkUploadPath(bean)){
			return ResultData.build().error();
		}
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
		bean.setUploadPath(bean.getUploadPath() + (bean.isUploadDatePath()?File.separator + date:""));
		String type = ConfigUtil.getString("存储设置", "storeSelect");
		IUploadBaseService uploadBaseService = null;
		if (StringUtils.isNotBlank(type)) {
			//ms-file 插件启用
			uploadBaseService = (IUploadBaseService) SpringUtil.getBean(type);
		}
		//需要根据糊涂工具的类型判断，此处使用hutool工具直接stream流形式获取会报错，采用临时文件解决
		File temp = FileUtil.newFile(BasicUtil.getRealPath("temp/"+bean.getFile().getOriginalFilename()));
		FileUtils.copyInputStreamToFile(bean.getFile().getInputStream(), temp);
		String uploadFileType =  FileTypeUtil.getType(temp);
		// 切片为blob类型，做容错处理
		uploadFileType = StringUtils.isNotBlank(uploadFileType) ? uploadFileType : FileUtil.getSuffix(bean.getFileName());
		//由于hutool工具不能正常识别zip文件，这里做容错处理
		if(uploadFileType.equalsIgnoreCase("jar") && FileUtil.getSuffix(temp).equalsIgnoreCase("zip")) {
			uploadFileType = "zip";
		}
		temp.delete();
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
		String fileUploadedPath = bean.getFileUploadedPath(fileBiz);
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
		String[] imageTypes = ConfigUtil.getString(Const.CONFIG_UPLOAD, "imageType", "jpg").split(",");
		for (String imageType : imageTypes) {
			if (uploadFileType.equalsIgnoreCase(imageType)) {
				long imageSize = ConfigUtil.getInt(Const.CONFIG_UPLOAD, "imageSize", 4);
				// 检测文件是否过大
				// 进行大小转换 单位MB 1024*1024 = 1048576
				if (bean.getFileSize() > (imageSize * 1048576)) {
					return ResultData.build().error("上传图片大于"+imageSize+"MB");
				}
				if(uploadBaseService != null) {
					return uploadBaseService.checkFileIfExist(fileRealPath) ? ResultData.build().success(fileUploadedPath) : uploadBaseService.upload(bean);
				} else {
					return new File(fileRealPath).exists() ? ResultData.build().success(fileUploadedPath) : this.upload(bean);
				}
			}
		}
		String[] videoTypes = ConfigUtil.getString(Const.CONFIG_UPLOAD, "videoType", "mp4").split(",");
		for (String videoType : videoTypes) {
			if (uploadFileType.equalsIgnoreCase(videoType)) {
				long videoSize = ConfigUtil.getInt(Const.CONFIG_UPLOAD, "videoSize", 10);
				// 检测文件是否过大
				// 进行大小转换 单位MB 1024*1024 = 1048576
				if (bean.getFileSize() > (videoSize * 1048576)) {
					return ResultData.build().error("上传视频大于"+videoSize+"MB");
				}
				if(uploadBaseService != null) {
					return uploadBaseService.checkFileIfExist(fileRealPath) ? ResultData.build().success(fileUploadedPath) : uploadBaseService.upload(bean);
				} else {
					return new File(fileRealPath).exists() ? ResultData.build().success(fileUploadedPath) : this.upload(bean);
				}
			}
		}
		String[] fileTypes = ConfigUtil.getString(Const.CONFIG_UPLOAD, "fileType", "zip").split(",");
		for (String fileType : fileTypes) {
			if (uploadFileType.equalsIgnoreCase(fileType)) {
				long fileSize = ConfigUtil.getInt(Const.CONFIG_UPLOAD, "fileSize", 20);
				// 检测文件是否过大
				// 进行大小转换 单位MB 1024*1024 = 1048576
				if (bean.getFileSize() > (fileSize * 1048576)) {
					return ResultData.build().error("上传文件大于"+fileSize+"MB");
				}
				if(uploadBaseService != null) {
					return uploadBaseService.checkFileIfExist(fileRealPath) ? ResultData.build().success(fileUploadedPath) : uploadBaseService.upload(bean);
				} else {
					return new File(fileRealPath).exists() ? ResultData.build().success(fileUploadedPath) : this.upload(bean);
				}
			}
		}
		return ResultData.build().error("文件类型被拒绝:"+uploadFileType);
	}



	@ApiOperation(value = "处理post请求上传模板文件")
	@PostMapping("/uploadTemplate")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "uploadPath", value = "上传文件夹地址", required =false,paramType="form"),
			@ApiImplicitParam(name = "file", value = "文件流", dataType="__file",required =false,paramType="form"),
			@ApiImplicitParam(name = "rename", value = "是否重命名", required =false,paramType="form",defaultValue="true"),
			@ApiImplicitParam(name = "appId", value = "上传路径是否需要拼接appId", required =false,paramType="form",defaultValue="false"),
			@ApiImplicitParam(name = "uploadFolderPath", value = "是否修改上传目录", required =false,paramType="form",defaultValue="false"),
	})
	@ResponseBody
	public ResultData uploadTemplate(@ApiIgnore UploadConfigBean bean, HttpServletResponse res) throws IOException {
		//检查文件类型
		String uploadTemplatePath = ConfigUtil.getString("文件上传配置", "uploadTemplate", "template");

		//需要根据糊涂工具的类型判断，此处使用hutool工具直接stream流形式获取会报错，采用临时文件解决
		File temp = FileUtil.newFile(BasicUtil.getRealPath("temp/"+bean.getFile().getOriginalFilename()));
		FileUtils.copyInputStreamToFile(bean.getFile().getInputStream(), temp);
		String uploadFileType =  FileTypeUtil.getType(temp);
		temp.delete();

		//如果上传文件后缀为jar则直接返回错误信息
		String _uploadFileType = FileUtil.getSuffix(bean.getFile().getOriginalFilename());
		if(_uploadFileType.equalsIgnoreCase("jar")) {
			return ResultData.build().error("文件类型被拒绝:"+bean.getFile().getOriginalFilename());
		}
		//非法路径过滤
		if(checkUploadPath(bean)){
			return ResultData.build().error(getResString("err.error", new String[]{getResString("file.type")}));
		}
		// 上传模板
		if (StringUtils.isEmpty(bean.getUploadPath())) {
			bean.setUploadPath(uploadTemplatePath + File.separator +  BasicUtil.getApp().getAppId());
		} else{
			// 上传模板文件
			bean.setUploadPath(uploadTemplatePath + File.separator +  bean.getUploadPath());
		}

		String[] fileTypes = ConfigUtil.getString(Const.CONFIG_UPLOAD, "fileType", "zip").split(",");
		for (String fileType : fileTypes) {
			//压缩文件存储方式会导致hutool工具识别zip文件为jar类型，这里对hutool工具判断错误进行容错处理
			if (uploadFileType.equalsIgnoreCase(fileType) || uploadFileType.equalsIgnoreCase("jar")) {
				long fileSize = ConfigUtil.getInt(Const.CONFIG_UPLOAD, "fileSize", 20);
				// 检测文件是否过大
				// 进行大小转换 单位MB 1024*1024 = 1048576
				if (bean.getFile().getSize() > (fileSize * 1048576)) {
					return ResultData.build().error("上传文件大于"+fileSize+"MB");
				}
				ResultData resultData = this.uploadTemplate(bean);
				String templateUrl = uploadTemplatePath + File.separator + BasicUtil.getApp().getAppId();
				// 上传模板zip才解压
				if (!templateUrl.equals(bean.getUploadPath())){
					return ResultData.build().success();
				}
				// 开始解压
				String fileUrl = (String) resultData.get(ResultData.DATA_KEY);
				//校验路径
				if (fileUrl != null && (fileUrl.contains("../") || fileUrl.contains("..\\"))) {
					ResultData.build().error();
				}
				File zipFile = new File(BasicUtil.getRealTemplatePath(fileUrl));
				ZipUtil.unzip(zipFile.getPath(),zipFile.getParent(), Charset.forName("gbk"));
				FileUtil.del(zipFile);

				//获取文件夹下所有文件
				List<File> files = FileUtil.loopFiles(zipFile.getParent());
				//禁止上传的格式
				List<String> deniedList = Arrays.stream(MSProperties.upload.denied.split(",")).map(String::toLowerCase).collect(Collectors.toList());
				for (File file : files) {
					FileInputStream fileInputStream = new FileInputStream(file);
					//文件的真实类型
					String tmplFileType = FileTypeUtil.getType(file).toLowerCase();
					//通过yml的配置检查文件格式是否合法
					if (deniedList.contains(tmplFileType)){
						IOUtils.closeQuietly(fileInputStream);
						// 如果同目录或其他模板下存在不合法的文件类型，会清除站点下所有文件
						FileUtil.del(zipFile.getParent());
						throw new RuntimeException(StrUtil.format("压缩包内文件{}的类型{}禁止上传",file.getName(),tmplFileType));
					}
					IOUtils.closeQuietly(fileInputStream);
				}
				return ResultData.build().success();
			}
		}
		return ResultData.build().error("文件类型被拒绝:"+bean.getFile().getOriginalFilename());
	}

	protected boolean checkUploadPath(UploadConfigBean bean){
		return (bean.getUploadPath()!=null&&(bean.getUploadPath().contains("../")||bean.getUploadPath().contains("..\\")));
	}

	@ApiOperation(value = "处理WEB-INF文件下载接口")
	@PostMapping(value = "/download")
	public void download(String filePath, HttpServletResponse response) {
		//检测空
		if (StringUtils.isEmpty(filePath)) {
			return;
		}
		//检测非法路径
		if (filePath.contains("../") || filePath.contains("..\\")) {
			return;
		}

		response.setHeader("content-type", "application/octet-stream");
		response.setContentType("application/octet-stream");
		String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
		try {
			fileName = new String(fileName.getBytes("UTF-8"),"ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		try {
			FileUtil.writeToStream(BasicUtil.getRealPath("WEB-INF/" + filePath), response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
