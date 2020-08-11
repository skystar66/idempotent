package com.tbex.idmpotent.client;

import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;


/**
 * @ClassName: MethodSignatureWrapper
 * @Description: todo 执行切面业务，获取切面相关信息
 * @Author: xuliang
 * @Date: 2020/4/17 下午4:52
 * @Version: 1.0
 */
@AllArgsConstructor
public class MethodSignatureWrapper {

    private ProceedingJoinPoint pjp;

    public Object invoke() throws Throwable {
        return pjp.proceed();
    }

    public String getMethodSignature() {
        return pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
    }

    /**
     * 获取方法返回值类型
     */
    public Class getReturnType() {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        return methodSignature.getReturnType();
    }

}
