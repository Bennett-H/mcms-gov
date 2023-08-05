/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循铭软科技《保密协议》
 */


package net.mingsoft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author by 铭软开源团队
 * @Description TODO
 * @date 2020/5/23 9:44
 * 历史修订: 实时日志传输使用socket
 */
@Configuration("CoWebSocketConfig")
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 添加这个Endpoint，这样在网页中就可以通过websocket连接上服务,也就是我们配置websocket的服务地址,并且可以指定是否使用socketjs
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //websocket就是websocket的端点，客户端需要注册这个端点进行链接，withSockJS允许客户端利用sockjs进行浏览器兼容性处理
        registry.addEndpoint("/logwebsocket")
                .setAllowedOrigins("*")
                .withSockJS();
    }
    /**
     * 配置消息代理，哪种路径的消息会进行代理处理
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //设置服务器广播消息的基础路径
        //前端stompClient.subscribe路径前缀
        registry.enableSimpleBroker( "/logserver");

        //设置客户端订阅消息的基础路径
        //前端stompClient.send路径前缀
        registry.setApplicationDestinationPrefixes("/logclient");
    }
}
