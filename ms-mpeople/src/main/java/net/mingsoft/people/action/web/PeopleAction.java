/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.action.web;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.RestTemplateUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.people.action.BaseAction;
import net.mingsoft.people.annotation.PeopleLogAnn;
import net.mingsoft.people.bean.PeopleLoginBean;
import net.mingsoft.people.biz.IPeopleBiz;
import net.mingsoft.people.biz.IPeopleUserBiz;
import net.mingsoft.people.constant.e.CookieConstEnum;
import net.mingsoft.people.constant.e.PeopleEnum;
import net.mingsoft.people.constant.e.PeopleLogTypeEnum;
import net.mingsoft.people.constant.e.SessionConstEnum;
import net.mingsoft.people.entity.PeopleEntity;
import net.mingsoft.people.entity.PeopleUserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 铭飞会员模块,前端调用（不需要用户登录进行的操作）
 *
 * @author 铭飞开发团队
 * @version 版本号：0.0<br/>
 *          创建日期：2017-8-23 10:10:22<br/>
 *          历史修订：<br/>
 */
@Api(tags={"前端-会员模块接口"})
@Controller("webPeople")
@RequestMapping("/")
public class PeopleAction extends BaseAction {

	/**
	 * 注入用户基础业务层
	 */
	@Autowired
	private IPeopleBiz peopleBiz;

	/**
	 * 注入用户基础业务层
	 */
	@Autowired
	private IPeopleUserBiz peopleUserBiz;

	@ApiOperation(value = "验证码验证,例如流程需要短信验证或邮箱验证，为有效防止恶意发送验证码。提供给ajax异步请求使用,注意：页面提交对验证码表单属性名称必须是rand_code，否则无效")
	@ApiImplicitParam(name = "rand_code", value = "验证码", required =true,paramType="query")
	@PostMapping(value = "/checkCode")
	@ResponseBody
	public ResultData checkCode(HttpServletRequest request, HttpServletResponse response) {
		// 验证码验证 验证码不为null 或 验证码不相等
		if (!checkRandCode()) {
			return ResultData.build().error(
					this.getResString("err.error", this.getResString("rand.code")));
		}
		return ResultData.build().success();
	}

	@ApiOperation(value = "验证用户名、手机号、邮箱是否可用，同一时间只能判断一种，优先用户名称 ,只验证已绑定的用户,建议独立使用")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "peopleMail", value = "用户邮箱", required =false,paramType="query"),
		@ApiImplicitParam(name = "peopleName", value = "登录帐号", required =false,paramType="query"),
		@ApiImplicitParam(name = "peoplePhone", value = "用户电话", required =false,paramType="query")
	})
	@PostMapping(value = "/check")
	@ResponseBody
	public ResultData check(@ModelAttribute @ApiIgnore PeopleEntity people, HttpServletRequest request, HttpServletResponse response) {
		PeopleEntity _people = peopleBiz.getByPeople(people);
		if (_people != null) {
			return ResultData.build().success();
		} else {
			return ResultData.build().error();
		}
	}

	@ApiOperation(value = "登录验证,登录必须存在验证码")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "peoplePassword", value = "用户密码", required =true,paramType="query"),
		@ApiImplicitParam(name = "peopleName", value = "登录帐号", required =true,paramType="query"),
		@ApiImplicitParam(name = "rand_code", value = "验证码", required =true,paramType="query"),
		@ApiImplicitParam(name = "peopleAutoLogin", value = " 大于0表示自动登录", required =false,paramType="query"),
	})
	@PostMapping(value = "/checkLogin")
	@ResponseBody
	@PeopleLogAnn(title = "登录", businessType = PeopleLogTypeEnum.LOGIN)
	public ResultData checkLogin(@ModelAttribute @ApiIgnore PeopleEntity people, HttpServletRequest request,
			HttpServletResponse response) {

		// 是否开启了登录功能
		String switchLogin = ConfigUtil.getString("会员配置", "enableLogin");
		if ("false".equalsIgnoreCase(switchLogin)){
			return ResultData.build().error("登录功能已被关闭！");
		}

		// 验证码验证 验证码不为null 或 验证码不相等
		if (!this.checkRandCode()) {
			return ResultData.build().error( this.getResString("err.error", this.getResString("rand.code")));
		}
		// 用户名和密码不能为空
		if (StringUtils.isBlank(people.getPeopleName()) || StringUtils.isBlank(people.getPeoplePassword())) {
			return ResultData.build().error(
					this.getResString("err.error", this.getResString("people")));
		}

		PeopleEntity peopleEntity = this.peopleBiz.getEntityByUserName(people.getPeopleName());
		if (peopleEntity == null) {
			// 用户名或密码错误
			return ResultData.build().error(
					this.getResString("err.error", this.getResString("people.no.exist")));
		}

		// 将用户输入的密码用MD5加密再和数据库中的进行比对
		String peoplePassWord = SecureUtil.md5(people.getPeoplePassword());
		// 用户密码是否正确
		if (peoplePassWord.equals(peopleEntity.getPeoplePassword())) {
			if(PeopleEnum.STATE_CHECK.toInt()!=peopleEntity.getPeopleState().intValue()){
				// 用户审核状态是否通过
				return ResultData.build().error( this.getResString("people.state.error"));
			}
			// 登录成功,压入用户session
			this.setPeopleBySession(request, peopleEntity);
			PeopleLoginBean tempPeople = new PeopleLoginBean();
			tempPeople.setId(peopleEntity.getId());
			tempPeople.setPeopleName(peopleEntity.getPeopleName());
			tempPeople.setPeopleMail(peopleEntity.getPeopleMail());
			tempPeople.setPeopleState(peopleEntity.getPeopleState()); // 状态
			tempPeople.setCookie(request.getHeader("cookie"));
			// 判断用户是否点击了自动登录
			if (people.getPeopleAutoLogin() > 0) {
				tempPeople.setPeoplePassword(people.getPeoplePassword());
				BasicUtil.setCookie( response, CookieConstEnum.PEOPLE_COOKIE, JSONUtil.toJsonStr(tempPeople),
						60 * 60 * 24 * people.getPeopleAutoLogin());
			}
			peopleEntity.setPeopleIp(BasicUtil.getIp());
			//保存用户ip
			peopleBiz.updateEntity(peopleEntity);
			return ResultData.build().success(tempPeople);

		} else {
			// 用户名或密码错误
			return ResultData.build().error(
					this.getResString("err.error", this.getResString("people.no.exist")));
		}

	}



	@ApiOperation(value = "验证用户是否登录")
	@PostMapping(value = "/checkLoginStatus")
	@ResponseBody
	public ResultData checkLoginStatus(HttpServletRequest request, HttpServletResponse response) {
		PeopleEntity people = this.getPeopleBySession();
		if(people == null){
			return ResultData.build().error();
		}else {
			return ResultData.build().success();
		}
	}

	@ApiOperation(value = "验证重置密码过程中收到的验证码是否正确")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "peopleCode", value = "用户随机验证码", required =true,paramType="query"),
		@ApiImplicitParam(name = "rand_code", value = "验证码", required =true,paramType="query")
	})
	@PostMapping(value = "/checkResetPasswordCode")
	@ResponseBody
	@PeopleLogAnn(title = "修改密码", businessType = PeopleLogTypeEnum.UPDATE)
	public ResultData checkResetPasswordCode(@ModelAttribute @ApiIgnore PeopleEntity people, HttpServletRequest request,
			HttpServletResponse response) {
		// 验证码验证 验证码不为null 或 验证码不相等
		if (StringUtils.isBlank(this.getRandCode()) || !this.checkRandCode()) {
			return ResultData.build().error( this.getResString("err.error", this.getResString("rand.code")));
		}

		PeopleEntity _people = (PeopleEntity) BasicUtil.getSession( SessionConstEnum.PEOPLE_EXISTS_SESSION);
		if (_people == null) {
			// 用户不存在
			return ResultData.build().error(
					this.getResString("err.not.exist", this.getResString("people")));

		}

		LOG.debug(_people.getPeoplePhoneCheck() + ":" + PeopleEnum.PHONE_CHECK.toInt());
		LOG.debug(_people.getPeopleCode() + ":" + people.getPeopleCode());
		// 判断用户验证是否通过\判断用户输入对邮箱验证码是否与系统发送对一致\判断验证码对有效时间
		if (_people.getPeoplePhoneCheck() == PeopleEnum.PHONE_CHECK.toInt()
				&& _people.getPeopleCode().equals(people.getPeopleCode())) {
			BasicUtil.setSession( SessionConstEnum.PEOPLE_RESET_PASSWORD_SESSION, _people);
			return ResultData.build().success(
					this.getResString("success", this.getResString("people.get.password")));
		} else if (_people.getPeopleMailCheck() == PeopleEnum.MAIL_CHECK.toInt()
				&& _people.getPeopleCode().equals(people.getPeopleCode())) {
			BasicUtil.setSession( SessionConstEnum.PEOPLE_RESET_PASSWORD_SESSION, _people);
			return ResultData.build().success(
					this.getResString("success", this.getResString("people.get.password")));
		} else {
			return ResultData.build().error(
					this.getResString("fail", this.getResString("people.get.password")));
		}
	}

	@ApiOperation(value = "用户名、邮箱、手机号验证 ,用户重置密码必须使用该接口,适用场景:1、用户注册是对用户名、邮箱或手机号唯一性判断 2、用户取回密码是判断账号是否存在")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "peoplePhone", value = "用户电话", required =true,paramType="query"),
		@ApiImplicitParam(name = "peopleMail", value = "用户邮箱", required =true,paramType="query"),
		@ApiImplicitParam(name = "peopleName", value = "登录帐号", required =true,paramType="query"),
	})
	@PostMapping(value = "/isExists")
	@ResponseBody
	public ResultData isExists(@ModelAttribute @ApiIgnore PeopleEntity people, HttpServletRequest request,
			HttpServletResponse response) {
		LOG.debug(JSONUtil.toJsonStr(people));
		if (StringUtils.isBlank(people.getPeopleName()) && StringUtils.isBlank(people.getPeoplePhone())
				&& StringUtils.isBlank(people.getPeopleMail())) {
			return ResultData.build().error(
					this.getResString("err.empty", this.getResString("people.name")));
		}

		// 如果接收到mail值，就给mail_check赋值1
		if (!StringUtils.isBlank(people.getPeopleMail())) {
			people.setPeopleMailCheck(PeopleEnum.MAIL_CHECK);
		}
		// 如果接收到phone值，就给phone_check赋值1
		if (!StringUtils.isBlank(people.getPeoplePhone())) {
			people.setPeoplePhoneCheck(PeopleEnum.PHONE_CHECK);
		}
		PeopleEntity _people = (PeopleEntity) this.peopleBiz.getEntity(people);
		if (_people != null) {
			BasicUtil.setSession( SessionConstEnum.PEOPLE_EXISTS_SESSION, _people);
			return ResultData.build().success();
		}
		return ResultData.build().error();
	}

	@ApiOperation(value = "用户注册,用户可以根据用名称、手机号、邮箱进行注册")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "peoplePassword", value = "登录密码", required =true,paramType="query"),
		@ApiImplicitParam(name = "peopleCode", value = "用户随机验证码", required =true,paramType="query"),
		@ApiImplicitParam(name = "peoplePhone", value = "用户电话", required =false,paramType="query"),
		@ApiImplicitParam(name = "peopleMail", value = "用户邮箱", required =false,paramType="query"),
		@ApiImplicitParam(name = "peopleName", value = "登录帐号", required =false,paramType="query"),
	})
	@PostMapping(value = "/register")
	@ResponseBody
	public ResultData register(@ModelAttribute @ApiIgnore PeopleUserEntity people, HttpServletRequest request,
			HttpServletResponse response) {
		LOG.debug("people register");

		// 验证码验证 验证码不为null 或 验证码不相等
//		if (!checkRandCode()) {
//			return ResultData.build().error( this.getResString("err.error", this.getResString("rand.code")));
//		}

		// 是否开启了注册功能
		String switchRegister = ConfigUtil.getString("会员配置", "enableReg");
		if ("false".equalsIgnoreCase(switchRegister)){
			return ResultData.build().error("注册功能已被关闭！");
		}

		// 判断用户信息是否为空
		if (people == null) {
			return ResultData.build().error(
					this.getResString("err.empty", this.getResString("people")));
		}

		// 如果用户名不为空表示使用的是账号注册方式
		if (!StringUtils.isBlank(people.getPeopleName())) {
			// 验证用户名
			if (StringUtils.isBlank(people.getPeopleName())) {
				return ResultData.build().error(
						this.getResString("err.empty", this.getResString("people.name")));
			}

			if (people.getPeopleName().contains(" ")) {
				return ResultData.build().error(
						this.getResString("people.name") + this.getResString("people.space"));
			}

			if (!StringUtil.checkLength(people.getPeopleName(), 3, 30)) {
				return ResultData.build().error(
						this.getResString("err.length", this.getResString("people.name"), "3", "30"));
			}

			// 判断用户名是否已经被注册
			PeopleEntity peopleName = this.peopleBiz.getEntityByUserName(people.getPeopleName());
			if (peopleName != null) {
				return ResultData.build().error(
						this.getResString("err.exist", this.getResString("people.name") + peopleName.getPeopleName()));
			}
		}
		String sendCode = ConfigUtil.getString("会员配置", "sendCode");
		if (!StringUtils.isBlank(people.getPeoplePhone()) && "true".equalsIgnoreCase(sendCode)) {// 验证手机号
			PeopleEntity peoplePhone = this.peopleBiz.getEntityByUserName(people.getPeoplePhone());
			if (peoplePhone != null && peoplePhone.getPeoplePhoneCheck() == PeopleEnum.PHONE_CHECK.toInt()) { // 已存在
				return ResultData.build().error(
						this.getResString("err.exist", this.getResString("people.phone")));
			} else {
				Object obj = BasicUtil.getSession( SessionConstEnum.SEND_CODE_SESSION);
				if (obj != null) {
					PeopleEntity _people = (PeopleEntity) obj;
					if (_people.getPeoplePhone().equals(people.getPeoplePhone())) {
						if (_people.getPeopleCode().equals(people.getPeopleCode())) {
							people.setPeoplePhoneCheck(PeopleEnum.PHONE_CHECK);
						} else {
							return ResultData.build().error(
									this.getResString("err.error", this.getResString("people.phone.code")));
						}
					}
				}
			}
		}


		if (!StringUtils.isBlank(people.getPeopleMail()) && "true".equalsIgnoreCase(sendCode)) {// 验证邮箱
				// 检查邮箱格式是否含有空格
				if (people.getPeopleMail().contains(" ")) {
					return ResultData.build().error(
							this.getResString("people.mail") + this.getResString("people.space"));
				}
				PeopleEntity peopleMail = this.peopleBiz.getEntityByUserName(people.getPeopleMail());
				if (peopleMail != null && peopleMail.getPeopleMailCheck() == PeopleEnum.MAIL_CHECK.toInt()) {
					return ResultData.build().error(
							this.getResString("err.exist", this.getResString("people.mail")));
				} else {
					Object obj = BasicUtil.getSession(SessionConstEnum.SEND_CODE_SESSION);
					if (obj != null) {
						PeopleEntity _people = (PeopleEntity) obj;
						if (_people.getPeopleMail().equals(people.getPeopleMail())) {
							if (_people.getPeopleCode().equals(people.getPeopleCode())) {
								people.setPeopleMailCheck(PeopleEnum.MAIL_CHECK);
							} else {
								return ResultData.build().error(
										this.getResString("err.error", this.getResString("people.mail")));
							}
						}
					}else {
						return ResultData.build().error(
								this.getResString("err.error", this.getResString("people.mail.code")));
					}
				}
			}

		// 密码
		if (StringUtils.isBlank(people.getPeoplePassword())) {
			return ResultData.build().error(
					this.getResString("err.empty", this.getResString("people.password")));
		}

		if (people.getPeoplePassword().contains(" ")) {
			return ResultData.build().error(
					this.getResString("people.password") + this.getResString("people.space"));
		}

		if (people.getPeoplePassword().length() < 6 || people.getPeoplePassword().length() > 30) {
			return ResultData.build().error(
					this.getResString("err.length", this.getResString("people.password"), "6", "30"));
		}
		//如果用户未设置昵称，则昵称默认为用户名
		if(StringUtils.isBlank(people.getPuNickname())){
			people.setPuNickname(people.getPeopleName());
		}
		// 将密码使用MD5加密
		people.setPeoplePassword(SecureUtil.md5(people.getPeoplePassword()));
		// 是否开启审核
		String state = ConfigUtil.getString("会员配置", "peopleState");
		if ("false".equals(state)){
			people.setPeopleState(1); // 审核默认值
		} else {
			people.setPeopleState(0);
		}

		people.setPeopleDateTime(new Date());
		peopleUserBiz.savePeople(people);
		LOG.debug("people register ok");
		return ResultData.build().success();

	}

	@ApiOperation(value = "用户重置密码")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "peoplePassword", value = "登录密码", required =true,paramType="query"),
		@ApiImplicitParam(name = "rand_code", value = "验证码", required =true,paramType="query"),
		@ApiImplicitParam(name = "peopleCode", value = "用户随机验证码", required =true,paramType="query"),
	})
	@PostMapping(value = "/resetPassword")
	@ResponseBody
	@PeopleLogAnn(title = "重置密码", businessType = PeopleLogTypeEnum.UPDATE)
	public ResultData resetPassword(@ModelAttribute @ApiIgnore PeopleEntity people, HttpServletRequest request,
			HttpServletResponse response) {
		// 验证码验证 验证码不为null 或 验证码不相等
		if (StringUtils.isBlank(this.getRandCode()) || !this.checkRandCode()) {
			return ResultData.build().error( this.getResString("err.error", this.getResString("rand.code")));
		}

		// 验证新密码的长度
		if (!StringUtil.checkLength(people.getPeoplePassword(), 3, 12)) {
			return ResultData.build().error(
					this.getResString("err.error", this.getResString("people.password")));
		}

		PeopleEntity _people = (PeopleEntity) BasicUtil.getSession( SessionConstEnum.PEOPLE_RESET_PASSWORD_SESSION);

		if (_people == null) {
			// 用户不存在
			return ResultData.build().error(
					this.getResString("err.not.exist", this.getResString("people")));

		}

		// 判断用户验证是否通过\判断用户输入对邮箱验证码是否与系统发送对一致\判断验证码对有效时间
		if (_people.getPeoplePhoneCheck() == PeopleEnum.PHONE_CHECK.toInt()
				&& _people.getPeopleCode().equals(people.getPeopleCode())) {
			_people.setPeoplePassword(SecureUtil.md5(people.getPeoplePassword()));
			peopleBiz.updateEntity(_people);
			LOG.debug("更新密码成功");
			return ResultData.build().success(
					this.getResString("success", this.getResString("people.get.password")));
		} else if (_people.getPeopleMailCheck() == PeopleEnum.MAIL_CHECK.toInt()
				&& _people.getPeopleCode().equals(people.getPeopleCode())) {
			_people.setPeoplePassword(SecureUtil.md5(people.getPeoplePassword()));
			peopleBiz.updateEntity(_people);
			LOG.debug("更新密码成功");
			return ResultData.build().success(
					this.getResString("success", this.getResString("people.get.password")));
		} else {
			LOG.debug("更新密码失败");
			return ResultData.build().error(
					this.getResString("fail", this.getResString("people.get.password")));
		}
	}

	@ApiOperation(value = "自动登录")
	@PostMapping(value = "/autoLogin")
	@ResponseBody
	public ResultData autoLogin(HttpServletRequest request, HttpServletResponse response) {

		// 获取页面上标记为PEOPLE_COOKIE的cookies值
		String cookie = BasicUtil.getCookie( CookieConstEnum.PEOPLE_COOKIE);
		if (StringUtils.isBlank(cookie)) {
			return ResultData.build().error();
		}

		PeopleEntity people = JSONUtil.toBean(cookie, PeopleEntity.class);
		// 查找到cookies里用户名对应的用户实体
		PeopleEntity peopleEntity = this.peopleBiz.getByPeople(people);
		if (peopleEntity != null) {
			// 登录成功,压入用户session
			setPeopleBySession(request, peopleEntity);
			return ResultData.build().success();
		} else {
			return ResultData.build().error();
		}

	}

	@ApiOperation(value = "用户发送验证码，可以通过邮箱或手机发送")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "receive", value = "接收地址，只能是邮箱或手机号，邮箱需要使用邮箱插件，手机号需要短信插件", required =true,paramType="query"),
		@ApiImplicitParam(name = "modelCode", value = "对应邮件插件的模块编号", required =true,paramType="query"),
		@ApiImplicitParam(name = "type", value = "类型", required =true,paramType="query"),
		@ApiImplicitParam(name = "isSession", value = "true启用session保存code,false 关联用户信息，true一般是当用户手机还不存在系统中时使用，", required =true,paramType="query"),
		@ApiImplicitParam(name = "peoplePhone", value = "用户电话", required =false,paramType="query"),
		@ApiImplicitParam(name = "peopleMail", value = "用户邮箱", required =false,paramType="query"),
		@ApiImplicitParam(name = "peopleName", value = "登录帐号", required =false,paramType="query"),
	})
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping(value = "/sendCode")
	@ResponseBody
	public ResultData sendCode(@ModelAttribute @ApiIgnore PeopleEntity people, HttpServletRequest request,
			HttpServletResponse response) {
		if (!this.checkRandCode()) {
			return ResultData.build().error(
					this.getResString("err.error", this.getResString("rand.code")));
		}
		String receive = request.getParameter("receive");
		String modelCode = request.getParameter("modelCode");
		String configType = request.getParameter("configType");
		boolean isSession = BasicUtil.getBoolean("isSession");

		if (StringUtils.isBlank(receive)) {
			return ResultData.build().error( this.getResString("err.empty", this.getResString("receive")));
		}
		if (StringUtils.isBlank(modelCode)) {
			return ResultData.build().error( this.getResString("err.empty", "modelCode"));
		}
		if (StringUtils.isBlank(configType)) {
			return ResultData.build().error( this.getResString("err.empty", this.getResString("type")));
		}
		// 获取应用ID
		String peopleCode = StringUtil.randomNumber(6);
		// 解密得到的模块编码

		long time = new Date().getTime();
		BasicUtil.setSession("tokenSession", time);
		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("modelCode", modelCode);
		requestBody.add("receive", receive);
		requestBody.add("tokenSession", time);
		requestBody.add("configType", configType);
		Map map = new HashMap();
		map.put("code",peopleCode);
		requestBody.add("content", JSONUtil.toJsonStr(map));
		this.LOG.debug("验证码：{}", peopleCode);

		if (isSession) { // 启用session
			Object obj = BasicUtil.getSession( SessionConstEnum.SEND_CODE_SESSION);
			if (obj != null) {
				PeopleEntity p = (PeopleEntity) obj;
				if (DateUtil.betweenMs(new Date(), (Date)p.getPeopleCodeSendDate()) == 60) {
					return ResultData.build().error( this.getResString("people.code.time.error"));
				}
			}

			PeopleEntity _people = new PeopleEntity();
			_people.setPeopleCode(peopleCode);
			_people.setPeopleCodeSendDate(Timestamp.valueOf(DateUtil.now()));
			_people.setPeopleMail(receive);
			_people.setPeoplePhone(receive);
			BasicUtil.setSession( SessionConstEnum.SEND_CODE_SESSION, _people);


			//调用发送插件
			ResponseEntity<JSONObject> content  = RestTemplateUtil.post(this.getUrl(request) + "/msend/send.do",RestTemplateUtil.getHeaders(request),requestBody,JSONObject.class);
			LOG.debug("send " + receive + ":content " + peopleCode);
			return ResultData.build().success(JSONUtil.toJsonStr(content));
		}

		// 给people赋值（邮箱或电话）
		if (StringUtil.isMobile(receive)) {
			people.setPeoplePhone(receive);
		} else {
			people.setPeopleMail(receive);
		}
		// 判断是否接到用户名，应用于找回密码发送验证码
		if (StringUtils.isBlank(people.getPeopleName()) && this.getPeopleBySession() == null) {
			// 如果接收到mail值，就给mail_check赋值1
			if (!StringUtils.isBlank(people.getPeopleMail())) {
				people.setPeopleMailCheck(PeopleEnum.MAIL_CHECK);
			}
			// 如果接收到phone值，就给phone_check赋值1
			if (!StringUtils.isBlank(people.getPeoplePhone())) {
				people.setPeoplePhoneCheck(PeopleEnum.PHONE_CHECK);
			}
		}


		// 通过用户名地址和应用id得到用户实体
		PeopleEntity peopleEntity = (PeopleEntity) this.peopleBiz.getEntity(people);
		if (peopleEntity == null) {
			return ResultData.build().error(
					this.getResString("err.not.exist", this.getResString("people")));
		}

		if (peopleEntity.getPeopleUser() != null) {
			Map _map = new HashMap();
			_map.put("code",peopleCode);
			_map.put("userName",peopleEntity.getPeopleUser().getPuNickname());
			requestBody.remove("content");
			requestBody.add("content", JSONUtil.toJsonStr(_map));
		}

		// 将生成的验证码加入用户实体
		peopleEntity.setPeopleCode(peopleCode);

		// 将当前时间转换为时间戳格式保存进people表
		peopleEntity.setPeopleCodeSendDate(Timestamp.valueOf(DateUtil.now()));

		// 更新该实体
		this.peopleBiz.updateEntity(peopleEntity);

		PeopleEntity _people = (PeopleEntity) BasicUtil.getSession( SessionConstEnum.PEOPLE_EXISTS_SESSION);
		if (_people != null) {
			BasicUtil.setSession( SessionConstEnum.PEOPLE_EXISTS_SESSION, peopleEntity);
		}

		//调用发送插件
		ResponseEntity<JSONObject> content = RestTemplateUtil.post(this.getUrl(request) + "/msend/send.do",RestTemplateUtil.getHeaders(request),requestBody,JSONObject.class);
		LOG.debug("content :" + content);
		if(content.getBody().getBool("result")) {
			LOG.debug("send " + receive + ":content " + peopleCode);
			return ResultData.build().success();
		}else {
			return ResultData.build().error(content.getBody().getStr("msg"));
		}


	}

	@ApiOperation(value = "验证用户输入的接收验证码")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "receive", value = "接收地址，只能是邮箱或手机号，邮箱需要使用邮箱插件，手机号需要短信插件", required =true,paramType="query"),
		@ApiImplicitParam(name = "code", value = "接收到的验证码", required =true,paramType="query"),
	})
	@PostMapping(value = "/checkSendCode")
	@ResponseBody
	public ResultData checkSendCode(@ModelAttribute @ApiIgnore PeopleEntity people, HttpServletRequest request,
			HttpServletResponse response) {
		String code = request.getParameter("code");
		String receive = request.getParameter("receive");
		Boolean isMobile = Validator.isMobile(receive);
		Boolean isEmail = Validator.isEmail(receive);
		if (isMobile) {
			// 手机验证码
			if (StringUtils.isBlank(code)) {
				return ResultData.build().error(
						this.getResString("err.error", this.getResString("people.phone.code")));
			}
			people.setPeoplePhone(receive);
		}


		// 跳过邮箱验证码验证
		if (isEmail) {
			// 邮箱验证码
			if (StringUtils.isBlank(code)) {
				return ResultData.build().error(
						this.getResString("err.error", this.getResString("people.mail.code")));
			}
			people.setPeopleMail(receive);
		}


		// 根据邮箱地址查找用户实体
		PeopleEntity peopleEntity = (PeopleEntity) this.peopleBiz.getEntity(people);

		// 在注册流程，在发送验证码的时候数据库可能还不存在用户信息
		if (BasicUtil.getSession(SessionConstEnum.SEND_CODE_SESSION) != null) {
			peopleEntity = (PeopleEntity) BasicUtil.getSession(SessionConstEnum.SEND_CODE_SESSION);
			// 判断用户输入的随机码是否正确
			if (!peopleEntity.getPeopleCode().equals(code)) {
				if (isMobile) {
					return ResultData.build().error(
							this.getResString("err.error", this.getResString("people.phone.code")));
				}else{
					// 邮箱验证
					return ResultData.build().error(
							this.getResString("err.error", this.getResString("people.mail.code")));
				}
			} else {
				return ResultData.build().success();
			}
		} else {
			if (isMobile) {
				// 如果用户已经绑定过手机直接返回错误
				if (peopleEntity==null ||  peopleEntity.getPeoplePhoneCheck() == PeopleEnum.PHONE_CHECK.toInt()) {
					return ResultData.build().error(this.getResString("err.error", this.getResString("people.phone.code")));
				}
			} else {
				// 如果用户已经绑定过邮箱直接返回错误
				if (peopleEntity==null || peopleEntity.getPeopleMailCheck() == PeopleEnum.MAIL_CHECK.toInt()) {
					return ResultData.build().error(this.getResString("err.error", this.getResString("people.mail.code")));
				}
			}

			// 得到发送验证码时间，并转换为String类型
			String date = peopleEntity.getPeopleCodeSendDate().toString();

			// 如果发送时间和当前时间只差大于30分钟，则返回false
			if (DateUtil.betweenMs(new Date(), DateUtil.parse(date)) > 60 * 60 * 24) {
				return ResultData.build().error(getResString("overdue", getResString("rand.code")));
			}

			// 判断用户输入的随机码是否正确
			if (!peopleEntity.getPeopleCode().equals(code)) {
				if (isMobile) {
					return ResultData.build().error(
							this.getResString("err.error", this.getResString("people.phone.code")));
				}else{
					return ResultData.build().error(
							this.getResString("err.error", this.getResString("people.mail.code")));
				}
			}

			// 将随机码在数据库中清空
			peopleEntity.setPeopleCode("");
			if (StringUtil.isMobile(receive)) {
				peopleEntity.setPeoplePhoneCheck(PeopleEnum.PHONE_CHECK);
			} else {
				peopleEntity.setPeopleMailCheck(PeopleEnum.MAIL_CHECK);
			}
			peopleBiz.updateEntity(peopleEntity);
			return ResultData.build().success();

		}
	}

	@ApiOperation(value = "解绑邮箱-> 验证用户输入的接收验证码")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "code", value = "接收到的验证码", required =true,paramType="query"),
		@ApiImplicitParam(name = "receive", value = "接收地址，只能是邮箱或手机号，邮箱需要使用邮箱插件，手机号需要短信插件", required =true,paramType="query")
	})
	@PostMapping(value = "/cancelBind")
	public ResultData cancelBind(@ModelAttribute @ApiIgnore PeopleEntity people, HttpServletRequest request,
			HttpServletResponse response) {
		String code = request.getParameter("code");
		String receive = request.getParameter("receive");
		Boolean isMobile = Validator.isMobile(receive);
		Boolean isEmail = Validator.isEmail(receive);

		if (isMobile) {
			// 验证码
			if (StringUtils.isBlank(code)) {
				return ResultData.build().error(
						this.getResString("err.error", this.getResString("people.phone.code")));
			}
			people.setPeoplePhone(receive);
		}
		if (isEmail) {
			// 验证码
			if (StringUtils.isBlank(code)) {
				return ResultData.build().error(
						this.getResString("err.error", this.getResString("people.mail.code")));
			}
			people.setPeopleMail(receive);
		}
		// 根据用户名和邮箱或手机号查找用户实体
		PeopleEntity peopleEntity = (PeopleEntity) this.peopleBiz.getEntity(people);

		// 在注册流程，在发送验证码的时数据库可能还不存在用户信息
		if (BasicUtil.getSession(SessionConstEnum.SEND_CODE_SESSION) != null) {
			peopleEntity = (PeopleEntity) BasicUtil.getSession(SessionConstEnum.SEND_CODE_SESSION);
			// 判断用户输入的随机码是否正确
			if (!peopleEntity.getPeopleCode().equals(code)) {
				if (isMobile) {
					return ResultData.build().error(
							this.getResString("err.error", this.getResString("people.phone.code")));
				}else{
					return ResultData.build().error(
							this.getResString("err.error", this.getResString("people.mail.code")));
				}
			} else {
				return ResultData.build().success();
			}
		} else {
			if (isMobile) {
				// 如果用户未绑定过手机直接返回错误
				if (peopleEntity.getPeoplePhoneCheck() == PeopleEnum.PHONE_NO_CHECK.toInt()) {
					return ResultData.build().error();
				}
			} else {
				// 如果用户未绑定过邮箱直接返回错误
				if (peopleEntity.getPeopleMailCheck() == PeopleEnum.MAIL_NO_CHECK.toInt()) {
					return ResultData.build().error();
				}
			}

			// 得到发送验证码时间，并转换为String类型
			String date = peopleEntity.getPeopleCodeSendDate().toString();

			// 如果发送时间和当前时间只差大于30分钟，则返回false

			if (DateUtil.betweenMs(new Date(), DateUtil.parse(date)) > 60 * 60 * 24) {
				return ResultData.build().error(getResString("overdue", getResString("rand.code")));
			}

			// 判断用户输入的随机码是否正确
			if (!peopleEntity.getPeopleCode().equals(code)) {
				if (isMobile) {
					return ResultData.build().error(
							this.getResString("err.error", this.getResString("people.phone.code")));
				}else{
					return ResultData.build().error(
							this.getResString("err.error", this.getResString("people.mail.code")));
				}
			}

			// 将随机码在数据库中清空
			peopleEntity.setPeopleCode("");
			if (StringUtil.isMobile(receive)) {
				peopleEntity.setPeoplePhoneCheck(PeopleEnum.PHONE_NO_CHECK);
			} else {
				peopleEntity.setPeopleMailCheck(PeopleEnum.MAIL_NO_CHECK);
			}
			peopleBiz.updateEntity(peopleEntity);
			return ResultData.build().success();

		}

	}

	@ApiOperation(value = "安全退出")
	@PostMapping(value = "/loginOut")
	@ResponseBody
	public ResultData loginOut( HttpServletRequest request,
						  HttpServletResponse response) {
		this.removePeopleBySession(request);
		return ResultData.build().success();
	}

}
