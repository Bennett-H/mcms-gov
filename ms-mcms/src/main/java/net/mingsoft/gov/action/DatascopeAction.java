/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.gov.action;

import io.swagger.annotations.Api;
import net.mingsoft.cms.action.BaseAction;
import net.mingsoft.cms.biz.ICategoryBiz;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 数据权限控制层
 * @author 铭飞开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-数据权限接口"})
@Controller("govdatascopeAction")
@RequestMapping("/${ms.manager.path}/gov/datascope")
public class DatascopeAction extends BaseAction {


	@Autowired
	private ICategoryBiz categoryBiz;

	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("datascope:data:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/gov/datascope/index";
	}


}
