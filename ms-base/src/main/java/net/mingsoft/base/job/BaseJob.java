/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.base.job;

import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @ClassName:  BaseJob   
 * @Description:TODO(基础job类)   
 * @author: 铭飞开发团队
 * @date:   2018年3月19日 下午3:44:09   
 *     
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 */
public abstract class BaseJob  implements Job {

	/*
	 * log4j日志记录
	 */
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	
}
