package com.tbex.idmpotent.netty.client.handler;

import com.tbex.idmpotent.netty.client.init.RpcClientInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.Objects;

@Component
public class NettyRetryConnect {

    @Autowired
    private RpcClientInitializer nettyRpcClientInitializer;

    /**
     * 重连
     */
    public void reConnect(SocketAddress socketAddress) {
        Objects.requireNonNull(socketAddress, "non support!");
        nettyRpcClientInitializer.connect(socketAddress);
    }


}
