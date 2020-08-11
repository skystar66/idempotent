package com.tbex.idmpotent.server;

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
public class IdmpotentServerApplication implements ApplicationRunner, DisposableBean {


    @Autowired
    RpcServerInitializer rpcServerInitializer;

    @Autowired
    IdmpotentServerConfig idmpotentServerConfig;

    RpcMsgConsumer rpcMsgConsumer;

    @Autowired
    IdmpotentServerHandler idmpotentServerHandler;

    public static void main(String[] args) {
        SpringApplication.run(IdmpotentServerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread thread = new Thread(new IdmpotentServer(rpcServerInitializer, idmpotentServerConfig));
        thread.setDaemon(true);
        thread.start();

        rpcMsgConsumer = new RpcMsgConsumer(idmpotentServerHandler);
        rpcMsgConsumer.start();
    }

    @Override
    public void destroy() throws Exception {
        rpcMsgConsumer.stop();
    }
}

