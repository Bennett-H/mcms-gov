/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.basic.entity;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.base.entity.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 网站基本信息实体类
 * @author 铭软
 * @version
 * 版本号：200-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：增加多模板字段 appStyles<br/>
 * 2022-1-6 basic覆写 增加app树形字段 leaf topId 等 将app改为树形结构
 */
@TableName("app")
public class AppEntity extends BaseEntity {


	/**
	 * 应用名称
	 */
	private String appName;

	/**
	 * 是否能够删除 0-能删除 1-不能删除
	 */
	@TableField(whereStrategy = FieldStrategy.NEVER)
	private int notDel;

	/**
	 * 父节点id
	 */
	private  String parentId;

	/**
	 * 父类型编号
	 */
	private String parentIds;

	/**
	 * 叶子节点
	 */
	private Boolean leaf;

	/**
	 * 顶级id
	 */
	private String topId;

	/**
	 * 应用描述
	 */
	private String appDescription;

	/**
	 * 应用logo
	 */
	private String appLogo;


	/**
	 * 网站采用的模板风格
	 */

	private String appStyle;
	/**
	 * 网站采用的多模板风格 {name:"内网模版",template:"net"}
	 */
	private String appStyles;

	/**
	 * 网站关键字
	 */
	private String appKeyword;

	/**
	 * 网站版权信息
	 */
	private String appCopyright;

	/**
	 * 网站生成的文件夹
	 */
	private String appDir;

	/**
	 * 网站域名
	 */
	private String appUrl;

	/**
	 * 站点日期
	 */
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date appDatetime;

	/**
	 * 应用续费时间
	 */

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date appPayDate;

	/**
	 * 应用费用清单
	 */
	private String appPay;

	/**
	 * 网站状态
	 */
	private String appState;


	public Date getAppPayDate() {
		return appPayDate;
	}

	public void setAppPayDate(Date appPayDate) {
		this.appPayDate = appPayDate;
	}

	public String getAppPay() {
		return appPay;
	}

	public void setAppPay(String appPay) {
		this.appPay = appPay;
	}

	public Date getAppDatetime() {
		return appDatetime;
	}

	public void setAppDatetime(Date appDatetime) {
		this.appDatetime = appDatetime;
	}

	/**
	 * 获取网站版权信息
	 *
	 * @return 返回网站版权信息
	 */
	public String getAppCopyright() {
		return appCopyright;
	}

	public String getAppDescription() {
		return appDescription;
	}

	/**
	 * 获取网站的关键字
	 *
	 * @return 返回网站关键字
	 */
	public String getAppKeyword() {
		return appKeyword;
	}


	public String getAppName() {
		return appName;
	}

	/**
	 * 获取网站使用的模板风格
	 *
	 * @return 返回网站使用的模板风格
	 */
	public String getAppStyle() {
		return appStyle;
	}

	/**
	 * 获取网站域名
	 */
	public String getAppUrl() {
		//判断域名是否为空
		if(ObjectUtil.isNotNull(appUrl)){
			return appUrl.replaceAll(" ","");
		}
		return appUrl;
	}

	/**
	 * 获取网站域名
	 */
	public String getAppHostUrl() {
		//判断域名是否为空
		if(ObjectUtil.isNotNull(appUrl)){
			//存在多个域名绑定
			if (appUrl.indexOf("\n") > 0) {
				return appUrl.split("\n")[0].trim();
			}
		}
		return appUrl;
	}

	public String getAppLogo() {
		return appLogo;
	}

	/**
	 * 设置网站版权信息
	 *
	 * @param appCopyright
	 */
	public void setAppCopyright(String appCopyright) {
		this.appCopyright = appCopyright;
	}

	public void setAppDescription(String appDescription) {
		this.appDescription = appDescription;
	}

	public int getAppId() {
		return id==null? 0:Integer.parseInt(id);
	}

	public void setAppId(int id) {
		this.id = id+"";
	}

	/**
	 * 设置网站关键字
	 *
	 * @param appKeyword
	 */
	public void setAppKeyword(String appKeyword) {
		this.appKeyword = appKeyword;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * 设置网站使用的模板风格
	 *
	 * @param appStyle
	 */
	public void setAppStyle(String appStyle) {
		this.appStyle = appStyle;
	}

	/**
	 * 设置网站域名
	 *
	 * @param appUrl
	 */
	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}


	public void setAppLogo(String appLogo) {
		this.appLogo = appLogo;
	}

	public String getAppDir() {
		return appDir;
	}

	public void setAppDir(String appDir) {
		this.appDir = appDir;
	}

	public String getAppStyles() {
		return appStyles;
	}

	public void setAppStyles(String appStyles) {
		this.appStyles = appStyles;
	}

	public String getAppState() {
		return appState;
	}

	public void setAppState(String appState) {
		this.appState = appState;
	}

	public int getNotDel() {
		return notDel;
	}

	public void setNotDel(int notDel) {
		this.notDel = notDel;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	public Boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	public String getTopId() {
		return topId;
	}

	public void setTopId(String topId) {
		this.topId = topId;
	}
}