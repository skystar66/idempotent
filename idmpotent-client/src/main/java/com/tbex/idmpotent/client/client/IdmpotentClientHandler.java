package com.tbex.idmpotent.client.client;

import com.tbex.idmpotent.netty.msg.RpcAnswer;
import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;


@Component
@Slf4j
public class IdmpotentClientHandler implements RpcAnswer {


    @Autowired
    private ExecutorService executorService;

    @Override
    public void callback(RpcCmd rpcCmd) {

        executorService.submit(() -> {
            log.info("recive msg : {}", rpcCmd);

        });

    }
}
