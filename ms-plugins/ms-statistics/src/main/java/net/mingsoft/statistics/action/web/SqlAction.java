/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.statistics.action.web;

import cn.hutool.core.collection.CollUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.statistics.bean.SqlBean;
import net.mingsoft.statistics.action.BaseAction;
import net.mingsoft.statistics.biz.ISqlBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 统计管理控制层
 * @author 铭软科技
 * 创建日期：2021-1-15 9:32:36<br/>
 * 历史修订：<br/>
 */
@Api(tags={"前端-统计模块接口"})
@Controller("webStatisticsSqlAction")
@RequestMapping("/statistics/sql")
public class SqlAction extends BaseAction {


	/**
	 * 注入统计业务层
	 */
	@Autowired
	private ISqlBiz sqlBiz;

	/**
	 * 查询统计列表
	 * 参数范例：[{"ssName":"文章数量","params":[106]}]
	 * 对应的SQL范例：SELECT count(0) as 'num' FROM cms_content where category_id={}
	 * @param sqls 统计实体
	 */
	@ApiOperation(value = "统计接口")
	@ApiImplicitParam(name = "sqls", value = "数组", dataType = "SqlBean",allowMultiple = true,required =true, paramType="body")
	@RequestMapping(value ="/list",method = {RequestMethod.POST})
	@ResponseBody
	public ResultData list(@RequestBody @ApiIgnore List<SqlBean> sqls) {
		if (CollUtil.isNotEmpty(sqls)) {
			return ResultData.build().success(sqlBiz.results(sqls));
		}
		return ResultData.build().error(getResString("err.empty",getResString("ss.name")));
	}
}
