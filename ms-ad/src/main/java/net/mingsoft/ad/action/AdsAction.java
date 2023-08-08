/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.ad.action;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.ad.bean.AdsBean;
import net.mingsoft.ad.biz.IAdsBiz;
import net.mingsoft.ad.entity.AdsEntity;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
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
 * 广告管理控制层
 * @author 铭飞开发团队
 * 创建日期：2019-11-23 8:49:39<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-广告模块接口"})
@Controller("adAdsAction")
@RequestMapping("/${ms.manager.path}/ad/ads")
public class AdsAction extends BaseAction{


	/**
	 * 注入广告业务层
	 */
	@Autowired
	private IAdsBiz adsBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping({"/index"})
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/ad/ads/index";
	}

	/**
	 * 查询广告列表
	 * @param ads 广告实体
	 */
	@ApiOperation(value = "查询广告列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "positionId", value = "广告位编号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "adsName", value = "广告名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "adsType", value = "广告类型", required =false,paramType="query"),
    	@ApiImplicitParam(name = "adsStartTime", value = "开始时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "adsEndTime", value = "结束时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "adsState", value = "是否开启", required =false,paramType="query"),
    	@ApiImplicitParam(name = "adsPeopleName", value = "广告联系人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "adsPeoplePhone", value = "广告联系人电话", required =false,paramType="query"),
    	@ApiImplicitParam(name = "adsPeopleEmail", value = "广告联系人邮箱", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@GetMapping("/list")
	@ResponseBody
	@RequiresPermissions("ad:ads:view")
	public ResultData list(@ModelAttribute @ApiIgnore AdsBean ads,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		List adsList = adsBiz.query(ads);
		return ResultData.build().success(new EUListBean(adsList,(int)BasicUtil.endPage(adsList).getTotal()));
	}

	/**
	 * 返回编辑界面ads_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	public String form(@ModelAttribute AdsEntity ads,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		if(ads.getId()!=null){
			BaseEntity adsEntity = adsBiz.getEntity(Integer.parseInt(ads.getId()));
			model.addAttribute("adsEntity",adsEntity);
		}
		return "/ad/ads/form";
	}

	/**
	 * 返回预览界面
	 */
	@ApiIgnore
	@GetMapping("/view")
	public String view(@ModelAttribute AdsEntity ads,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		if(ads.getId()!=null){
			BaseEntity adsEntity = adsBiz.getEntity(Integer.parseInt(ads.getId()));
			model.addAttribute("adsEntity",adsEntity);
		}
		return "/ad/ads/view";
	}

	/**
	 * 获取广告
	 * @param ads 广告实体
	 */
	@ApiOperation(value = "获取广告列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions("ad:ads:view")
	public ResultData get(@ModelAttribute @ApiIgnore AdsBean ads, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model){
		if(ads.getId()==null) {
			return ResultData.build().error();
		}
		AdsEntity _ads = (AdsEntity)adsBiz.getEntity(Integer.parseInt(ads.getId()));
		return ResultData.build().success(_ads);
	}

	@ApiOperation(value = "保存广告列表接口")
	 @ApiImplicitParams({
		@ApiImplicitParam(name = "positionId", value = "广告位编号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "adsName", value = "广告名称", required =true,paramType="query"),
    	@ApiImplicitParam(name = "adsType", value = "广告类型", required =true,paramType="query"),
    	@ApiImplicitParam(name = "adsStartTime", value = "开始时间", required =true,paramType="query"),
    	@ApiImplicitParam(name = "adsEndTime", value = "结束时间", required =true,paramType="query"),
    	@ApiImplicitParam(name = "adsState", value = "是否开启", required =true,paramType="query"),
		@ApiImplicitParam(name = "adsPeopleName", value = "广告联系人", required =false,paramType="query"),
		@ApiImplicitParam(name = "adsPeoplePhone", value = "广告联系人电话", required =false,paramType="query"),
		@ApiImplicitParam(name = "adsPeopleEmail", value = "广告联系人邮箱", required =false,paramType="query"),
	})

	/**
	* 保存广告
	* @param ads 广告实体
	*/
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存广告", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("ad:ads:save")
	public ResultData save(@ModelAttribute @ApiIgnore AdsEntity ads, HttpServletResponse response, HttpServletRequest request) {
		if(!StringUtil.checkLength(ads.getPositionId()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("post.id"), "0", "11"));
		}
		//验证广告名称的值是否合法
		if(StringUtil.isBlank(ads.getAdsName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ads.name  ")));
		}
		//验证广告类型的值是否合法
		if(StringUtil.isBlank(ads.getAdsType())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ads.type")));
		}
		//验证开始时间的值是否合法
		if(StringUtil.isBlank(ads.getAdsStartTime())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ads.start.time")));
		}
		//验证结束时间的值是否合法
		if(StringUtil.isBlank(ads.getAdsEndTime())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ads.end.time")));
		}
		//验证是否开启的值是否合法
		if(StringUtil.isBlank(ads.getAdsState())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ads.state")));
		}
		adsBiz.saveEntity(ads);
		return ResultData.build().success(ads);
	}

	@ApiOperation(value = "批量删除广告列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除广告", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("ad:ads:del")
	public ResultData delete(@RequestBody List<AdsEntity> adss,HttpServletResponse response, HttpServletRequest request) {
		int[] ids = new int[adss.size()];
		for(int i = 0;i<adss.size();i++){
			ids[i] =Integer.parseInt(adss.get(i).getId()) ;
		}
		adsBiz.delete(ids);
		return ResultData.build().success();
	}
	/**
	*	更新广告列表
	* @param ads 广告实体
	*/
	 @ApiOperation(value = "更新广告列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
		@ApiImplicitParam(name = "positionId", value = "广告位编号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "adsName", value = "广告名称", required =true,paramType="query"),
    	@ApiImplicitParam(name = "adsType", value = "广告类型", required =true,paramType="query"),
    	@ApiImplicitParam(name = "adsStartTime", value = "开始时间", required =true,paramType="query"),
    	@ApiImplicitParam(name = "adsEndTime", value = "结束时间", required =true,paramType="query"),
    	@ApiImplicitParam(name = "adsState", value = "是否开启", required =true,paramType="query"),
		@ApiImplicitParam(name = "adsPeopleName", value = "广告联系人", required =false,paramType="query"),
		@ApiImplicitParam(name = "adsPeoplePhone", value = "广告联系人电话", required =false,paramType="query"),
		@ApiImplicitParam(name = "adsPeopleEmail", value = "广告联系人邮箱", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新广告", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("ad:ads:update")
	public ResultData update(@ModelAttribute @ApiIgnore AdsEntity ads, HttpServletResponse response,
			HttpServletRequest request) {
		if(!StringUtil.checkLength(ads.getPositionId()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("post.id"), "0", "11"));
		}
		//验证广告名称的值是否合法
		if(StringUtil.isBlank(ads.getAdsName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ads.name  ")));
		}
		//验证广告类型的值是否合法
		if(StringUtil.isBlank(ads.getAdsType())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ads.type")));
		}
		//验证开始时间的值是否合法
		if(StringUtil.isBlank(ads.getAdsStartTime())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ads.start.time")));
		}
		//验证结束时间的值是否合法
		if(StringUtil.isBlank(ads.getAdsEndTime())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ads.end.time")));
		}
		//验证是否开启的值是否合法
		if(StringUtil.isBlank(ads.getAdsState())){
			return ResultData.build().error(getResString("err.empty", this.getResString("ads.state")));
		}
		adsBiz.updateEntity(ads);
		return ResultData.build().success(ads);
	}



}
