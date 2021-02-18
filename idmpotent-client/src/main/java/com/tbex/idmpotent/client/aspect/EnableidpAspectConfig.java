package com.tbex.idmpotent.client.aspect;

import com.tbex.idmpotent.client.checker.IdpInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: EnableidpAspectConfig
 * @Description: todo 注解切面配置类
 * @Author: xuliang
 * @Date: 2020/4/20 上午10:32
 * @Version: 1.0
 */
@Aspect
@Configuration
@Slf4j
public class EnableidpAspectConfig {


    @Autowired
    IdpInterceptor idpInterceptor;

    /**
     * 幂等切点
     */
    @Pointcut("@annotation(com.tbex.idmpotent.client.annotation.EnableIdp)")
    public void interceptIdpInter() {
    }

    @Around("interceptIdpInter()")
    public Object interceptHttp(ProceedingJoinPoint pjp) throws Throwable {
        return idpInterceptor.intercept(pjp);
    }


}
