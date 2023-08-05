/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.action.people;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.people.action.BaseAction;
import net.mingsoft.people.annotation.PeopleLogAnn;
import net.mingsoft.people.biz.IPeopleBiz;
import net.mingsoft.people.biz.IPeopleUserBiz;
import net.mingsoft.people.constant.e.PeopleEnum;
import net.mingsoft.people.constant.e.PeopleLogTypeEnum;
import net.mingsoft.people.entity.PeopleEntity;
import net.mingsoft.people.entity.PeopleUserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * 铭飞会员系统 详细的用户信息
 * @author 铭飞开发团队
 * @version
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
@Api(tags={"前端-用户-会员模块接口"})
@Controller("webPeopleUser")
@RequestMapping("/people/user")
public class PeopleUserAction extends BaseAction {

	/**
	 * 注入用户详细信息业务层
	 */
	@Autowired
	private IPeopleUserBiz peopleUserBiz;

	/**
	 * 注入用户业务层
	 */
	@Autowired
	private IPeopleBiz peopleBiz;

	@ApiOperation(value = "查询人员表详情接口")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "people", value = "当前用户", required =true,paramType="query"),
	})
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore PeopleUserEntity people, HttpServletResponse response, HttpServletRequest request, ModelMap model, BindingResult result) {
		PeopleUserEntity _people = peopleUserBiz.getByEntity(people);
		return ResultData.build().success( _people);
	}

	@ApiOperation(value = "读取当前登录用户的基本信息接口")
	@GetMapping("/info")
	@ResponseBody
	public ResultData info(HttpServletRequest request, HttpServletResponse response) {
		// 获取用户session
		PeopleEntity people = this.getPeopleBySession();
		PeopleUserEntity peopleUser = (PeopleUserEntity) this.peopleUserBiz.getEntity(Integer.parseInt(people.getId()));
		if (peopleUser == null) {
			// 没用用户详细信息
			return ResultData.build().error();
		}
		// 返回用户详细信息
		return ResultData.build().success(peopleUser);
	}

	@ApiOperation(value = "更新用户信息接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "puRealName", value = "用户真实名称", required =false,paramType="query"),
		@ApiImplicitParam(name = "puAddress", value = "用户地址", required =false,paramType="query"),
		@ApiImplicitParam(name = "puIcon", value = "用户头像图标地址", required =false,paramType="query"),
		@ApiImplicitParam(name = "puNickname", value = "用户昵称", required =false,paramType="query"),
		@ApiImplicitParam(name = "puSex", value = "用户性别(0.未知、1.男、2.女)", required =false,paramType="query"),
		@ApiImplicitParam(name = "puBirthday", value = "用户出生年月日", required =false,paramType="query"),
		@ApiImplicitParam(name = "puCard", value = "身份证", required =false,paramType="query"),
		@ApiImplicitParam(name = "puProvince", value = "省", required =false,paramType="query"),
		@ApiImplicitParam(name = "puCity", value = "城市", required =false,paramType="query"),
		@ApiImplicitParam(name = "puDistrict", value = "区", required =false,paramType="query"),
		@ApiImplicitParam(name = "puStreet", value = "街道", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	public ResultData update(@ModelAttribute @ApiIgnore PeopleUserEntity peopleUser, HttpServletRequest request,
			HttpServletResponse response) {
		if (peopleUser == null) {
			// 未填写信息返回错误信息
			return ResultData.build().error(
					this.getResString("err.empty", this.getResString("people")));
		}
		String peopleId = this.getPeopleBySession().getId();
		peopleUser.setId(peopleId);
		peopleUser.setPeopleId(Integer.parseInt(peopleId));
		//以免用户修改登录账号信息，邮箱、手机
		peopleUser.setPeoplePhone(null);
		peopleUser.setPeoplePhoneCheck(-1);
		peopleUser.setPeopleName(null);
		peopleUser.setPeopleMail(null);
		peopleUser.setPeopleMailCheck(-1);
		PeopleUserEntity pue = (PeopleUserEntity) this.peopleUserBiz.getEntity(Integer.parseInt(peopleUser.getId()));
		if (pue.getPeopleId() == 0) {
			this.peopleUserBiz.saveEntity(peopleUser);
		} else {
			peopleUserBiz.updatePeople(peopleUser);
		}
		// 返回更新成功
		return ResultData.build().success( this.getResString("success"));
	}
	@ApiOperation(value = "更新邮箱")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "peopleMail", value = "邮箱", required =false,paramType="query"),
	})
	@PostMapping("/updateMail")
	@ResponseBody
	@PeopleLogAnn(title = "更新邮箱", businessType = PeopleLogTypeEnum.UPDATE)
	public ResultData updateMail(String peopleMail, HttpServletRequest request,
			HttpServletResponse response) {
		if (StrUtil.isBlank(peopleMail)) {
			// 未填写信息返回错误信息
			return ResultData.build().error(
					this.getResString("err.empty", this.getResString("people.mail")));
		}
		if(StringUtils.isNotEmpty(peopleMail)){
			PeopleEntity peopleByMail = peopleBiz.getEntityByMailOrPhone(peopleMail);
			if(ObjectUtil.isNotNull(peopleByMail)){
				return ResultData.build().error( this.getResString("err.exist", this.getResString("people.mail")));
			}
		}
		String peopleId = this.getPeopleBySession().getId();
		PeopleUserEntity peopleUser =new PeopleUserEntity();
		peopleUser.setId(peopleId);
		peopleUser.setPeopleId(Integer.parseInt(peopleId));
		//以免用户修改登录账号信息，邮箱、手机
		peopleUser.setPeopleMail(peopleMail);
		peopleUser.setPeopleMailCheck(PeopleEnum.MAIL_CHECK);
		peopleBiz.updatePeople(peopleUser);
		// 返回更新成功
		return ResultData.build().success( this.getResString("success"));
	}

	/**
	 * 推荐使用update
	 */
	@Deprecated
	@ApiOperation(value = "保存用户头像(包含头像)接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "puIcon", value = "用户头像", required =true,paramType="query"),
		@ApiImplicitParam(name = "peopleMail", value = "用户手机", required =false,paramType="query"),
		@ApiImplicitParam(name = "peoplePhone", value = "用户邮箱", required =false,paramType="query")
	})
	@PostMapping("/saveUserIcon")
	@ResponseBody
	public ResultData saveUserIcon(@ModelAttribute @ApiIgnore PeopleUserEntity peopleUser, HttpServletRequest request,
			HttpServletResponse response) {
		if (peopleUser == null) {
			// 未填写信息返回错误信息
			return ResultData.build().error(this.getResString("people.user.msg.null.error"));
		}
		// 获取用户session
		PeopleEntity people = this.getPeopleBySession();
		String imgPath = peopleUser.getPuIcon().trim(); // 新图片路径
		if (!StringUtils.isBlank(imgPath)) {
			peopleUser.setPuIcon(imgPath);
		}

		peopleUser.setId(people.getId());
		this.peopleUserBiz.saveEntity(peopleUser);
		// 更新手机和电子邮件
		if (!StringUtils.isBlank(peopleUser.getPeopleMail())) {
			people.setPeopleMail(peopleUser.getPeopleMail());
		}
		if (!StringUtils.isBlank(peopleUser.getPeoplePhone())) {
			people.setPeoplePhone(peopleUser.getPeoplePhone());
		}
		this.peopleBiz.updateEntity(people);
		// 返回用户添加成功
		return ResultData.build().success(
				this.getResString("people.user.save.msg.success"));
	}

	/**
	 * 推荐使用update
	 */
	@Deprecated
	@ApiOperation(value = "更新用户信息(包含头像)接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "puIcon", value = "用户头像", required =false,paramType="query")
	})
	@PostMapping("/updateUserIcon")
	@ResponseBody
	public ResultData updateUserIcon(@ModelAttribute @ApiIgnore PeopleUserEntity peopleUser, HttpServletRequest request,
			HttpServletResponse response) {
		if (peopleUser == null) {
			// 未填写信息返回错误信息
			return ResultData.build().error(this.getResString("people.user.msg.null.error"));
		}
		// 获取用户session
		PeopleEntity people = this.getPeopleBySession();
		PeopleUserEntity oldPeopleUser = (PeopleUserEntity) peopleUserBiz.getEntity(Integer.parseInt(people.getId()));
		String imgPath = peopleUser.getPuIcon().trim();
		if (!StringUtils.isBlank(imgPath)) {
			oldPeopleUser.setPuIcon(imgPath);
		}

		this.peopleUserBiz.updatePeople(oldPeopleUser);
		// 返回更新成功
		return ResultData.build().success(this.getResString("people.user.update.msg.success"));
	}


}
