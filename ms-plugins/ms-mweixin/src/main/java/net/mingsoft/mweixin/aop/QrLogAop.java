/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.aop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.mweixin.biz.IQrCodeBiz;
import net.mingsoft.mweixin.biz.IQrLogBiz;
import net.mingsoft.mweixin.biz.IWeixinPeopleBiz;
import net.mingsoft.mweixin.entity.QrCodeEntity;
import net.mingsoft.mweixin.entity.QrLogEntity;
import net.mingsoft.mweixin.entity.WeixinPeopleEntity;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component("qrLogApo")
public class QrLogAop extends BaseAop {


    @Autowired
    private IWeixinPeopleBiz weixinPeopleBiz;

    @Autowired
    private IQrCodeBiz qrCodeBiz;

    @Autowired
    private IQrLogBiz qrLogBiz;


    @After("execution(* net.mingsoft.mweixin.handle.IScanHandle.handleSpecial(..))")
    public void scanOrSubscribe(JoinPoint jp){
        LOG.debug("记录扫码日志");
        WxMpXmlMessage message = this.getType(jp, WxMpXmlMessage.class);
        String openId = message.getFromUser();
        WeixinPeopleEntity weixinPeople = weixinPeopleBiz.getByOpenId(openId);
        String sceneStr = message.getEventKey();
        if (StringUtils.isBlank(sceneStr)){
            LOG.debug("用户可能通过搜索或其他途径关注");
            return;
        }
        QrCodeEntity qrCodeEntity = qrCodeBiz.getOne(new LambdaQueryWrapper<QrCodeEntity>().eq(QrCodeEntity::getQcSceneStr, sceneStr).eq(QrCodeEntity::getWeixinId, weixinPeople.getWeixinId()));
        if (qrCodeEntity != null && weixinPeople!=null){
            LOG.debug("保存扫码日志信息");
            QrLogEntity qrLog = new QrLogEntity();
            qrLog.setWeixinId(weixinPeople.getWeixinId()+"");
            qrLog.setOpenId(weixinPeople.getOpenId());
            qrLog.setQcId(qrCodeEntity.getId());
            qrLog.setCreateDate(new Date());
            qrLog.setUpdateDate(new Date());
            qrLogBiz.save(qrLog);
        }

    }
}
