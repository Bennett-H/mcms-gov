/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.msend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import net.mingsoft.base.entity.BaseEntity;

import cn.hutool.crypto.SecureUtil;

import org.apache.commons.lang3.StringUtils;

 /**
 * 发送消息模板表实体
 * @author 铭飞开发团队
 * @version
 * 版本号：0.0<br/>
 * 创建日期：2017-8-24 17:52:29<br/>
 * 历史修订：<br/>
 */
@TableName("msend_template")
public class TemplateEntity extends BaseEntity {

	private static final long serialVersionUID = 1503568349408L;

	 /**
	 * 模块编号
	 */
	private Integer modelId;
	/**
	 * 标题
	 */
	private String templateTitle;
	/**
	 *
	 */
	private String templateMail;
	/**
	 *
	 */
	private String templateSms;
	/**
	 * 邮件模块代码
	 */
	private String templateCode;

	/**
	 * 设置模块编号
	 */
	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}

	/**
	 * 获取模块编号
	 */
	public Integer getModelId() {
		return this.modelId;
	}

	/**
	 * 设置标题
	 */
	public void setTemplateTitle(String templateTitle) {
		this.templateTitle = templateTitle;
	}

	/**
	 * 获取标题
	 */
	public String getTemplateTitle() {
		return this.templateTitle;
	}

	/**
	 * 设置
	 */
	public void setTemplateMail(String templateMail) {
		this.templateMail = templateMail;
	}

	/**
	 * 获取
	 */
	public String getTemplateMail() {
		return this.templateMail;
	}

	/**
	 * 设置
	 */
	public void setTemplateSms(String templateSms) {
		this.templateSms = templateSms;
	}

	/**
	 * 获取
	 */
	public String getTemplateSms() {
		return this.templateSms;
	}

	/**
	 * 设置邮件模块代码
	 */
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	/**
	 * 获取邮件模块代码
	 */
	public String getTemplateCode() {
		return this.templateCode;
	}

}
