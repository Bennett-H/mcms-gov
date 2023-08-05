/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */


package net.mingsoft.msend.service;

import cn.hutool.json.JSONUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.msend.biz.ILogBiz;
import net.mingsoft.msend.entity.LogEntity;
import net.mingsoft.msend.entity.TemplateEntity;
import net.mingsoft.msend.util.SendcloudUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service("sendCouldSmsService")
public class SendCouldSmsService extends BaseSendService {
    @Override
    public boolean send(Map<String, String> config, String[] toUser, TemplateEntity template, Map values) {
        ILogBiz logBiz = SpringUtil.getBean(ILogBiz.class);


        //注意：templateSms这里必须填写 SendCould的消息模板id，所以这了用了数值型判断
        String templateSms = template.getTemplateSms();
        if (!StringUtil.isInteger(templateSms)) {
            LOG.error("sendcloud 的模板id不正确");
            return false;
        }

        boolean flag = false;
        for (int i = 0; i < toUser.length; i++) {
            flag = SendcloudUtil.sendSms(
                    config.get("smsUsername"),
                    config.get("smsPassword"),
                    Integer.parseInt(templateSms),
                    "0",
                    toUser[i],
                    JSONUtil.toJsonStr(values));

            if (flag) {
                LogEntity log = new LogEntity();
                log.setLogType(SMS);
                log.setTemplateId(Integer.parseInt(template.getId()));
                log.setLogDatetime(new Date());
                log.setLogContent(JSONUtil.toJsonStr(values));
                log.setLogReceive(toUser[i]);
                logBiz.save(log);
            } else {
                LOG.error("发送失败：" + toUser[i]);
                break;
            }
        }
        return flag;
    }
}
