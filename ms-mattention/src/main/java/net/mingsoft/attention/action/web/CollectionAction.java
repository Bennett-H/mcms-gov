/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.attention.action.web;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.attention.bean.CollectionBean;
import net.mingsoft.attention.biz.ICollectionBiz;
import net.mingsoft.attention.biz.ICollectionLogBiz;
import net.mingsoft.attention.entity.CollectionEntity;
import net.mingsoft.attention.entity.CollectionLogEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mdiy.util.DictUtil;
import org.apache.commons.lang3.StringUtils;
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
@Api(tags={"前端-关注记录模块接口"})
@Controller("webCollectionAction")
@RequestMapping("/attention/collection")
public class CollectionAction extends net.mingsoft.attention.action.BaseAction{

	/**
	 * 注入关注记录业务层
	 */
	@Autowired
	private ICollectionBiz collectionBiz;

	/**
	 * 注入关注业务层
	 */
	@Autowired
	private ICollectionLogBiz collectionLogBiz;

	/**
	 * 查询当前业务的关注记录列表信息
	 * @param collection 关注实体
	 */
	@ApiOperation(value = "根据业务编号与类型查询指定关注列表记录")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "collectionDataTitle", value = "关注的业务标题，模糊查询", required =false,paramType="query"),
    	@ApiImplicitParam(name = "dataType", value = "业务类型", required =true,paramType="query"),
    })
	@PostMapping(value ="/list")
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore CollectionEntity collection) {
		// 业务编号为空
		if (StringUtils.isBlank(collection.getDataId())) {
			return ResultData.build().error(getResString("err.empty", this.getResString("data.id")));
		}
		String dataType = DictUtil.getDictValue("关注类型", collection.getDataType(),null);
		// 判断业务类型是否为空
		if (StringUtils.isBlank(dataType)){
			return ResultData.build().error(getResString("err.empty", this.getResString("data.type")));
		}
		collection.setDataType(dataType);
		LambdaQueryWrapper<CollectionEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(CollectionEntity::getDataType,collection.getDataType())
				.eq(StringUtils.isNotBlank(collection.getCollectionDataTitle()), CollectionEntity::getCollectionDataTitle,collection.getCollectionDataTitle());
		wrapper.orderByDesc(CollectionEntity::getCreateDate);
		BasicUtil.startPage();
		List collectionList = collectionBiz.list(wrapper);
		return ResultData.build().success(new EUListBean(collectionList,(int)BasicUtil.endPage(collectionList).getTotal()));
	}


	/**
	 * 批量获取数据的关注数或者点赞数,使用场景如：文章详情页面显示关注数或者点赞数
	 * @param dataIds 业务ids，逗号分隔
	 * @param dataType 业务类型
	 * @return 返回CollectionBean集合数据
	 */
	@ApiOperation(value = "批量获取数据的关注数或者点赞数")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dataIds", value = "业务数据Ids，逗号分隔", required =true,paramType="query"),
			@ApiImplicitParam(name = "dataType", value = "关注类型", required =true,paramType="query")
	})
	@PostMapping("/queryCollectionCount")
	@ResponseBody
	public ResultData queryCollectionCount(String dataIds, String dataType,String peopleId){
		if (StringUtils.isBlank(dataIds)){
			return ResultData.build().error(getResString("err.error",this.getResString("data.id")));
		}
		dataType = DictUtil.getDictValue("关注类型", dataType);
		if (StringUtils.isBlank(dataType)){
			return ResultData.build().error(getResString("err.error",this.getResString("data.type")));
		}
		String[] ids = dataIds.split(",");
		List<String> data = CollectionUtil.newArrayList(ids);
		List<CollectionBean> collectionBeans = collectionLogBiz.queryCollectionCount(data, dataType, peopleId);
		// todo 先把ip过滤 后续待转成地理位置显示
		return ResultData.build().success(BasicUtil.filter(new EUListBean(collectionBeans,collectionBeans.size()),"collectionIp"));
	}

}
