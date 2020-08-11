package com.tbex.idmpotent.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author boyce
 * @date 2017/10/10
 */
@Component
@ConfigurationProperties(prefix = "executor.task")
@Data
public class TaskExecutorConfig {
    private String name;
    private int corePoolSize;
    private int maxSize;
    private int keepAlive;
    private int queueSize;
}
