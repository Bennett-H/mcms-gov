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
import net.mingsoft.ad.biz.IPositionBiz;
import net.mingsoft.ad.entity.PositionEntity;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 广告位管理控制层
 * @author 铭飞开发团队
 * 创建日期：2019-11-23 8:49:39<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-广告模块接口"})
@Controller("adPositionAction")
@RequestMapping("/${ms.manager.path}/ad/position")
public class PositionAction extends BaseAction{


	/**
	 * 注入广告位业务层
	 */
	@Autowired
	private IPositionBiz positionBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/ad/position/index";
	}

	/**
	 * 查询广告位列表
	 * @param position 广告位实体
	 */
	@ApiOperation(value = "查询广告位列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "positionName", value = "广告位名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "positionWidth", value = "广告位宽度", required =false,paramType="query"),
    	@ApiImplicitParam(name = "positionHeight", value = "广告位高度", required =false,paramType="query"),
    	@ApiImplicitParam(name = "positionDesc", value = "广告位描述", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	@GetMapping("/list")
	@ResponseBody
	@RequiresPermissions("ad:position:view")
	public ResultData list(@ModelAttribute @ApiIgnore PositionEntity position,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		List positionList = positionBiz.query(position);
		return ResultData.build().success(new EUListBean(positionList,(int)BasicUtil.endPage(positionList).getTotal()));
	}

	/**
	 * 返回编辑界面position_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	public String form(@ModelAttribute PositionEntity position,HttpServletResponse response,HttpServletRequest request,ModelMap model){
		if(position.getId()!=null){
			BaseEntity positionEntity = positionBiz.getEntity(Integer.parseInt(position.getId()));
			model.addAttribute("positionEntity",positionEntity);
		}
		return "/ad/position/form";
	}

	/**
	 * 获取广告位
	 * @param position 广告位实体
	 */
	@ApiOperation(value = "获取广告位列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions("ad:position:view")
	public ResultData get(@ModelAttribute @ApiIgnore PositionEntity position,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(position.getId()==null) {
			return ResultData.build().error();
		}
		PositionEntity _position = (PositionEntity)positionBiz.getEntity(Integer.parseInt(position.getId()));
		return ResultData.build().success(_position);
	}

	@ApiOperation(value = "保存广告位列表接口")
	 @ApiImplicitParams({
    	@ApiImplicitParam(name = "positionName", value = "广告位名称", required =true,paramType="query"),
    	@ApiImplicitParam(name = "positionWidth", value = "广告位宽度", required =true,paramType="query"),
    	@ApiImplicitParam(name = "positionHeight", value = "广告位高度", required =true,paramType="query"),
		@ApiImplicitParam(name = "positionDesc", value = "广告位描述", required =false,paramType="query"),
	})

	/**
	* 保存广告位
	* @param position 广告位实体
	*/
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存广告位", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("ad:position:save")
	public ResultData save(@ModelAttribute @ApiIgnore PositionEntity position, HttpServletResponse response, HttpServletRequest request) {
		//验证广告位名称的值是否合法
		if(StringUtil.isBlank(position.getPositionName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("position.name")));
		}
		//验证广告位宽度的值是否合法
		if(StringUtil.isBlank(position.getPositionWidth())){
			return ResultData.build().error(getResString("err.empty", this.getResString("position.width")));
		}
		if(!StringUtil.checkLength(position.getPositionWidth()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("position.width"), "0", "11"));
		}
		//验证广告位高度的值是否合法
		if(StringUtil.isBlank(position.getPositionHeight())){
			return ResultData.build().error(getResString("err.empty", this.getResString("position.height")));
		}
		if(!StringUtil.checkLength(position.getPositionHeight()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("position.height"), "0", "11"));
		}
		positionBiz.saveEntity(position);
		return ResultData.build().success(position);
	}

	@ApiOperation(value = "批量删除广告位列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除广告位", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("ad:position:del")
	public ResultData delete(@RequestBody List<PositionEntity> positions,HttpServletResponse response, HttpServletRequest request) {
		int[] ids = new int[positions.size()];
		for(int i = 0;i<positions.size();i++){
			ids[i] =Integer.parseInt(positions.get(i).getId()) ;
		}
		positionBiz.delete(ids);
		return ResultData.build().success();
	}
	/**
	*	更新广告位列表
	* @param position 广告位实体
	*/
	 @ApiOperation(value = "更新广告位列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
    	@ApiImplicitParam(name = "positionName", value = "广告位名称", required =true,paramType="query"),
    	@ApiImplicitParam(name = "positionWidth", value = "广告位宽度", required =true,paramType="query"),
    	@ApiImplicitParam(name = "positionHeight", value = "广告位高度", required =true,paramType="query"),
		@ApiImplicitParam(name = "positionDesc", value = "广告位描述", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新广告位", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("ad:position:update")
	public ResultData update(@ModelAttribute @ApiIgnore PositionEntity position, HttpServletResponse response,
			HttpServletRequest request) {
		//验证广告位名称的值是否合法
		if(StringUtil.isBlank(position.getPositionName())){
			return ResultData.build().error(getResString("err.empty", this.getResString("position.name")));
		}
		//验证广告位宽度的值是否合法
		if(StringUtil.isBlank(position.getPositionWidth())){
			return ResultData.build().error(getResString("err.empty", this.getResString("position.width")));
		}
		if(!StringUtil.checkLength(position.getPositionWidth()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("position.width"), "0", "11"));
		}
		//验证广告位高度的值是否合法
		if(StringUtil.isBlank(position.getPositionHeight())){
			return ResultData.build().error(getResString("err.empty", this.getResString("position.height")));
		}
		if(!StringUtil.checkLength(position.getPositionHeight()+"", 0, 11)){
			return ResultData.build().error(getResString("err.length", this.getResString("position.height"), "0", "11"));
		}
		positionBiz.updateEntity(position);
		return ResultData.build().success(position);
	}



}
