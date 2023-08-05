/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.co.action;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.co.bean.CategoryDataBean;
import net.mingsoft.co.biz.IDataBiz;
import net.mingsoft.datascope.biz.IDataConfigBiz;
import net.mingsoft.datascope.entity.DataConfigEntity;
import net.mingsoft.datascope.entity.DataEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 分类管理控制层
 * @author 铭软开发团队
 * 创建日期：2019-11-28 15:12:32<br/>
 * 历史修订：增加管理员栏目权限处理<br/>
 */
@Api(tags={"后端-企业模块接口"})
@Controller("coCmsCategoryAction")
@RequestMapping("/${ms.manager.path}/cms/co/category")
public class CategoryAction extends BaseAction {

	@Autowired
	private IDataBiz dataBiz;

	@Autowired
	private IDataBiz coDataBiz;

	@Autowired
	private IDataConfigBiz dataConfigBiz;
	/**
	 * 返回权限控制
	 */
	@ApiIgnore
	@GetMapping("/auth")
	@RequiresPermissions("datascope:data:view")
	public String auth(HttpServletResponse response,HttpServletRequest request){
		return "/cms/category/auth";
	}


	@ApiOperation(value = "查询分类权限列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dataTargetId", value = "目标编号", required =true,paramType="query"),
	})
	@RequestMapping(value="/categoryList",method = {RequestMethod.GET, RequestMethod.POST})
	@RequiresPermissions("datascope:data:view")
	@ResponseBody
	public ResultData categoryList(@ModelAttribute @ApiIgnore DataEntity data, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model, BindingResult result) {
		List<CategoryDataBean> categoryDataBeans = coDataBiz.categoryDataList(data);
		return ResultData.build().success(categoryDataBeans);
	}

	/**
	 * 批量保存数据权限
	 * @param datas 数据权限实体
	 */
	@ApiOperation(value = "批量保存数据权限接口")
	@PostMapping("/saveBatch")
	@ResponseBody
	@LogAnn(title = "保存数据权限", businessType = BusinessTypeEnum.INSERT)
	@RequiresPermissions("datascope:data:save")
	public ResultData saveBatch(@RequestBody List<CategoryDataBean> datas, HttpServletResponse response, HttpServletRequest request) {

		if(StringUtils.isBlank(datas.get(0).getDataType())) {
			this.LOG.debug("没有设置数据权限名称");
			throw  new BusinessException(this.getResString("err"));

		}

		if (CollUtil.isNotEmpty(datas)) {
			// 根据名称查询配置表的id
			DataConfigEntity dataConfigEntity = new DataConfigEntity();
			dataConfigEntity.setConfigName(datas.get(0).getDataType());
			DataConfigEntity one = dataConfigBiz.getOne(new QueryWrapper<>(dataConfigEntity), false);

			if(one == null) {
				this.LOG.debug("没有设置数据权限名称");
				throw  new BusinessException(this.getResString("err"));

			}

			dataBiz.saveToBatch(datas, BasicUtil.getManager().getId(), one.getId());

		}
		return ResultData.build().success();
	}
}
