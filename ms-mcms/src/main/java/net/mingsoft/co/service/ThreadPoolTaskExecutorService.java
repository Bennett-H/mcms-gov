/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.co.service;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池通用类
 * @Author: 铭软开发团队
 * @Date: 2022/11/9 20:47
 */
public  class ThreadPoolTaskExecutorService extends ThreadPoolTaskExecutor {


    /**
     * 构造方法
     * @param name 线程池名前缀
     */
    public ThreadPoolTaskExecutorService(String name) {
        ThreadGroup group = new ThreadGroup(name);
        this.setThreadGroup(group);
        // 线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        this.setThreadNamePrefix(name+"-");
    }

    /**
     * 重置线程池资源
     */
    public void restart() {

        int corePoolSize = this.getCorePoolSize();
        int maxPoolSize = this.getMaxPoolSize();

        //int awaitTerminationSeconds = this.getaw

        this.setCorePoolSize(corePoolSize);
        this.setMaxPoolSize(maxPoolSize);
        this.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 缓冲队列满了之后的拒绝策略：由调用线程处理（一般是主线程）
        this.setAllowCoreThreadTimeOut(true);
        // 强制关闭
        this.setWaitForTasksToCompleteOnShutdown(false);
        this.setAwaitTerminationSeconds(60);

        this.shutdown();
        this.initialize();
    }
}
