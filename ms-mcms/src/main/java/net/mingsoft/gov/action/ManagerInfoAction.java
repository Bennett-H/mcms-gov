/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.gov.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.gov.biz.IManagerInfoBiz;
import net.mingsoft.gov.entity.ManagerInfoEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 管理员扩展信息管理控制层
 * @author 铭飞科技
 * 创建日期：2022-5-25 16:03:06<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-基础接口"})
@Controller("govManagerInfoAction")
@RequestMapping("/${ms.manager.path}/gov/managerInfo")
public class ManagerInfoAction extends net.mingsoft.gov.action.BaseAction{


	/**
	 * 注入管理员扩展信息业务层
	 */
	@Autowired
	private IManagerInfoBiz managerInfoBiz;

	/**
	 * 注入管理员扩展信息业务层
	 */
	@Autowired
	private IManagerBiz managerBiz;



	@ApiOperation(value = "保存或更新手机号")
	@PostMapping("/saveOrUpdatePhone")
	@ApiImplicitParam(name = "managerPhone", value = "手机号", required = true, paramType = "query")
	@ResponseBody
	public ResultData saveOrUpdatePhone(String managerPhone) {
		if (StringUtils.isBlank(managerPhone)){
			return ResultData.build().error("手机号为空!");
		}

		ManagerEntity manager = BasicUtil.getManager();
		ManagerInfoEntity managerInfo = managerInfoBiz.getOne(new LambdaQueryWrapper<ManagerInfoEntity>().eq(ManagerInfoEntity::getManagerId, manager.getId()));
		if (managerInfo == null){
			managerInfo = new ManagerInfoEntity();
			managerInfo.setManagerId(manager.getId());
			managerInfo.setManagerPhone(managerPhone);
			managerInfoBiz.save(managerInfo);
		}else {
			managerInfo.setManagerPhone(managerPhone);
			managerInfoBiz.updateById(managerInfo);
		}

		return ResultData.build().success(managerInfo);
	}

	/**
	 * 获取当前管理员扩展信息
	 */
	@ApiOperation(value = "获取当前管理员扩展信息列表接口")
	@GetMapping("/getManagerInfo")
	@ResponseBody
	public ResultData getManagerInfo(HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model) {

		ManagerEntity manager = BasicUtil.getManager();
		ManagerInfoEntity managerInfo = managerInfoBiz.getOne(new LambdaQueryWrapper<ManagerInfoEntity>().eq(ManagerInfoEntity::getManagerId, manager.getId()));
		return ResultData.build().success(managerInfo);
	}


}
