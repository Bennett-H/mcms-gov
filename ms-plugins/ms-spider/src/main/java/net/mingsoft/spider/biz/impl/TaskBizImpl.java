/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.spider.biz.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.config.MSProperties;
import net.mingsoft.mdiy.util.ConfigUtil;
import net.mingsoft.spider.bean.LogBean;
import net.mingsoft.spider.bean.SocketMsgBean;
import net.mingsoft.spider.bean.TaskRegularBean;
import net.mingsoft.spider.biz.ILogBiz;
import net.mingsoft.spider.biz.ITaskBiz;
import net.mingsoft.spider.biz.ITaskRegularBiz;
import net.mingsoft.spider.component.ParserDefinition;
import net.mingsoft.spider.component.SpiderContext;
import net.mingsoft.spider.component.builder.UrlCreatorAdapter;
import net.mingsoft.spider.component.downloader.ExtraHttpClientDownloader;
import net.mingsoft.spider.component.paser.AbstractParser;
import net.mingsoft.spider.component.pipline.SpiderLogPipeline;
import net.mingsoft.spider.component.pipline.StaticPipeline;
import net.mingsoft.spider.component.pipline.WebSocketPipeline;
import net.mingsoft.spider.component.process.DefaultProcessor;
import net.mingsoft.spider.constant.Const;
import net.mingsoft.spider.dao.ITaskDao;
import net.mingsoft.spider.dao.ITaskRegularDao;
import net.mingsoft.spider.entity.TaskEntity;
import net.mingsoft.spider.entity.TaskRegularEntity;
import net.mingsoft.spider.util.LogBatchImportUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.codecraft.webmagic.Spider;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.mingsoft.spider.constant.Const.*;

/**
 * 采集任务管理持久化层
 *
 * @author 铭软科技
 * 创建日期：2020-9-10 14:12:40<br/>
 * 历史修订：<br/>
 */
@Service("spidertaskBizImpl")
@Transactional
public class TaskBizImpl extends BaseBizImpl<ITaskDao,TaskEntity> implements ITaskBiz {


    @Autowired
    private ITaskDao taskDao;

    @Autowired
    private ITaskRegularBiz taskRegularBiz;

    @Autowired
    private ILogBiz logBiz;

    /**
     * 构建url的适配器
     */
    @Autowired
    private UrlCreatorAdapter urlCreatorAdapter;

    /**
     * 请求http页面的下载器
     */
    @Autowired
    private ExtraHttpClientDownloader httpClientDownloader;

    /**
     * 存储至数据日志表的pipeline
     */
    @Autowired
    private SpiderLogPipeline spiderLogPipeline;

    /**
     * webSocket交互所用的pipeline
     */
    @Autowired
    private WebSocketPipeline webSocketPipeline;

    /**
     * 自动导入所用的工具类
     */
    @Autowired
    private LogBatchImportUtil batchImportUtil;

    /**
     * webSocket发送消息
     */
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Autowired
    private ITaskRegularDao taskRegularDao;

    /**
     * 处理img标签pipeline
     */
    @Autowired
    private StaticPipeline staticPipeline;

    /**
     * 采集完成状态
     */
    private boolean collection = true;

    @Override
    protected IBaseDao getDao() {
        // TODO Auto-generated method stub
        return taskDao;
    }

    @Override
    @Async
    public void startCollect(String taskId, List<TaskRegularBean> regulars) {
        collection = false;
        //1.获取
        String uploadPath = ConfigUtil.getString("文件上传配置", "uploadPath", MSProperties.upload.path);
        //2.构建解析上下文
        List<SpiderContext> contexts = new ArrayList<>();
        regulars.forEach(t -> {
            //根据表名存放图片
            String staticFolder = uploadPath.concat(File.separator).concat(BasicUtil.getApp().getId())
                    .concat(File.separator).concat(MODEL);
            SpiderContext context = buildSpiderContext(taskId, t);
            context.setStaticFolder(staticFolder);
            contexts.add(context);
        });

        //TODO 线程池抽取出来自定义 ?
        contexts.forEach(c -> {
                    Spider.create(new DefaultProcessor(c))
                            .startUrls(c.getLinks())
                            .setDownloader(httpClientDownloader)
                            .addPipeline(staticPipeline)
                            .addPipeline(spiderLogPipeline)
                            .thread(c.getThreadNum())
                            .setExitWhenComplete(true)
                            .run();

                    //异步导入数据库
                    try {
                        syncImport(taskId,c.getRegularId());
                    } catch(Exception e) {
                        collection = true;
                        e.printStackTrace();
                        LOG.debug("同步数据异常");
                    }

                }
        );
        collection = true;
    }

    @Override
    public Spider startTest(TaskRegularEntity regularEntity) {
        SpiderContext spiderContext = buildSpiderContext("-1", regularEntity);
        //最终所构建出的urls
        messagingTemplate.convertAndSend(SPIDER_PUBLISH_DESTINATION,
                JSON.toJSON(SocketMsgBean.buildUrl(spiderContext.getLinks())));

        return Spider.create(new DefaultProcessor(spiderContext))
                .startUrls(spiderContext.getLinks())
                .setDownloader(httpClientDownloader)
                .addPipeline(webSocketPipeline)
                .thread(1)
                .setExitWhenComplete(true);
    }


    @Async("taskExecutor")
    public void syncImport(String taskId, String regularId) {
        TaskEntity task = taskDao.selectById(taskId);
        if (Const.YES.equals(task.getIsAutoImport())){
            //导入
            LogBean logBean = new LogBean();
            logBean.setRegularId(regularId);
            logBean.setTaskId(taskId);
            logBean.setImported(NO);
            List list = logBiz.query(logBean);
            batchImportUtil.batchInsert(task.getImportTable(),list);
        }
    }

    private SpiderContext buildSpiderContext(String taskId, TaskRegularEntity t){
        List<ParserDefinition> definitions = JSON.parseArray(t.getMetaData(), ParserDefinition.class);
        //2.1字段解析器
        List<AbstractParser> filedParsers = definitions.stream().map(AbstractParser::convertToParser).collect(Collectors.toList());

        //2.2构建初始链接
        List<String> urls;
        if (YES.equalsIgnoreCase(t.getIsPage())) {
            urls = urlCreatorAdapter.build(t.getLinkUrl().trim(), t.getStartPage(), t.getEndPage());
        } else {
            urls = Collections.singletonList(t.getLinkUrl().trim());
        }

        //2.4创建上下文

        SpiderContext context = new SpiderContext(taskId,
                t.getId(),
                urls,
                t.getArticleUrl(),
                t.getStartArea() + MIDDLE + t.getEndArea(),
                filedParsers);
        context.setCharset(t.getCharset());
        context.setThreadNum(t.getThreadNum());
        return context;
    }

    @Override
    public void job(String taskName) {
        LOG.debug("开始采集任务");
        TaskEntity task = new TaskEntity();
        task.setTaskName(taskName);
        task = taskDao.selectOne(new QueryWrapper<>(task));
        List<TaskRegularBean> collect = taskRegularDao.selectList(new QueryWrapper<>()).stream().map(taskRegularEntity -> {
            TaskRegularBean taskRegularBean = new TaskRegularBean();
            BeanUtils.copyProperties(taskRegularEntity, taskRegularBean);
            return taskRegularBean;
        }).collect(Collectors.toList());
        if(!StringUtils.isBlank(task.getId())) {
            this.startCollect(task.getId(),collect);
        }
        LOG.debug("采集任务结束");

    }

    @Override
    public boolean isCollection() {
        return collection;
    }

}
