/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.action;

import com.alibaba.fastjson.JSON;
import net.mingsoft.spider.bean.SocketMsgBean;
import net.mingsoft.spider.biz.ITaskBiz;
import net.mingsoft.spider.constant.e.MessageEnum;
import net.mingsoft.spider.entity.TaskRegularEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;

import java.util.concurrent.ConcurrentHashMap;

import static net.mingsoft.spider.constant.Const.SPIDER_PUBLISH_DESTINATION;

@RestController
public class WebSocketAction {

    @Autowired
    private ITaskBiz taskBiz;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private ConcurrentHashMap<String, Spider> spiderMap = new ConcurrentHashMap<>();

    /**
     * 服务器接收消息的地址
     * 用于爬虫测试功能
     * @param msgBean 双方通信对象
     */
    @MessageMapping("/spider/test")
    public void getMsg(@RequestBody SocketMsgBean msgBean) {
        MessageEnum messageEnum = MessageEnum.findMessageEnumByStr(msgBean.getAction());

        if (null == messageEnum) {
            return;
        }

        switch (messageEnum){
            case START_UP: {
                Spider spider = taskBiz.startTest(JSON.parseObject(msgBean.getData(), TaskRegularEntity.class));
                spiderMap.put(msgBean.getUser(),spider);
                spider.run();
                spiderMap.remove(msgBean.getUser());
                //发送完成消息
                messagingTemplate.convertAndSend(SPIDER_PUBLISH_DESTINATION,
                        SocketMsgBean.buildFinish());
                break; }
            case STOP:{
                Spider remove = spiderMap.remove(msgBean.getUser());
                if (null !=remove){
                    remove.stop();
                }
                break; }
            default:{
                // ignore
                break; }
        }

    }
}
