/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */









package net.mingsoft.gov.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.approval.constant.e.ProgressStatusEnum;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseAction;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.biz.IContentBiz;
import net.mingsoft.cms.constant.e.CategoryDisplayEnum;
import net.mingsoft.cms.constant.e.CategoryTypeEnum;
import net.mingsoft.cms.entity.CategoryEntity;
import net.mingsoft.cms.entity.ContentEntity;
import net.mingsoft.cms.constant.e.ContentEnum;
import net.mingsoft.elasticsearch.service.IESService;
import net.mingsoft.gov.bean.ESContentBean;
import net.mingsoft.gov.service.IESContentService;
import net.mingsoft.gov.util.ESContentBeanUtil;
import net.mingsoft.mdiy.biz.IModelBiz;
import net.mingsoft.mdiy.entity.ModelEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * es搜索接口
 * @author 铭飞开发团队
 * 创建日期：2020/12/29 9:53<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-全文检索接口"})
@Controller("govESSearchAction")
@RequestMapping("/${ms.manager.path}/gov/es")
public class ESSearchAction extends BaseAction {


	@Autowired
	private IContentBiz contentBiz;

	@Autowired
	private IModelBiz modelBiz;

	@Autowired
	private ICategoryBiz categoryBiz;

	@Autowired
	private IESService esService;

	@ApiIgnore
	@GetMapping("/index")
	@RequiresPermissions("gov:es:view")
	public String index(HttpServletResponse response, HttpServletRequest request){
		return "/gov/es/index";
	}


	/**
	 * 同步es文章
	 * @return
	 */
	@ApiOperation(value = "同步es文章接口")
	@PostMapping("/sync")
	@RequiresPermissions("gov:es:sync")
	@ResponseBody
	public ResultData sync() {
		// 没有索引需要先去创建索引
		if (!esService.existDoc(esService.getBeanClass())){
			return ResultData.build().error("当前不存在es索引，请先创建es索引");
		}
		// 获取栏目列表
		LambdaQueryWrapper<CategoryEntity> categoryWrapper = new LambdaQueryWrapper<>();
		categoryWrapper.ne(CategoryEntity::getCategoryType, CategoryTypeEnum.LINK.toString());
		// 排除不显示的栏目
		List<CategoryEntity> categoryList = categoryBiz.list(categoryWrapper).stream().filter(categoryEntity -> CategoryDisplayEnum.ENABLE.toString().equalsIgnoreCase(categoryEntity.getCategoryDisplay())).collect(Collectors.toList());
		for (CategoryEntity category : categoryList) {
			// 获取文章列表
			LambdaQueryWrapper<ContentEntity> contentWrapper = new LambdaQueryWrapper<>();
			contentWrapper.eq(ContentEntity::getProgressStatus, ProgressStatusEnum.APPROVED.toString());
			contentWrapper.eq(ContentEntity::getCategoryId, category.getId());
			contentWrapper.eq(ContentEntity::getContentDisplay, ContentEnum.DISPLAY.toString());
			List<ContentEntity> contentList = contentBiz.list(contentWrapper);
			boolean hasModel = false;

			ModelEntity model = null;
			if (StringUtils.isNotBlank(category.getMdiyModelId())) {
				hasModel = true;
				// 获取模型实体
				model = modelBiz.getById(category.getMdiyModelId());
			}
			List<ESContentBean> beanList = new ArrayList<>();
			for (ContentEntity content : contentList) {
				// 跳过未到发布时间的文章
				if (DateUtil.compare(new Date(),content.getContentDatetime(),"yyyy-MM-dd")<0){
					continue;
				}
				ESContentBean esContentBean = new ESContentBean();
				ESContentBeanUtil.fixESContentBean(esContentBean, content, category);
				if (hasModel) {
					//配置link_id Map
					Map<String, String> modelMap = new HashMap<>(1);
					modelMap.put("link_id", content.getId());
					List<Map<String, Object>> modelFieldList = contentBiz.queryBySQL(
							model.getModelTableName(),
							null ,
							modelMap, null, null, null, null, null);
					// 防止NP
					if (CollUtil.isNotEmpty(modelFieldList)) {
						// 模型字段MAP
						Map<String, Object> objectMap = modelFieldList.get(0);
						for (String key : objectMap.keySet()) {
							// 类型为BigInteger时需要转换为long防止es类型构造器转换错误
							if (objectMap.get(key) instanceof BigInteger) {
								objectMap.put(key, ((BigInteger) objectMap.get(key)).longValue());
							}
						}
						esContentBean.setMDiyModel(objectMap);
					}
				}
				beanList.add(esContentBean);
			}
			if (CollUtil.isNotEmpty(beanList)) {
				try {
					IESContentService esContentService = SpringUtil.getBean(IESContentService.class);
					esContentService.saveAll(beanList);
				} catch (DataAccessResourceFailureException e) {
					e.printStackTrace();
					return ResultData.build().error("未找到当前ES信息,请检查当前ES链接是否正常");
				} catch (NoSuchIndexException e) {
					e.printStackTrace();
					return ResultData.build().error("未找到当前ES索引信息,请检查是否创建索引");
				} catch (NoSuchBeanDefinitionException e) {
					e.printStackTrace();
					return ResultData.build().error("未找到当前ES信息,请检查当前ES配置是否正常开启");

				}

			}

		}
		return ResultData.build().success().msg("全部同步完成!");
	}

}
