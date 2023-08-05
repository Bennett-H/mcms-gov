/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.cms.action.people;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.cms.action.BaseAction;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.constant.e.CategoryTypeEnum;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.datascope.utils.DataScopeUtil;
import net.mingsoft.people.biz.IPeopleUserBiz;
import net.mingsoft.people.constant.e.SessionConstEnum;
import net.mingsoft.people.entity.PeopleEntity;
import net.mingsoft.people.entity.PeopleUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 分类管理控制层
 * @author 铭飞开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：增加分类列表<br/>
 */
@Api(tags = {"前端-用户-栏目接口"})
@Controller("peoplecmsCategoryAction")
@RequestMapping("people/cms/category")
public class CategoryAction extends BaseAction {


	/**
	 * 注入分类业务层
	 */
	@Autowired
	private ICategoryBiz categoryBiz;

	@Autowired
	private IPeopleUserBiz peopleUserBiz;

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
    	@ApiImplicitParam(name = "categoryKeyword", value = "栏目管理关键字", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryDescrip", value = "栏目管理描述", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryDiyUrl", value = "自定义链接", required =false,paramType="query"),
    	@ApiImplicitParam(name = "mdiyModelId", value = "栏目管理的内容模型id", required =false,paramType="query"),
    	@ApiImplicitParam(name = "dictId", value = "字典对应编号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryFlag", value = "栏目属性", required =false,paramType="query"),
    	@ApiImplicitParam(name = "categoryParentId", value = "父类型编号", required =false,paramType="query"),
    })
	@RequestMapping(value = "/list", method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore CategoryEntity category, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
		PeopleEntity peopleEntity = (PeopleEntity) BasicUtil.getSession(SessionConstEnum.PEOPLE_SESSION);
		PeopleUserEntity peopleUserEntity = (PeopleUserEntity) peopleUserBiz.getEntity(peopleEntity.getIntegerId());
		BasicUtil.startPage();
		DataScopeUtil.start(peopleEntity.getId(),peopleUserEntity.getPuLevel(),"会员投稿栏目权限",true);
		//只显示栏目类型为列表类型的数据
		category.setCategoryType(CategoryTypeEnum.LIST.toString());
		List categoryList = categoryBiz.query(category);
		return ResultData.build().success(new EUListBean(categoryList,(int) BasicUtil.endPage(categoryList).getTotal()));
	}

}
