/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.co.bean;

import net.mingsoft.co.constant.e.MessageEnum;

import java.util.Collections;
import java.util.List;

/**
 * 用于socket传递消息
 * 历史修订: 实时日志传输需要
 */
public class SocketMsgBean {

    /**
     * 用于表示当前属于什么操作 see net.mingsoft.co.constant.e.MessageEnum
     */
    private String action;
    /**
     *  传输的数据(json格式)
     */
    private String data;
    /**
     * 当前用户
     */
    private String user;
    /**
     * 解析的url
     */
    private List<String> links;

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    /**
     *  服务器向客户端 发送构建出的url的信息格式
     * @param links 目标链接
     * @return
     */
    public static SocketMsgBean buildUrl(List<String> links){
        SocketMsgBean msgBean = new SocketMsgBean();
        msgBean.setAction(MessageEnum.URL.action);
        msgBean.setLinks(links);
        return msgBean;
    }

    /**
     * 服务器向客户端 发送匹配到的内容 信息格式
     * @param contentUrl 内容链接
     * @param data 匹配的内容数据
     * @return
     */
    public static SocketMsgBean buildData(String contentUrl, String data){
        SocketMsgBean msgBean = new SocketMsgBean();
        msgBean.setAction(MessageEnum.CONTENT.action);
        msgBean.setLinks(Collections.singletonList(contentUrl));
        msgBean.setData(data);
        return msgBean;
    }

    /**
     * 服务器向客户端发送完成信息
     * @return
     */
    public static SocketMsgBean buildFinish(){
        SocketMsgBean msgBean = new SocketMsgBean();
        msgBean.setAction(MessageEnum.FINISH.action);
        return msgBean;
    }
}
