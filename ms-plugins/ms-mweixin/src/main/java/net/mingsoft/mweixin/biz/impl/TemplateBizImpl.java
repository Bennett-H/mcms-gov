/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.biz.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mweixin.biz.ITemplateBiz;
import net.mingsoft.mweixin.dao.ITemplateDao;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.entity.TemplateEntity;
import net.mingsoft.mweixin.service.PortalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 微信消息模板管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2023-6-8 15:43:33<br/>
 * 历史修订：<br/>
 */
@Service("mweixinTemplateBizImpl")
public class TemplateBizImpl extends BaseBizImpl<ITemplateDao,TemplateEntity> implements ITemplateBiz {


	@Autowired
	private ITemplateDao templateDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return templateDao;
	}


	@Override
	public boolean syncTemplate(WeixinEntity weixin) {
		// TODO: 2023/6/19 同步模板时，如果存在相同名称的模板，把后面的模板用hutool随机生成编码在后面用作区分，注意在比较时同时比对数据库里以及此次同步到的模板
		if (weixin == null) {
			return false;
		}
		// 先根据weixinId查询他当前所有的模板, 再把数据库的删除
		LambdaQueryWrapper<TemplateEntity> wrapper = new LambdaQueryWrapper();
		// 先存储数据，再去匹配数据，如果之前有则用之前的昵称，没有则用同步的昵称
		List<TemplateEntity> templateEntityList = templateDao.selectList(wrapper);
		// 保存之前删除之前的模板数据
		wrapper.eq(TemplateEntity::getWeixinId, weixin.getId());
		templateDao.delete(wrapper);
		PortalService portalService = new PortalService().build(weixin);
		try {
			List<WxMpTemplate> allPrivateTemplate = portalService.getTemplateMsgService().getAllPrivateTemplate();
			List<TemplateEntity> templateEntities = new ArrayList<>();
			TemplateEntity templateEntity;
			for (WxMpTemplate wxMpTemplate : allPrivateTemplate) {
				templateEntity = new TemplateEntity();

				// 查templateId且微信Id是否相同，相同就用之前的code和关键字
				List<TemplateEntity> _templateEntityList = templateEntityList.stream().filter(template ->
						template.getTemplateId().equals(wxMpTemplate.getTemplateId()) && template.getWeixinId().equals(weixin.getId())
				).collect(Collectors.toList());
				if (CollectionUtil.isNotEmpty(_templateEntityList)) {
					templateEntity.setTemplateCode(_templateEntityList.get(0).getTemplateCode());
					templateEntity.setTemplateKeyword(_templateEntityList.get(0).getTemplateKeyword());
				}
				templateEntity.setTemplateTitle(wxMpTemplate.getTitle());
				templateEntity.setWeixinId(weixin.getId());
				templateEntity.setTemplateId(wxMpTemplate.getTemplateId());
				templateEntity.setTemplateContent(wxMpTemplate.getContent());
				templateEntity.setTemplateExample(wxMpTemplate.getExample());
				templateEntity.setTemplateDeputyIndustry(wxMpTemplate.getDeputyIndustry());
				templateEntity.setTemplatePrimaryIndustry(wxMpTemplate.getPrimaryIndustry());
				templateEntity.setCreateDate(new Date());
				templateEntities.add(templateEntity);
			}
			if (CollectionUtil.isNotEmpty(templateEntities)) {
				saveBatch(templateEntities,templateEntities.size());
			}
		} catch (WxErrorException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
