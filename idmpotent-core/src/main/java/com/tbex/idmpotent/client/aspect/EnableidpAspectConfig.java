package com.tbex.idmpotent.client.aspect;

import com.tbex.idmpotent.client.http.HttpInterceptor;
import com.tbex.idmpotent.client.spring.IdpInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
* @ClassName:      EnableidpAspectConfig
* @Description:    todo 注解切面配置类
* @Author:         xuliang
* @Date:     2020/4/20 上午10:32
* @Version:        1.0
*/
@Aspect
@Configuration
@Slf4j
public class EnableidpAspectConfig {

  private HttpInterceptor httpInter;

  private IdpInterceptor idpInter;

  @Autowired
  public EnableidpAspectConfig(HttpInterceptor httpInter, IdpInterceptor idpInter) {
    this.httpInter = httpInter;
    this.idpInter = idpInter;
  }

  /**
   * 幂等切点
   */
  @Pointcut("@annotation(com.tbex.idmpotent.client.annotation.EnableIdp)")
  public void interceptIdpInter() {
  }

  @Around("interceptIdpInter()")
  @Order(1)
  public Object interceptHttp(ProceedingJoinPoint pjp) throws Throwable {
    log.info("aspect http interfacept ");
    return httpInter.intercept(pjp);
  }

  /**
   * 织入通知，注意织入顺序
   */
  @Around("interceptIdpInter()")
  @Order(2)
  public Object interceptIdp(ProceedingJoinPoint pjp) throws Throwable {
    log.info("aspect idp interfacept ");
    return idpInter.intercept(pjp);
  }


}
