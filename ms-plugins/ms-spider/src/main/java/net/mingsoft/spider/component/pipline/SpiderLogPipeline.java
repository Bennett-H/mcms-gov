/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.component.pipline;

import com.alibaba.fastjson.JSON;
import net.mingsoft.spider.bean.LogBean;
import net.mingsoft.spider.biz.ILogBiz;
import net.mingsoft.spider.constant.Const;
import net.mingsoft.spider.dao.ITaskDao;
import net.mingsoft.spider.entity.LogEntity;
import net.mingsoft.spider.entity.TaskEntity;
import net.mingsoft.spider.util.LogBatchImportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static net.mingsoft.spider.constant.Const.*;

/**
 * 持久化至数据库
 * thread safe
 */
@Component
public class SpiderLogPipeline implements Pipeline {

    private ILogBiz logBiz;

    private ITaskDao taskDao;

    /**
     * 自动导入所用的工具类
     */
    private LogBatchImportUtil batchImportUtil;

    private final static Logger log = LoggerFactory.getLogger(SpiderLogPipeline.class);

    public SpiderLogPipeline(ILogBiz logBiz, ITaskDao taskDao, LogBatchImportUtil batchImportUtil) {
        this.logBiz = logBiz;
        this.taskDao = taskDao;
        this.batchImportUtil = batchImportUtil;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<Map<String,String>> data = resultItems.get(DESTINATION);
        LogEntity logEntity = new LogEntity();
        logEntity.setTaskId(resultItems.get(TASK_ID));
        logEntity.setRegularId(resultItems.get(REGULAR_ID));
        logEntity.setSourceUrl(resultItems.getRequest().getUrl());
        logEntity.setContent(JSON.toJSONString(data));
        logEntity.setImported(NO);
        logEntity.setCreateDate(new Date());
        try {
            logBiz.save(logEntity);
            //自动导入
            TaskEntity taskEntity = taskDao.selectById(resultItems.get(TASK_ID));
            if (Const.YES.equals(taskEntity.getIsAutoImport())){
                LogBean logBean = new LogBean();
                logBean.setRegularId(resultItems.get(REGULAR_ID));
                logBean.setTaskId(resultItems.get(TASK_ID));
                logBean.setImported(NO);
                List list = logBiz.query(logBean);
                batchImportUtil.batchInsert(taskEntity.getImportTable(),list);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("出现重复url: {} , taskId: {}",resultItems.getRequest().getUrl(),resultItems.get(TASK_ID));
        }
    }
}
