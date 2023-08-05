/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.people.action;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.FileUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mdiy.biz.IModelBiz;
import net.mingsoft.mdiy.entity.ModelEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.people.bean.PeopleBean;
import net.mingsoft.people.biz.IPeopleBiz;
import net.mingsoft.people.biz.IPeopleUserBiz;
import net.mingsoft.people.constant.e.PeopleEnum;
import net.mingsoft.people.entity.PeopleEntity;
import net.mingsoft.people.entity.PeopleUserEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户基础信息表管理控制层
 * @author 伍晶晶
 * @version
 * 版本号：0.0<br/>
 * 创建日期：2017-8-23 10:10:22<br/>
 * 历史修订：修改list接口，过滤掉未审核的用户数据<br/>
 */
@Api(tags={"后端-会员模块接口"})
@Controller
@RequestMapping("/${ms.manager.path}/people/peopleUser")
public class PeopleUserAction extends BaseAction{

	/**
	 * 注入用户基础信息表业务层
	 */
	@Autowired
	private IPeopleUserBiz peopleUserBiz;

	/**
	 * 注入配置模型业务层
	 */
	@Autowired
	private IModelBiz modelBiz;


	/**
	 * 注入用户基础信息控制层
	 */
	@Autowired
	private IPeopleBiz peopleBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("people:user:view")
	public String index(HttpServletResponse response,HttpServletRequest request){
		return "/people/user/index";
	}



    @ApiOperation(value = "获取用户基础详情接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "peopleId", value = "用户ID关联people表的（people_id）", required = true,paramType="query"),
    })
    @GetMapping("/get")
    @ResponseBody
	@RequiresPermissions("people:user:view")
    public ResultData get(@ModelAttribute @ApiIgnore PeopleUserEntity peopleUser, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model){
        if(peopleUser.getPeopleId()<=0) {
            return ResultData.build().error(getResString("err.error", this.getResString("pu.people.id")));
        }
        PeopleUserEntity _peopleUser = (PeopleUserEntity)peopleUserBiz.getEntity(peopleUser.getPeopleId());
        return ResultData.build().success( _peopleUser);
    }

	@ApiOperation(value = "用户基础信息列表接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "peopleName", value = "用户账号", required =false,paramType="query"),
    	@ApiImplicitParam(name = "puNickname", value = "用户昵称", required = false,paramType="query"),
    	@ApiImplicitParam(name = "peopleState", value = "审核状态", required = false,paramType="query"),
    	@ApiImplicitParam(name = "puRealName", value = "真实姓名", required = false,paramType="query"),
    	@ApiImplicitParam(name = "peopleDateTimes", value = "注册时间范围，如2019-01-23至2019-01-24", required = false,paramType="query"),
    	@ApiImplicitParam(name = "peopleId", value = "用户ID关联people表的（people_id）", required = false,paramType="query"),
    	@ApiImplicitParam(name = "puAddress", value = "用户地址", required = false,paramType="query"),
    	@ApiImplicitParam(name = "puIcon", value = "用户头像图标地址", required = false,paramType="query"),
    	@ApiImplicitParam(name = "puSex", value = "用户性别(0.未知、1.男、2.女)", required = false,paramType="query"),
    	@ApiImplicitParam(name = "puBirthday", value = "用户出生年月日", required = false,paramType="query"),
    	@ApiImplicitParam(name = "puCard", value = "身份证", required = false,paramType="query"),
    	@ApiImplicitParam(name = "puAppId", value = "用户所属应用ID", required = false,paramType="query"),
    	@ApiImplicitParam(name = "puProvince", value = "省", required = false,paramType="query"),
    	@ApiImplicitParam(name = "puCity", value = "城市", required = false,paramType="query"),
    	@ApiImplicitParam(name = "puDistrict", value = "区", required = false,paramType="query"),
    	@ApiImplicitParam(name = "puStreet", value = "街道", required = false,paramType="query")

    })
	@GetMapping("/auditList")
	@ResponseBody
	@RequiresPermissions("people:user:view")
	public ResultData auditList(@ModelAttribute @ApiIgnore PeopleBean peopleUser, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
		if(peopleUser == null){
			peopleUser = new PeopleBean();
		}
		peopleUser.setPeopleStateEnum(PeopleEnum.STATE_CHECK);
		BasicUtil.startPage();
		List<PeopleBean> peopleUserList = peopleUserBiz.query(peopleUser);
		EUListBean list = new EUListBean(peopleUserList,(int) BasicUtil.endPage(peopleUserList).getTotal());
		return ResultData.build().success( list);
	}

	@ApiOperation(value = "查询未审核的用户列表接口")
	@GetMapping("/unAuditList")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "peopleName", value = "用户账号", required =false,paramType="query"),
			@ApiImplicitParam(name = "puNickname", value = "用户昵称", required = false,paramType="query"),
			@ApiImplicitParam(name = "peopleState", value = "审核状态", required = false,paramType="query"),
			@ApiImplicitParam(name = "puRealName", value = "真实姓名", required = false,paramType="query"),
			@ApiImplicitParam(name = "peopleDateTimes", value = "注册时间范围，如2019-01-23至2019-01-24", required = false,paramType="query"),
			@ApiImplicitParam(name = "peopleId", value = "用户ID关联people表的（people_id）", required = false,paramType="query"),
			@ApiImplicitParam(name = "puAddress", value = "用户地址", required = false,paramType="query"),
			@ApiImplicitParam(name = "puIcon", value = "用户头像图标地址", required = false,paramType="query"),
			@ApiImplicitParam(name = "puSex", value = "用户性别(0.未知、1.男、2.女)", required = false,paramType="query"),
			@ApiImplicitParam(name = "puBirthday", value = "用户出生年月日", required = false,paramType="query"),
			@ApiImplicitParam(name = "puCard", value = "身份证", required = false,paramType="query"),
			@ApiImplicitParam(name = "puAppId", value = "用户所属应用ID", required = false,paramType="query"),
			@ApiImplicitParam(name = "puProvince", value = "省", required = false,paramType="query"),
			@ApiImplicitParam(name = "puCity", value = "城市", required = false,paramType="query"),
			@ApiImplicitParam(name = "puDistrict", value = "区", required = false,paramType="query"),
			@ApiImplicitParam(name = "puStreet", value = "街道", required = false,paramType="query")
	})
	@ResponseBody
	@RequiresPermissions("people:view")
	public ResultData unAuditList(@ModelAttribute @ApiIgnore PeopleBean peopleUser, HttpServletResponse response, HttpServletRequest request, @ApiIgnore ModelMap model) {
		if(peopleUser == null){
			peopleUser = new PeopleBean();
		}
		peopleUser.setPeopleStateEnum(PeopleEnum.STATE_NOT_CHECK);
		BasicUtil.startPage();
		List<PeopleBean> peopleUserList = peopleUserBiz.query(peopleUser);

		EUListBean list = new EUListBean(peopleUserList,(int) BasicUtil.endPage(peopleUserList).getTotal());
//		list = BasicUtil.filter(list,"peopleOldPassword","peoplePassword","del");
		return ResultData.build().success(list);
	}

	/**
	 * 返回编辑界面peopleUser_form
	 */
	@ApiIgnore
	@GetMapping("/form")
	@RequiresPermissions(value = {"people:user:save", "people:user:update"}, logical = Logical.OR)
	public String form(@ModelAttribute PeopleUserEntity peopleUser, HttpServletResponse response, HttpServletRequest request, ModelMap model){
		if(peopleUser.getPeopleId() != null){
			BaseEntity peopleUserEntity = peopleUserBiz.getEntity(peopleUser.getPeopleId());
			model.addAttribute("peopleUserEntity",peopleUserEntity);
		}
		return "/people/user/form";
	}

	@ApiOperation(value = "保存用户基础信息接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "peopleName", value = "登录账号", required = true,paramType="query"),
		@ApiImplicitParam(name = "peoplePassword", value = "用户密码", required = true,paramType="query"),
        @ApiImplicitParam(name = "peopleId", value = "用户ID关联people表的（people_id）", required = false,paramType="query"),
        @ApiImplicitParam(name = "puRealName", value = "用户真实名称", required = false,paramType="query"),
        @ApiImplicitParam(name = "puAddress", value = "用户地址", required = false,paramType="query"),
        @ApiImplicitParam(name = "puIcon", value = "用户头像图标地址", required = false,paramType="query"),
        @ApiImplicitParam(name = "puNickname", value = "用户昵称", required = false,paramType="query"),
        @ApiImplicitParam(name = "puSex", value = "用户性别(0.未知、1.男、2.女)", required = false,paramType="query"),
        @ApiImplicitParam(name = "puBirthday", value = "用户出生年月日", required = false,paramType="query"),
        @ApiImplicitParam(name = "puCard", value = "身份证", required = false,paramType="query"),
        @ApiImplicitParam(name = "puAppId", value = "用户所属应用ID", required = false,paramType="query"),
        @ApiImplicitParam(name = "puProvince", value = "省", required = false,paramType="query"),
        @ApiImplicitParam(name = "puCity", value = "城市", required = false,paramType="query"),
        @ApiImplicitParam(name = "puDistrict", value = "区", required = false,paramType="query"),
        @ApiImplicitParam(name = "puStreet", value = "街道", required = false,paramType="query")
    })
	@LogAnn(title = "保存用户基础信息接口",businessType= BusinessTypeEnum.INSERT)
	@PostMapping("/save")
	@ResponseBody
	@RequiresPermissions("people:user:save")
	public ResultData save(@ModelAttribute @ApiIgnore PeopleUserEntity peopleUser, HttpServletResponse response, HttpServletRequest request) {
		//验证用户真实名称的值是否合法
		if(!StringUtil.checkLength(peopleUser.getPuRealName()+"", 0, 50)){
			return ResultData.build().error(getResString("err.length", this.getResString("pu.real.name"), "0", "50"));
		}
		if(!StringUtil.checkLength(peopleUser.getPuAddress()+"", 0, 200)){
			return ResultData.build().error(getResString("err.length", this.getResString("pu.address"), "0", "200"));
		}
		if(!StringUtil.checkLength(peopleUser.getPuIcon()+"", 0, 200)){
			return ResultData.build().error(getResString("err.length", this.getResString("pu.icon"), "0", "200"));
		}
		if(!StringUtil.checkLength(peopleUser.getPuNickname()+"", 0, 50)){
			return ResultData.build().error(getResString("err.length", this.getResString("pu.nickname"), "0", "50"));
		}
		//验证用户性别(0.未知、1.男、2.女)的值是否合法
		if(!StringUtil.checkLength(peopleUser.getPuSex()+"", 0, 10)){
			return ResultData.build().error(getResString("err.length", this.getResString("pu.sex"), "0", "10"));
		}
		//验证身份证的值是否合法
		if(!StringUtil.checkLength(peopleUser.getPuCard()+"", 0, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("pu.card"), "0", "255"));
		}
		if(StringUtils.isNotBlank(peopleUser.getPeoplePassword()) && StringUtils.isNotBlank(peopleUser.getPeopleName())){
			//设置用户密码
			peopleUser.setPeoplePassword(SecureUtil.md5(peopleUser.getPeoplePassword()));
		}
		// 手机号不为空则标记为通过
		if (StringUtils.isNotBlank(peopleUser.getPeoplePhone())){
			peopleUser.setPeoplePhoneCheck(PeopleEnum.PHONE_CHECK);
		}
		// 邮箱不为空则标记为通过
		if (StringUtils.isNotBlank(peopleUser.getPeopleMail())){
			peopleUser.setPeopleMailCheck(PeopleEnum.MAIL_CHECK);
		}
		// 验证用户输入的信息是否合法
		if(!peopleUserBiz.isExists(peopleUser)){
			return ResultData.build().error("用户输入信息不合法");
		}
		// 验证是否开启了 审核功能
		String peopleState = ConfigUtil.getString("会员配置", "peopleState");
		if ("true".equalsIgnoreCase(peopleState)){
			peopleUser.setPeopleState(0); // 开启
		}
		else {
			peopleUser.setPeopleState(1); // 关闭
		}

		peopleUser.setPeopleDateTime(new Date());
		peopleUserBiz.savePeople(peopleUser);

		return ResultData.build().success("保存成功,记得审核!",peopleUser);
	}

	@ApiOperation(value = "批量删除用户接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "peopleId", value = "多个puPeopleId直接用逗号隔开,例如puPeopleId=1,2,3,4", required = true,paramType="query")
    })
	@LogAnn(title = "批量删除用户接口",businessType= BusinessTypeEnum.DELETE)
	@PostMapping("/delete")
	@ResponseBody
	@RequiresPermissions("people:user:del")
	public ResultData delete(@RequestBody List<PeopleUserEntity> peopleUsers, HttpServletResponse response, HttpServletRequest request) {
		int[] ids = new int[peopleUsers.size()];
		QueryWrapper<ModelEntity> queryWrapper = new QueryWrapper<ModelEntity>();
		queryWrapper.eq("model_type", "people");
		queryWrapper.eq("model_name", "扩展会员信息");
		ModelEntity model = modelBiz.getOne(queryWrapper);

		for(int i = 0;i<peopleUsers.size();i++){
			ids[i] = peopleUsers.get(i).getPeopleId();
			Map<String, String> map = new HashMap<>();
			if(model!=null) {
				map.put("link_id", ids[i]+"");
				peopleUserBiz.deleteBySQL(model.getModelTableName(), map);
			}
		}
		FileUtil.del(peopleUsers);
		peopleUserBiz.deletePeople(ids);
		return ResultData.build().success();

	}

	@ApiOperation(value = "修改用户基础信息接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "peoplePassword", value = "用户密码", required = false,paramType="query"),
        @ApiImplicitParam(name = "puRealName", value = "用户真实名称", required = false,paramType="query"),
        @ApiImplicitParam(name = "puAddress", value = "用户地址", required = false,paramType="query"),
        @ApiImplicitParam(name = "puIcon", value = "用户头像图标地址", required = false,paramType="query"),
        @ApiImplicitParam(name = "puNickname", value = "用户昵称", required = false,paramType="query"),
        @ApiImplicitParam(name = "puSex", value = "用户性别(0.未知、1.男、2.女)", required = false,paramType="query"),
        @ApiImplicitParam(name = "puBirthday", value = "用户出生年月日", required = false,paramType="query"),
        @ApiImplicitParam(name = "puCard", value = "身份证", required = false,paramType="query"),
        @ApiImplicitParam(name = "puAppId", value = "用户所属应用ID", required = false,paramType="query"),
        @ApiImplicitParam(name = "puProvince", value = "省", required = false,paramType="query"),
        @ApiImplicitParam(name = "puCity", value = "城市", required = false,paramType="query"),
        @ApiImplicitParam(name = "puDistrict", value = "区", required = false,paramType="query"),
        @ApiImplicitParam(name = "puStreet", value = "街道", required = false,paramType="query")
    })
	@LogAnn(title = "修改用户基础信息接口",businessType= BusinessTypeEnum.UPDATE)
	@PostMapping("/update")
	@ResponseBody
	@RequiresPermissions("people:user:update")
	public ResultData update(@ModelAttribute @ApiIgnore PeopleUserEntity peopleUser, HttpServletResponse response,
                             HttpServletRequest request) {
		if(!StringUtil.checkLength(peopleUser.getPuRealName()+"", 0, 50)){
			return ResultData.build().error(getResString("err.length", this.getResString("pu.real.name"), "0", "50"));
		}
		if(!StringUtil.checkLength(peopleUser.getPuAddress()+"", 0, 200)){
			return ResultData.build().error(getResString("err.length", this.getResString("pu.address"), "0", "200"));
		}
		if(!StringUtil.checkLength(peopleUser.getPuIcon()+"", 0, 200)){
			return ResultData.build().error(getResString("err.length", this.getResString("pu.icon"), "0", "200"));
		}
		if(!StringUtil.checkLength(peopleUser.getPuNickname()+"", 0, 50)){
			return ResultData.build().error(getResString("err.length", this.getResString("pu.nickname"), "0", "50"));
		}
		//验证用户性别(0.未知、1.男、2.女)的值是否合法
		if(!StringUtil.checkLength(peopleUser.getPuSex()+"", 0, 10)){
			return ResultData.build().error(getResString("err.length", this.getResString("pu.sex"), "0", "10"));
		}
		//验证身份证的值是否合法
		if(!StringUtil.checkLength(peopleUser.getPuCard()+"", 0, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("pu.card"), "0", "255"));
		}
		// 手机号不为空则标记为通过
		if (StringUtils.isNotBlank(peopleUser.getPeoplePhone())){
			peopleUser.setPeoplePhoneCheck(PeopleEnum.PHONE_CHECK);
		}
		// 邮箱不为空则标记为通过
		if (StringUtils.isNotBlank(peopleUser.getPeopleMail())){
			peopleUser.setPeopleMailCheck(PeopleEnum.MAIL_CHECK);
		}
		// 验证用户输入的信息是否合法
		if(!peopleUserBiz.isExists(peopleUser)){
			return ResultData.build().error("用户输入信息不合法");
		}
		//判断用户密码是否为空，如果不为空则进行密码的更新
		if(!StringUtils.isBlank(peopleUser.getPeoplePassword())){
			//设置用户密码
			peopleUser.setPeoplePassword(SecureUtil.md5(peopleUser.getPeoplePassword()));
		}
		peopleUser.setId(peopleUser.getPeopleId() + "");
		peopleUserBiz.updatePeople(peopleUser);
		return ResultData.build().success(peopleUser);
	}


    @ApiOperation(value = "获取用户详细信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "peopleId", value = "用户id", required =true,paramType="query")
    })
	@Deprecated
	@GetMapping("/getEntity")
    @ResponseBody
	@RequiresPermissions("people:user:view")
	public ResultData getEntity(String peopleId,HttpServletRequest request,HttpServletResponse response){
		if(StringUtils.isBlank(peopleId) || !StringUtil.isInteger(peopleId)){
			return ResultData.build().error();
		}
		PeopleUserEntity peopleUser = (PeopleUserEntity) this.peopleUserBiz.getEntity(Integer.parseInt(peopleId));
		if(peopleUser == null){
			return ResultData.build().error();
		}
		return ResultData.build().success( peopleUser);
	}

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/audit")
	@RequiresPermissions("people:view")
	public String indexAudit(HttpServletResponse response,HttpServletRequest request){
		return "/people/user/audit";
	}
}
