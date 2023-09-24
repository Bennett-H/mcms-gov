/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.mweixin.biz;

import java.util.List;

import net.mingsoft.base.biz.IBaseBiz;

import net.mingsoft.mweixin.entity.GroupMessageEntity;
import net.mingsoft.mweixin.entity.MessageEntity;

/**
 * 微信被动消息回复业务
 * @author 铭飞
 * @version 
 * 版本号：100<br/>
 * 创建日期：2017-12-22 9:43:04<br/>
 * 历史修订：<br/>
 */
public interface IMessageBiz extends IBaseBiz {

	/**
	 * 获取回复消息实体
	 * @param message
	 * @return
	 */
	public MessageEntity getEntity(MessageEntity message);
	
	/**
	 * 查询关键字列表，后台使用如果类型的图文，newsTitle显示素材的标题
	 * @param message
	 * @return
	 */
	public List<MessageEntity> query(MessageEntity message);

	/**
	 * 保存消息实体
	 * @param messageEntity
	 */
	int saveMessage(MessageEntity messageEntity);

	/**更新消息实体
	 * @param messageEntity
	 */
	void updateFile(MessageEntity messageEntity);

}
