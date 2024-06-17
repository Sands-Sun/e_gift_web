package com.bayer.gifts.process.config;

import com.bayer.gifts.process.common.SelfRejectedExecutionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class ThreadPoolConfig {

    @Value("${async.executor.thread.core_pool_size}")
    private int corePoolSize;
    @Value("${async.executor.thread.max_pool_size}")
    private int maxPoolSize;
    @Value("${async.executor.thread.queue_capacity}")
    private int queueCapacity;
    @Value("${async.executor.thread.name.prefix}")
    private String namePrefix;

    @Bean("threadExecutor")
    public ThreadPoolTaskExecutor threadExecutor() {
        log.info("start config threadExecutor");
        ThreadPoolTaskExecutor threadPoolTaskExecutor = null;
        try {
            threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
            // 获取CPU核心数
            int i = Runtime.getRuntime().availableProcessors();
            //核心线程数目
            threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
            //指定最大线程数
            threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
            //队列中最大的数目
            threadPoolTaskExecutor.setQueueCapacity(maxPoolSize * 2 * 10);
            //线程空闲后的最大存活时间
            threadPoolTaskExecutor.setKeepAliveSeconds(60);
            //线程名称前缀
            threadPoolTaskExecutor.setThreadNamePrefix("threadExecutor-");
            //拒绝策略
            threadPoolTaskExecutor.setRejectedExecutionHandler(new SelfRejectedExecutionHandler());
            //当调度器shutdown被调用时等待当前被调度的任务完成
            threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
            //加载
            threadPoolTaskExecutor.initialize();
            log.info("init threadExecutor success");
        } catch (Exception e) {
            log.error("init threadExecutor fail: {}", e.getMessage());
        }
        return threadPoolTaskExecutor;
    }
}
