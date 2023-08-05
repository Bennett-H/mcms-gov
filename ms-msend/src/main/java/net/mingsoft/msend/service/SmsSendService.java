/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.msend.service;

import cn.hutool.http.HttpUtil;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.msend.biz.ILogBiz;
import net.mingsoft.msend.entity.LogEntity;
import net.mingsoft.msend.entity.TemplateEntity;
import net.mingsoft.msend.util.SendUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service("smsSendService")
public class SmsSendService extends BaseSendService {

    @Override
    public boolean send(Map<String,String> configType, String[] toUser, TemplateEntity template, Map values) {
        Map<String, String> sms = ConfigUtil.getMap("短信配置");
        if (sms == null) {
            throw new BusinessException(String.format("%s不存在", configType));
        }
        String content = template.getTemplateSms();
        content = this.rendering(values, content);
        values.put("content",content);
        String smsUrl = this.rendering(values, sms.get("smsSendUrl"));
        String result = HttpUtil.post(smsUrl, "");
        LOG.debug("消息发送结果" + result);
        ILogBiz logBiz = SpringUtil.getBean(ILogBiz.class);
        for (int i = 0; i < toUser.length; i++) {
            LogEntity log = new LogEntity();
            log.setLogType(SMS);
            log.setTemplateId(Integer.parseInt(template.getId()));
            log.setLogDatetime(new Date());
            log.setLogContent(content);
            log.setLogReceive(toUser[i]);
            logBiz.save(log);
        }
        return true;
    }
}
