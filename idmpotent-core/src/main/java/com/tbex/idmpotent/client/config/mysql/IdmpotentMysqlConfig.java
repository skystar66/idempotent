package com.tbex.idmpotent.client.config.mysql;

import com.tbex.idmpotent.client.http.BussinessInterceptor;
import com.tbex.idmpotent.client.http.HttpInterceptor;
import com.tbex.idmpotent.client.idpchecker.IdpChecker;
import com.tbex.idmpotent.client.idpchecker.blocking.DefaultIdpChecker;
import com.tbex.idmpotent.client.keyprovider.DefaultKeyProvider;
import com.tbex.idmpotent.client.keyprovider.KeyProvider;
import com.tbex.idmpotent.client.keystore.KeyStore;
import com.tbex.idmpotent.client.keystore.jdbc.JdbcKeyStore;
import com.tbex.idmpotent.client.spring.IdpInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @ClassName: IdmpotentConfig
 * @Description: todo java类作用描述
 * @Author: xuliang
 * @Date: 2020/4/20$ 上午10:48$
 * @Version: 1.0
 */
@Conditional(IdmpoentMysqlCondition.class)
@Configuration
public class IdmpotentMysqlConfig {


    @Bean
    public KeyProvider keyProvider() {
        return new DefaultKeyProvider();
    }

    /**
     * 数据持久化
     */

    @Bean
    public KeyStore keyStore(DataSource dataSource) {
        return new JdbcKeyStore()
                .setDataSource(dataSource);
    }


    /**
     * id checker
     */
    @Bean
    public IdpChecker idpChecker(KeyProvider keyProvider, KeyStore keyStore) {
        return new DefaultIdpChecker()
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
    public BussinessInterceptor bussinessInterceptor() {
        return new BussinessInterceptor();
    }


}
