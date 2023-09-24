/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.mweixin.entity.QrCodeEntity;

import java.util.List;
import java.util.Map;

/**
 * 场景二维码管理业务
 * @author 铭飞开发团队
 * 创建日期：2023-6-5 14:21:08<br/>
 * 历史修订：<br/>
 */
public interface IQrCodeBiz extends IBaseBiz<QrCodeEntity> {


    /**
     * 获取微信公众号场景二维码
     * @param qrCodeEntity 二维码实体
     *               weixinId 必须有
     *               qrSceneStr  场景值(必填)
     *               qrExpireSeconds 二维码有效期(必填)
     * @return 返回带有二维码的Map
     * map.qCode 装有二维码字节文件
     * map.key 临时二维码ticket
     */
    Map qrCodeCreateTmpTicket(QrCodeEntity qrCodeEntity);

    /**
     * 根据条件返回场景二维码集合，weixinNo如果为空,则查询出全部，其他参数为条件搜索参数
     * @param weixinNo 微信NO
     * @param qrSceneStr 微信场景ID
     * @param qrActionName 是否长期有效二维码
     * @return 返回指定场景二维码集合
     */
    List<QrCodeEntity> queryQrCode(String weixinNo, String qrSceneStr, String qrActionName);
}
