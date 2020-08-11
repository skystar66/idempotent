package com.tbex.idmpotent.server.server;

import com.tbex.idmpotent.netty.msg.RpcAnswer;
import com.tbex.idmpotent.netty.msg.dto.MessageDto;
import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.msg.enums.EventType;
import com.tbex.idmpotent.netty.msg.enums.ResponseCode;
import com.tbex.idmpotent.server.mq.MQProvider;
import com.tbex.idmpotent.server.server.channel.NettyChannelManager;
import com.tbex.idmpotent.server.server.manager.IdmpotentManager;
import com.tbex.idmpotent.server.utils.JsonUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import static com.tbex.idmpotent.netty.msg.MessageConstants.MSG_NULL;
import static com.tbex.idmpotent.netty.msg.MessageConstants.STATE_OK;

@Component
@Slf4j
public class RpcServerAnswer implements RpcAnswer {

    @Resource(name = "taskExecutor")
    private ExecutorService executorService;


    @Override
    public void callback(RpcCmd rpcCmd) {

        //先走安全服务 token


        //添加到消息处理器中
        executorService.submit(() -> {
            MQProvider.push(rpcCmd);
        });
    }
}
