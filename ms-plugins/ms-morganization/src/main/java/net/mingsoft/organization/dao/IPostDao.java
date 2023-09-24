/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.dao;

import net.mingsoft.base.dao.IBaseDao;
import java.util.*;
import net.mingsoft.organization.entity.PostEntity;

/**
 * 岗位管理持久层
 * @author 铭飞开源团队
 * 创建日期：2020-1-6 18:25:28<br/>
 * 历史修订：<br/>
 */
public interface IPostDao extends IBaseDao<PostEntity> {
    List<PostEntity> queryEachPostMembers();

    List<PostEntity> getPostsByIds(String postIds);
}
