/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.msend.service;

import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.msend.biz.ILogBiz;
import net.mingsoft.msend.entity.LogEntity;
import net.mingsoft.msend.entity.TemplateEntity;
import net.mingsoft.msend.util.SendUtil;
import net.mingsoft.msend.util.SendcloudUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Service("sendCouldMailService")
public class SendCouldMailService extends BaseSendService{
    @Override
    public boolean send(Map<String,String> config, String[] toUser, TemplateEntity template, Map values) {
        String mailContent =  this.rendering(values, template.getTemplateMail());

        String _toUser = "";
        for (int i = 0; i < toUser.length; i++) {
            if (StringUtil.isEmail(toUser[i])) {
                _toUser += toUser[i];
                if (i < toUser.length) {
                    _toUser += ";";
                }
            }
        }
        boolean flag = false;
        try {
            flag = SendcloudUtil.sendMail(
                    config.get("mailName"),
                    config.get("mailPassword"),
                    config.get("mailForm"),
                    config.get("mailFormName"),
                    _toUser,template.getTemplateTitle(),
                    mailContent);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("Sendcloud 接口调用失败");
        }
        //写入日志
        ILogBiz logBiz = SpringUtil.getBean(ILogBiz.class);
        LogEntity log = new LogEntity();
        if(flag){
            log.setLogType(MAIL);
            log.setTemplateId(Integer.parseInt(template.getId()));
            log.setLogDatetime(new Date());
            log.setLogContent(mailContent);
            log.setLogReceive(_toUser.replace(";",""));
            logBiz.save(log);
        }
        return flag;
    }
}
