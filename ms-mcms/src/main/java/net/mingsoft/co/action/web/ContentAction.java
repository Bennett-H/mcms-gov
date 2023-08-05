/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.action.web;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.cms.bean.CategoryBean;
import net.mingsoft.cms.bean.ContentBean;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.constant.e.CategoryTypeEnum;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.mdiy.bean.PageBean;
import net.mingsoft.mdiy.biz.IModelBiz;
import net.mingsoft.mdiy.entity.ModelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文章管理控制层
 * @author 铭飞开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"前端-文章接口"})
@Controller("WebCOcmsContentAction")
@RequestMapping("/co/cms/content")
public class ContentAction extends net.mingsoft.cms.action.BaseAction{

	/**
	 * 注入文章业务层
	 */
	@Resource(name="cmscontentBizImpl")
	private net.mingsoft.cms.biz.IContentBiz contentBiz;

	/**
	 * 注入文章业务层
	 */
	@Resource(name="cmscontentBizImpl")
	private net.mingsoft.cms.biz.IContentBiz baseContentBiz;

	@Autowired
	private ICategoryBiz categoryBiz;

	@Autowired
	private IModelBiz modelBiz;

	/**
	 * 查询文章列表
	 */
	@ApiOperation(value = "查询文章列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "typeid", value = "所属栏目", required =false,paramType="query"),
			@ApiImplicitParam(name = "pageNo", value = "页码", required =false,paramType="query"),
			@ApiImplicitParam(name = "pageSize", value = "一页显示数量", required =false,paramType="query"),
			@ApiImplicitParam(name = "orderby", value = "排序", required =false,paramType="query"),
	})
	@RequestMapping("/list")
	@ResponseBody
	public ResultData list(HttpServletResponse response, HttpServletRequest request) {
		//会将请求参数全部转换map
		Map map = BasicUtil.assemblyRequestMap();
		String typeid = (String) map.get("typeid");
		if (StrUtil.isBlank(typeid)){
			return ResultData.build().error(getResString("err.empty", this.getResString("content.category.id")));
		}
		ContentBean content = new ContentBean();
		content.setCategoryType(CategoryTypeEnum.LIST.toString());
		content.setProgressStatus("终审通过");
		content.setContentDisplay("0");
		content.setCategoryId(typeid);
		List<CategoryBean> articleList = baseContentBiz.queryIdsByCategoryIdForParser(content);
		PageBean page = new PageBean();
		List filedStr = new ArrayList<>();
		page.setPageNo(BasicUtil.getInt("pageNo",1));
		page.setSize(BasicUtil.getInt("size",10));
		map.put("ispaging","true");
		map.put("size",BasicUtil.getPageSize());
		if (BasicUtil.getWebsiteApp() != null) {
			map.put("appid", BasicUtil.getWebsiteApp().getId());
		}
		map.put(ParserUtil.PAGE, page);
		if (typeid != null) {
			CategoryEntity column = categoryBiz.getById(typeid);
			// 获取表单类型的id
			if (column != null && ObjectUtil.isNotNull(column.getMdiyModelId())) {
				ModelEntity	contentModel = (ModelEntity) modelBiz.getById(column.getMdiyModelId());
				if (contentModel != null) {
					// 保存自定义模型的数据
					Map<String, String> fieldMap = contentModel.getFieldMap();
					for (String s : fieldMap.keySet()) {
						filedStr.add(fieldMap.get(s));
					}
					// 设置自定义模型表名，方便解析的时候关联表查询
					map.put(ParserUtil.TABLE_NAME, contentModel.getModelTableName());

				}
			}

			// 设置栏目，方便解析的时候关联表查询
			map.put(ParserUtil.COLUMN, column);
		}
		//实际上list是需要参数，例如分页、栏目分类、属性等待，具体看标签arclist对应的参数

		List contentList = contentBiz.list(map);
		return ResultData.build().success(new EUListBean(contentList,articleList.size()));
	}


}
