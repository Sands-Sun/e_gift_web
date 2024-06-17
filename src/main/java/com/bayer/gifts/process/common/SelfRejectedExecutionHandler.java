package com.bayer.gifts.process.common;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class SelfRejectedExecutionHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        if (null != runnable) {
            // 线程池没来得及执行的任务先放入队列
            ThreadReader.put(runnable);
        }
    }
}
