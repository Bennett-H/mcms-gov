/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.statistics.action.web;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.annotation.LogAnn;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.IpUtils;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.statistics.action.BaseAction;
import net.mingsoft.statistics.biz.IAccessStatisticsBiz;
import net.mingsoft.statistics.constant.Const;
import net.mingsoft.statistics.entity.AccessStatisticsEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 访问统计管理控制层
 * @author 铭飞科技
 * 创建日期：2022-4-2 14:07:51<br/>
 * 历史修订：<br/>
 */
@Api(tags={"前端-统计模块接口"})
@Controller("webAccessStatisticsAction")
@RequestMapping("/statistics/accessStatistics")
public class AccessStatisticsAction extends BaseAction {


	/**
	 * 注入访问统计业务层
	 */
	@Autowired
	private IAccessStatisticsBiz accessStatisticsBiz;

	/**
	 * 网站访问统计入口
	 */
	@ApiOperation(value = "网站访问统计入口")
	@RequestMapping(value = "/in",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public void in(String url,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model) {
		AccessStatisticsEntity entity = new AccessStatisticsEntity();
		String ip = BasicUtil.getIp();
		//浏览器标识
		String agent= request.getHeader("user-agent");
		//访问设备
		String deviceType = BasicUtil.isMobileDevice() ? "phone":"pc";
		//根据ip查询用户地址
		String address = IpUtils.getRealAddressByIp(ip);

		//浏览器cookie
		String cookie = "";

		Cookie[] cookies = request.getCookies();
		if (cookies != null){
			//获取cookie
			for (Cookie requestCookie : cookies) {
				if (requestCookie.getName().equalsIgnoreCase(Const.COOKIE_NAME)){
					cookie = requestCookie.getValue();
				}
			}
		}

		//没有cookie,重新创建一个
		if (StringUtils.isBlank(cookie)){
			Cookie userCookie = new Cookie("user", IdUtil.objectId());  // 创建cookie对象
			userCookie.setMaxAge(Integer.MAX_VALUE);
			response.addCookie(userCookie);
			cookie = userCookie.getValue();
		}


		Date time = new Date();
		entity.setIp(ip);
		entity.setAddress(address);
		entity.setCookie(cookie);
		entity.setUrl(url);
		entity.setUserAgent(agent);
		entity.setDeviceType(deviceType);
		entity.setCreateDate(time);
		//默认设置浏览一秒钟
		entity.setAccessTime(1L);
		entity.setEndViewTime(DateUtil.offsetMinute(time,1));

		AccessStatisticsEntity pre = accessStatisticsBiz.getOne(new LambdaQueryWrapper<AccessStatisticsEntity>().eq(AccessStatisticsEntity::getIp, ip).orderByDesc(AccessStatisticsEntity::getCreateDate), false);
		if (pre != null){
			//不是第一次访问,计算上次的访问浏览时长
			Date date = new Date();
			Date createDate = pre.getCreateDate();
			Long viewTime = (date.getTime() - createDate.getTime())/1000;
			//小于一个小时才计算
			if (viewTime < 1000*60*60){
				pre.setAccessTime(viewTime);
				pre.setEndViewTime(date);
				accessStatisticsBiz.updateById(pre);
			}
		}
		accessStatisticsBiz.save(entity);
	}



}
