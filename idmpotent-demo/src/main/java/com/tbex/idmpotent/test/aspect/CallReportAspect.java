//package com.tbex.idmpotent.test.aspect;
//
//import com.tbex.idmpotent.test.service.AspectIdmpotentService;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * @author xuliang
// * @date 2020-03-16
// */
//@Aspect
//@Component
//@Slf4j
//public class CallReportAspect {
//
//
//    @Autowired
//    AspectIdmpotentService callReportService;
//
//    @Pointcut("@annotation(com.tbex.idmpotent.test.annotation.CallReport)")
//    public void callReportPointcut() {
//    }
//
//    @Around("callReportPointcut()")
//    public Object runWithCallReport(ProceedingJoinPoint point) throws Throwable {
//        log.info("start excute bussines >>>>>>>>>>");
//        Object result = point.proceed();
//        callReportService.test("111");
//        log.info("end finish bussines >>>>>>>>>> result :{}", result);
//        return result;
//    }
//}
