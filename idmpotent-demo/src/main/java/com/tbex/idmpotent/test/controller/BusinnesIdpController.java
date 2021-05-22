package com.tbex.idmpotent.test.controller;

import com.tbex.idmpotent.client.annotation.EnableIdp;
import com.tbex.idmpotent.client.client.MessageCreator;
import com.tbex.idmpotent.client.client.ReqRpcClient;
import com.tbex.idmpotent.netty.msg.dto.MessageDto;
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
    ReqRpcClient reqRpcClient;


    @RequestMapping("/login")
    private String login() throws Exception {

        MessageDto messageDto = reqRpcClient.request(MessageCreator.serverLogin("admin", "123456"));
        return messageDto.loadBean(String.class);
    }


    @RequestMapping("/createIdpId")
    private String createIdpId(@RequestParam("token") String token) throws Exception {

        MessageDto messageDtoValidate = reqRpcClient.request(MessageCreator.createIdpId(token));

        return messageDtoValidate.loadBean(String.class);
    }


    @RequestMapping("/hello")
    @EnableIdp
    private String hello(@RequestParam("traceId") String traceId) {
        System.out.println("处理业务");
        return "hello";
    }

}
