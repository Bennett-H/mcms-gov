/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.mweixin.bean;

import java.util.*;

/**
 * 微信消息模板bean
 * @author by 铭飞开源团队
 * @Description TODO
 * @date 2023/6/19 15:49
 */
public class TemplateBean {

    /**
     * 关键词Map，和模板实体的templateKeyword字段相对应，templateKeyword使用freemarker语法获取wordList参数<br/>
     * 例：模板为
     *          标题: {{first.DATA}}
     *          项目名称：{{projectName.DATA}}
     *          奖金金额：{{money.DATA}}
     *    templateKeyword的值应为: '${first},${name},${money}',且使用逗号分隔
     *    则map中参数必须包含这几个参数，例如{money: 0.02,first: '这是第一行字',name: '项目名'}
     */
    private Map<String,String> wordList;

    /**
     * 接收人集合，多个openId之间用逗号隔开
     */
    private String openIds;

    /**
     * 模板点击跳转地址
     */
    private String url;

    /**
     * 模板标题
     */
    private String templateCode;


    public Map<String, String> getWordList() {
        return wordList;
    }

    public void setWordList(Map<String, String> wordList) {
        this.wordList = wordList;
    }

    public String getOpenIds() {
        return openIds;
    }

    public void setOpenIds(String openIds) {
        this.openIds = openIds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUrl(Url url) {
        this.url = url.toString();
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    /**
     * 会自动拼接出微信消息详情的链接地址
     */
    public static class Url {
        /**
         * 链接地址
         */
        private String link = "";
        /**
         * 消息类型，由外部业务定义
         */
        private String type = "";
        /**
         * 业务数据id
         */
        private String id = "";

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return this.link.concat("?").concat("type=").concat(this.type).concat("&id=").concat(this.id);
        }
    }
}
