/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.action.people;

import cn.hutool.core.lang.Validator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.people.action.BaseAction;
import net.mingsoft.people.biz.IPeopleAddressBiz;
import net.mingsoft.people.entity.PeopleAddressEntity;
import net.mingsoft.people.entity.PeopleEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * 普通用户收货地址信息控制层(外部请求接口)
 * @author 铭飞开发团队
 * @version
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
@Api(tags={"前端-用户-会员模块接口"})
@Controller("peopleAddress")
@RequestMapping("/people/address")
public class PeopleAddressAction extends BaseAction {
	/**
	 * 注入用户收货地址业务层
	 */
	@Autowired
	private IPeopleAddressBiz peopleAddressBiz;

    @ApiOperation(value = "用户收货地址列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "peopleAddressConsigneeName", value = "用户收货人姓名", required =true,paramType="query"),
			@ApiImplicitParam(name = "peopleAddressProvince", value = "收货人所在的省", required = true,paramType="query"),
			@ApiImplicitParam(name = "peopleAddressProvinceId", value = "收货人所在的省编号", required = true,paramType="query"),
			@ApiImplicitParam(name = "peopleAddressCity", value = "收货人所在的市", required = true,paramType="query"),
			@ApiImplicitParam(name = "peopleAddressCityId", value = "收货人所在的市编号", required = true,paramType="query"),
			@ApiImplicitParam(name = "peopleAddressDistrict", value = "收货人所在区", required = true,paramType="query"),
			@ApiImplicitParam(name = "peopleAddressDistrictId", value = "收货人所在区编号", required = true,paramType="query"),
			@ApiImplicitParam(name = "peopleAddressStreet", value = "街道", required = true,paramType="query"),
			@ApiImplicitParam(name = "peopleAddressStreetId", value = "街道编号", required = true,paramType="query"),
			@ApiImplicitParam(name = "peopleAddressAddress", value = "收货人的详细收货地址", required = true,paramType="query"),
			@ApiImplicitParam(name = "peopleAddressMail", value = "收货人邮箱", required = true,paramType="query"),
			@ApiImplicitParam(name = "peopleAddressPhone", value = "收货人手机", required = true,paramType="query"),
			@ApiImplicitParam(name = "peopleAddressDefault", value = "是否是收货人最终收货地址。0代表是，1代表不是，默认为0", required = false,paramType="query")
	})
	@ResponseBody
	@GetMapping("/list")
	public ResultData list(@ModelAttribute @ApiIgnore PeopleAddressEntity peopleAddress, HttpServletRequest request,
						   HttpServletResponse response) {
		// 通过session得到用户实体
		PeopleEntity people = this.getPeopleBySession();
		// 通过用户id和站点id查询用户收货地址列表
		peopleAddress.setPeopleId(Integer.parseInt(people.getId()));
		List list = peopleAddressBiz.query(peopleAddress);
		return ResultData.build().success(list);
	}

	@ApiOperation(value = "保存用户收货地址接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "peopleAddressConsigneeName", value = "用户收货人姓名", required =true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressProvince", value = "收货人所在的省", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressProvinceId", value = "收货人所在的省编号", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressCity", value = "收货人所在的市", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressCityId", value = "收货人所在的市编号", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressDistrict", value = "收货人所在区", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressDistrictId", value = "收货人所在区编号", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressStreet", value = "街道", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressStreetId", value = "街道编号", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressAddress", value = "收货人的详细收货地址", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressMail", value = "收货人邮箱", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressPhone", value = "收货人手机", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressDefault", value = "是否是收货人最终收货地址。0代表是，1代表不是，默认为0", required = false,paramType="query")
    })
	@ResponseBody
	@PostMapping(value="/save")
	public ResultData save(@ModelAttribute @ApiIgnore PeopleAddressEntity peopleAddress, HttpServletRequest request,
			HttpServletResponse response) {

		// 通过session得到用户实体
		PeopleEntity peopleEntity = this.getPeopleBySession();
		// 判断用户信息是否为空
		if (peopleAddress == null) {
			return ResultData.build().error(getResString("err.empty" ,this.getResString("people.no.exist")));
		}
		// 验证手机号
		if (StringUtils.isBlank(peopleAddress.getPeopleAddressPhone())) {
			return ResultData.build().error(getResString("err.empty" ,this.getResString("people.phone")));
		}
		// 验证邮箱
		if (!StringUtils.isBlank(peopleAddress.getPeopleAddressMail())) {
			if (!Validator.isEmail(peopleAddress.getPeopleAddressMail())) {
				return ResultData.build().error(getResString("err.error" ,this.getResString("people.mail")));
			}
		}
		// 判断省是否为空
		if (StringUtils.isBlank(peopleAddress.getPeopleAddressProvince())) {
			return ResultData.build().error( this.getResString("people.address"));
		}
		// 注入用户id
		peopleAddress.setPeopleId(Integer.parseInt(peopleEntity.getId()));
		// 注入站点id
		// 进行保存
		peopleAddressBiz.saveEntity(peopleAddress);
		return ResultData.build().success(peopleAddress);
	}

	@ApiOperation(value = "更新用户收货地址接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "peopleAddressId", value = "用户收货地址编号", required =true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressConsigneeName", value = "用户收货人姓名", required =true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressProvince", value = "收货人所在的省", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressProvinceId", value = "收货人所在的省编号", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressCity", value = "收货人所在的市", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressCityId", value = "收货人所在的市编号", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressDistrict", value = "收货人所在区", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressDistrictId", value = "收货人所在区编号", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressStreet", value = "街道", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressStreetId", value = "街道编号", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressAddress", value = "收货人的详细收货地址", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressMail", value = "收货人邮箱", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressPhone", value = "收货人手机", required = true,paramType="query"),
    	@ApiImplicitParam(name = "peopleAddressDefault", value = "是否是收货人最终收货地址。0代表是，1代表不是，默认为0", required = false,paramType="query")
    })
	@ResponseBody
	@PostMapping(value="/update")
	public ResultData update(@ModelAttribute @ApiIgnore PeopleAddressEntity peopleAddress, HttpServletRequest request,
			HttpServletResponse response) {
		// 通过session得到用户实体
		PeopleEntity people = this.getPeopleBySession();
		peopleAddress.setPeopleId(Integer.parseInt(people.getId()));
		PeopleAddressEntity address = (PeopleAddressEntity) peopleAddressBiz.getEntity(peopleAddress);
		if (Integer.parseInt(people.getId()) != address.getPeopleId()) {
			return ResultData.build().error();
		}
		// 判断用户信息是否为空
		if (StringUtils.isBlank(peopleAddress.getPeopleAddressProvince())
				|| StringUtils.isBlank(peopleAddress.getPeopleAddressAddress())) {
			return ResultData.build().error(this.getResString("people.address"));
		}
		// 验证手机号
		if (StringUtils.isBlank(peopleAddress.getPeopleAddressPhone())) {
			return ResultData.build().error(getResString("err.empty", this.getResString("people.phone")));
		}
		peopleAddress.setPeopleId(Integer.parseInt(people.getId()));
		// 更新用户地址
		peopleAddressBiz.updateEntity(peopleAddress);
		return ResultData.build().success();
	}

	@ApiOperation(value = "设置默认地址接口")
	@ApiImplicitParam(name = "peopleAddressId", value = "用户收货地址编号", required = true,paramType="query")
	@ResponseBody
	@PostMapping("/setDefault")
	public ResultData setDefault(@ModelAttribute @ApiIgnore PeopleAddressEntity peopleAddress, HttpServletRequest request,
			HttpServletResponse response) {
		// 通过session得到用户实体
		PeopleEntity people = this.getPeopleBySession();
		// 将获取用户 PeopleAddressDefault 值还原为1，更新设为用户默认地址
		peopleAddress.setPeopleId(Integer.parseInt(people.getId()));
		// 更新用户地址
		peopleAddressBiz.setDefault(peopleAddress);
		return ResultData.build().success();
	}

	@ApiOperation(value = "根据收货地址编号删除收货地址信息")
	@ApiImplicitParam(name = "peopleAddressId", value = "用户收货地址编号", required = true,paramType="query")
	@ResponseBody
	@PostMapping("/delete")
	public ResultData delete(@ModelAttribute @ApiIgnore PeopleAddressEntity peopleAddress, HttpServletRequest request,
			HttpServletResponse response) {
		// 根据收货地址id删除收货信息
		peopleAddress.setPeopleId(Integer.parseInt(this.getPeopleBySession().getId()));
		peopleAddressBiz.deleteEntity(peopleAddress);
		return ResultData.build().success();
	}

	@ApiOperation(value = "获取收货地址详情接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "peopleAddressId", value = "用户收货地址编号", required = true,paramType="query"),
	})
	@ResponseBody
	@GetMapping("/get")
	public ResultData get(@ModelAttribute @ApiIgnore PeopleAddressEntity peopleAddress, HttpServletRequest request,
			HttpServletResponse response) {
		int peopleId = Integer.parseInt(this.getPeopleBySession().getId());
		// 通过用户地址id查询用户地址实体
		peopleAddress.setPeopleId(peopleId);
		PeopleAddressEntity address = (PeopleAddressEntity) peopleAddressBiz.getEntity(peopleAddress);
		if (peopleId != address.getPeopleId()) {
			return ResultData.build().error();
		}
		return ResultData.build().success(address);
	}
}
