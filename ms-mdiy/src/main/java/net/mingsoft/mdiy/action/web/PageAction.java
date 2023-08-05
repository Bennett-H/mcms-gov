/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.mdiy.action.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.config.MSProperties;
import net.mingsoft.mdiy.action.BaseAction;
import net.mingsoft.mdiy.biz.IPageBiz;
import net.mingsoft.mdiy.entity.PageEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.mdiy.util.ParserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @ClassName:  PageAction
 * @Description:TODO(自定义页面)
 * @author: 铭飞开发团队
 * @date:   2018年12月17日 下午6:10:12
 *
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 */
@Api(tags={"前端-自定义模块接口"})
@Controller("webDiyPath")
@RequestMapping(value={"/mdiyPage"})
public class PageAction extends BaseAction {

	/**
	 * 自定义页面业务层
	 */
	@Autowired
	private IPageBiz pageBiz;

	@ApiOperation(value = "自定义页面")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "diy", value = "请求地址", required = true,paramType="path")
	})
	@GetMapping(value = "/{diy}")
	public void diy(@PathVariable(value = "diy") String diy, HttpServletRequest req, HttpServletResponse resp) {
		String htmlDir = MSProperties.diy.htmlDir;
		Map<String,Object> map = BasicUtil.assemblyRequestMap();
		map.forEach((k,v)->{
			if(k!=null && v!=null){
				map.put(k, v.toString().replaceAll("('|\"|\\\\)", "\\\\$1"));
			}
		});

		//站点编号
		Boolean shortSwitch = ConfigUtil.getBoolean("短链配置", "shortLinkSwitch");
		if (BasicUtil.getWebsiteApp() != null) {
			map.put(ParserUtil.APP_DIR, BasicUtil.getWebsiteApp().getAppDir());
			map.put(ParserUtil.URL, BasicUtil.getWebsiteApp().getAppHostUrl());
			map.put(ParserUtil.APP_ID, BasicUtil.getWebsiteApp().getAppId());
		} else if (shortSwitch){
			map.put(ParserUtil.URL, BasicUtil.getUrl());
			map.put(ParserUtil.APP_DIR, "");
		} else {
			map.put(ParserUtil.URL, BasicUtil.getUrl());
			map.put(ParserUtil.APP_DIR, BasicUtil.getApp().getAppDir());
		}
		String contextPath = BasicUtil.getContextPath();
		if (StringUtils.isNotBlank(contextPath) && "/".equalsIgnoreCase(contextPath)) {
			contextPath = "";
		}

		map.putIfAbsent("contextPath", contextPath);
		map.put(ParserUtil.IS_DO,false);
		map.put(ParserUtil.HTML,htmlDir);

		PageEntity page = new PageEntity();
		page.setPageKey(diy);
		PageEntity _page = pageBiz.getOne(new QueryWrapper<>(page), false);
		try {
			String content = ParserUtil.rendering(_page.getPagePath().replace(" ", ""), map);

			BasicUtil.outString(resp, content);
		} catch (TemplateNotFoundException e1) {
			e1.printStackTrace();
		} catch (MalformedTemplateNameException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
