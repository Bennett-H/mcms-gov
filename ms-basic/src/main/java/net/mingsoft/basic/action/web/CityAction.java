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
import net.mingsoft.basic.bean.CityBean;
import net.mingsoft.basic.biz.ICityBiz;
import net.mingsoft.basic.entity.CityEntity;
import net.mingsoft.basic.util.BasicUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 省市县镇村数据管理控制层
 * @author 铭飞开发团队
 * @version
 * 版本号：100<br/>
 * 创建日期：2017-7-27 14:47:29<br/>
 * 历史修订：<br/>
 */
@Api(tags={"前端-基础接口"})
@Controller("webCityAction")
@RequestMapping("/basic/city")
public class CityAction extends net.mingsoft.basic.action.BaseAction{

	/**
	 * 注入省市县镇村数据业务层
	 */
	@Autowired
	private ICityBiz cityBiz;


	@ApiOperation(value = "查询省市县镇村数据列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "provinceId", value = "省／直辖市／自治区级id", required = false,paramType="query"),
		@ApiImplicitParam(name = "provinceName", value = "省／直辖市／自治区级名称", required = false,paramType="query"),
		@ApiImplicitParam(name = "cityId", value = "市级id", required = false,paramType="query"),
		@ApiImplicitParam(name = "cityName", value = "市级名称", required = false,paramType="query"),
		@ApiImplicitParam(name = "cityPy", value = "城市拼音首字母", required = false,paramType="query"),
		@ApiImplicitParam(name = "countyId", value = "县／区级id", required = false,paramType="query"),
		@ApiImplicitParam(name = "countyName", value = "县／区级名称", required = false,paramType="query"),
		@ApiImplicitParam(name = "townId", value = "街道／镇级id", required = false,paramType="query"),
		@ApiImplicitParam(name = "townName", value = "街道／镇级名称", required = false,paramType="query"),
		@ApiImplicitParam(name = "villageId", value = "村委会id", required = false,paramType="query"),
		@ApiImplicitParam(name = "villageName", value = "村委会名称", required = false,paramType="query"),
	})
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore CityEntity city,HttpServletResponse response, HttpServletRequest request,ModelMap model) {
		BasicUtil.startPage();
		List cityList = cityBiz.query(city);
		BasicUtil.endPage(cityList);
		return ResultData.build().success(cityList);
	}


	@ApiOperation(value = "获取省市县镇村数据")
	@ApiImplicitParam(name = "id", value = "城市主键编号", required = true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore CityEntity city, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model){
		if(StringUtils.isEmpty(city.getId())) {
			return ResultData.build().error( getResString("err.error", this.getResString("id")));
		}
		CityEntity _city = (CityEntity)cityBiz.getEntity(Integer.parseInt(city.getId()));
		return ResultData.build().success( _city);
	}


	@ApiOperation(value = "更新省市县镇村数据信息省市县镇村数据")
	@GetMapping("/query")
	@ResponseBody
	public ResultData query(HttpServletResponse response,HttpServletRequest request) {
		int level = BasicUtil.getInt("level",3);//默认3级
		String type = BasicUtil.getString("type","tree"); //默认为树形结构
		List<CityBean> cityList = (List<CityBean>) cityBiz.queryForTree(level,type);
		return ResultData.build().success(cityList);
	}
	/**
	 * 根据拼音首字母查询城市
	 * @param response
	 * @param request
	 */
	@ApiOperation(value = "根据拼音首字母查询城市")
	@ApiImplicitParam(name = "cityPy", value = "城市拼音首字母", required = true,paramType="query")
	@GetMapping("/queryCityPy")
	@ResponseBody
	public ResultData queryPy(HttpServletResponse response,HttpServletRequest request) {
		String cityPy = BasicUtil.getString("cityPy");
		CityEntity city = new CityEntity();
		city.setCityPy(cityPy);
		List<CityEntity> cityList = (List<CityEntity>) cityBiz.query(city);
		return ResultData.build().success(cityList);
	}

}
