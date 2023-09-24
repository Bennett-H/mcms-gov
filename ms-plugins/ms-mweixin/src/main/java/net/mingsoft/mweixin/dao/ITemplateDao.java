/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.dao;

import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mweixin.entity.TemplateEntity;
import org.springframework.stereotype.Component;

/**
 * 微信消息模板持久层
 * @author 铭飞开发团队
 * 创建日期：2023-6-8 15:43:33<br/>
 * 历史修订：<br/>
 */
@Component("wxTemplateDao")
public interface ITemplateDao extends IBaseDao<TemplateEntity> {
}
