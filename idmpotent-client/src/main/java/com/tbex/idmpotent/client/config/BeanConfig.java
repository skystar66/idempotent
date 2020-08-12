package com.tbex.idmpotent.client.config;

import com.tbex.idmpotent.client.kit.serializer.MyZkSerializer;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

@Configuration
public class BeanConfig {


    @Autowired
    IdmpomtentClientConfig idmpomtentClientConfig;



    @Bean
    public ZkClient buildZKClient() {
        ZkClient zkClient =  new ZkClient(idmpomtentClientConfig.getZkRoot(), 30000);
        zkClient.setZkSerializer(new MyZkSerializer());
        return zkClient;
    }

}
