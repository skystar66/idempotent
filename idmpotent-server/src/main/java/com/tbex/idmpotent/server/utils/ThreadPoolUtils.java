package com.tbex.idmpotent.server.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ThreadPoolUtils {


    private static final class ThreadPoolUtilsHold{
        private static final ThreadPoolUtils instance = new ThreadPoolUtils();
    }

    public static ThreadPoolUtils getInstance(){
        return ThreadPoolUtilsHold.instance;
    }

    private ThreadPoolUtils(){
        init();
    }

    private static ExecutorService executorService;

    public ExecutorService init(){
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("bussiness-thread-pool")
                .build();
        executorService = new ThreadPoolExecutor(50, 100, 1000,
                TimeUnit.HOURS,
                new ArrayBlockingQueue<>(500), threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                super.rejectedExecution(r, e);
                log.warn("taskExecutor  DiscardOldestPolicy ", e);
            }
        });
        return executorService;
    }

    public void shutDown(){

        executorService.shutdown();
    }


    public boolean isTerminated(){

        return executorService.isTerminated();
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
    public void setExecutorService(ExecutorService executorService) {
       this.executorService = executorService;
    }
}
