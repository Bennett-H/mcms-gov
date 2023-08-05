/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.action;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.ManagerAdminEnum;
import net.mingsoft.basic.constant.e.SessionConstEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.co.biz.IFileBiz;
import net.mingsoft.co.entity.FileEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 文件管理控制层,用于管理资源文件
 * @author 铭软科技
 * 创建日期：2021-5-27 10:18:02<br/>
 * 历史修订：2023-3-16 10:37:31 移除无用接口<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-企业模块接口"})
@Controller("coFileAction")
@RequestMapping("/${ms.manager.path}/co/file")
public class FileAction extends BaseAction {


	/**
	 * 注入文件管理业务层
	 */
	@Autowired
	private IFileBiz fileBiz;



	/**
	 * 查询当前管理员的文件管理列表
	 * @param file 文件管理实体
	 */
	@ApiOperation(value = "查询当前管理员的文件管理列表接口")
	@RequestMapping(value ="/listForManager",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData listForManager(@ModelAttribute @ApiIgnore FileEntity file,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		ManagerEntity session =  BasicUtil.getManager();
		// 设置当前管理员ID
		file.setCreateBy(session.getId());
		List<FileEntity> fileList = fileBiz.list(new QueryWrapper<>(file).lambda().orderByDesc(FileEntity::getCreateDate));
		return ResultData.build().success(new EUListBean(fileList,(int)BasicUtil.endPage(fileList).getTotal()));
	}


	/**
	 * 查询当前管理员的图片、视频文件列表
	 * @param file 文件管理实体
	 */
	@ApiOperation(value = "查询当前管理员的图片、视频文件列表")
	@RequestMapping(value ="/listImageAndVideoForManager",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData listImageAndVideoForManager(@ModelAttribute @ApiIgnore FileEntity file,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		ManagerEntity session =  BasicUtil.getManager();
		// 设置当前管理员ID
		if (!ManagerAdminEnum.SUPER.toString().equalsIgnoreCase(session.getManagerAdmin())){
			file.setCreateBy(session.getId());
		}
		String imageType = ConfigUtil.getString("文件上传配置", "imageType", "jpg");
		String videoType = ConfigUtil.getString("文件上传配置", "videoType", "mp4");
		// 图片视频文件类型数组
		String[] ivTypes = (imageType + "," + videoType).split(",");
		List<FileEntity> fileList = fileBiz.list(new QueryWrapper<>(file).lambda().in(FileEntity::getFileType,ivTypes).orderByDesc(FileEntity::getCreateDate));
		return ResultData.build().success(new EUListBean(fileList,(int)BasicUtil.endPage(fileList).getTotal()));
	}

}
