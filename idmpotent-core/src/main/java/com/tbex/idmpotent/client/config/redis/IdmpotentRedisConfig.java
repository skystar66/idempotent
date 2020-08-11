package com.tbex.idmpotent.client.config.redis;

import com.tbex.idmpotent.client.http.BussinessInterceptor;
import com.tbex.idmpotent.client.http.HttpInterceptor;
import com.tbex.idmpotent.client.idpchecker.IdpChecker;
import com.tbex.idmpotent.client.idpchecker.blocking.DefaultIdpChecker;
import com.tbex.idmpotent.client.idpchecker.nonblocking.FastPassIdpChecker;
import com.tbex.idmpotent.client.keyprovider.DefaultKeyProvider;
import com.tbex.idmpotent.client.keyprovider.KeyProvider;
import com.tbex.idmpotent.client.keystore.KeyStore;
import com.tbex.idmpotent.client.keystore.redis.RedisClient;
import com.tbex.idmpotent.client.keystore.redis.RedisKeyStore;
import com.tbex.idmpotent.client.spring.IdpInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: IdmpotentConfig
 * @Description: todo redis 配置文件
 * @Author: xuliang
 * @Date: 2020/4/20$ 上午10:48$
 * @Version: 1.0
 */
@Conditional(IdmpoentRedisCondition.class)
@Configuration
public class IdmpotentRedisConfig {
    /**
     * 唯一id
     */
    @Bean
    public KeyProvider getKeyProvider() {
        return new DefaultKeyProvider();
    }

    /**
     * id存储中间件
     */
    @Bean
    public KeyStore getKeyStore(RedisClient redisClient) {
        return new RedisKeyStore()
                .setRedisClient(redisClient);
    }

    /**
     * id check  策略
     */
    @Bean
    public IdpChecker getIdpChecker(KeyProvider keyProvider, KeyStore keyStore) {
        return new FastPassIdpChecker()
                .setKeyProvider(keyProvider)
                .setKeyStore(keyStore);
    }

    /**
     * 幂等切面
     */
    @Bean
    public IdpInterceptor idpInterceptor(IdpChecker idpChecker) {
        return new IdpInterceptor()
                .setIdpChecker(idpChecker);
    }

    @Bean
    public HttpInterceptor httpInterceptor() {
        return new HttpInterceptor();
    }


    @Bean
    public BussinessInterceptor bussinessInterceptor(IdpChecker idpChecker, KeyProvider keyProvider) {
        return new BussinessInterceptor()
                .setIdpChecker(idpChecker).setKeyProvider(keyProvider);
    }


}
