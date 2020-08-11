package com.tbex.idmpotent.test;

import com.tbex.idmpotent.client.spring.DebugConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;

/**
 *
 */
@EnableAspectJAutoProxy
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},
        scanBasePackages = {
                "com.tbex.idmpotent.test",
                "com.tbex.idmpotent.client.*",
                "com.tbex.idmpotent.client.aspect"})
@PropertySource({"classpath:properties/redis.properties"})
public class TestRedisIdpApplication {

    public static void main(String[] args) {
        DebugConfig.toggleDebugMode();
        SpringApplication.run(TestRedisIdpApplication.class, args);
    }

}
