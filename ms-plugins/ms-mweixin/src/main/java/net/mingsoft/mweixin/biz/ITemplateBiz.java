/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.mweixin.entity.WeixinEntity;
import net.mingsoft.mweixin.entity.TemplateEntity;

/**
 * 微信消息模板业务
 * @author 铭飞开发团队
 * 创建日期：2023-6-8 15:43:33<br/>
 * 历史修订：<br/>
 */
public interface ITemplateBiz extends IBaseBiz<TemplateEntity> {

    /**
     * 同步微信消息模板，同步之前会进行删除操作，防止同步到相同模板
     * @param weixin weixin实体类
     * @return 返回是否同步成功
     */
    boolean syncTemplate(WeixinEntity weixin);

}
