/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.mweixin.action;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mweixin.biz.IWeixinPeopleBiz;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.entity.WeixinPeopleEntity;
import net.mingsoft.mweixin.service.PortalService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 微信用户控制层
 * @author 铭飞开发团队
 * @version
 * 版本号：100<br/>
 * 创建日期：2017-11-18 11:23:59<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-微信模块接口"})
@Controller("netWeixinPeopleAction")
@RequestMapping("/${ms.manager.path}/mweixin/weixinPeople")
public class WeixinPeopleAction extends BaseAction{

	/**
	 * 注入微信用户业务层
	 */
	@Resource(name="netWeixinPeopleBiz")
	private IWeixinPeopleBiz weixinPeopleBiz;

	@Autowired
	private PortalService weixinService;


	/**
	 * 微信用户管理主界面
	 * @param request
	 * @param response
	 * @return 页面
	 */
	@GetMapping("/index")
	@ApiIgnore
	@RequiresPermissions("weixin:people:view")
	public String index(HttpServletRequest request, HttpServletResponse response){
		return "/mweixin/people/index";
	}

	/**
	 * 分页查询所有的微信用户信息
	 * @param request
	 * @return 微信用户列表
	 */
	@ApiOperation(value="分页查询所有的微信用户信息")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "weixinPeopleWeixinId", value = "关联微信Id", required =false,paramType="query"),
    	@ApiImplicitParam(name = "weixinPeopleOpenId", value = "用户在微信中的唯一标识", required =false,paramType="query"),
    	@ApiImplicitParam(name = "weixinPeopleProvince", value = "用户所在省份", required =false,paramType="query"),
    	@ApiImplicitParam(name = "weixinPeopleCity", value = "用户所在城市", required = false,paramType="query"),
    	@ApiImplicitParam(name = "weixinPeopleState", value = "用户关注状态1.关注中用户(默认)2.取消关注用户", required = false,paramType="query"),
    })
	@GetMapping("/list")
	@ResponseBody
	@RequiresPermissions("weixin:people:view")
	public ResultData list(WeixinPeopleEntity weixinPeople, HttpServletRequest request){
		//取出微信实体
		WeixinEntity weixin = this.getWeixinSession(request);
		weixinPeople.setWeixinId(weixin.getIntId());
		//分页开始
		BasicUtil.startPage();
		//分页查询
		List listPeople = weixinPeopleBiz.query(weixinPeople);
		EUListBean _list = new EUListBean(listPeople,(int) BasicUtil.endPage(listPeople).getTotal());
		return ResultData.build().success(_list);

	}
	/**
	 * 导入微信所有用户
	 * @param request
	 */
	@ApiOperation(value="导入微信所有用户")
	@LogAnn(title = "导入微信所有用户",businessType= BusinessTypeEnum.OTHER)
	@GetMapping("/importPeople")
	@ResponseBody
	@RequiresPermissions("weixin:people:sync")
	public ResultData importPeople(HttpServletRequest request,HttpServletResponse response){
		//取出微信实体
		WeixinEntity weixin = this.getWeixinSession(request);
		//进行同步
		return ResultData.build().success(getWeixinPeople(weixin,null));
	}

	/**
	 * 同步用户数据，如果已经存在，那么进行更新，否则进行保存
	 * @param weixin 微信实体
	 * @param nextOpenId 下一个用户的微信唯一编号（表示从下一个用户再次获取数据）
	 * @return
	 */
	private boolean getWeixinPeople(WeixinEntity weixin,String nextOpenId){
		//拿到微信工具类服务
		weixinService = weixinService.build(weixin);
		//获取用户数据
		try {
			WxMpUserList wxUsers = weixinService.getUserService().userList(nextOpenId);
			//储蓄转化后的用户信息
			List<String> openIds = wxUsers.getOpenids();
			for(String openid : openIds){
				//通过openId拿到用户信息
				WxMpUser user = weixinService.getUserService().userInfo(openid,"zh_CN");
				weixinPeopleBiz.saveOrUpdate(user,weixin.getIntId());
			}
			//如果没有下一个，那么返回成功信息
			if(wxUsers.getNextOpenid().length()>0){
				getWeixinPeople(weixin, wxUsers.getNextOpenid());
				return true;
			}

		} catch (WxErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return false;
	}

}
