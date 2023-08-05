/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.action.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.mdiy.action.BaseAction;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义配置表管理控制层
 * @author 铭飞开发团队
 * @version
 * 版本号：1<br/>
 * 创建日期：2017-8-12 15:58:29<br/>
 * 历史修订：<br/>
 */
@Api(tags={"前端-自定义模块接口"})
@Controller("webMdiyConfig")
@RequestMapping("/mdiy/config")
public class ConfigAction extends BaseAction {


	/**
	 *  获取配置中的key指定value值
	 * @param configName 配置名称
	 * @param key 配置的key值
	 * @param response
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "获取配置中的key指定value值")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "configName", value = "配置名称", required = true,paramType="query"),
			@ApiImplicitParam(name = "key", value = "配置key", required = true,paramType="query"),
	})
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(String configName,String key, HttpServletResponse response, HttpServletRequest request){
		if (StringUtils.isEmpty(configName) || StringUtils.isEmpty(key)) {
			return ResultData.build().error(getResString("err.empty",getResString("config.name")));
		}
		return ResultData.build().success(ConfigUtil.getString(configName,key));
	}

}
