/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.ad.action.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.ad.biz.IPositionBiz;
import net.mingsoft.ad.entity.PositionEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.util.BasicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
@Api(tags={"前端-广告模块接口"})
@Controller("WebadPositionAction")
@RequestMapping("/ad/position")
public class PositionAction extends net.mingsoft.ad.action.BaseAction{


	/**
	 * 注入广告位业务层
	 */
	@Autowired
	private IPositionBiz positionBiz;

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
	public ResultData list(@ModelAttribute @ApiIgnore PositionEntity position,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		BasicUtil.startPage();
		List positionList = positionBiz.query(position);
		return ResultData.build().success(new EUListBean(positionList,(int)BasicUtil.endPage(positionList).getTotal()));
	}

	/**
	 * 获取广告位
	 * @param position 广告位实体
	 */
	@ApiOperation(value = "获取广告位列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore PositionEntity position,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(position.getId()==null) {
			return ResultData.build().error();
		}
		PositionEntity _position = (PositionEntity)positionBiz.getEntity(Integer.parseInt(position.getId()));
		return ResultData.build().success(_position);
	}


}
