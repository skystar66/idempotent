package com.tbex.idmpotent.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "topic.client")
@Data
public class IdmpomtentClientConfig {

    /**
     * manager host
     */
    private String host = "127.0.0.1";

    /**
     * support  port
     */
    private int port;


    /**
     * 每个server的连接池数
     */

    private int poolServerSize;


    /**
     * zk path
     */
    private String zkRoot;

    /**
     * zk address
     */
    private String zkAddress;


}
