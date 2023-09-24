/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.organization.biz.impl;

import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.organization.biz.IPostBiz;
import net.mingsoft.organization.dao.IPostDao;
import net.mingsoft.organization.entity.PostEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 岗位管理管理持久化层
 * @author 铭飞开源团队
 * 创建日期：2020-1-6 14:53:26<br/>
 * 历史修订：<br/>
 */
 @Service("organizationpostBizImpl")
 @Transactional
public class PostBizImpl extends BaseBizImpl<IPostDao, PostEntity> implements IPostBiz {


	@Autowired
	private IPostDao postDao;


	@Override
	protected IBaseDao getDao() {
		// TODO Auto-generated method stub
		return postDao;
	}

	@Override
	public List<PostEntity> eachPostMembers() {
		List<PostEntity> eachPostMembersList = postDao.queryEachPostMembers();
		return eachPostMembersList;
	}

	/**
	 * 通过id 查询所有的post
	 * @param postIds
	 * @return
	 */
	@Override
	public List<PostEntity> getPostsByIds(String postIds) {
		List<PostEntity> postEntities = postDao.getPostsByIds(postIds);
		return postEntities;
	}
}
