/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.component.process;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import freemarker.template.TemplateException;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.config.MSProperties;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.mdiy.util.ParserUtil;
import net.mingsoft.spider.biz.ILogBiz;
import net.mingsoft.spider.biz.ITaskRegularBiz;
import net.mingsoft.spider.component.SpiderContext;
import net.mingsoft.spider.component.pipline.StaticPipeline;
import net.mingsoft.spider.entity.LogEntity;
import net.mingsoft.spider.entity.TaskRegularEntity;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static net.mingsoft.spider.constant.Const.*;

/**
 * 通用处理器
 * 1.在目标地址中规定范围，
 * 在范围内匹配内容链接
 * <p>
 * 2.在内容页面中匹配对应的内容 交由 {@link Pipeline} 处理
 */
public class DefaultProcessor implements PageProcessor {

    private Site site;

    private SpiderContext context;

    public DefaultProcessor(SpiderContext context) {
        this.context = context;
        //TODO 抽参数,可配置选项
        this.site = new Site()
                .setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36")
                //字符编码集 (非法的字符或不匹配的字符编码集可能出现乱码)
//                .setCharset(context.getCharset())
                //请求时间间隔
                .setSleepTime(1000)
                //超时时间
                .setTimeOut(3000)
                //重试次数
                .setRetryTimes(3)
                //重试时间间隔
                .setRetryTimes(3000);
    }

    @Override
    public void process(Page page) {

        //当前页面源码
        String rawText = page.getRawText();
        //手动解析html，防止标签缺失
        Document doc = Jsoup.parse(rawText);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.html);
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        Html html = new Html(doc);
        //优先监测列表区域
        //Tip: 当前selectable实例为PlainText无法支持Css和Xpath
        Selectable area;
        if (JSONUtil.isJson(rawText)) {
            Json json = new Json(rawText.replaceAll("\\\\u0026", "&"));
            area = json.regex(context.getAreaExpression());
        }else {
            area = html.regex(context.getAreaExpression());
        }

        if (area.get() == null) {
            //判断
            List<Map<String, String>> content = new ArrayList<>();
            context.getFiledParsers().
                    forEach(
                            p -> {
                                Map<String, String> data = new HashMap<>(8);
                                String fieldFormat = p.getContent().getFieldFormat();

                                //判断是否为列表规则
                                if ("true".equalsIgnoreCase(p.getContent().getIsMatch())) {
                                    Map<String, HashMap<String, List<String>>> dataMap = (Map<String, HashMap<String, List<String>>>) context.getParameterMap().get("dataMap");
                                    HashMap<String, List<String>> hashMap = dataMap.get(p.getContent().getFiled());
                                    if (CollUtil.isNotEmpty(hashMap)) {
                                        List<String> list = hashMap.get(page.getRequest().getUrl());
                                        if (CollUtil.isNotEmpty(list)) {
                                            String format = format(fieldFormat, list);
                                            data.put(DATA, format);
                                            data.put(FILED, p.getContent().getFiled());
                                            data.put(FILED_TYPE, p.getContent().getFieldType());
                                            data.put(SOURCE, JSON.toJSONString(list));
                                            content.add(data);
                                        }
                                    }
                                } else {
                                    String format = format(fieldFormat, p.parser(page, html).get());
                                    data.put(DATA, format);
                                    data.put(FILED, p.getContent().getFiled());
                                    data.put(FILED_TYPE, p.getContent().getFieldType());
                                    content.add(data);
                                }

                                //判断是否是html内容
                                if ("true".equalsIgnoreCase(p.getContent().getIsHtml())) {
                                    data.put(IS_HTML,"true");
                                }

                                //设置静态资源下载目录   开始
                                if ("appId".equalsIgnoreCase(p.getContent().getName())){
                                    String appId = p.getContent().getExpression();
                                    String uploadPath = ConfigUtil.getString("文件上传配置", "uploadPath", MSProperties.upload.path);
                                    String staticFolder = uploadPath.concat(File.separator).concat(appId)
                                            .concat(File.separator).concat(MODEL);
                                    StaticPipeline.staticFolderThreadLocal.set(staticFolder);
                                }
                                //设置静态资源下载目录  结束


                            }
                    );
            //增加content栏目匹配的数据

            page.putField(DESTINATION, content);
            page.putField(TASK_ID, context.getTaskId());
            page.putField(REGULAR_ID, context.getRegularId());
            page.putField(STATIC_FOLDER, context.getStaticFolder());

        } else {
            //划分区域,搜索内容链接
            //1.划分区域
            if(!area.match()) {
                throw new BusinessException("没有匹配到列表区域");
            }
            //2.搜索指定区域中的内容链接
            List<String> allLinks = area.regex(context.getUrlExpression())
                    .all()
                    .stream()
                    .distinct()
                    .collect(Collectors.toList());
            //3.获取栏目页面数据
            ITaskRegularBiz taskRegularBiz = SpringUtil.getBean(ITaskRegularBiz.class);
            TaskRegularEntity taskRegularEntity = taskRegularBiz.getById(context.getRegularId());

            List<Map<String, Object>> dataList = JSON.parseObject(taskRegularEntity.getMetaData(), List.class);
            //过滤需要匹配栏目数据的规则
            dataList = dataList.stream().filter(
                    dataMap -> "true".equalsIgnoreCase(dataMap.get("isMatch") == null ? "" : dataMap.get("isMatch").toString())
            ).collect(Collectors.toList());
            //格式: key: 字段名,value:{key: 文章url, value:匹配到的list集合}
            HashMap<String, HashMap<String, List<String>>> dataMap = new HashMap<>();
            // 获取到数据
            for (Map<String, Object> map : dataList) {
                HashMap<String, List<String>> hashMap = new HashMap<>();
                for (String link : allLinks) {
                    String type = map.get("type").toString();
                    List<String> list = null;
                    switch (type){
                        case REGULAR:
                            list = html.regex(StrUtil.format(map.get("expression").toString(), link)).all().stream().distinct().collect(Collectors.toList());
                            break;
                        case XPATH:
                            list = html.xpath(StrUtil.format(map.get("expression").toString(), link)).all().stream().distinct().collect(Collectors.toList());
                            break;
                        case CSS:
                            list = html.css(StrUtil.format(map.get("expression").toString(), link)).all().stream().distinct().collect(Collectors.toList());
                            break;
                    }
                    if (CollUtil.isNotEmpty(list)) {
                        hashMap.put(link, list);
                    }
                }
                dataMap.put(map.get("filed").toString(), hashMap);
            }





            //设置到上下文中
            context.getParameterMap().put("dataMap", dataMap);


            ILogBiz logBiz = SpringUtil.getBean(ILogBiz.class);
            List<String> urls = CollUtil.newLinkedList();
            for (String allLink : allLinks) {
                //如果当前连接在日志则不采集
                LambdaQueryWrapper<LogEntity> logWrapper = new QueryWrapper<LogEntity>().lambda();
                logWrapper.eq(LogEntity::getTaskId, context.getTaskId());
                logWrapper.like(LogEntity::getSourceUrl, allLink);
                List<LogEntity> _list = logBiz.list(logWrapper);
                if (CollUtil.isEmpty(_list)) {
                    urls.add(allLink);
                }
            }
            page.addTargetRequests(urls);
            //当前页不进行内容解析
            page.setSkip(true);
        }


    }

    @Override
    public Site getSite() {
        return site;
    }

    /**
     * 格式化数据
     * @return
     */
    private String format(String template,Object obj){
        if (StringUtils.isBlank(template)){
            return obj == null ? null:obj.toString();
        }
        // 为空时直接返回空
        if (obj==null || StringUtils.isBlank(obj.toString())) {
            return obj == null ? null:obj.toString();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("content",obj);
        AppEntity app = BasicUtil.getApp();
        map.put("app",app);
        try {
            return ParserUtil.rendering(map, template);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }



}
