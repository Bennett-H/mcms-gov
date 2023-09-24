/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.entity;


import com.baomidou.mybatisplus.annotation.*;
import net.mingsoft.base.entity.BaseEntity;
/**
 * 微信消息模板实体
 * @author 铭飞开发团队
 * 创建日期：2023-6-8 15:43:33<br/>
 * 历史修订：<br/>
 */
@TableName("WX_TEMPLATE")
public class TemplateEntity extends BaseEntity {

	private static final long serialVersionUID = 1687846921998L;


	/**
	 * 雪花ID规则
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;


	/**
	 * 微信编号
	 */
	private String weixinId;

	/**
	 * 标题
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String templateTitle;

	/**
	 * 模板编码
	 */
	private String templateCode;

	/**
	 * 模板ID
	 */
	private String templateId;

	/**
	 * 主属行业
	 */
	private String templatePrimaryIndustry;

	/**
	 * 副属行业
	 */
	private String templateDeputyIndustry;

	/**
	 * 内容
	 */

	private String templateContent;

	/**
	 * 样例
	 */

	private String templateExample;

	/**
	 * 模板关键词
	 */
	private String templateKeyword;


	/**
	 * 设置微信编号
	 */
	public void setWeixinId(String weixinId) {
		this.weixinId = weixinId;
	}

	/**
	 * 获取微信编号
	 */
	public String getWeixinId() {
		return this.weixinId;
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
	 * 设置模板编码
	 */
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	/**
	 * 获取模板编码
	 */
	public String getTemplateCode() {
		return this.templateCode;
	}
	/**
	 * 设置模板ID
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	/**
	 * 获取模板ID
	 */
	public String getTemplateId() {
		return this.templateId;
	}
	/**
	 * 设置主属行业
	 */
	public void setTemplatePrimaryIndustry(String templatePrimaryIndustry) {
		this.templatePrimaryIndustry = templatePrimaryIndustry;
	}

	/**
	 * 获取主属行业
	 */
	public String getTemplatePrimaryIndustry() {
		return this.templatePrimaryIndustry;
	}
	/**
	 * 设置副属行业
	 */
	public void setTemplateDeputyIndustry(String templateDeputyIndustry) {
		this.templateDeputyIndustry = templateDeputyIndustry;
	}

	/**
	 * 获取副属行业
	 */
	public String getTemplateDeputyIndustry() {
		return this.templateDeputyIndustry;
	}
	/**
	 * 设置内容
	 */
	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}

	/**
	 * 获取内容
	 */
	public String getTemplateContent() {
		return this.templateContent;
	}
	/**
	 * 设置样例
	 */
	public void setTemplateExample(String templateExample) {
		this.templateExample = templateExample;
	}

	/**
	 * 获取样例
	 */
	public String getTemplateExample() {
		return this.templateExample;
	}
	/**
	 * 设置模板关键词
	 */
	public void setTemplateKeyword(String templateKeyword) {
		this.templateKeyword = templateKeyword;
	}

	/**
	 * 获取模板关键词
	 */
	public String getTemplateKeyword() {
		return this.templateKeyword;
	}


}
