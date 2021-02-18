package com.tbex.idmpotent.client.pool;

import com.tbex.idmpotent.client.utils.AttributeKeys;
import com.tbex.idmpotent.netty.client.init.RpcClientInitializer;
import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.node.NodeInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RpcClient {


    /**连接索引*/
    private int index;
    /**通道*/
    private Channel channel;
    /**节点信息*/
    private NodeInfo nodeInfo;

    private String key;

    RpcClientInitializer rpcClientInitializer;


    public RpcClient(NodeInfo nodeInfo, int index,String key,RpcClientInitializer rpcClientInitializer) {
        this.index = index;
        this.nodeInfo=nodeInfo;
        this.key=key;
        this.rpcClientInitializer=rpcClientInitializer;
//        connection();
    }


    public boolean connection() {
        if (isConnect()){
            log.info("###### channel is open！");
            return true;
        }
        ChannelFuture channelFuture = rpcClientInitializer.initFuture(nodeInfo.getIp(),nodeInfo.getPort(),true);

        channel = channelFuture.channel();

        channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {

            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                /**channel上绑定rpc数据*/
                channel.attr(AttributeKeys.RPC_SERVER).setIfAbsent(nodeInfo.getIp());
                channel.attr(AttributeKeys.RPC_PORT).setIfAbsent(nodeInfo.getPort());
                channel.attr(AttributeKeys.RPC_INDEX).setIfAbsent(index);
                channel.attr(AttributeKeys.RPC_POOL_KEY).setIfAbsent(key);
                log.info("###### index : {} RPC_SERVER: {} RPC_PORT: {} RPC_POOL_KEY: {}",
                        channel.attr(AttributeKeys.RPC_INDEX).get(),
                        channel.attr(AttributeKeys.RPC_SERVER).get(),
                        channel.attr(AttributeKeys.RPC_PORT).get(),
                        channel.attr(AttributeKeys.RPC_POOL_KEY).get());
            }
        });
        return isConnect();
    }

    public boolean isConnect() {
        return (channel != null && channel.isOpen() && channel.isActive());
    }


    /**
     * 发送消息
     */
    public void sendMsg(RpcCmd rpcCmd) {
        channel.writeAndFlush(rpcCmd);
    }



    public String getInfo() {
        if (channel != null)
            return channel.toString();
        else
            return getIpPort();
    }

    public String getIpPort() {
        return nodeInfo.getIp() + ":" + nodeInfo.getPort();
    }

}
