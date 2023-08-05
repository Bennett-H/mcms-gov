/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.attention.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.attention.bean.CollectionLogBean;
import net.mingsoft.attention.biz.ICollectionLogBiz;
import net.mingsoft.attention.entity.CollectionLogEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mdiy.util.DictUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 * 关注记录管理控制层
 * @author 铭飞开发团队
 * 创建日期：2022-1-21 16:28:31<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-关注模块接口"})
@Controller("attentionCollectionLogAction")
@RequestMapping("/${ms.manager.path}/attention/collectionLog")
public class CollectionLogAction extends BaseAction{

	/**
	 * 注入关注业务层
	 */
	@Autowired
	private ICollectionLogBiz collectionLogBiz;

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("attention:collection:view")
	public String index(HttpServletResponse response,HttpServletRequest request) {
		// 权限验证
		if(!getPermissions("attention:collection:view","attention:collection:" + request.getParameter("dataType") + ":view")){
			// 无权限
			throw new UnauthorizedException();
		}
		return "/attention/collection-log/index";
	}

	/**
	 * 返回主界面index
	 */
	@ApiIgnore
	@GetMapping("/data/index")
	@RequiresPermissions("attention:collection:view")
	public String dataIndex(HttpServletResponse response,HttpServletRequest request) {
		// 权限验证
		if(!getPermissions("attention:collection:view","attention:collection:" + request.getParameter("dataType") + ":view")){
			// 无权限
			throw new UnauthorizedException();
		}
		return "/attention/collection-log/data/index";
	}

	/**
	 * 返回关注记录的详情页
	 */
	@ApiIgnore
	@GetMapping("/detail")
	@RequiresPermissions("attention:collectionLog:view")
	public String detail(@ModelAttribute CollectionLogEntity collectionLog, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		return "/attention/collection-log/detail";
	}

	/**
	 * 查询关注记录列表
	 * @param collectionLog 关注记录实体
	 */
	@ApiOperation(value = "查询关注记录列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dataId", value = "数据id", required =false, paramType = "query"),
			@ApiImplicitParam(name = "dataType", value = "业务类型", required =true, paramType = "query"),
			@ApiImplicitParam(name = "collectionDataTitle", value = "业务名称", required =false,paramType="query"),
	})
	@PostMapping(value ="/list")
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore CollectionLogBean collectionLog) {
		// 权限验证
		if(!getPermissions("attention:collection:view","attention:collection:" + BasicUtil.getString("dataType") + ":view")){
			// 无权限
			throw new UnauthorizedException();
		}
		String dataType = DictUtil.getDictValue("关注类型", collectionLog.getDataType(),null);
		// 判断业务类型是否为空
		if (StringUtils.isBlank(collectionLog.getDataType())){
			return ResultData.build().error(getResString("err.empty", this.getResString("data.type")));
		}
		collectionLog.setDataType(dataType);
		// 开始分页
		BasicUtil.startPage();
		List collectionLogList = collectionLogBiz.query(collectionLog);
		return ResultData.build().success(new EUListBean(collectionLogList,(int)BasicUtil.endPage(collectionLogList).getTotal()));
	}

	/**
	 * 根据id获取指定的一条关注记录
	 * @param collectionLog 关注记录实体
	 */
	@ApiOperation(value = "根据id获取指定的一条关注记录")
	@ApiImplicitParam(name = "id", value = "主键ID", required =true,paramType="query")
	@GetMapping(value ="/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore CollectionLogEntity collectionLog) {
		if (collectionLog.getId()==null) {
			return ResultData.build().error(getResString("err.empty", this.getResString("data.id")));
		}
		CollectionLogEntity _collection = collectionLogBiz.getById(collectionLog.getId());
		return ResultData.build().success(_collection);
	}
}
