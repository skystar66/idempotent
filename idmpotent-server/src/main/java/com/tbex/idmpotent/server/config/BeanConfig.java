package com.tbex.idmpotent.server.config;

import com.tbex.idmpotent.server.kit.serializer.MyZkSerializer;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {


    @Autowired
    IdmpotentServerConfig idmpotentServerConfig;



    @Bean
    public ZkClient buildZKClient() {
        ZkClient zkClient =  new ZkClient(idmpotentServerConfig.getZkAddress(), 30000);
        zkClient.setZkSerializer(new MyZkSerializer());
        return zkClient;
    }

}
