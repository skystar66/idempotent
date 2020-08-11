package com.tbex.idmpotent.client.aspect;

import com.tbex.idmpotent.client.checker.IdpInterceptor;
import com.tbex.idmpotent.client.client.MessageCreator;
import com.tbex.idmpotent.client.client.RpcClient;
import com.tbex.idmpotent.client.exception.Msgs;
import com.tbex.idmpotent.client.exception.RejectException;
import com.tbex.idmpotent.client.utils.Constants;
import com.tbex.idmpotent.netty.msg.MessageConstants;
import com.tbex.idmpotent.netty.msg.dto.MessageDto;
import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.msg.enums.EventType;
import com.tbex.idmpotent.netty.util.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

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
