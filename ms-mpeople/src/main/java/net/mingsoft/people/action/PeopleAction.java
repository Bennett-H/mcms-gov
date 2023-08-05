/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.action;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.people.biz.IPeopleBiz;
import net.mingsoft.people.constant.e.PeopleEnum;
import net.mingsoft.people.entity.PeopleUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户基础信息表管理控制层
 * @author 铭飞开发团队
 * @version 
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-会员模块接口"})
@Controller
@RequestMapping("/${ms.manager.path}/people")
public class PeopleAction extends BaseAction{
	
	/**
	 * 注入用户控制层
	 */
	@Autowired
	private IPeopleBiz peopleBiz;

	@ApiOperation(value = "更新用户状态接口")
    @ApiImplicitParam(name = "peoples", value = "所选用户", required = true,paramType="query")
	@LogAnn(title = "更新用户状态接口",businessType= BusinessTypeEnum.UPDATE)
	@PostMapping("/updateState")
    @ResponseBody
	public ResultData updateState(@RequestBody List<PeopleUserEntity> peoples, HttpServletRequest request, HttpServletResponse response){
		if(peoples.size() <= 0){
			return ResultData.build().error();
		}
		for(int i = 0;i < peoples.size(); i++){
			if(peoples.get(i).getPeopleState() == PeopleEnum.STATE_CHECK.toInt()){
				peoples.get(i).setPeopleStateEnum(PeopleEnum.STATE_NOT_CHECK);
			}else{
				peoples.get(i).setPeopleStateEnum(PeopleEnum.STATE_CHECK);
			}
			this.peopleBiz.updateEntity(peoples.get(i));
		}
		return ResultData.build().success();
	}
}
