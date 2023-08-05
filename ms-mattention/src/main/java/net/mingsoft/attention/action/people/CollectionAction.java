/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.attention.action.people;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.attention.bean.CollectionBean;
import net.mingsoft.attention.biz.ICollectionBiz;
import net.mingsoft.attention.biz.ICollectionLogBiz;
import net.mingsoft.attention.entity.CollectionEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.EUListBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mdiy.util.DictUtil;
import net.mingsoft.people.biz.IPeopleUserBiz;
import net.mingsoft.people.entity.PeopleEntity;
import net.mingsoft.people.entity.PeopleUserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 会员关注插件管理控制层，提供通用的关注列表、保存与取消操作等
 * @author 铭飞开发团队
 * 创建日期：2019-11-22 14:34:49<br/>
 * 历史修订：<br/>
 */
@Api(tags={"前端-用户-关注模块接口"})
@Controller("peopleCollectionAction")
@RequestMapping("/people/attention/collection")
public class CollectionAction extends net.mingsoft.attention.action.BaseAction{

	/**
	 * 注入关注业务层
	 */
	@Autowired
	private ICollectionLogBiz collectionLogBiz;

	/**
	 * 注入pepoleUser业务层
	 */
	@Autowired
	private IPeopleUserBiz peopleUserBiz;
	/**
	 * 注入关注记录业务层
	 */
	@Autowired
	private ICollectionBiz collectionBiz;


	/**
	 * 查询会员关注的业务数据列表，
	 * @param collection
	 * @return 返回  CollectionEntity 集合
	 */
	@ApiOperation(value = "根据业务编号与类型查询指定关注列表记录")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "collectionDataTitle", value = "关注的业务标题，模糊查询", required =false,paramType="query"),
    	@ApiImplicitParam(name = "dataType", value = "业务类型", required =true,paramType="query"),
    })
	@PostMapping(value ="/list")
	@ResponseBody
	public ResultData list(@ModelAttribute @ApiIgnore CollectionEntity collection) {
		String dataType = DictUtil.getDictValue("关注类型", collection.getDataType(),null);
		// 判断业务类型是否为空
		if (StringUtils.isBlank(dataType)){
			return ResultData.build().error(getResString("err.empty", this.getResString("data.type")));
		}
		collection.setDataType(dataType);
		collection.setPeopleId(this.getPeopleBySession().getIntId());
		LambdaQueryWrapper<CollectionEntity> collectionLqw = new LambdaQueryWrapper<>();
		collectionLqw.eq(CollectionEntity::getDataType,collection.getDataType())
				.eq(StringUtils.isNotBlank(collection.getCollectionDataTitle()), CollectionEntity::getCollectionDataTitle,collection.getCollectionDataTitle());
		collectionLqw.orderByDesc(CollectionEntity::getCreateDate);
		BasicUtil.startPage();
		List collectionList = collectionBiz.list(collectionLqw);
		return ResultData.build().success(new EUListBean(collectionList,(int)BasicUtil.endPage(collectionList).getTotal()));
	}

	/**
	 * 保存关注,重复调用会进行删除一条记录操作
	 * @param collection
	 * @return true表示关注,false表示取消关注
	 */
	@ApiOperation(value = "保存关注接口")
	@ApiImplicitParams({
    	@ApiImplicitParam(name = "dataId", value = "业务编号", required = true,paramType="query"),
		@ApiImplicitParam(name = "dataType", value = "业务类型", required = true,paramType="query"),
		@ApiImplicitParam(name = "collectionDataTitle", value = "业务名称", required = false,paramType="query"),
		@ApiImplicitParam(name = "collectionDataUrl", value = "业务链接", required = false,paramType="query"),
		@ApiImplicitParam(name = "collectionDataJson", value = "业务拓展信息，JSON格式，根据实际业务自定义", required = false,paramType="query"),
		@ApiImplicitParam(name = "collectionDataImg", value = "业务图片地址，前端通过el-upload上传，是json格式", required = false,paramType="query"),
	})
	@PostMapping(value ="/save")
	@ResponseBody
	public ResultData save(@ModelAttribute @ApiIgnore CollectionEntity collection) {

		if(!StringUtil.checkLength(collection.getCollectionDataTitle()+"", 0, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("collection.data.title"), "0", "255"));
		}
		if(!StringUtil.checkLength(collection.getCollectionDataUrl()+"", 0, 255)){
			return ResultData.build().error(getResString("err.length", this.getResString("collection.data.url"), "0", "255"));
		}

		PeopleEntity people = this.getPeopleBySession();
		// 设置用户信息，
		PeopleUserEntity peopleUserEntity = (PeopleUserEntity) peopleUserBiz.getEntity(people.getIntId());
		collection.setPeopleId(people.getIntId());
		collection.setPeopleName(peopleUserEntity.getPuNickname());
		Map map = new HashMap<>();

		// 设置用户拓展信息
		map.put("puIcon", peopleUserEntity.getPuIcon());
		String peopleInfo = JSONUtil.toJsonStr(map);

		collection.setPeopleInfo(peopleInfo);
		// 根据关注日志实体来判断新增或者删除关注信息
		Boolean flag = collectionBiz.saveOrDelete(collection);

		//布尔值,true表示关注,false表示取消关注
		return ResultData.build().success(flag);
	}

	/**
	 * 批量获取业务数据对应的关注总数或者点赞总数,使用场景如：文章详情页面显示关注总数或者点赞总数
	 * 并且通过返回的isLike字段可以判断当前会员是否点赞该业务
	 * @param dataIds 业务ids，多个id之间逗号隔开
	 * @param dataType 业务类型
	 * @return 返回CollectionBean集合数据
	 */
	@ApiOperation(value = "批量获取数据的关注数或者点赞数")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dataIds", value = "数据Ids，多个id之间逗号隔开", required =true,paramType="query"),
			@ApiImplicitParam(name = "dataType", value = "关注类型", required =true,paramType="query")
	})
	@PostMapping("/queryCollectionCount")
	@ResponseBody
	public ResultData queryCollectionCount(String dataIds, String dataType){
		if (StringUtils.isBlank(dataIds)){
			return ResultData.build().error(getResString("err.error",this.getResString("data.id")));
		}
		dataType = DictUtil.getDictValue("关注类型", dataType);
		if (StringUtils.isBlank(dataType)){
			return ResultData.build().error(getResString("err.error",this.getResString("data.type")));
		}
		String[] ids = dataIds.split(",");
		List<String> data = CollectionUtil.newArrayList(ids);
		List<CollectionBean> collectionBeans = collectionLogBiz.queryCollectionCount(data, dataType, this.getPeopleBySession().getId());
		// 待过滤Ip字段
		return ResultData.build().success(collectionBeans);
	}
}
