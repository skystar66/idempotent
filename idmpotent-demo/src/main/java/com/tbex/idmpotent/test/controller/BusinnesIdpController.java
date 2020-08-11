package com.tbex.idmpotent.test.controller;

import com.tbex.idmpotent.client.http.BussinessInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TestIdpController
 * @Description: todo 业务幂等性测试类
 * @Author: xuliang
 * @Date: 2020/4/20 上午11:19
 * @Version: 1.0
 */
@RestController
@RequestMapping("/business/idp")
@Slf4j
public class BusinnesIdpController {


    @Autowired
    BussinessInterceptor bussinessInterceptor;


    @RequestMapping("/hello")
    private String hello(@RequestParam("traceId") String traceId) {

        boolean isflag = false;
        try {
            isflag = bussinessInterceptor.intercept(traceId);


        } catch (Throwable throwable) {

        }

        return String.valueOf(isflag);
    }

    @RequestMapping("/spanId")
    public String spanId(@RequestParam("traceId") String traceId) throws InterruptedException {
        System.out.println(">> 请求处理中...");

        Thread.sleep(1000);
        boolean isflag = false;
        try {
             isflag = bussinessInterceptor.intercept(traceId);

        } catch (Throwable throwable) {

        }
        System.out.println("<< 请求处理完毕...");
        return String.valueOf(isflag);
    }

    @RequestMapping("/throwEx")
    public String throwEx(@RequestParam("traceId") String traceId) throws Exception {
        System.out.println(">> 请求处理中...");
        try {
            boolean isflag = bussinessInterceptor.intercept(traceId);

            log.info("throw exception :{}",isflag);
        } catch (Throwable throwable) {

        }

        throw new Exception("故意抛出的Exception");
    }

    @RequestMapping("/throwRuntimeEx")
    public String throwRuntimeEx(@RequestParam("traceId") String traceId) {
        System.out.println(">> 请求处理中...");
        try {
            boolean isflag = bussinessInterceptor.intercept(traceId);

            log.info("throw runntime exception :{}",isflag);

        } catch (Throwable throwable) {

        }

        throw new RuntimeException("故意抛出的RuntimeException");
    }

}
