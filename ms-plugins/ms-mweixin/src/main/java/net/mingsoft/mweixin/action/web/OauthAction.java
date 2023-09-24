/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.action.web;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mweixin.biz.IWeixinBiz;
import net.mingsoft.mweixin.biz.IWeixinPeopleBiz;
import net.mingsoft.mweixin.constant.SessionConst;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.entity.WeixinPeopleEntity;
import net.mingsoft.mweixin.service.PortalService;
import net.mingsoft.people.biz.IPeopleUserBiz;
import net.mingsoft.people.constant.e.PeopleEnum;
import net.mingsoft.people.constant.e.SessionConstEnum;
import net.mingsoft.people.entity.PeopleEntity;
import net.mingsoft.people.entity.PeopleUserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 微信网页2.0授权表管理控制层
 * 微信官方文档：https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html
 * @author 铭飞开发团队
 * @version
 * 版本号：1.0.0<br/>
 * 创建日期：2017-5-25 22:04:59<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"前端-微信模块接口"})
@Controller("netOauthActionWeb")
@RequestMapping("/mweixin/oauth")
public class OauthAction extends net.mingsoft.mweixin.action.BaseAction{
	@Resource(name="netWeixinPeopleBiz")
	private IWeixinPeopleBiz weixinPeopleBiz;
	@Autowired
	private IWeixinBiz weixinBiz;
	/**
	 * 注入用户基础业务层
	 */
	@Autowired
	private IPeopleUserBiz peopleUserBiz;

	/**
	 * 微信授权登录组织授权地址，
	 * 场景一：微信菜单连接需要获取用户信息
	 * 场景二：分享出去的页面用户打开需要获取用信息
	 * @param weixinNo 微信号,必须为公众号业务表里的数据，
	 * @param url 需要授权地址，可以是任意地址
	 * @param oauthLoginUrl 微信授权登录地址，如果业务系统已经存在用户信息，需要新增微信用户信息，那么就需要通过开发代码来实现原始用户信息与微信信息一对一绑定，
	 * @param response
	 * @param request
	 */
	@ApiOperation(value = "微信授权登录组织授权地址接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "weixinNo", value = "微信号,必须为公众号业务表里的数据", required =true,paramType="query"),
			@ApiImplicitParam(name = "url", value = "需要授权地址，可以是任意地址", required =true,paramType="query"),
			@ApiImplicitParam(name = "oauthLoginUrl", value = "微信授权登录地址，如果业务系统已经存在用户信息，需要新增微信用户信息，那么就需要通过开发代码来实现原始用户信息与微信信息一对一绑定，", required =false,paramType="query"),
	})
	@GetMapping("/getUrl")
	public void getUrl(@RequestParam(required = true) String weixinNo,
						 @RequestParam(required = true) String url,
						 @RequestParam(required = false) String oauthLoginUrl,
						 HttpServletResponse response,HttpServletRequest request){
		try {
			//获取微信服务
			PortalService portalService = this.builderWeixinService(weixinNo);
			url = URLEncoder.encode(url,"UTF-8");
			//组织重定向链接，通过链接进行授权
			String oauthUrl = BasicUtil.getUrl()+"/mweixin/oauth/info.do?weixinNo="+weixinNo+"&url="+url+"&oauthLoginUrl="+oauthLoginUrl;
			String urlstr = portalService.getOAuth2Service().buildAuthorizationUrl(oauthUrl, "snsapi_userinfo" , null);
			response.sendRedirect(urlstr);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 授权登录，用户同意，通过code获取用户信息
	 * 使用上面getUrl接口微信授权登录组织授权，地址一般不用额外调取，用户同意授权会直接跳转该地址
	 * 存在第三方业务绑定用户，需要oauthLoginUrl授权登录地址，设置微信用户session,已经授权登录过则直接跳转对于的页面
	 * 不存在第三方业务绑定用户，不需要oauthLoginUrl授权登录地址，直接注册用户信息
	 */
	@ApiOperation(value = "授权登录接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "weixinNo", value = "微信号", required =true,paramType="query"),
	})
	@ResponseBody
	@GetMapping(value="/info")
	public void login(HttpServletResponse response,HttpServletRequest request)throws ServletException, IOException {
		String weixinNo = BasicUtil.getString("weixinNo");
		//获取微信配置
		PortalService portalService = this.builderWeixinService(weixinNo);
		WeixinEntity weixin = weixinBiz.getByWeixinNo(weixinNo);
		String code = request.getParameter("code"); //用户同意授权就可以获得
		String url = BasicUtil.getString("url");
		String oauthLoginUrl = BasicUtil.getString("oauthLoginUrl");
		try {
			//通过code获取用户token
			WxOAuth2AccessToken wxMpOAuth2AccessToken = portalService.getOAuth2Service().getAccessToken(code);
			//通过用户token获取用户信息
			WxOAuth2UserInfo user = portalService.getOAuth2Service().getUserInfo(wxMpOAuth2AccessToken, null);
			//授权登录页面存在，拼接openId,
			if(StringUtils.isNotEmpty(oauthLoginUrl)){
				//openId 之前就存在直接登录，返回对应的授权地址
				WeixinPeopleEntity wxPeople = weixinPeopleBiz.getByOpenId(user.getOpenid());
				if(ObjectUtil.isNotNull(wxPeople)){
					//不为空表示第二次授权后台直接登录
					PeopleEntity _people =  (PeopleEntity) peopleUserBiz.getEntity(wxPeople.getIntId());
					if (ObjectUtil.isNotNull(_people)) {
						BasicUtil.setSession(SessionConstEnum.PEOPLE_SESSION, _people);
						response.sendRedirect(url);
						return;
					}
				}
				url = URLEncoder.encode(url,"UTF-8");
				BasicUtil.setSession(SessionConst.WEIXIN_PEOPLE_SESSION,user);
				url = oauthLoginUrl+  "?url=" + url ;
			}else {
				weixinPeopleBiz.saveOrUpdate(user, weixin.getIntId());
				//将用户压入session:weixn_people_session
				WeixinPeopleEntity wpe = weixinPeopleBiz.getEntityByOpenIdAndAppIdAndWeixinId(user.getOpenid(), weixin.getIntId());
				LOG.debug("微信授权设置用户session：" + wpe);
				BasicUtil.setSession(SessionConstEnum.PEOPLE_SESSION,wpe);
				BasicUtil.setSession(SessionConst.WEIXIN_PEOPLE_SESSION,wpe);
			}
			//将微信实体压入session:weinxin_session
			BasicUtil.setSession(SessionConst.WEIXIN_SESSION,weixin);
			//重定向
			response.sendRedirect(url);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 将微信用户与第三方用户绑定
	 * 若不存在用户直接注册，需要发送短信验证码，存在只创建微信用户和第三方业务的关系
	 * @param people 包含peoplePhone手机号、peopleCode 短信验证码（需要接发送插件来发送短信）、rand_code图片验证码
	 * @param response
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "微信用户与第三方用户绑定接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "weixinPeopleWeixinId", value = "关联微信Id", required =false,paramType="query"),
			@ApiImplicitParam(name = "weixinPeopleOpenId", value = "用户在微信中的唯一标识", required =false,paramType="query"),
			@ApiImplicitParam(name = "weixinPeopleProvince", value = "用户所在省份", required =false,paramType="query"),
			@ApiImplicitParam(name = "weixinPeopleCity", value = "用户所在城市", required =false,paramType="query"),
			@ApiImplicitParam(name = "weixinPeopleHeadimgUrl", value = "用户头像链接地址", required =false,paramType="query"),
			@ApiImplicitParam(name = "weixinPeopleState", value = "用户关注状态 1:关注中用户(默认) 2:取消关注用户", required =false,paramType="query"),
	})
	@PostMapping("/bindPeople")
	@ResponseBody
	public ResultData bindPeople(@ModelAttribute @ApiIgnore WeixinPeopleEntity people, HttpServletResponse response, HttpServletRequest request) {
		if (!(checkRandCode())) {
			return ResultData.build().error(getResString("err.error", new String[] { getResString("rand.code") }));
		}
		//验证手机号的值是否合法
		if(StringUtil.isBlank(people.getPeoplePhone())){
			return ResultData.build().error(getResString("err.empty", this.getResString("people.phone")));
		}

		//验证短信验证码的值是否合法
		if(StringUtil.isBlank(people.getPeopleCode())){
			return ResultData.build().error(getResString("err.empty", this.getResString("people.code")));
		}
		Object obj = BasicUtil.getSession(SessionConstEnum.SEND_CODE_SESSION);
		if (obj != null) {
			PeopleEntity _people = (PeopleEntity) obj;
			if (_people.getPeoplePhone().equals(people.getPeoplePhone())) {
				//验证发送的短信验证码是否一致
				if (_people.getPeopleCode().equals(people.getPeopleCode())) {
					people.setPeoplePhoneCheck(PeopleEnum.PHONE_CHECK);
				} else {
					return ResultData.build().error(this.getResString("err.error", this.getResString("people.phone.code")));
				}
			}
		}

		//授权的时候存入了session
		WxMpUser wxMpUser = (WxMpUser) BasicUtil.getSession(SessionConst.WEIXIN_PEOPLE_SESSION);
		WeixinEntity weixin  = (WeixinEntity) BasicUtil.getSession(SessionConst.WEIXIN_SESSION);
		PeopleUserEntity _people = new PeopleUserEntity();
		_people.setPeoplePhone(people.getPeoplePhone());
		_people =  (PeopleUserEntity) peopleUserBiz.getEntity(_people);
		if(ObjectUtil.isNotNull(_people)){
			//绑定微信与原有用户的关系
			WeixinPeopleEntity _weixinPeople = weixinPeopleBiz.getByOpenId(wxMpUser.getOpenId());
			//用户第一次授权登录，但存在用户信息，只需要绑定微信用户与平台用户的关系
			if(ObjectUtil.isNull(_weixinPeople)){
				if(StringUtils.isEmpty(_people.getPuIcon())){
					_people.setPuIcon(_weixinPeople.getHeadimgUrl());
				}
				if(StringUtils.isEmpty(_people.getPuNickname())){
					_people.setPuNickname(wxMpUser.getNickname().replaceAll("[\ud800\udc00-\udbff\udfff\ud800-\udfff]", ""));
				}
				peopleUserBiz.updateEntity(_people);
				weixinPeopleBiz.saveEntity(wxMpUser,weixin.getIntId(),_people.getIntId());
			 }
		}else {
			//注册新用户
			weixinPeopleBiz.saveOrUpdate(wxMpUser,weixin.getIntId());
			WeixinPeopleEntity _weixinPeople = weixinPeopleBiz.getByOpenId(people.getOpenId());
			//默认手机验证通过
			_people = new PeopleUserEntity();
			_people.setPeoplePhoneCheck(PeopleEnum.PHONE_CHECK);
			_people.setPeopleIp(BasicUtil.getIp());
			_people.setPeoplePhone(people.getPeoplePhone());
			_people.setId(_weixinPeople.getId());
			peopleUserBiz.updateEntity(_people);
		}
		BasicUtil.setSession(SessionConstEnum.PEOPLE_SESSION, _people);
		return ResultData.build().success();
	}

}
