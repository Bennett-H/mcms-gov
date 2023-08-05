/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.co.action;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.co.bean.ContentBean;
import net.mingsoft.co.biz.IContentBiz;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 文章管理控制层
 * @author 铭软开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：增加复制移动文章接口<br/>
 */
@Api(tags={"后端-企业模块接口"})
@Controller("cmsCoContentAction")
@RequestMapping("/${ms.manager.path}/cms/co/content")
public class ContentAction extends BaseAction {


	/**
	 * 注入文章业务层
	 */
	@Autowired
	private IContentBiz contentBiz;

	/**
	 * 返回文章移动视图
	 */
	@ApiIgnore
	@GetMapping("/removeOrCopyIndex")
	@RequiresPermissions("cms:content:saveRemoveOrCopy")
	public String removeView(HttpServletResponse response,HttpServletRequest request){
		return "/cms/content/remove-or-copy-index";
	}


	@ApiOperation(value = "复制移动文章接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "contentIds", value = "文章编号集合", required =true,paramType="query"),
			@ApiImplicitParam(name = "categoryIds", value = "分类编号集合", required =true,paramType="query"),
			@ApiImplicitParam(name = "operationType", value = "操作类型", required =true,paramType="query"),
	})
	@PostMapping("/saveRemoveOrCopy")
	@ResponseBody
	@LogAnn(title = "复制移动文章", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("cms:content:saveRemoveOrCopy")
	public ResultData saveRemoveOrCopy(@ModelAttribute @ApiIgnore ContentBean content, HttpServletResponse response,
						   HttpServletRequest request) {
		if(StringUtils.isEmpty(content.getContentIds())){
			return ResultData.build().error(getResString("err.empty", this.getResString("content.content.ids")));
		}
		if(StringUtils.isEmpty(content.getCategoryIds())){
			return ResultData.build().error(getResString("err.empty", this.getResString("content.category.ids")));
		}
		if(StringUtils.isEmpty(content.getOperationType())){
			return ResultData.build().error(getResString("err.empty", this.getResString("content.operation.type")));
		}
		List<String> ids = contentBiz.removeOrCopy(content,content.getOperationType());
		return ResultData.build().success(ids);
	}


}
