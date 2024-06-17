package com.bayer.gifts.process.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class ThreadReader {

    private static final BlockingQueue<Runnable> BLOCKING_QUEUE = new LinkedBlockingQueue<>();

    @Autowired
    @Qualifier("threadExecutor")
    private ThreadPoolTaskExecutor threadExecutor;

    public static void put(Runnable runnable) {
        try {
            BLOCKING_QUEUE.put(runnable);
        } catch (InterruptedException e) {
            log.error("ThreadReader.put异常:{}", e.getMessage());
        }
    }

    public void take() {
        if (CollectionUtils.isEmpty(BLOCKING_QUEUE)) {
            return;
        }
        try {
            Runnable runnable = BLOCKING_QUEUE.take();
            log.info("取出当前线程池没来得及执行的任务, runnable:{}", runnable);
            threadExecutor.execute(runnable);
        } catch (InterruptedException e) {
            log.error("ThreadReader.take异常:{}", e.getMessage());
        }
    }
}
