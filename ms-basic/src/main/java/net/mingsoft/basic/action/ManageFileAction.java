/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */








package net.mingsoft.basic.action;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
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
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.config.MSProperties;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * 上传文件
 */
@Api(tags={"后端-基础接口"})
@Controller("ManageFileAction")
@RequestMapping("${ms.manager.path}/file")
public class ManageFileAction extends BaseFileAction {



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
	@ApiImplicitParams({
			@ApiImplicitParam(name = "uploadPath", value = "上传文件夹地址", required =false,paramType="form"),
			@ApiImplicitParam(name = "file", value = "文件流", dataType="__file",required =false,paramType="form"),
			@ApiImplicitParam(name = "rename", value = "是否重命名", required =false,paramType="form",defaultValue="true"),
			@ApiImplicitParam(name = "appId", value = "上传路径是否需要拼接appId", required =false,paramType="form",defaultValue="false"),
			@ApiImplicitParam(name = "uploadFolderPath", value = "是否修改上传目录", required =false,paramType="form",defaultValue="false"),
	})
	@PostMapping(value = "/upload",consumes = "multipart/*",headers = "content-type=multipart/form-data")
	@ResponseBody
	public ResultData upload(@ApiIgnore UploadConfigBean bean, @ApiIgnore boolean uploadFolderPath, HttpServletRequest req, HttpServletResponse res) throws IOException {
		//非法路径过滤
		if(checkUploadPath(bean)){
			return ResultData.build().error();
		}
		// 是否需要拼接appId
		if (bean.isAppId()) {
			bean.setUploadPath(BasicUtil.getApp().getAppId() + File.separator + bean.getUploadPath());
		}

		UploadConfigBean config = new UploadConfigBean(bean.getUploadPath(),bean.getFile(),null,uploadFolderPath,bean.isRename());
		return this.upload(config);
	}

	@ApiOperation(value = "处理post请求上传模板文件")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "uploadPath", value = "上传文件夹地址", required =false,paramType="form"),
			@ApiImplicitParam(name = "file", value = "文件流", dataType="__file",required =false,paramType="form"),
			@ApiImplicitParam(name = "rename", value = "是否重命名", required =false,paramType="form",defaultValue="true"),
			@ApiImplicitParam(name = "appId", value = "上传路径是否需要拼接appId", required =false,paramType="form",defaultValue="false"),
			@ApiImplicitParam(name = "uploadFolderPath", value = "是否修改上传目录", required =false,paramType="form",defaultValue="false"),
	})
	@PostMapping("/uploadTemplate")
	@ResponseBody
	public ResultData uploadTemplate(@ApiIgnore UploadConfigBean bean, @ApiIgnore boolean uploadFolderPath, HttpServletResponse res) throws IOException {
		String uploadTemplatePath = MSProperties.upload.template;
		//非法路径过滤
		if(checkUploadPath(bean)){
			return ResultData.build().error(getResString("err.error", new String[]{getResString("file.type")}));
		}
		if (StringUtils.isEmpty(bean.getUploadPath())) {
			bean.setUploadPath(uploadTemplatePath + File.separator +  BasicUtil.getApp().getAppId());
		} else if(!bean.getUploadPath().substring(0,uploadTemplatePath.length()).equalsIgnoreCase(uploadTemplatePath)){
			throw new BusinessException("uploadPath参数错误");
		}

		UploadConfigBean config = new UploadConfigBean(bean.getUploadPath(), bean.getFile(),null, uploadFolderPath, bean.isRename());
		ResultData resultData = this.uploadTemplate(config);
		String templateUrl = uploadTemplatePath + File.separator +  BasicUtil.getApp().getAppId();
		// 上传模板zip才解压
		if (!templateUrl.equals(bean.getUploadPath())){
			return ResultData.build().success();
		}
		// TODO: 2023/7/11  开始解压
		String fileUrl = (String) resultData.get(ResultData.DATA_KEY);
		//校验路径
		if (fileUrl != null && (fileUrl.contains("../") || fileUrl.contains("..\\"))) {
			return ResultData.build().error();
		}
		File zipFile = new File(BasicUtil.getRealTemplatePath(fileUrl));
		ZipUtil.unzip(zipFile.getPath(),zipFile.getParent(), Charset.forName("gbk"));
		FileUtil.del(zipFile);

		//删除分享模板下zip下的index.html文件\html\___data文件夹
		String htmlPath = zipFile.getParent()+"/html";
		if(FileUtil.exist(htmlPath)) {
			FileUtil.del(htmlPath);
		}

		String dataPath = zipFile.getParent()+"/data";
		if(FileUtil.exist(dataPath)) {
			FileUtil.del(dataPath);
		}

		//获取文件夹下所有文件
		List<File> files = FileUtil.loopFiles(zipFile.getParent());

		//禁止上传的格式
		List<String> deniedList = Arrays.stream(MSProperties.upload.denied.split(",")).map(String::toLowerCase).collect(Collectors.toList());
		for (File file : files) {
			FileInputStream fileInputStream = new FileInputStream(file);
			//文件的真实类型
			String fileType = FileTypeUtil.getType(file).toLowerCase();
			//通过yml的配置检查文件格式是否合法
			if (deniedList.contains(fileType)){
				IOUtils.closeQuietly(fileInputStream);
				// 如果同目录或其他模板下存在不合法的文件类型，会清除站点下所有文件
				FileUtil.del(zipFile.getParent());
				throw new RuntimeException(StrUtil.format("压缩包内文件{}的类型{}禁止上传",file.getName(),fileType));
			}
			IOUtils.closeQuietly(fileInputStream);
		}

		return ResultData.build().success();
	}

	protected boolean checkUploadPath(UploadConfigBean bean){
		return (bean.getUploadPath()!=null&&(bean.getUploadPath().contains("../")||bean.getUploadPath().contains("..\\")));
	}



}
