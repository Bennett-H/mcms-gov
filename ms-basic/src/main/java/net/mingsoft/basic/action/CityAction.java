/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.basic.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.CityBean;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.biz.ICityBiz;
import net.mingsoft.basic.entity.CityEntity;
import net.mingsoft.basic.util.BasicUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 省市县镇村数据管理控制层
 * @author 铭飞开发团队
 * @version
 * 版本号：100<br/>
 * 创建日期：2017-7-27 14:47:29<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-基础接口"})
@Controller
@RequestMapping("/${ms.manager.path}/basic/city")
public class CityAction extends BaseAction{

	/**
	 * 注入省市县镇村数据业务层
	 */
	@Autowired
	private ICityBiz cityBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@ApiOperation(value = "返回主界面index")
	@GetMapping("/index")
	@RequiresPermissions("city:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return  "/basic/city/index";
	}

	/**
	 * 返回编辑界面city_form
	 */
	@ApiOperation(value = "返回编辑界面city_form")
	@ApiImplicitParam(name = "id", value = "主键编号", required = true,paramType="query")
	@GetMapping("/form")
	@RequiresPermissions("city:view")
	@ApiIgnore
	public String form(@ModelAttribute @ApiIgnore CityEntity city,HttpServletResponse response,HttpServletRequest request,@ApiIgnore ModelMap model){
		if(!StringUtils.isEmpty(city.getId())){
			BaseEntity cityEntity = cityBiz.getEntity(Integer.parseInt(city.getId()));
			model.addAttribute("cityEntity",cityEntity);
		}
		return "/basic/city/form";
	}



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
	@RequiresPermissions("city:view")
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore CityEntity city, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
		BasicUtil.startPage();
		List<CityEntity> cityList = new ArrayList();
		if(city.getProvinceId() == null){
			cityList = cityBiz.queryProvinceAndName(city);
		}else {
			cityList = cityBiz.queryById(city);
		}
		return ResultData.build().success(new EUListBean(cityList, (int) BasicUtil.endPage(cityList).getTotal()));
	}


	/**
	 * 获取省市县镇村数据
	 * @param city 省市县镇村数据实体
	 * <i>city参数包含字段信息参考：</i><br/>
	 * id 主键编号<br/>
	 * provinceId 省／直辖市／自治区级id<br/>
	 * provinceName 省／直辖市／自治区级名称<br/>
	 * cityId 市级id <br/>
	 * cityName 市级名称<br/>
	 * countyId 县／区级id<br/>
	 * countyName 县／区级名称<br/>
	 * townId 街道／镇级id<br/>
	 * townName 街道／镇级名称<br/>
	 * villageId 村委会id<br/>
	 * villageName 村委会名称<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * id: 主键编号<br/>
	 * provinceId: 省／直辖市／自治区级id<br/>
	 * provinceName: 省／直辖市／自治区级名称<br/>
	 * cityId: 市级id <br/>
	 * cityName: 市级名称<br/>
	 * countyId: 县／区级id<br/>
	 * countyName: 县／区级名称<br/>
	 * townId: 街道／镇级id<br/>
	 * townName: 街道／镇级名称<br/>
	 * villageId: 村委会id<br/>
	 * villageName: 村委会名称<br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value = "获取省市县镇村数据")
	@ApiImplicitParam(name = "id", value = "主键编号", required = true,paramType="query")
	@GetMapping("/get")
	@RequiresPermissions("city:view")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore CityEntity city,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model){
		if(StringUtils.isEmpty(city.getId())) {
			return ResultData.build().error(getResString("err.error", this.getResString("id")));
		}
		CityEntity _city = (CityEntity)cityBiz.getEntity(Integer.parseInt(city.getId()));
		return ResultData.build().success(_city);
	}

	/**
	 * 保存省市县镇村数据实体
	 * @param city 省市县镇村数据实体
	 * <i>city参数包含字段信息参考：</i><br/>
	 * id 主键编号<br/>
	 * provinceId 省／直辖市／自治区级id<br/>
	 * provinceName 省／直辖市／自治区级名称<br/>
	 * cityId 市级id <br/>
	 * cityName 市级名称<br/>
	 * countyId 县／区级id<br/>
	 * countyName 县／区级名称<br/>
	 * townId 街道／镇级id<br/>
	 * townName 街道／镇级名称<br/>
	 * villageId 村委会id<br/>
	 * villageName 村委会名称<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * id: 主键编号<br/>
	 * provinceId: 省／直辖市／自治区级id<br/>
	 * provinceName: 省／直辖市／自治区级名称<br/>
	 * cityId: 市级id <br/>
	 * cityName: 市级名称<br/>
	 * countyId: 县／区级id<br/>
	 * countyName: 县／区级名称<br/>
	 * townId: 街道／镇级id<br/>
	 * townName: 街道／镇级名称<br/>
	 * villageId: 村委会id<br/>
	 * villageName: 村委会名称<br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value = "保存省市县镇村数据实体")
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
	@RequiresPermissions("city:save")
	@PostMapping("/save")
	@ResponseBody
	public ResultData save(@ModelAttribute @ApiIgnore CityEntity city, HttpServletResponse response, HttpServletRequest request) {
		if (StringUtils.isBlank(city.getProvinceName()) && StringUtils.isBlank(city.getCityName()) && StringUtils.isBlank(city.getCountyName()) && StringUtils.isBlank(city.getTownName()) && StringUtils.isBlank(city.getVillageName())){
			return ResultData.build().error(getResString("err.empty",this.getResString("area")));
		}
		// 保存
		Snowflake snowflake = IdUtil.getSnowflake(0, 0);

		CityEntity _city = new CityEntity();
		BeanUtils.copyProperties(city,_city);
		_city.setId(null);
		_city.setCreateDate(null);
		_city.setUpdateDate(null);
		_city.setCreateBy(null);
		LambdaQueryWrapper<CityEntity> wrapper = new LambdaQueryWrapper<>(_city);
		List<CityEntity> list = cityBiz.list(wrapper);
		if (list != null && list.size()>0){
			return ResultData.build().error(getResString("err.exist",this.getResString("area")));
		}
		CityEntity queryEntity = new CityEntity();
		queryEntity.setProvinceId(city.getProvinceId());
		queryEntity.setCityId(city.getCityId());
		queryEntity.setCountyId(city.getCountyId());
		queryEntity.setTownId(city.getTownId());
		queryEntity.setVillageId(city.getVillageId());
		wrapper = new LambdaQueryWrapper<>(queryEntity);
		List<CityEntity> citys = cityBiz.list(wrapper);
		CityEntity cityEntity = null;
		if(CollUtil.isNotEmpty(citys)) {
			cityEntity = cityBiz.list(wrapper).get(0);
		}
		if (city.getProvinceId() != null){
			//新增市
			city.setProvinceName(cityEntity.getProvinceName());
			city.setCityId(snowflake.nextId());
		}
		else if (city.getCityId() != null){
			//新增县
			city.setProvinceId(cityEntity.getProvinceId());
			city.setProvinceName(cityEntity.getProvinceName());
			city.setCityName(cityEntity.getCityName());
			city.setCountyId(snowflake.nextId());
		}else if (city.getCountyId() != null){
			//新增镇
			city.setProvinceId(cityEntity.getProvinceId());
			city.setProvinceName(cityEntity.getProvinceName());
			city.setCityId(cityEntity.getCityId());
			city.setCityName(cityEntity.getCityName());
			city.setCountyId(cityEntity.getCountyId());
			city.setCountyName(cityEntity.getCountyName());
			city.setTownId(snowflake.nextId());
		}else if (city.getTownId() != null){
			//新增村
			city.setProvinceId(cityEntity.getProvinceId());
			city.setProvinceName(cityEntity.getProvinceName());
			city.setCityId(cityEntity.getCityId());
			city.setCityName(cityEntity.getCityName());
			city.setCountyId(cityEntity.getCountyId());
			city.setCountyName(cityEntity.getCountyName());
			city.setTownId(cityEntity.getTownId());
			city.setTownName(cityEntity.getTownName());
			city.setVillageId(snowflake.nextId());
		}else if (city.getProvinceId() == null){
			//省
			city.setProvinceId(snowflake.nextId());
		}
		cityBiz.save(city);
		cityBiz.updateCache();
			return ResultData.build().success(city);
	}

	/**
	 // * @param city 省市县镇村数据实体
	 * <i>city参数包含字段信息参考：</i><br/>
	 * id:多个id直接用逗号隔开,例如id=1,2,3,4
	 * 批量删除省市县镇村数据
	 *            <dt><span class="strong">返回</span></dt><br/>
	 *            <dd>{code:"错误编码",<br/>
	 *            result:"true｜false",<br/>
	 *            resultMsg:"错误信息"<br/>
	 *            }</dd>
	 */
	@ApiOperation(value = "批量删除省市县镇村数据")
	@RequiresPermissions("city:del")
	@PostMapping("/delete")
	@ResponseBody
	public ResultData delete(@RequestBody List<CityEntity> citys,HttpServletResponse response, HttpServletRequest request) {
		for (CityEntity city : citys) {
			cityBiz.deleteEntity(city);
		}
		cityBiz.updateCache();
		return ResultData.build().success();
	}

	/**
	 * 更新省市县镇村数据信息省市县镇村数据
	 * @param city 省市县镇村数据实体
	 * <i>city参数包含字段信息参考：</i><br/>
	 * id 主键编号<br/>
	 * provinceId 省／直辖市／自治区级id<br/>
	 * provinceName 省／直辖市／自治区级名称<br/>
	 * cityId 市级id <br/>
	 * cityName 市级名称<br/>
	 * countyId 县／区级id<br/>
	 * countyName 县／区级名称<br/>
	 * townId 街道／镇级id<br/>
	 * townName 街道／镇级名称<br/>
	 * villageId 村委会id<br/>
	 * villageName 村委会名称<br/>
	 * <dt><span class="strong">返回</span></dt><br/>
	 * <dd>{ <br/>
	 * id: 主键编号<br/>
	 * provinceId: 省／直辖市／自治区级id<br/>
	 * provinceName: 省／直辖市／自治区级名称<br/>
	 * cityId: 市级id <br/>
	 * cityName: 市级名称<br/>
	 * countyId: 县／区级id<br/>
	 * countyName: 县／区级名称<br/>
	 * townId: 街道／镇级id<br/>
	 * townName: 街道／镇级名称<br/>
	 * villageId: 村委会id<br/>
	 * villageName: 村委会名称<br/>
	 * }</dd><br/>
	 */
	@ApiOperation(value ="更新省市县镇村数据信息省市县镇村数据")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "主键编号", required = true,paramType="query"),
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
	@RequiresPermissions("city:update")
	@PostMapping("/update")
	@ResponseBody
	public ResultData update(@ModelAttribute @ApiIgnore CityEntity city, HttpServletResponse response,
			HttpServletRequest request) {
		if (StringUtils.isBlank(city.getProvinceName()) && StringUtils.isBlank(city.getCityName()) && StringUtils.isBlank(city.getCountyName()) && StringUtils.isBlank(city.getTownName()) && StringUtils.isBlank(city.getVillageName())){
			return ResultData.build().error(getResString("err.empty",this.getResString("area")));
		}
		CityEntity _city = new CityEntity();
		_city.setProvinceName(city.getProvinceName());
		_city.setCityName(city.getCityName());
		_city.setCountyName(city.getCountyName());
		_city.setTownName(city.getTownName());
		_city.setVillageName(city.getVillageName());
		LambdaQueryWrapper<CityEntity> wrapper = new LambdaQueryWrapper<>(_city);
		List<CityEntity> list = cityBiz.list(wrapper);
		if (list != null && list.size()>1){
			return ResultData.build().error(getResString("err.exist",this.getResString("area")));
		}
		cityBiz.updateEntity(city);
		cityBiz.updateCache();
		return ResultData.build().success(city);
	}

	/**
	 * 查询省列表
	 * @param response
	 * @param request
	 */
	@ApiOperation(value = "查询省列表")
	@GetMapping("/province")
	@ResponseBody
	public ResultData province(HttpServletResponse response, HttpServletRequest request) {
		List cityList = cityBiz.queryProvince();
		return ResultData.build().success(cityList);
	}

	/**
	 * 根据省id查询城市列表
	 * @param city
	 * @param response
	 * @param request
	 */
	@ApiOperation(value = "根据省id查询城市列表")
	@ApiImplicitParam(name = "provinceId", value = "省／直辖市／自治区级id", required = true,paramType="query")
	@GetMapping("/city")
	@ResponseBody
	public ResultData city(@ModelAttribute @ApiIgnore CityEntity city,HttpServletResponse response, HttpServletRequest request) {
		List cityList = cityBiz.queryCity(city);
		return ResultData.build().success(cityList);
	}

	/**
	 * 根据城市id查询区域列表
	 * @param city
	 * @param response
	 * @param request
	 */
	@ApiOperation(value = "根据城市id查询区域列表")
	@ApiImplicitParam(name = "cityId", value = "市级id", required = true,paramType="query")
	@GetMapping("/county")
	@ResponseBody
	public ResultData county(@ModelAttribute @ApiIgnore CityEntity city,HttpServletResponse response, HttpServletRequest request) {
		List cityList = cityBiz.queryCounty(city);
		return ResultData.build().success(cityList);
	}

	/**
	 * 根据城市id查询区域列表
	 * @param city
	 * @param response
	 * @param request
	 */
	@ApiOperation(value = "根据区县id查询城镇列表")
	@ApiImplicitParam(name = "countyId", value = "区县Id", required = true,paramType="query")
	@GetMapping("/town")
	@ResponseBody
	public ResultData town(@ModelAttribute @ApiIgnore CityEntity city,HttpServletResponse response, HttpServletRequest request) {
		BasicUtil.startPage();
		List cityList = cityBiz.queryCounty(city);
		return ResultData.build().success(cityList);
	}

	/**
	 * 根据城市id查询区域列表
	 * @param city
	 * @param response
	 * @param request
	 */
	@ApiOperation(value = "根据城镇id查询街道列表")
	@ApiImplicitParam(name = "townId", value = "城镇Id", required = true,paramType="query")
	@GetMapping("/village")
	@ResponseBody
	public ResultData village(@ModelAttribute @ApiIgnore CityEntity city,HttpServletResponse response, HttpServletRequest request) {
		List cityList = cityBiz.queryCounty(city);
		return ResultData.build().success(cityList);
	}

	/**
	 * 生成json
	 * @param response
	 * @param request
	 */
	@ApiOperation(value = "生成json文件")
	@PostMapping("/createJson")
	@ResponseBody
	@RequiresPermissions("city:createJson")
	public ResultData creatJson(HttpServletResponse response, HttpServletRequest request) {
		int level = 5;//默认5级
		String type = "tree"; //默认为树形结构
		List<CityBean> cityList = (List<CityBean>) cityBiz.queryForTree(level,type);
		File file = FileUtil.file(BasicUtil.getRealPath(null), "/static/json/city.json");
		FileUtil.writeBytes(JSONUtil.toJsonStr(ResultData.build().success(cityList)).getBytes(StandardCharsets.UTF_8),file);
		return ResultData.build().success();
	}


}
