/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.organization.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.organization.entity.PostEntity;

import java.util.List;
import java.util.Map;


/**
 * 岗位管理业务
 * @author 铭飞开源团队
 * 创建日期：2020-1-6 14:53:26<br/>
 * 历史修订：<br/>
 */
public interface IPostBiz extends IBaseBiz<PostEntity> {

    List<PostEntity> eachPostMembers();

    /**
     * 通过ids 查询 所以的岗位
     * @param postIds
     * @return
     */
    List<PostEntity> getPostsByIds(String postIds);
}
