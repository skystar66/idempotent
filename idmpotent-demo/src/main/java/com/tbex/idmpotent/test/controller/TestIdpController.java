package com.tbex.idmpotent.test.controller;

import com.tbex.idmpotent.test.service.AspectIdmpotentService;
import com.tbex.idmpotent.client.annotation.EnableIdp;
import com.tbex.idmpotent.test.service.CallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
* @ClassName:      TestIdpController
* @Description:    todo 接口幂等性测试类
* @Author:         xuliang
* @Date:     2020/4/20 上午11:19
* @Version:        1.0
*/
@RestController
@RequestMapping("/test/idp")
public class TestIdpController {

    @Autowired
    CallService callService;



    @EnableIdp
    @RequestMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @EnableIdp
    @RequestMapping("/spanId")
    public String spanId() throws InterruptedException {
        System.out.println(">> 请求处理中...");
        Thread.sleep(1000);
        System.out.println("<< 请求处理完毕...");
        return "OK";
    }

    @EnableIdp
    @RequestMapping("/throwEx")
    public String throwEx() throws Exception {
        System.out.println(">> 请求处理中...");
        throw new Exception("故意抛出的Exception");
    }

    @EnableIdp
    @RequestMapping("/throwRuntimeEx")
    public String throwRuntimeEx() {
        System.out.println(">> 请求处理中...");
        throw new RuntimeException("故意抛出的RuntimeException");
    }
}
