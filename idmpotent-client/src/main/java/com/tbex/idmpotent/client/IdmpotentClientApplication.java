package com.tbex.idmpotent.client;

import com.tbex.idmpotent.client.cluster.ClusterCenter;
import com.tbex.idmpotent.client.pool.manager.NodePoolManager;
import com.tbex.idmpotent.client.pool.monitor.ConnectQueueMonitor;
import com.tbex.idmpotent.netty.server.init.RpcServerInitializer;
import com.tbex.idmpotent.server.config.IdmpotentServerConfig;
import com.tbex.idmpotent.server.consumer.RpcMsgConsumer;
import com.tbex.idmpotent.server.server.IdmpotentServer;
import com.tbex.idmpotent.server.server.IdmpotentServerHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.tbex.*"})
public class IdmpotentClientApplication implements ApplicationRunner, DisposableBean {


    @Autowired
    RpcServerInitializer rpcServerInitializer;

    @Autowired
    IdmpotentServerConfig idmpotentServerConfig;

    RpcMsgConsumer rpcMsgConsumer;

    @Autowired
    IdmpotentServerHandler idmpotentServerHandler;

    public static void main(String[] args) {
        SpringApplication.run(IdmpotentClientApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        /**初始化客户端连接池*/
        NodePoolManager.getInstance().initNodePool();
        /**监控rpc 服务*/
        ClusterCenter.getInstance().listenerServerRpc();
        /**监控连接池队列*/
        ConnectQueueMonitor.getInstance().start();
        /**启动消费IO消息线程*/
        rpcMsgConsumer = new RpcMsgConsumer(idmpotentServerHandler);
        rpcMsgConsumer.start();
    }

    @Override
    public void destroy() throws Exception {
        rpcMsgConsumer.stop();
    }
}

