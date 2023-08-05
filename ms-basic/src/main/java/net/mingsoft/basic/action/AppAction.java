/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.basic.action;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.biz.IAppBiz;
import net.mingsoft.basic.util.MapCacheUtil;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.CookieConstEnum;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 网站基本信息控制层
 *
 * @author 铭飞开发团队
 * @version 版本号：100-000-000<br/>
 *          创建日期：2014-07-14<br/>
 *          历史修订：<br/>
 */
@Api(tags={"后端-基础接口"})
@Controller
@RequestMapping("/${ms.manager.path}/basic/app")
public class AppAction extends BaseAction {

	/**
	 * appBiz业务层的注入
	 */
	@Autowired
	private IAppBiz appBiz;

	/**
	 * 跳转到修改页面
	 *
	 * @param mode
	 *            ModelMap实体对象
	 * @param appId
	 *            站点id
	 * @param request
	 *            请求对象
	 * @return 站点修改页面
	 */
	@ApiOperation(value = "跳转到修改页面")
	@ApiImplicitParam(name = "appId", value = "站点ID", required = true,paramType="path")
	@GetMapping(value = "/app")
	@RequiresPermissions("basic:app:view")
	public String app(ModelMap mode, HttpServletRequest request) {
		int appId = BasicUtil.getInt("appId", -1);
		AppEntity app = null;
		//若有appid直接根据appId查询
		if (appId < 0) {
			app = BasicUtil.getApp();
			if(app!=null) {
				//防止session再次压入appId
				if(BasicUtil.getSession("appId")==null){
					BasicUtil.setSession("appId",app.getAppId());
				}
			} else {
				appId = (int) BasicUtil.getSession("appId");
				app =  appBiz.getById(appId);
			}
		} else {
			app =  appBiz.getById(appId);
		}


		mode.addAttribute("app", app);
		mode.addAttribute("appId", appId);
		return "/basic/app/app";

	}

    /**
     * 获取站点信息
     * @param appId
     * @return
     */
    @ApiOperation(value = "获取站点信息")
    @ApiImplicitParam(name = "appId", value = "站点ID", required = true,paramType="path")
    @GetMapping(value = "/get")
    @ResponseBody
    public ResultData get() {
		int appId = BasicUtil.getInt("appId", -1);
		AppEntity app = null;
        //若有appid直接根据appId查询
        if (appId < 0) {
            app = BasicUtil.getApp();
            if(app!=null) {
                //防止session再次压入appId
                if(BasicUtil.getSession("appId")==null){
                    BasicUtil.setSession("appId",app.getAppId());
                }
            } else {
                appId = (int) BasicUtil.getSession("appId");
                app =  appBiz.getById(appId);
            }
        } else {
            app =  appBiz.getById(appId);
        }
		return ResultData.build().success(app);

    }

	/**
	 * 更新站点信息
	 *
	 * @param mode
	 *            ModelMap实体对象
	 * @param app
	 *            站点对象
	 * @param request
	 *            请求对象
	 * @param response
	 *            相应对象
	 */
	@ApiOperation(value ="更新站点信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "appName", value = "应用名称", required = false,paramType="query"),
		@ApiImplicitParam(name = "appDescription", value = "应用描述", required = false,paramType="query"),
		@ApiImplicitParam(name = "appLogo", value = "应用logo", required = false,paramType="query"),
		@ApiImplicitParam(name = "appDatetime", value = "站点日期", required = false,paramType="query"),
		@ApiImplicitParam(name = "appKeyword", value = "网站关键字", required = false,paramType="query"),
		@ApiImplicitParam(name = "appCopyright", value = "网站版权信息", required = false,paramType="query"),
		@ApiImplicitParam(name = "appStyle", value = "网站采用的模板风格", required = false,paramType="query"),
		@ApiImplicitParam(name = "appUrl", value = "网站域名", required = false,paramType="query"),
		@ApiImplicitParam(name = "appManagerId", value = "管理站点的管理员id", required = false,paramType="query"),
		@ApiImplicitParam(name = "appPayDate", value = "应用续费时间", required = false,paramType="query"),
		@ApiImplicitParam(name = "appPay", value = "应用费用清单", required = false,paramType="query"),
	})
	@PostMapping("/update")
	@LogAnn(title = "更新站点信息",businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("basic:app:update")
	@ResponseBody
	public ResultData update(@ModelAttribute @ApiIgnore AppEntity app,ModelMap mode, HttpServletRequest request,
							 HttpServletResponse response) {
		mode.clear();
		// 获取Session值
		ManagerEntity managerSession = BasicUtil.getManager();
		if (managerSession == null) {
			return ResultData.build().error();
		}
		mode.addAttribute("managerSession", managerSession);

		//验证重复
		if(super.validated("app", "app_dir", app.getAppDir(), app.getId(), "id")){
			return ResultData.build().error(getResString("err.exist", this.getResString("app.dir")));
		}
		if(StringUtils.isBlank(app.getAppDir())){
			return ResultData.build().error(getResString("err.empty", this.getResString("app.dir")));
		}
		if(!StringUtil.checkLength(app.getAppDir()+"", 0, 50)){
			return ResultData.build().error(getResString("err.length", this.getResString("app.dir"), "0", "10"));
		}
		// 判断站点数据的合法性
		// 获取cookie
		String cookie = BasicUtil.getCookie(CookieConstEnum.PAGENO_COOKIE);
		int pageNo = 1;
		// 判断cookies是否为空
		if (StringUtils.isNotBlank(cookie) && Integer.valueOf(cookie) > 0) {
			pageNo = Integer.valueOf(cookie);
		}
		mode.addAttribute("pageNo", pageNo);
		ResultData resultData = ResultData.build();
		if (!checkForm(app, resultData)) {
			return resultData;
		}
		if (StringUtils.isNotBlank(app.getAppLogo())) {
			app.setAppLogo(app.getAppLogo().replace("|", ""));
		}
		app.setAppUrl(BasicUtil.getUrl());
		MapCacheUtil.remove(BasicUtil.getDomain());
		appBiz.updateById(app);
		appBiz.updateCache();
		return ResultData.build().success();
	}

	@ApiOperation(value = "刷新站点缓存")
	@PostMapping("/refreshCache")
	@ResponseBody
	public ResultData refreshCache(HttpServletRequest request) {
		MapCacheUtil.remove(BasicUtil.getDomain());
		appBiz.updateCache();
		return ResultData.build().success();
	}

	/**
	 * 判断站点域名的合法性
	 *
	 * @param app
	 *            要验证的站点信息
	 * @param resultData
	 *            resultData对象
	 */
	public boolean checkForm(AppEntity app, ResultData resultData) {

		/*
		 * 判断数据的合法性
		 */
		if (StringUtils.isNotBlank(app.getAppKeyword()) && !StringUtil.checkLength(app.getAppKeyword(), 0, 1000)) {
			resultData.error(getResString("err.length", this.getResString("appKeyword"), "0", "1000"));
			return false;
		}
		if (StringUtils.isNotBlank(app.getAppCopyright()) && !StringUtil.checkLength(app.getAppCopyright(), 0, 1000)) {
			resultData.error(getResString("err.length", this.getResString("appCopyright"), "0", "1000"));
			return false;
		}
		if (StringUtils.isNotBlank(app.getAppDescription()) && !StringUtil.checkLength(app.getAppDescription(), 0, 1000)) {
			resultData.error(getResString("err.length", this.getResString("appDescrip"), "0", "1000"));
			return false;
		}
		if (!StringUtil.checkLength(app.getAppName(), 1, 50)) {
			resultData.error(getResString("err.length", this.getResString("appTitle"), "1", "50"));
			return false;
		}
		if (StringUtils.isNotBlank(app.getAppStyle()) && !StringUtil.checkLength(app.getAppStyle(), 1, 30)) {
			resultData.error(getResString("err.length", this.getResString("appStyle"), "1", "30"));
			return false;
		}
		if(ObjectUtil.isNotNull(app.getAppHostUrl())){
			if (!StringUtil.checkLength(app.getAppHostUrl(), 10, 150)) {
				resultData.error(getResString("err.length", this.getResString("appUrl"), "10", "150"));
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否有重复的域名
	 *
	 * @param request
	 *            请求对象
	 * @return true:重复,false:不重复
	 */
	@ApiOperation(value = "判断是否有重复的域名")
	@GetMapping("/checkUrl")
	@ResponseBody
	public ResultData checkUrl(HttpServletRequest request) {
		if (request.getParameter("appUrl") != null) {
			if (appBiz.countByUrl(request.getParameter("appUrl")) > 0) {
				return ResultData.build().success();
			} else {
				return ResultData.build().error();
			}
		} else {
			return ResultData.build().error();
		}

	}
}
