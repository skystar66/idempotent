package com.tbex.idmpotent.netty.client.init;

import com.tbex.idmpotent.netty.client.handler.init.NettyRpcClientChannelInitializer;
import com.tbex.idmpotent.netty.dto.ManagerProperties;
import com.tbex.idmpotent.netty.enums.NettyType;
import com.tbex.idmpotent.netty.msg.manager.SocketChannelManager;
import com.tbex.idmpotent.netty.util.NettyContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class RpcClientInitializer implements DisposableBean {


    @Autowired
    private NettyRpcClientChannelInitializer nettyRpcClientChannelInitializer;

    @Autowired
    SocketChannelManager socketChannelManager;


    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    ;


    public void init(List<ManagerProperties> hosts, boolean sync) {
        NettyContext.nettyType = NettyType.client;
        NettyContext.params = hosts;
        for (ManagerProperties host : hosts) {
            Optional<Future> future = connect(new InetSocketAddress(host.getRpcHost(), host.getRpcPort()));
            log.info("Success Connect Topic Server Address : {}", host.getRpcHost() + ":" + host.getRpcPort());
            if (sync && future.isPresent()) {
                try {
                    future.get().get(10, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public Channel init(String host, Integer port, boolean sync) {
        NettyContext.nettyType = NettyType.client;
        NettyContext.params = host;
        Optional<Future> future = connect(new InetSocketAddress(host, port));
        log.info("Success Connect Idp Server Address : {}", host + ":" + port);
        if (sync && future.isPresent()) {
            try {
                return ((ChannelFuture) future.get().get(10, TimeUnit.SECONDS)).channel();
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }


    public synchronized Optional<Future> connect(SocketAddress socketAddress) {
        for (int i = 0; i < 3; i++) {
            if (!socketChannelManager.contains(socketAddress.toString())) {
                try {
                    log.info("Try connect socket({}) - count {}", socketAddress, i + 1);
                    Bootstrap b = new Bootstrap();
                    b.group(workerGroup);
                    b.channel(NioSocketChannel.class);
                    b.option(ChannelOption.TCP_NODELAY, true);
                    b.option(ChannelOption.SO_REUSEADDR, true);
                    b.option(ChannelOption.SO_KEEPALIVE, true);
                    b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
                    b.handler(nettyRpcClientChannelInitializer);
                    return Optional.of(b.connect(socketAddress).syncUninterruptibly());
                } catch (Exception ex) {
                    log.warn("Connect socket({}) fail. {}ms latter try again.", socketAddress, 6000);
                    try {
                        /**重连间隔 默认：6s*/
                        Thread.sleep(6 * 1000);
                    } catch (InterruptedException ex2) {
                        ex2.printStackTrace();
                    }
                }
            }
        }
        log.warn("Finally, netty connection fail , socket is {}", socketAddress);
        //报警，该服务失效
        return Optional.empty();
    }


    @Override
    public void destroy() throws Exception {
        workerGroup.shutdownGracefully();
        log.info("RPC client was down.");
    }


}
