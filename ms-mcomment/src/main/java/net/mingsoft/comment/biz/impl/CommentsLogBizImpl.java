/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.comment.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.comment.entity.CommentsLogEntity;
import net.mingsoft.comment.biz.ICommentsLogBiz;
import net.mingsoft.comment.dao.ICommentsLogDao;

/**
 * 评论记录管理持久化层
 * @author 铭飞开发团队
 * 创建日期：2022-1-21 10:14:30<br/>
 * 历史修订：<br/>
 */
 @Service("commentcommentsLogBizImpl")
public class CommentsLogBizImpl extends BaseBizImpl<ICommentsLogDao,CommentsLogEntity> implements ICommentsLogBiz {


	@Autowired
	private ICommentsLogDao commentsLogDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return commentsLogDao;
	}



}
