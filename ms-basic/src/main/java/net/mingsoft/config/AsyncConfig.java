/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 设置线程池配置
 * @author by 铭软开发团队
 * @Description TODO
 * @date 2019/11/20 15:20
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${ms.thread.core-pool-size:10}")
    private int corePoolSize;

    @Value("${ms.thread.max-pool-size:50}")
    private int maxPoolSize;

    @Value("${ms.thread.core-pool-size:1000}")
    private int queueCapacity;

    @Value("${ms.thread.keep-alive-seconds:300}")
    private int KeepAliveSeconds;

    @Value("${ms.thread.thread-name-prefix:lh-something-}")
    private String threadNamePrefix;

    @Value("${ms.thread.await-termination-seconds:60}")
    private int awaitTerminationSeconds;


    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：线程池创建时候初始化的线程数
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(maxPoolSize);
        // 缓冲队列：用来缓冲执行任务的队列
        executor.setQueueCapacity(queueCapacity);
        // 允许线程的空闲时间60秒：当超过了核心线程之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(KeepAliveSeconds);
        // 线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix(threadNamePrefix);
        // 缓冲队列满了之后的拒绝策略：由调用线程处理（一般是主线程）
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
