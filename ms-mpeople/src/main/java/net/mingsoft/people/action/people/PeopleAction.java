/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.action.people;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SecureUtils;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mdiy.biz.IPageBiz;
import net.mingsoft.mdiy.entity.PageEntity;
import net.mingsoft.mdiy.util.ParserUtil;
import net.mingsoft.people.action.BaseAction;
import net.mingsoft.people.annotation.PeopleLogAnn;
import net.mingsoft.people.biz.IPeopleBiz;
import net.mingsoft.people.constant.e.CookieConstEnum;
import net.mingsoft.people.constant.e.PeopleEnum;
import net.mingsoft.people.constant.e.PeopleLogTypeEnum;
import net.mingsoft.people.constant.e.SessionConstEnum;
import net.mingsoft.people.entity.PeopleEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 *
 * 用户基础信息表管理控制层
 * @author 铭飞开发团队
 * @version
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
@Api(tags={"前端-用户-会员模块接口"})
@Controller("peopleMain")
@RequestMapping("/people")
public class PeopleAction extends BaseAction {
	/**
	 * 注入用户基础业务层
	 */
	@Autowired
	private IPeopleBiz peopleBiz;

	@Value("${ms.diy.html-dir:html}")
	private String htmlDir;

	/**
	 * 自定义页面业务层
	 */
	@Autowired
	private IPageBiz pageBiz;

	@ApiOperation(value = "重置密码接口")
    @ApiImplicitParam(name = "peoplePassword", value = "用户密码", required = true,paramType="query")
	@PostMapping(value = "/resetPassword")
	@ResponseBody
    @PeopleLogAnn(title = "重置密码", businessType = PeopleLogTypeEnum.UPDATE)
	public ResultData resetPassword(@ModelAttribute @ApiIgnore PeopleEntity people, HttpServletRequest request,
			HttpServletResponse response) {
		// 验证新密码的长度
		if (!StringUtil.checkLength(people.getPeoplePassword(), 6, 30)) {
			return ResultData.build().error(
					this.getResString("err.length", this.getResString("people.password"), "6", "20"));
		}
		if (StringUtils.isBlank(people.getPeoplePassword())) {
			// 用户或密码不能为空
			return ResultData.build().error(
					this.getResString("err.empty", this.getResString("people.password")));
		}
		// 获取用户session
		PeopleEntity _people = (PeopleEntity)peopleBiz.getEntity(Integer.parseInt(this.getPeopleBySession().getId()));
		// 将用户输入的原始密码用MD5加密再和数据库中的进行比对
		String peoplePassWord = people.getPeoplePassword();
		// 执行修改
		_people.setPeoplePassword(peoplePassWord);
		this.peopleBiz.updateEntity(_people);
		return ResultData.build().success();
	}

	@ApiOperation(value = "修改密码接口")
	@PostMapping(value = "/changePassword")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "peoplePassword", value = "用户密码", required = true,paramType="query"),
		@ApiImplicitParam(name = "peopleOldPassword", value = "用户旧密码", required = true,paramType="query")
	})
	@ResponseBody
	@PeopleLogAnn(title = "修改密码", businessType = PeopleLogTypeEnum.UPDATE)
	public ResultData changePassword(@ModelAttribute @ApiIgnore PeopleEntity people, HttpServletRequest request,
			HttpServletResponse response) {
		// 验证码验证 验证码不为null 或 验证码不相等
		if (!checkRandCode()) {
			return ResultData.build().error(
					this.getResString("err.error", this.getResString("rand.code")));
		}
		if (StringUtils.isBlank(people.getPeoplePassword())) {
			// 用户或密码不能为空
			return ResultData.build().error(
					this.getResString("err.empty", this.getResString("people.password")));
		}


		// 验证新密码的长度
		if (!StringUtil.checkLength(people.getPeoplePassword(), 6, 30)) {
			return ResultData.build().error(
					this.getResString("err.length", this.getResString("people.password"), "6", "30"));
		}

		// 验证新密码的合法：空格字符
		if (people.getPeoplePassword().contains(" ")) {
			return ResultData.build().error(
					 this.getResString("people.password") + this.getResString("people.space"));
		}

		// 获取用户session
		PeopleEntity curPeople = (PeopleEntity) peopleBiz.getEntity(getPeopleBySession().getIntId());
		if (!curPeople.isNewUser()&&StringUtils.isBlank(people.getPeopleOldPassword())) {
			// 用户或密码不能为空
			return ResultData.build().error(
					this.getResString("err.empty", this.getResString("people.old.password")));
		}
		if (!curPeople.isNewUser()&&!curPeople.getPeoplePassword().equals(SecureUtil.md5(people.getPeopleOldPassword()))) {
			// 用户或密码不能为空
			return ResultData.build().error(
					this.getResString("err.error", this.getResString("people.password")));
		}
		// 将用户输入的原始密码用MD5加密再和数据库中的进行比对
		String peoplePassWord = SecureUtil.md5(people.getPeoplePassword());
		// 执行修改
		curPeople.setPeoplePassword(peoplePassWord);
		this.peopleBiz.updateEntity(curPeople);
		return ResultData.build().success();
	}

	@ApiOperation(value = "修改手机号接口")
	@PostMapping(value = "/changePhone")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "peoplePassword", value = "用户密码", required = true,paramType="query"),
		@ApiImplicitParam(name = "peopleCode", value = "手机验证码", required = true,paramType="query")
	})
	@ResponseBody
	@PeopleLogAnn(title = "修改手机号", businessType = PeopleLogTypeEnum.UPDATE)
	public ResultData changePhone(@ModelAttribute @ApiIgnore PeopleEntity people, HttpServletRequest request,
			HttpServletResponse response) {
		// 获取用户发送验证码session
		PeopleEntity _people = (PeopleEntity) BasicUtil.getSession( SessionConstEnum.SEND_CODE_SESSION);
		PeopleEntity peopleSession = this.getPeopleBySession();
		// 判断手机是否已经存在
		if (StringUtils.isBlank(people.getPeoplePhone())) {
			return ResultData.build().error(
					this.getResString("err.empty", this.getResString("people.phone")));
		}
		// 如果手机号码已经绑定过就需要验证手机短信吗
		if (_people.getPeoplePhoneCheck() == PeopleEnum.PHONE_CHECK.toInt()) {
			// 判断用户输入的验证是否正确
			if (!people.getPeopleCode().equals(_people.getPeopleCode())) {
				// 返回错误信息
				return ResultData.build().error(
						this.getResString("err.error", this.getResString("people.phone.code")));
			}
		}
		people.setId(peopleSession.getId());
		people.setPeoplePhone(people.getPeoplePhone());
		peopleBiz.updateEntity(people);
		return ResultData.build().success();
	}

	@ApiOperation(value = "验证用户短信、邮箱验证码是否正确接口")
	@ApiImplicitParam(name = "peopleCode", value = "手机验证码", required = true,paramType="query")
	@PostMapping(value = "/checkPeopleCode")
	@ResponseBody
	public ResultData checkPeopleCode(@ModelAttribute @ApiIgnore PeopleEntity people, HttpServletRequest request,
			HttpServletResponse response) {
		// 获取session中的用户实体
		PeopleEntity _people = this.getPeopleBySession();
		PeopleEntity _temp = peopleBiz.getByPeople(_people);
		if (people.getPeopleCode().equals(_temp.getPeopleCode())) {
			return ResultData.build().success();
		} else {
			return ResultData.build().error();
		}
	}

	@ApiOperation(value = "读取当前登录用户的基本信息 用户信息接口")
	@GetMapping("/info")
	@ResponseBody
	public ResultData info(HttpServletRequest request, HttpServletResponse response) {
		// 得到登录后session中的用户实体值
		PeopleEntity people = (PeopleEntity) this.getPeopleBySession();
		// 返回用户信息
		return ResultData.build().success(people);
	}

	@ApiOperation(value = "退出登录接口")
	@GetMapping(value = "/quit")
	@ResponseBody
	public ResultData quit(HttpServletRequest request, HttpServletResponse response) {
		// 移除当前用户session
		this.removePeopleBySession(request);
		BasicUtil.setCookie( response, CookieConstEnum.PEOPLE_COOKIE, null);
		return ResultData.build().success();
	}

	@ApiOperation(value = "更新用户邮箱或手机号接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "peopleMail", value = "用户邮箱", required = false,paramType="query"),
		@ApiImplicitParam(name = "peoplePhone", value = "手机号", required = false,paramType="query")
	})
	@PostMapping(value = "/update")
	@ResponseBody
	@PeopleLogAnn(title = "更新用户邮箱或手机号接口", businessType = PeopleLogTypeEnum.UPDATE)
	public ResultData update(@ModelAttribute @ApiIgnore PeopleEntity people, HttpServletRequest request, HttpServletResponse response) {
		if (people == null) {
			// 未填写信息返回错误信息
			return ResultData.build().error(
					this.getResString("err.empty", this.getResString("people")));
		}
		String peopleId = this.getPeopleBySession().getId();
		PeopleEntity _people = (PeopleEntity)peopleBiz.getEntity(Integer.parseInt(peopleId));

		if (_people.getPeopleMailCheck() == PeopleEnum.MAIL_CHECK.toInt()) {
			people.setPeopleMail(null);
		}
		if (_people.getPeoplePhoneCheck() == PeopleEnum.PHONE_CHECK.toInt()) {
			people.setPeoplePhone(null);
		}
		people.setPeopleName(null);
		people.setId(peopleId);
		this.peopleBiz.updateEntity(people);
		// 返回更新成功
		return ResultData.build().success();
	}

	@ApiOperation(value = "前端会员中心所有页面都可以使用该方法 支持参数传递与解析，例如页面中有参数id=10 传递过来，跳转页面可以使用{id/}获取该参数")
	@ApiImplicitParam(name = "diy", value = "id", required = true,paramType="path")
	@ResponseBody
	@GetMapping(value = "/{diy}")
	public String diy(@PathVariable(value = "diy") String diy, HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> map = BasicUtil.assemblyRequestMap();

		//站点编号
		if (BasicUtil.getWebsiteApp() != null) {
			map.put(ParserUtil.APP_DIR, BasicUtil.getWebsiteApp().getAppDir());
			map.put(ParserUtil.URL, BasicUtil.getWebsiteApp().getAppHostUrl());
			map.put(ParserUtil.APP_ID, BasicUtil.getWebsiteApp().getAppId());
		} else {
			map.put(ParserUtil.URL, BasicUtil.getUrl());
			map.put(ParserUtil.APP_DIR, BasicUtil.getApp().getAppDir());
		}

		map.put(ParserUtil.IS_DO,false);
		map.put(ParserUtil.HTML,htmlDir);

		//解析后的内容
		String content = "";
		PageEntity page = new PageEntity();
		page.setPageKey("people/"+diy);
		//根据请求路径查询模版文件
		PageEntity _page = (PageEntity) pageBiz.getEntity(page);
		if(ObjectUtil.isNull(_page)){
			throw new BusinessException(getResString("templet.file"));
		}
		try {
			content = ParserUtil.rendering(_page.getPagePath(), map);
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedTemplateNameException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

}
