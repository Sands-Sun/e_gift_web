//package com.bayer.gifts.process.common;
//
//import com.google.common.util.concurrent.ThreadFactoryBuilder;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Component
//public class CustomizeScheduler implements ApplicationListener<ContextRefreshedEvent> {
//
//    @Autowired
//    private ThreadReader threadReader;
//
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent event) {
//        // 线程工厂
//        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("scheduledExecutor-pool-%d")
//                .setUncaughtExceptionHandler((thread, throwable) -> log.error("ThreadPool {} got exception", thread, throwable)).build();
//        ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(1, threadFactory, new SelfRejectedExecutionHandler());
//        // 之后每隔1秒执行队列中没有来得及执行的任务
//        scheduledExecutor.scheduleAtFixedRate(() -> threadReader.take(), 1, 1000, TimeUnit.MILLISECONDS);
//    }
//}
