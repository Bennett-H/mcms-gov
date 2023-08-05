/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.msend.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.msend.biz.ITemplateBiz;
import net.mingsoft.msend.entity.TemplateEntity;
import net.mingsoft.msend.service.BaseSendService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SendUtil {

	private static final Logger LOG = LoggerFactory.getLogger(SendUtil.class);

	/**
	 * 发送邮件
	 * @param configType  	邮件配置的类型  默认:邮件配置|短信配置|sendCloud短信配置|sendCloud邮件配置(configType就是自定义配置的名字)
	 * @param receive  		接收用户
	 * @param code			模板编码
	 * @param values		根据values.key值替换替换模版里面内容的${key}，
	 * @return
	 */
	public static boolean send(String configType,String code, String receive, Map values)  {
		//获取配置信息
		Map<String, String> config = ConfigUtil.getMap(configType);
		if (config == null) {
			throw new BusinessException(String.format("%s 不存在", configType));
		}
		if (StringUtils.isBlank(config.get("class"))) {
			throw new BusinessException(String.format("没有配置监听类 calss"));
		}
		ITemplateBiz templateBiz = SpringUtil.getBean(ITemplateBiz.class);
		TemplateEntity template = templateBiz.getOne(Wrappers.<TemplateEntity>lambdaQuery().eq(TemplateEntity::getTemplateCode,code), false);

		if(template==null) {
			throw new BusinessException("消息模板未找到");
		}
		if(StringUtils.isBlank(receive)) {
			throw new BusinessException("接收人不能为空");
		}
		String[] toUser = receive.split(",");
		BaseSendService sendService = (BaseSendService)SpringUtil.getBean(String.valueOf(config.get("class")));
		return sendService.send(config,toUser,template,values);
	}



}
