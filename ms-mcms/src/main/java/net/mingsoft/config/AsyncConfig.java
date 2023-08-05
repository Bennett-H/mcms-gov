/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.config;

import net.mingsoft.co.cache.GeneraterProgressCache;
import net.mingsoft.co.service.ThreadPoolTaskExecutorService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 设置线程池配置
 * @author by 铭软开发团队
 * @Description TODO
 * @date 2019/11/20 15:20
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncConfig.class);

    @Value("${ms.thread.core-pool-size:5}")
    private int corePoolSize;

    @Value("${ms.thread.max-pool-size:20}")
    private int maxPoolSize;

    @Value("${ms.thread.core-pool-size:200}")
    private int queueCapacity;

    @Value("${ms.thread.keep-alive-seconds:50}")
    private int KeepAliveSeconds;

    @Value("${ms.thread.await-termination-seconds:60}")
    private int awaitTerminationSeconds;

    @Resource
    public GeneraterProgressCache generaterProgressCache;




    @Bean
    public org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor BackServiceHandleProcessThreadPool() {
        ThreadPoolTaskExecutorService executor = new ThreadPoolTaskExecutorService("Handle-");
        // 核心线程数：线程池创建时候初始化的线程数
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(maxPoolSize);
        // 缓冲队列：用来缓冲执行任务的队列
        executor.setQueueCapacity(queueCapacity);
        // 允许线程的空闲时间60秒：当超过了核心线程之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(KeepAliveSeconds);
        // 缓冲队列满了之后的拒绝策略：由调用线程处理（一般是主线程）
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor BackServiceDbBackUpThreadPool() {
        ThreadPoolTaskExecutorService executor = new ThreadPoolTaskExecutorService("BackUp-");
        // 核心线程数：线程池创建时候初始化的线程数
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(maxPoolSize);
        // 缓冲队列：用来缓冲执行任务的队列
        executor.setQueueCapacity(queueCapacity);
        // 允许线程的空闲时间60秒：当超过了核心线程之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(KeepAliveSeconds);
        // 缓冲队列满了之后的拒绝策略：由调用线程处理（一般是主线程）
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }



    @Bean
    public org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor cmsParserServiceContentThreadPool() {
        ThreadPoolTaskExecutorService executor = new ThreadPoolTaskExecutorService("content-");
        // 核心线程数：线程池创建时候初始化的线程数
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(maxPoolSize);
        // 缓冲队列：用来缓冲执行任务的队列
        executor.setQueueCapacity(queueCapacity);
        // 允许线程的空闲时间60秒：当超过了核心线程之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(KeepAliveSeconds);
        // 缓冲队列满了之后的拒绝策略：由调用线程处理（一般是主线程）
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor cmsParserServiceCategoryThreadPool() {
        ThreadPoolTaskExecutorService executor = new ThreadPoolTaskExecutorService("category-");
        // 核心线程数：线程池创建时候初始化的线程数
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(maxPoolSize);
        // 缓冲队列：用来缓冲执行任务的队列
        executor.setQueueCapacity(queueCapacity);
        // 允许线程的空闲时间60秒：当超过了核心线程之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(KeepAliveSeconds);
        // 缓冲队列满了之后的拒绝策略：由调用线程处理（一般是主线程）
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor renderingServiceThreadPool() {
        ThreadPoolTaskExecutorService executor = new ThreadPoolTaskExecutorService("write-file");
        // 核心线程数：线程池创建时候初始化的线程数
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(maxPoolSize);
        // 缓冲队列：用来缓冲执行任务的队列
        executor.setQueueCapacity(queueCapacity);
        // 允许线程的空闲时间60秒：当超过了核心线程之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(KeepAliveSeconds);
        // 缓冲队列满了之后的拒绝策略：由调用线程处理（一般是主线程）
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SpringAsyncExceptionHandler();
    }

    class SpringAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
            LOGGER.error("线程异常");
            throwable.printStackTrace();
            // 处理静态化异常后进度条卡死的问题
            for (Object obj : objects) {
                if (obj instanceof Map) {
                    String progressKey = ((Map<?, ?>) obj).get("progressKey").toString();
                    if (StringUtils.isBlank(progressKey)) {
                        continue;
                    }
                    // 线程异常后,如果有进度则把进度缓存减一
                    generaterProgressCache.saveOrUpdate(progressKey, generaterProgressCache.getInt(progressKey) - 1);
                }
            }
        }
    }
}
