package com.tbex.idmpotent.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "topic.server")
@Data
public class IdmpotentServerConfig {

    /**
     * manager host
     */
    private String host = "127.0.0.1";

    /**
     * support  port
     */
    private int port;

    /**
     * netty heart check time (ms) 20s 心跳检测一次
     */
    private long heartTime = 20 * 1000;


    /*node*/
    private String nodeId;

    private Integer periodTime;

    private Integer countnum;



    /**
     * zk path
     */
    private String zkRoot;

    /**
     * zk address
     */
    private String zkAddress;

    private boolean zkSwitch;

}
