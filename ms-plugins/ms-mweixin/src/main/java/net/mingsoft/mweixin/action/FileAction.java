/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.action;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialFileBatchGetResult;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mweixin.bean.CategoryFileBean;
import net.mingsoft.mweixin.bean.FileBean;
import net.mingsoft.mweixin.biz.ICategoryBiz;
import net.mingsoft.mweixin.biz.IFileBiz;
import net.mingsoft.mweixin.constant.SessionConst;
import net.mingsoft.mweixin.entity.CategoryEntity;
import net.mingsoft.mweixin.entity.FileEntity;
import net.mingsoft.mweixin.entity.WeixinEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 微信文件表管理控制层
 * @author 铭飞开发团队
 * 创建日期：2018-12-29 9:18:11<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-微信模块接口"})
@Controller("wxFileAction")
@RequestMapping("/${ms.manager.path}/mweixin/file")
public class FileAction extends BaseAction{


	/**
	 * 注入微信文件表业务层
	 */
	@Autowired
	private IFileBiz fileBiz;

	/**
	 * 注入微信文件表业务层
	 */
	@Autowired
	private ICategoryBiz categoryBiz;


	/**
	 * 返回图片主界面index
	 */
	@ApiIgnore
	@GetMapping("/picture/index")
	@RequiresPermissions("picture:view")
	public String pictureIndex(HttpServletResponse response, HttpServletRequest request) {
		return "/mweixin/picture/index";
	}

	/**
	 * 返回图片编辑界面form
	 */
	@ApiIgnore
	@GetMapping("/picture/form")
	@RequiresPermissions(value = {"picture:save", "picture:update"}, logical = Logical.OR)
	public String pictureForm(HttpServletResponse response, HttpServletRequest request) {
		return "/mweixin/picture/form";
	}

	/**
	 * 返回语音主界面index
	 */
	@ApiIgnore
	@GetMapping("/voice/index")
	@RequiresPermissions("voice:view")
	public String voiceIndex(HttpServletResponse response, HttpServletRequest request) {
		return "/mweixin/voice/index";
	}

	/**
	 * 返回语音编辑界面form
	 */
	@ApiIgnore
	@GetMapping("/voice/form")
	@RequiresPermissions(value = {"voice:save", "voice:edit"}, logical = Logical.OR)
	public String voiceForm(HttpServletResponse response, HttpServletRequest request) {
		return "/mweixin/voice/form";
	}


	/**
	 * 返回视频主界面index
	 */
	@ApiIgnore
	@GetMapping("/video/index")
	@RequiresPermissions("video:view")
	public String videoIndex(HttpServletResponse response, HttpServletRequest request) {
		return "/mweixin/video/index";
	}

	/**
	 * 返回视频编辑界面form
	 */
	@ApiIgnore
	@GetMapping("/video/form")
	@RequiresPermissions(value = {"video:save", "video:edit"}, logical = Logical.OR)
	public String videoForm(HttpServletResponse response, HttpServletRequest request) {
		return "/mweixin/video/form";
	}


	/**
	 * 查询微信文件表列表
	 * @param fileBean 微信文件表实体
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
	@ApiOperation(value="微信文章素材列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "categoryId", value = "文件分类编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileName", value = "文件名称", required = false,paramType="query"),
			@ApiImplicitParam(name = "fileUrl", value = "文件链接", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileSize", value = "文件大小", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileType", value = "文件类型：图片、音频、视频等", required = false,paramType="query"),
			@ApiImplicitParam(name = "isChild", value = "子业务", required = false,paramType="query"),
			@ApiImplicitParam(name = "weixinId", value = "微信编号", required = false,paramType="query"),
			@ApiImplicitParam(name = "isSync", value = "是否同步至微信", required = false,paramType="query")
	})
	@GetMapping("/list")
	@ResponseBody
	@RequiresPermissions(value = {"video:view", "voice:view", "picture:view"}, logical = Logical.OR)
	public ResultData list(@ModelAttribute FileBean fileBean, HttpServletResponse response, HttpServletRequest request) {
		WeixinEntity weixin = (WeixinEntity) BasicUtil.getSession(SessionConst.WEIXIN_SESSION);
		fileBean.setWeixinId(weixin.getIntId());
		BasicUtil.startPage();
		List fileList = fileBiz.query(fileBean);
		return ResultData.build().success(new EUListBean(fileList,(int)BasicUtil.endPage(fileList).getTotal()));
	}

	@ApiOperation(value="微信文章素材列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "categoryId", value = "文件分类编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileName", value = "文件名称", required = false,paramType="query"),
			@ApiImplicitParam(name = "fileUrl", value = "文件链接", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileSize", value = "文件大小", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileType", value = "文件类型：图片、音频、视频等", required = false,paramType="query"),
			@ApiImplicitParam(name = "isChild", value = "子业务", required = false,paramType="query"),
			@ApiImplicitParam(name = "weixinId", value = "微信编号", required = false,paramType="query"),
			@ApiImplicitParam(name = "isSync", value = "是否同步至微信", required = false,paramType="query")
	})
	@GetMapping("/categoryFile")
	@ResponseBody
	public ResultData categoryFile(FileBean fileBean,HttpServletResponse response, HttpServletRequest request) {
		//取出微信实体
		WeixinEntity weixin = this.getWeixinSession(request);
		//若微信或者微信相关数据不存在
		if(weixin == null || weixin.getIntId()<=0){
			return ResultData.build().error(this.getResString("weixin.not.found"));
		}
		fileBean.setWeixinId(weixin.getIntId());
		CategoryEntity category = new CategoryEntity();
		category.setWeixinId(weixin.getIntId());
		//获取分组集合
		List<CategoryEntity> categorys = categoryBiz.query(category);
		//获取文件集合
		List<FileEntity> fileList = fileBiz.query(fileBean);
		List<CategoryFileBean> categoryFiles = new ArrayList<CategoryFileBean>();
		for (CategoryEntity categoryEntity : categorys) {
			CategoryFileBean categoryFile = new CategoryFileBean();
			List<FileEntity> files = new ArrayList<FileEntity>();
			for (FileEntity wxFile : fileList) {
				if(ObjectUtil.isNotNull(wxFile) && ObjectUtil.isNotNull(wxFile.getCategoryId()) && StringUtils.isNotBlank(categoryEntity.getId())){
					//判断文件分组
					if(wxFile.getCategoryId().equals(Integer.parseInt(categoryEntity.getId()))){
						files.add(wxFile);
					}
				}
			}
			//设置分组文件总数
			categoryFile.setTotal(files.size());
			categoryFile.setCategory(categoryEntity);
			categoryFile.setFiles(files);
			categoryFiles.add(categoryFile);
		}
		return ResultData.build().success(categoryFiles);
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
	@ApiOperation(value="微信文章素材保存接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fileName", value = "文件名称", required = true,paramType="query"),
			@ApiImplicitParam(name = "weixinId", value = "微信编号", required = true,paramType="query"),
			@ApiImplicitParam(name = "categoryId", value = "文件分类编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileUrl", value = "文件链接", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileSize", value = "文件大小", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileType", value = "文件类型：图片、音频、视频等", required = true,paramType="query"),
			@ApiImplicitParam(name = "isChild", value = "子业务", required = false,paramType="query"),

	})
	@LogAnn(title = "微信文章素材保存接口",businessType= BusinessTypeEnum.INSERT)
	@PostMapping("/save")
	@ResponseBody
	@RequiresPermissions(value = {"video:save", "voice:save", "picture:save"}, logical = Logical.OR)
	public ResultData save(@ModelAttribute FileEntity file, HttpServletResponse response, HttpServletRequest request,BindingResult result) {
		//取出微信实体
		WeixinEntity weixin = this.getWeixinSession(request);
		//若微信或者微信相关数据不存在
		if(weixin == null || weixin.getIntId()<=0){
			return ResultData.build().error(this.getResString("weixin.not.found"));
		}
		file.setWeixinId(weixin.getIntId());
		file.setCreateDate(new Date());
		fileBiz.save(file);
		try {
			fileBiz.weiXinFileUpload(weixin,file);
		} catch (WxErrorException e) {
			e.printStackTrace();
			return ResultData.build().error(e.getMessage());
		}
		return ResultData.build().success(file);
	}

	/**
	 * 同步微信图片
	 * @param request
	 * @param response
	 */
	@ApiOperation(value = "同步微信图片")
	@GetMapping("/syncImages")
	@ResponseBody
	@RequiresPermissions("picture:sync")
	public ResultData weiXinNewsSyncLocal(HttpServletRequest request,HttpServletResponse response){
		try {
			//取出微信实体
			WeixinEntity weixin = this.getWeixinSession(request);
			//若微信或者微信相关数据不存在
			if(weixin == null || weixin.getIntId()<=0){
				return ResultData.build().error(this.getResString("weixin.not.found"));
			}
			for(int offset = 0;;) {
				int count =offset+20;
				WxMpMaterialFileBatchGetResult imageBatchGetResult = this.builderWeixinService(weixin.getWeixinNo()).getMaterialService().materialFileBatchGet("image", offset, count);
				fileBiz.weiXinFileSyncLocal(imageBatchGetResult, weixin.getIntId());
				offset = count;
				if (imageBatchGetResult.getItems().size()<=0){
					break;
				}
			}
		} catch (WxErrorException e) {
			e.printStackTrace();
			return ResultData.build().error(e.getMessage());
		}
		return ResultData.build().success();
	}


	/**
	 * @param files 基础文件表实体
	 * <i>file参数包含字段信息参考：</i><br/>
	 * id:多个id直接用逗号隔开,例如id=1,2,3,4
	 * 批量删除基础文件表
	 *            <dt><span class="strong">返回</span></dt><br/>
	 *            <dd>{code:"错误编码",<br/>
	 *            result:"true｜false",<br/>
	 *            resultMsg:"错误信息"<br/>
	 *            }</dd>
	 */
	@ApiOperation(value = "批量删除基础文件表实体接口")
	@LogAnn(title = "基础文件表实体",businessType= BusinessTypeEnum.DELETE)
	@PostMapping("/delete")
	@ResponseBody
	@RequiresPermissions(value = {"video:del", "voice:del", "picture:del"}, logical = Logical.OR)
	public ResultData delete(@RequestBody List<FileEntity> files,HttpServletResponse response, HttpServletRequest request) {
		//取出微信实体
		WeixinEntity weixin = this.getWeixinSession(request);
		//若微信或者微信相关数据不存在
		if(weixin == null || weixin.getIntId()<=0){
			return ResultData.build().error(this.getResString("weixin.not.found"));
		}
		int[] ids = new int[files.size()];
		for(int i = 0;i<files.size();i++){
			ids[i] =Integer.parseInt(files.get(i).getId());
			String picPath = files.get(i).getFileUrl();
			//判断是否存在图片路径
			if(!StringUtils.isBlank(picPath)){
				FileUtil.del(BasicUtil.getRealPath(picPath));
			}
		}
		//判断是否存在文件编号，图片上传错误时单独删除图片
		if(ids.length>0){
			fileBiz.deleteByIds(ids,weixin.getId());
			fileBiz.delete(ids);
		}
		return ResultData.build().success();
	}

	/**
	 * 更新基础文件表信息基础文件表
	 * @param file 基础文件表实体
	 * <i>file参数包含字段信息参考：</i><br/>
	 * id 文件编号<br/>
	 * categoryId 文件分类编号<br/>
	 * appId APP编号<br/>
	 * fileName 文件名称<br/>
	 * fileUrl 文件链接<br/>
	 * fileSize 文件大小<br/>
	 * fileJson 文件详情Json数据<br/>
	 * fileType 文件类型：图片、音频、视频等<br/>
	 * isChild 子业务<br/>
	 * updateDate 更新时间<br/>
	 * updateBy 更新者<br/>
	 * createBy 创建者<br/>
	 * createDate 创建时间<br/>
	 * del 删除标记<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * id: 文件编号<br/>
	 * categoryId: 文件分类编号<br/>
	 * appId: APP编号<br/>
	 * fileName: 文件名称<br/>
	 * fileUrl: 文件链接<br/>
	 * fileSize: 文件大小<br/>
	 * fileJson: 文件详情Json数据<br/>
	 * fileType: 文件类型：图片、音频、视频等<br/>
	 * isChild: 子业务<br/>
	 * updateDate: 更新时间<br/>
	 * updateBy: 更新者<br/>
	 * createBy: 创建者<br/>
	 * createDate: 创建时间<br/>
	 * del: 删除标记<br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value="微信文章素材更新接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fileName", value = "文件名称", required = true,paramType="query"),
			@ApiImplicitParam(name = "weixinId", value = "微信编号", required = true,paramType="query"),
			@ApiImplicitParam(name = "isSync", value = "是否同步至微信", required = true,paramType="query"),
			@ApiImplicitParam(name = "categoryId", value = "文件分类编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileUrl", value = "文件链接", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileSize", value = "文件大小", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileType", value = "文件类型：图片、音频、视频等", required = true,paramType="query"),
			@ApiImplicitParam(name = "isChild", value = "子业务", required = false,paramType="query"),

	})
	@LogAnn(title = "微信文章素材更新接口",businessType= BusinessTypeEnum.UPDATE)
	@PostMapping("/update")
	@ResponseBody
	@RequiresPermissions(value = {"video:edit", "voice:edit", "picture:update"}, logical = Logical.OR)
	public ResultData update(@ModelAttribute FileEntity file, HttpServletResponse response,
							 HttpServletRequest request) {
		//验证文件编号的值是否合法
		if(StringUtils.isBlank(file.getId())){
			return ResultData.build().success(getResString("err.empty", this.getResString("file.id")));
		}
		if(!StringUtil.checkLength(file.getId()+"", 1, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("file.id"), "1", "11"));
		}
		if(!StringUtil.checkLength(file.getCategoryId()+"", 1, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("category.id"), "1", "11"));
		}
		//验证文件名称的值是否合法
		if(StringUtils.isBlank(file.getFileName())){
			return ResultData.build().success(getResString("err.empty", this.getResString("file.name")));
		}
		if(!StringUtil.checkLength(file.getFileName()+"", 1, 200)){
			return ResultData.build().error(getResString("err.length", this.getResString("file.name"), "1", "200"));
		}
		if(!StringUtil.checkLength(file.getFileSize()+"", 1, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("file.size"), "1", "11"));
		}


		file.setUpdateDate(new Date());
		fileBiz.updateEntity(file);
		return ResultData.build().success(file);
	}


	/**
	 * 移动分组
	 * @param files 微信文件表实体
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
	@ApiOperation(value="微信文章素材保存接口")
	@ApiImplicitParam(name = "categoryId", value = "文件分类编号", required =true,paramType="path")
	@PostMapping("/{categoryId}/mobileCategory")
	@ResponseBody
	@RequiresPermissions("file:mobileCategory")
	public ResultData mobileCategory(@RequestBody List<FileEntity> files,@PathVariable @ApiIgnore int categoryId, HttpServletResponse response, HttpServletRequest request,BindingResult result) {
		for (FileEntity file : files) {
			file.setCategoryId(categoryId);
			fileBiz.updateEntity(file);
		}
		return ResultData.build().success();
	}

	/**
	 * 根据素材Id获取素材实体
	 * @param response
	 * @param mode
	 */
	@ApiOperation(value = "根据素材实体")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions(value = {"video:view", "voice:view", "picture:view"}, logical = Logical.OR)
	public ResultData get(@ModelAttribute FileEntity file,HttpServletResponse response,@ApiIgnore ModelMap mode){
		if(ObjectUtil.isNull(file)){
			return ResultData.build().error();
		}
		if(file.getFileId()<=0){
			return ResultData.build().error();
		}
		//根据素材ID获取相应素材
		file = (FileEntity)fileBiz.getEntity(file.getFileId());
		return ResultData.build().success(file);
	}



	/**
	 * 同步微信语音
	 * @param request
	 * @param response
	 */
	@ApiOperation(value = "同步微信语音")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fileName", value = "文件名称", required = true,paramType="query"),
			@ApiImplicitParam(name = "weixinId", value = "微信编号", required = true,paramType="query"),
			@ApiImplicitParam(name = "isSync", value = "是否同步至微信", required = true,paramType="query"),
			@ApiImplicitParam(name = "categoryId", value = "文件分类编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileUrl", value = "文件链接", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileSize", value = "文件大小", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileType", value = "文件类型：图片、音频、视频等", required = true,paramType="query"),
			@ApiImplicitParam(name = "isChild", value = "子业务", required = false,paramType="query"),

	})
	@GetMapping("/syncVoice")
	@ResponseBody
	@RequiresPermissions("voice:sync")
	public ResultData weiXinVoiceSyncLocal(@ModelAttribute FileEntity fileBean,HttpServletResponse response, HttpServletRequest request) {
		//取出微信实体
		WeixinEntity weixin = this.getWeixinSession(request);
		//若微信或者微信相关数据不存在
		if(weixin == null || weixin.getIntId()<=0){
			return ResultData.build().error(this.getResString("weixin.not.found"));
		}
		WxMpMaterialFileBatchGetResult voiceBatchGetResult = null;
		try {
			voiceBatchGetResult = this.builderWeixinService(weixin.getWeixinNo()).getMaterialService().materialFileBatchGet("voice", 0, 20);
		} catch (WxErrorException e) {
			e.printStackTrace();
			return ResultData.build().error(e.getMessage());
		}
		fileBiz.weiXinVoiceVideoSyncLocal(voiceBatchGetResult,fileBean,weixin);
		return ResultData.build().success();
	}


	/**
	 * 同步微信视频
	 * @param request
	 * @param response
	 */
	@ApiOperation(value = "同步微信视频")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fileName", value = "文件名称", required = true,paramType="query"),
			@ApiImplicitParam(name = "weixinId", value = "微信编号", required = true,paramType="query"),
			@ApiImplicitParam(name = "isSync", value = "是否同步至微信", required = true,paramType="query"),
			@ApiImplicitParam(name = "categoryId", value = "文件分类编号", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileUrl", value = "文件链接", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileSize", value = "文件大小", required =false,paramType="query"),
			@ApiImplicitParam(name = "fileType", value = "文件类型：图片、音频、视频等", required = true,paramType="query"),
			@ApiImplicitParam(name = "isChild", value = "子业务", required = false,paramType="query"),

	})
	@GetMapping("/syncVideo")
	@ResponseBody
	@RequiresPermissions("video:sync")
	public ResultData weiXinVideoSyncLocal(@ModelAttribute FileEntity fileBean,HttpServletResponse response, HttpServletRequest request) {
		//取出微信实体
		WeixinEntity weixin = this.getWeixinSession(request);
		//若微信或者微信相关数据不存在
		if(weixin == null || weixin.getIntId()<=0){
			return ResultData.build().error(this.getResString("weixin.not.found"));
		}
		WxMpMaterialFileBatchGetResult voiceBatchGetResult = null;
		try {
			voiceBatchGetResult = this.builderWeixinService(weixin.getWeixinNo()).getMaterialService().materialFileBatchGet("video", 0, 20);
		} catch (WxErrorException e) {
			e.printStackTrace();
			return ResultData.build().error(e.getMessage());
		}
		fileBiz.weiXinVoiceVideoSyncLocal(voiceBatchGetResult,fileBean,weixin);
		return ResultData.build().success();
	}
}

