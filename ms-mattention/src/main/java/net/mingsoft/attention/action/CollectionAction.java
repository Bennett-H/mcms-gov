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
import net.mingsoft.attention.biz.ICollectionBiz;
import net.mingsoft.attention.entity.CollectionEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mdiy.util.DictUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
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
 * 关注管理控制层
 * @author 铭飞开发团队
 * 创建日期：2019-11-22 14:34:49<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-关注记录模块接口"})
@Controller("CollectionLogAction")
@RequestMapping("/${ms.manager.path}/attention/collection")
public class CollectionAction extends BaseAction{

	/**
	 * 注入关注记录业务层
	 */
	@Autowired
	private ICollectionBiz collectionBiz;

	/**
	 * 根据主键id获取关注记录
	 * @param collection 关注实体
	 */
	@ApiOperation(value = "获取关注记录列表接口")
    @ApiImplicitParam(name = "id", value = "主键ID", required =true,paramType="query")
	@GetMapping(value ="/get")
	@ResponseBody
	public ResultData get(@ModelAttribute @ApiIgnore CollectionEntity collection) {
		if (collection.getId()==null) {
			return ResultData.build().error(getResString("err.empty", this.getResString("id")));
		}
		CollectionEntity collectionEntity = collectionBiz.getById(collection.getId());
		return ResultData.build().success(collectionEntity);
	}

	/**
	 * 根据编号与类型查询指定关注列表记录
	 * @param collection 关注实体
	 */
	@ApiOperation(value = "根据编号与类型查询指定关注列表记录")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dataId", value = "数据id", required = false, paramType = "query"),
			@ApiImplicitParam(name = "dataType", value = "业务类型", required = true, paramType = "query"),
			@ApiImplicitParam(name = "collectionDataTitle", value = "业务名称", required = false,paramType="query"),
			@ApiImplicitParam(name = "peopleId", value = "用户编号", required = false,paramType="query"),
			@ApiImplicitParam(name = "peopleName", value = "用户昵称", required = false,paramType="query"),
	})
	@PostMapping(value ="/list")
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore CollectionEntity collection) {
		// 权限验证
		if(!getPermissions("attention:collection:view","attention:collection:" + BasicUtil.getString("dataType") + ":view")){
			// 无权限
			throw new UnauthorizedException();
		}
		String dataType = DictUtil.getDictValue("关注类型", collection.getDataType(),null);
		// 判断业务类型是否为空
		if (StringUtils.isBlank(dataType)){
			return ResultData.build().error(getResString("err.empty", this.getResString("data.type")));
		}
		collection.setDataType(dataType);
		LambdaQueryWrapper<CollectionEntity> wrapper = new LambdaQueryWrapper<>(collection);
		wrapper.orderByDesc(CollectionEntity::getCreateDate);
		// 开始分页
		BasicUtil.startPage();
		List collectionLogList = collectionBiz.list(wrapper);
		return ResultData.build().success(new EUListBean(collectionLogList,(int)BasicUtil.endPage(collectionLogList).getTotal()));
	}
}
