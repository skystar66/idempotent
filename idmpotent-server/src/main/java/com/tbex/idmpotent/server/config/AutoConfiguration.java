package com.tbex.idmpotent.server.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
@Slf4j
public class AutoConfiguration {


    /**
     * redis container用的threadPool
     *
     * @param config pool setting
     * @return taskExecutor
     */
    @Autowired
    @Bean("taskExecutor")
    @ConditionalOnBean(TaskExecutorConfig.class)
    public Executor taskExecutor(TaskExecutorConfig config) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(config.getName())
                .build();
        return new ThreadPoolExecutor(config.getCorePoolSize(), config.getMaxSize(), config.getKeepAlive(),
                TimeUnit.HOURS,
                new ArrayBlockingQueue<>(config.getQueueSize()), threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                super.rejectedExecution(r, e);
                log.warn("taskExecutor  DiscardOldestPolicy ", e);
            }
        });
    }

    /**
     * redis container用的threadPool
     *
     * @param config pool setting
     * @return taskExecutor
     */
    @Autowired
    @Bean("saveStore")
    @ConditionalOnBean(TaskExecutorConfig.class)
    public Executor taskExecutorSaveStore(TaskExecutorConfig config) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("async-save-idmpotent-server-task-pool %d")
                .build();
        return new ThreadPoolExecutor(10, 50, config.getKeepAlive(),
                TimeUnit.HOURS,
                new ArrayBlockingQueue<>(config.getQueueSize()), threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                super.rejectedExecution(r, e);
                log.warn("taskExecutor  DiscardOldestPolicy ", e);
            }
        });
    }


}
