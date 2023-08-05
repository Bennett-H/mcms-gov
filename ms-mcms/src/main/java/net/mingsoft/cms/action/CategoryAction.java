/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.cms.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.PinYinUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.cms.bean.CategoryBean;
import net.mingsoft.cms.bean.ContentBean;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.constant.e.CategoryTypeEnum;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.co.constant.e.ProgressStatusEnum;
import net.mingsoft.co.service.RenderingService;
import net.mingsoft.co.util.ParserUtil;
import net.mingsoft.datascope.utils.DataScopeUtil;
import net.mingsoft.mdiy.bean.PageBean;
import net.mingsoft.mdiy.util.DictUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 分类管理控制层
 * @author 铭软开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：
 * 20210722 增加管理员栏目权限处理<br/>
 */
@Api(tags={"后端-内容模块接口"})
@Controller("cmsCategoryAction")
@RequestMapping("/${ms.manager.path}/cms/category")
public class CategoryAction extends BaseAction {


	/**
	 * 注入分类业务层
	 */
	@Autowired
	private ICategoryBiz categoryBiz;

	@Value("${ms.diy.html-dir:html}")
	private String htmlDir;

	/**
	 * 文章管理业务处理层
	 */
	@Autowired
	private IContentBiz contentBiz;

	@Autowired
	private RenderingService renderingService;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("cms:category:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/cms/category/index";
	}

	/**
	 * 返回编辑界面category_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions("cms:category:view")
	public String form(@ModelAttribute CategoryEntity category, HttpServletResponse response, HttpServletRequest request, ModelMap model){
		model.addAttribute("appId", BasicUtil.getApp().getAppId());
		return "/cms/category/form";
	}

	/**
	 * 查询分类列表
	 * @param category 分类实体
	 */
	@ApiOperation(value = "查询分类列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "categoryTitle", value = "栏目管理名称", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryId", value = "所属栏目", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryType", value = "栏目管理属性", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categorySort", value = "自定义顺序", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryListUrl", value = "列表模板", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryUrl", value = "内容模板", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryKeyword", value = "栏目管理关键字", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryDescrip", value = "栏目管理描述", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryImg", value = "缩略图", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryDiyUrl", value = "自定义链接", required =false,paramType="query"),
    	@ApiImplicitParam(name = "mdiyModelId", value = "栏目管理的内容模型id", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryDatetime", value = "类别发布时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "dictId", value = "字典对应编号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryFlag", value = "栏目属性", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryPath", value = "栏目路径", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryParentId", value = "父类型编号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createBy", value = "创建人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "createDate", value = "创建时间", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateBy", value = "修改人", required =false,paramType="query"),
    	@ApiImplicitParam(name = "updateDate", value = "修改时间", required =false,paramType="query"),
    })
	//@RequiresPermissions("cms:category:view") 此处权限不可放开，会导致无栏目查看权限者无法正常使用文章、静态化等功能
	@RequestMapping(value = "/list", method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore CategoryEntity category, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
		ManagerEntity manager =  BasicUtil.getManager();
		List<CategoryEntity> allCategory = categoryBiz.query(category);
		DataScopeUtil.start(manager.getId(),"管理员栏目权限",false, manager.getRoleIds());
		List<CategoryEntity> categoryList = categoryBiz.query(category);
		//是否拥有栏目部分的权限
		if (allCategory.size() != categoryList.size() && categoryList.size() !=0 ){
			//获取栏目的父栏目id并去重
			List<String> split = new ArrayList<>();
			for (CategoryEntity categoryEntity : categoryList) {
				String parentids = categoryEntity.getCategoryParentIds();
				//无父栏目就给变量赋“”避免NPE
				if (StrUtil.isBlank(parentids)){
					parentids = "";
				}

				Collections.addAll(split,parentids.split(","));
			}
			split = split.stream().distinct().collect(Collectors.toList());
			QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
			queryWrapper.in("id", split);
			List<CategoryEntity> parentCategory = categoryBiz.list(queryWrapper);
			//将栏目的父id加入categoryList
			for (CategoryEntity categoryEntity : parentCategory) {
					categoryList.add(categoryEntity);
			}
			//去重
			categoryList = categoryList.stream().collect(
					Collectors.collectingAndThen(Collectors.toCollection(
							() -> new TreeSet<>(Comparator.comparing(CategoryEntity::getId))), ArrayList::new));
		}


		return ResultData.build().success(new EUListBean(categoryList,categoryList.size()));
	}

	/**
	 * 获取分类
	 * @param category 分类实体
	 */
	@ApiOperation(value = "获取分类列表接口")
    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query")
	@RequiresPermissions("cms:category:view")
	@GetMapping("/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore CategoryEntity category, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model){
		if(category.getId()==null) {
			return ResultData.build().error();
		}
		CategoryEntity _category = (CategoryEntity)categoryBiz.getById(category.getId());
		return ResultData.build().success(_category);
	}

	@ApiOperation(value = "保存分类列表接口")
	 @ApiImplicitParams({
    	@ApiImplicitParam(name = "categoryTitle", value = "栏目管理名称", required =true,paramType="query"),
		@ApiImplicitParam(name = "categoryId", value = "所属栏目", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryType", value = "栏目管理属性", required =false,paramType="query"),
		@ApiImplicitParam(name = "categorySort", value = "自定义顺序", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryListUrl", value = "列表模板", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryUrl", value = "内容模板", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryKeyword", value = "栏目管理关键字", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryDescrip", value = "栏目管理描述", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryImg", value = "缩略图", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryDiyUrl", value = "自定义链接", required =false,paramType="query"),
		@ApiImplicitParam(name = "mdiyModelId", value = "栏目管理的内容模型id", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryDatetime", value = "类别发布时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "dictId", value = "字典对应编号", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryFlag", value = "栏目属性", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryPath", value = "栏目路径", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryParentId", value = "父类型编号", required =false,paramType="query"),
	})

	/**
	* 保存分类
	* @param category 分类实体
	*/
	@PostMapping("/save")
	@ResponseBody
	@LogAnn(title = "保存分类", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("cms:category:save")
	public ResultData save(@ModelAttribute @ApiIgnore CategoryEntity category, HttpServletResponse response, HttpServletRequest request) {
		//验证栏目管理名称的值是否合法
		if(StringUtil.isBlank(category.getCategoryTitle())){
			return ResultData.build().error(getResString("err.empty", this.getResString("category.title")));
		}
		if(!StringUtil.checkLength(category.getCategoryTitle()+"", 1, 100)){
			return ResultData.build().error(getResString("err.length", this.getResString("category.title"), "1", "100"));
		}

		if(!StringUtil.checkLength(category.getCategoryPath()+"", 0, 225)){
			return ResultData.build().error(getResString("err.length", this.getResString("category.path"), "1", "225"));
		}
		if(!StringUtil.checkLength(category.getCategoryParentIds()+"", 0, 120)){
			return ResultData.build().error(getResString("err.length", this.getResString("category.parent.id"), "1", "100"));
		}
		//判断拼音是否重复
		if(StrUtil.isNotBlank(category.getCategoryPinyin())) {
			if (!category.getCategoryPinyin().matches("^[a-zA-Z0-9]*$")){
				return ResultData.build().error(this.getResString("err.format"));
			}
			CategoryEntity _category = new CategoryEntity();
			_category.setCategoryPinyin(category.getCategoryPinyin());
			List<CategoryEntity> query = categoryBiz.query(_category);
			if (query.size() > 0) {
				return ResultData.build().error(getResString("err.exist", this.getResString("category.pinyin")));
			}
		}

		categoryBiz.saveEntity(category);
		return ResultData.build().success(category);
	}

	/**
	 * @param categoryList 分类实体数组
	 */
	@ApiOperation(value = "批量删除分类列表接口")
	@PostMapping("/delete")
	@ResponseBody
	@LogAnn(title = "删除分类", businessType = BusinessTypeEnum.DELETE)
	@RequiresPermissions("cms:category:del")
	public ResultData delete(@RequestBody List<CategoryEntity> categoryList, HttpServletResponse response, HttpServletRequest request) {
		for (CategoryEntity category : categoryList) {
			categoryBiz.delete(category.getId());
		}
		return ResultData.build().success();
	}
	/**
	*	更新分类列表
	* @param category 分类实体
	*/
	 @ApiOperation(value = "更新分类列表接口")
	 @ApiImplicitParams({
	    @ApiImplicitParam(name = "id", value = "编号", required =true,paramType="query"),
    	@ApiImplicitParam(name = "categoryTitle", value = "栏目管理名称", required =true,paramType="query"),
		@ApiImplicitParam(name = "categoryId", value = "所属栏目", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryType", value = "栏目管理属性", required =false,paramType="query"),
		@ApiImplicitParam(name = "categorySort", value = "自定义顺序", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryListUrl", value = "列表模板", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryUrl", value = "内容模板", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryKeyword", value = "栏目管理关键字", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryDescrip", value = "栏目管理描述", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryImg", value = "缩略图", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryDiyUrl", value = "自定义链接", required =false,paramType="query"),
		@ApiImplicitParam(name = "mdiyModelId", value = "栏目管理的内容模型id", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryDatetime", value = "类别发布时间", required =false,paramType="query"),
		@ApiImplicitParam(name = "dictId", value = "字典对应编号", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryFlag", value = "栏目属性", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryPath", value = "栏目路径", required =false,paramType="query"),
		@ApiImplicitParam(name = "categoryParentId", value = "父类型编号", required =false,paramType="query"),
	})
	@PostMapping("/update")
	@ResponseBody
	@LogAnn(title = "更新分类", businessType = BusinessTypeEnum.UPDATE)
	@RequiresPermissions("cms:category:update")
	public ResultData update(@ModelAttribute @ApiIgnore CategoryEntity category, HttpServletResponse response,
                             HttpServletRequest request) {
		//验证栏目管理名称的值是否合法
		if(StringUtil.isBlank(category.getCategoryTitle())){
			return ResultData.build().error(getResString("err.empty", this.getResString("category.title")));
		}
		if(!StringUtil.checkLength(category.getCategoryTitle()+"", 1, 100)){
			return ResultData.build().error(getResString("err.length", this.getResString("category.title"), "1", "100"));
		}

		if(!StringUtil.checkLength(category.getCategoryPath()+"", 0, 225)){
			return ResultData.build().error(getResString("err.length", this.getResString("category.path"), "1", "225"));
		}
		if(!StringUtil.checkLength(category.getCategoryParentIds()+"", 0, 120)){
			return ResultData.build().error(getResString("err.length", this.getResString("category.parent.id"), "1", "120"));
		}
		//限制栏目关键字0-100
		if(!StringUtil.checkLength(category.getCategoryKeyword()+"", 0, 120)){
			return ResultData.build().error(getResString("err.length", this.getResString("category.keyword"), "1", "120"));
		}
		 //判断拼音是否重复并且是否和原拼音相同
		 if(StrUtil.isNotBlank(category.getCategoryPinyin()) && !categoryBiz.getById(category.getId()).getCategoryPinyin().equals(category.getCategoryPinyin())) {
			 if (!category.getCategoryPinyin().matches("^[a-zA-Z0-9]*$")){
				 return ResultData.build().error(this.getResString("err.format"));
			 }
			 CategoryEntity _category = new CategoryEntity();
			 _category.setCategoryPinyin(category.getCategoryPinyin());
			 List<CategoryEntity> query = categoryBiz.query(_category);
			 if (query.size() > 0) {
				 return ResultData.build().error(getResString("err.exist", this.getResString("category.pinyin")));
			 }
		 }
		 String pingYin = PinYinUtil.getPingYin(category.getCategoryTitle());
		 //如果用户填写了拼音则使用用户填写的
		 if (StrUtil.isNotBlank(category.getCategoryPinyin())) {
		 	pingYin = category.getCategoryPinyin();
		 }
		 CategoryEntity categoryEntity=new CategoryEntity();
		 categoryEntity.setCategoryPinyin(pingYin);
		 CategoryEntity categoryBizEntity = categoryBiz.getEntity(categoryEntity);
		 category.setCategoryPinyin(pingYin);
		 //如果存在此拼音栏目则拼接上id
		 if(categoryBizEntity!=null&&!categoryBizEntity.getId().equals(category.getId())){
			 category.setCategoryPinyin(pingYin+category.getId());
		 }
		//判断是否选择子级为所属栏目
		 CategoryEntity _category = new CategoryEntity();
		 _category.setId(category.getId());
		 List<CategoryEntity> categoryList = categoryBiz.queryChildren(_category);
		 // categoryBiz.queryChildren 因为会包含自身，所以必定大于1才表示有子节点
		 if(categoryList.size()>1) {
			 for(CategoryEntity item:categoryList){
				 if(item.getId().equals(category.getCategoryId())){
					 return ResultData.build().error(getResString("cannot.select.child"));
				 }
			 }
			 category.setLeaf(false);
		 } else {
			 category.setLeaf(true);
		 }



		 // 这里不能使用mybitsplus 存在业务
		 categoryBiz.updateEntity(category);
		return ResultData.build().success(category);
	}

	@ApiOperation(value = "验证拼音")
	@GetMapping("/verifyPingYin")
	@ResponseBody
	public ResultData verifyPingYin(@ModelAttribute @ApiIgnore CategoryEntity category, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model){
		long count = categoryBiz.count(Wrappers.<CategoryEntity>lambdaQuery()
				.ne(StrUtil.isNotBlank(category.getId()), CategoryEntity::getId, category.getId())
				.eq(CategoryEntity::getCategoryPinyin, category.getCategoryPinyin()));

		if(count>0){
			return ResultData.build().error("存在相同拼音的栏目");
		}
		return ResultData.build().success();
	}

	@ApiOperation(value = "批量更新模版")
	@GetMapping("/updateTemplate")
	@ResponseBody
	@RequiresPermissions("cms:category:update")
	public ResultData updateTemplate(@ModelAttribute @ApiIgnore CategoryEntity category){
		if (category ==null || StringUtils.isEmpty(category.getId())) {
			return ResultData.build().error(getResString("err.error", this.getResString("id")));
		}
		category = categoryBiz.getById(category.getId());
		category.setCategoryParentIds(null);
		List<CategoryEntity> childs = categoryBiz.queryChildren(category);
		//更新与父节点相同类型的子栏目的模板内容
		for (int i =0; i < childs.size(); i++) {
			if (childs.get(i).getCategoryType().equals(category.getCategoryType())) {
				childs.get(i).setCategoryUrl(category.getCategoryUrl());
				childs.get(i).setCategoryListUrl(category.getCategoryListUrl());
				categoryBiz.updateEntity(childs.get(i));
			}
		}
		return ResultData.build().success();
	}

	@ApiOperation(value = "复制栏目")
	@GetMapping("/copyCategory")
	@ResponseBody
	@RequiresPermissions("cms:category:save")
	public ResultData copyCategory(@ModelAttribute @ApiIgnore CategoryEntity category){
		if (category == null || StringUtils.isEmpty(category.getId())) {
			return ResultData.build().error(getResString("err.error", this.getResString("id")));
		}
		categoryBiz.copyCategory(category);
		return ResultData.build().success();
	}

	/**
	 * 栏目预览
	 */
	@ApiIgnore
	@GetMapping("/view")
	@ResponseBody
	@RequiresPermissions("cms:category:view")
	public String view(HttpServletRequest req, HttpServletResponse resp){
		String templateName = BasicUtil.getString("style");
		AppEntity app = BasicUtil.getApp();
		List<Map> list = JSONUtil.toList(app.getAppStyles(),Map.class);
		String finalTemplateName = templateName;
		String style = "";
		List<Map<String, String>> styleList = new ArrayList<Map<String, String>>();
		list.stream().forEach(map -> {
			if(map.get("template").equals(finalTemplateName)) {
				styleList.add(map);
			}
		});

		if (CollUtil.isNotEmpty(styleList) && CollUtil.isNotEmpty(styleList.get(0))) {
			style = DictUtil.getDictValue("模板类型", styleList.get(0).get("name"));
		}
		if (StringUtils.isBlank(templateName)) {
			if (CollUtil.isNotEmpty(list)) {
				templateName = (String)list.get(0).get("template");
			}
		}
		int pageNo = BasicUtil.getInt("pageNo", 1);
		Map map = BasicUtil.assemblyRequestMap();


		map.forEach((k, v) -> {
			map.put(k, v.toString().replaceAll("('|\"|\\\\)", "\\\\$1"));
		});
		//获取栏目编号
		String typeId = BasicUtil.getString(ParserUtil.TYPE_ID, "");
		// 读取请求字段
		Map<String, Object> field = BasicUtil.assemblyRequestMap();
		// 设置发布到
		field.put("style", style);
		CategoryEntity category = categoryBiz.getById(typeId);
		ContentBean contentBean = new ContentBean();
		contentBean.setCategoryId(String.valueOf(typeId));
		contentBean.setCategoryType(CategoryTypeEnum.LIST.toString());
		contentBean.setProgressStatus(ProgressStatusEnum.APPROVED.toString());
		//获取文章总数
		List<CategoryBean> columnArticles = contentBiz.queryIdsByCategoryIdForParser(contentBean);
		//判断栏目下是否有文章

		// 模板路径
		String templateFolder = ParserUtil.buildTemplatePath(templateName);
		int size = ParserUtil.getPageSize(templateFolder,category.getCategoryListUrls(templateName), 20);
		//设置分页类
		PageBean page = new PageBean();
		page.setPageNo(BasicUtil.getPageNo());
		page.setSize(size);
		int total = PageUtil.totalPage(columnArticles.size(), size);

		// 设置模板名
		ParserUtil.putBaseParams(map,templateName);
		// 设置动态标识
		map.put(ParserUtil.IS_DO, true);

		CategoryBean categoryBean = new CategoryBean();
		if(columnArticles.size()>0) {
			categoryBean = columnArticles.get(0);
		}

		BeanUtil.copyProperties(category,categoryBean);
		map.put(ParserUtil.COLUMN, categoryBean);
		//标签中使用field获取当前栏目
		map.put(ParserUtil.FIELD, categoryBean);
		map.putAll(field);
		//获取总数
		page.setTotal(total);
		page.setRcount(columnArticles.size());


		//设置分页的统一链接
		String url = map.get(ParserUtil.URL).toString();
		if (pageNo >= total && total != 0) {
			pageNo = total;
		}

		url = url + req.getServletPath() + "?";
		String pageNoStr = "pageNo=";
		//下一页
		String nextUrl = url + pageNoStr + ((pageNo + 1 > total) ? total : pageNo + 1);
		//首页
		String indexUrl = url + pageNoStr + 1;
		//尾页
		String lastUrl = url + pageNoStr + total;
		//上一页 当前页为1时，上一页就是1
		String preUrl = url + pageNoStr + ((pageNo == 1) ? 1 : pageNo - 1);

		page.setIndexUrl(indexUrl);
		page.setNextUrl(nextUrl);
		page.setPreUrl(preUrl);
		page.setLastUrl(lastUrl);

		//设置栏目编号
		map.put(ParserUtil.TYPE_ID, typeId);

		map.put(ParserUtil.URL, BasicUtil.getUrl());
		map.put(ParserUtil.PAGE, page);

		map.put("modelName", "mcms");
		//解析后的内容
		String content = "";
		try {
			String templateContent = FileUtil.readString(FileUtil.file(templateFolder, category.getCategoryListUrls(templateName)), CharsetUtil.CHARSET_UTF_8);
			templateContent = ParserUtil.replaceTag(templateContent);
			Future<String> r = renderingService.rendering(map, templateFolder, category.getCategoryListUrl(), templateContent, null);
			content = r.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return content;
	}

	@ApiOperation(value = "强制转换类型接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "typeid", value = "编号", required =true,paramType="query"),
			@ApiImplicitParam(name = "categoryType", value = "栏目类型", required =true,paramType="query")
	})
	@GetMapping("/changeType")
	@ResponseBody
	@RequiresPermissions("cms:category:update")
	public ResultData changeType(){
		String typeId = BasicUtil.getString(ParserUtil.TYPE_ID);
		String categoryType = BasicUtil.getString("categoryType");
		CategoryEntity category = categoryBiz.getById(typeId);
		if (category == null){
			return ResultData.build().error(getResString("err.error",getResString("id")));
		}
		if (CategoryTypeEnum.get(categoryType).equals(CategoryTypeEnum.UN_KNOW)){
			return ResultData.build().error(getResString("err.error",getResString("category.type")));
		}
		categoryBiz.changeType(category,categoryType);
		return ResultData.build().success(category);
	}

}
