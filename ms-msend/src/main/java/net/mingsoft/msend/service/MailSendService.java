/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.msend.service;

import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.msend.biz.ILogBiz;
import net.mingsoft.msend.entity.LogEntity;
import net.mingsoft.msend.entity.TemplateEntity;
import net.mingsoft.msend.util.MailUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service("mailSendService")
public class MailSendService extends BaseSendService {
    @Override
    public boolean send(Map<String,String> config, String[] toUser, TemplateEntity template, Map values) {
        String mailContent = this.rendering(values, template.getTemplateMail());


        //发送
        MailUtil.sendHtml(config.get("mailServer"),
                Integer.parseInt(String.valueOf(config.get("mailPort"))),
                config.get("mailName"),
                config.get("mailPassword"),
                config.get("mailFormName"),
                template.getTemplateTitle(), mailContent, toUser);

        //写入日志
        ILogBiz logBiz = SpringUtil.getBean(ILogBiz.class);
        LogEntity log = new LogEntity();
        for (int i = 0; i < toUser.length; i++) {
            log.setLogType(MAIL);
            log.setTemplateId(Integer.parseInt(template.getId()));
            log.setLogDatetime(new Date());
            log.setLogContent(mailContent);
            log.setLogReceive(toUser[i]);
            logBiz.save(log);
        }
        return true;
    }
}
