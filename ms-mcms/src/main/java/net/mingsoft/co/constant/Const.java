/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.constant;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * progress定义
 * @author 铭软科技
 * 创建日期：2020-5-28 15:12:02<br/>
 * 历史修订：<br/>
 * 2021-12-27 增加文件上传配置进行统一管理模型名称
 */
public final class Const {

	/**
	 * 资源文件
	 */
	public final static String RESOURCES= "net.mingsoft.co.resources.resources";

	/**
	 * 文件自定义配置的配置名称,在此处统一管理
	 */
	public final static String CONFIG_UPLOAD = "文件上传配置";

	/**
	 * 后台管理配置的配置名称,在此处统一管理
	 */
	public final static String MANAGER_CONFIG = "后台开发配置";


	/**
	 * 序列化sessionId
	 */
	public final static List<Serializable> SESSION_IDS = new ArrayList<>();

}
