/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.statistics.action;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.statistics.biz.IAccessStatisticsBiz;
import net.mingsoft.statistics.entity.AccessStatisticsEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * 网站访问统计管理控制层
 * @author 铭飞科技
 * 创建日期：2022-4-2 14:07:51<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-统计模块接口"})
@Controller("AccessStatisticsAction")
@RequestMapping("/${ms.manager.path}/statistics/accessStatistics")
public class AccessStatisticsAction extends BaseAction{


	/**
	 * 注入访问统计业务层
	 */
	@Autowired
	private IAccessStatisticsBiz accessStatisticsBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("statistics:access:view")
	public String index(HttpServletResponse response,HttpServletRequest request) {
		return "/statistics/access-statistics/index";
	}


	/**
	 * 查询访问统计列表
	 * @param accessStatistics 访问统计实体
	 */
	@ApiOperation(value = "查询访问统计列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "accessTime", value = "浏览时长", paramType = "query"),
			@ApiImplicitParam(name = "userAgent", value = "浏览器标识", paramType = "query"),
			@ApiImplicitParam(name = "deviceType", value = "设备类型", paramType = "query"),
			@ApiImplicitParam(name = "url", value = "访问链接", paramType = "query"),
			@ApiImplicitParam(name = "ip", value = "用户ip", paramType = "query"),
    })
	@RequestMapping(value ="/list",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@RequiresPermissions("statistics:access:view")
	public ResultData list(@ModelAttribute @ApiIgnore AccessStatisticsEntity accessStatistics,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model,BindingResult result) {
		LambdaQueryWrapper<AccessStatisticsEntity> wrapper = new QueryWrapper<AccessStatisticsEntity>().lambda();
		wrapper.orderByAsc(AccessStatisticsEntity::getCreateDate);
		List<AccessStatisticsEntity> accessStatisticsList = accessStatisticsBiz.list(wrapper);
		Map<String, List<AccessStatisticsEntity>> map = accessStatisticsList.stream().collect(Collectors.groupingBy(AccessStatisticsEntity::getIp));
		List<Map<String, Object>> resultList = new ArrayList<>();
		for (String ip : map.keySet()) {
			Map<String, Object> resultMap = new HashMap<>(4);
			// 平均浏览时间
			long time = map.get(ip).stream().mapToLong(AccessStatisticsEntity::getAccessTime).sum() / map.get(ip).size();
			resultMap.put("ip", ip);
			// 访问量
			resultMap.put("pv", map.get(ip).size());
			resultMap.put("address", map.get(ip).get(0).getAddress());
			resultMap.put("url", map.get(ip).get(0).getUrl());
			resultMap.put("time", formatTime(time));
			resultList.add(resultMap);
		}
		// 分页
		int pageNo = BasicUtil.getPageNo();
		int pageSize = BasicUtil.getPageSize();
		int totalSize = resultList.size();
		List<Map<String, Object>> managerResultList = resultList.stream().skip((long) (pageNo - 1) * pageSize).
				limit(pageSize).collect(Collectors.toList());
		return ResultData.build().success(new EUListBean(managerResultList, totalSize));
	}

	/**
	 * 将秒格式化成 HH:mm:ss 格式，不满两位补零
	 *
	 * @param seconds
	 * @return
	 */
	public static String formatTime(long seconds) {
		String time;
		if (seconds >= 3600) {
			long hour = seconds / 3600;
			long minute = seconds % 3600 / 60;
			long second = seconds % 3600 % 60;
			time = StrUtil.format("{}:{}:{}",
					hour < 10 ? "0" + hour : hour,
					minute < 10 ? "0" + minute : minute,
					second < 10 ? "0" + second : second);
		} else if (seconds >= 60) {
			long minute = seconds / 60;
			long second = seconds % 60;
			time = StrUtil.format("00:{}:{}",
					minute < 10 ? "0" + minute : minute,
					second < 10 ? "0" + second : second);
		} else {
			long second = seconds % 60;
			time = StrUtil.format("00:00:{}", second < 10 ? "0" + second : second);
		}
		return time;
	}



	/**
	 * 获取访问统计
	 * @param accessStatistics 访问统计实体
	 */
	@ApiOperation(value = "获取访问统计列表接口")
    @ApiImplicitParam(name = "id", value = "主键ID", required =true,paramType="query")
	@GetMapping("/get")
	@ResponseBody
	@RequiresPermissions("statistics:access:view")
	public ResultData get(@ModelAttribute @ApiIgnore AccessStatisticsEntity accessStatistics,HttpServletResponse response, HttpServletRequest request,@ApiIgnore ModelMap model) {
		if (accessStatistics.getId()==null) {
			return ResultData.build().error();
		}
		AccessStatisticsEntity _accessStatistics = accessStatisticsBiz.getById(accessStatistics.getId());
		return ResultData.build().success(_accessStatistics);
	}

}
