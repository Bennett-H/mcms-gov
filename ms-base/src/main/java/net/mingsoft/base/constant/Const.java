/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.base.constant;

import java.util.ResourceBundle;
import org.springframework.context.ApplicationContext;


/**
 * @ClassName:  BaseAction   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: 铭飞开发团队
 * @date:   2018年3月19日 下午3:28:27   
 *     
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 */
public final class Const {

	/**
	 * action层对应的国际化资源文件
	 */
	public final static String RESOURCES = "net.mingsoft.base.resources.resources";

	
	/**
	 * 默认编码格式
	 */
	public final static String UTF8 = "utf-8";
	
	/**
	 * URL路径符
	 */
	public final static String SEPARATOR ="/";
	

	/**
	 * 统一定义error错误值，用户返回消息统一
	 */
	public final static String ERROR ="error";
	
	/**
	 * 统一定义error错误值，用户返回消息统一
	 */
	public final static String ERROR_500 ="/500/error.do";
}
