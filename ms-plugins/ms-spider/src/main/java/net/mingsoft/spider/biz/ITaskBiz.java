/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.spider.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.spider.bean.TaskRegularBean;
import net.mingsoft.spider.entity.TaskEntity;
import net.mingsoft.spider.entity.TaskRegularEntity;
import us.codecraft.webmagic.Spider;

import java.util.List;


/**
 * 采集任务业务
 * @author 铭软科技
 * 创建日期：2020-9-10 14:12:40<br/>
 * 历史修订：<br/>
 */
public interface ITaskBiz extends IBaseBiz<TaskEntity> {
    /**
     * 开始采集任务
     * @param taskId
     * @param regularIds
     * @return
     */
    void startCollect(String taskId, List<TaskRegularBean> regularIds);

    /**
     * 测试采集任务
     * @return
     */
    Spider startTest(TaskRegularEntity regularEntity);


    void job(String taskName);

    /**
     * 获取采集完成状态
     */
    boolean isCollection();
}
