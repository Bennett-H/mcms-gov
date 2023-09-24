/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.component.pipline;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import net.mingsoft.spider.bean.SocketMsgBean;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.jsoup.parser.Parser;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.mingsoft.spider.constant.Const.*;

/**
 * 测试场景: 发送数据至指定频道
 * thread safe
 */
@Component
public class WebSocketPipeline implements Pipeline {

    private SimpMessagingTemplate messagingTemplate;

    public WebSocketPipeline(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<Map<String,String>> data = resultItems.get(DESTINATION);
        //数据在后端转换
        HashMap<String, String> map = new HashMap<>();
        map.put("msg","成功");
        data.forEach(d-> {
            //是否校验html文本内容
            if("true".equalsIgnoreCase(d.get(IS_HTML))){
                String content = d.get(DATA);
                try {
                    parsingHtml(content);
                } catch (DocumentException e) {
                    map.put("msg","内容格式错误:"+e.getMessage().toString());
                }
            }


            map.put(d.get(FILED),d.get(DATA));
        });
        messagingTemplate.convertAndSend(SPIDER_PUBLISH_DESTINATION,
                SocketMsgBean.buildData(resultItems.getRequest().getUrl(),
                        JSON.toJSONString(map)));
    }

    /**
     * 解析html文本格式是否合法
     */
    private void parsingHtml(String content) throws DocumentException {
        //防止内容没有根标签,解析报错
        String html = "<div>" + content + "</div>";
        //把html的转移字符换成真实字符,否则报错
        html = Parser.unescapeEntities(html, true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                html.getBytes());
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(inputStream);
        }finally {
            IoUtil.close(inputStream);
        }
    }
}
