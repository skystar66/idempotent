package com.tbex.idmpotent.client.http;

import com.tbex.idmpotent.client.keyprovider.DefaultKeyProvider;
import com.tbex.idmpotent.client.keyprovider.TraceIdPoolProvider;
import com.tbex.idmpotent.client.util.Constants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: HttpInterceptor
 * @Description: todo http springmvc 拦截器使用
 * @Author: xuliang
 * @Date: 2020/4/20 上午10:40
 * @Version: 1.0
 */
@Slf4j
@Data
public class HttpInterceptor {

    public void httpService() {
    }

    private String getSpanId(ProceedingJoinPoint pjp) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getHeader(Constants.HEADER_KEY_TRACE_ID);
    }

    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
        String spanId = getSpanId(pjp);
        TraceIdPoolProvider.put(spanId);
        Object res = pjp.proceed();
        TraceIdPoolProvider.remove();
        return res;
    }
}