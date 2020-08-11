package com.tbex.idmpotent.client.client;

import com.tbex.idmpotent.client.loadbalance.RpcLoadBalance;
import com.tbex.idmpotent.netty.content.RpcCmdContext;
import com.tbex.idmpotent.netty.content.RpcContent;
import com.tbex.idmpotent.netty.msg.dto.MessageDto;
import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.util.SnowflakeIdWorker;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: netty rpc  client  通讯实现类
 * Company: wanbaApi
 * Date: 2018/12/10
 *
 * @author xuliang
 */
@Component
@Slf4j
public class NettyRpcClient extends RpcClient {

    @Autowired
    RpcLoadBalance rpcLoadBalancel;


    @Override
    public MessageDto request(RpcCmd rpcCmd) throws Exception {
        return request(rpcCmd, 2000);
    }

    @Override
    public MessageDto request(RpcCmd rpcCmd, long timeout) throws Exception {
        long startTime = System.currentTimeMillis();
        String key = String.valueOf(SnowflakeIdWorker.getInstance().nextId());
        RpcContent rpcContent = RpcCmdContext.getInstance().addKey(key);
        rpcCmd.setKey(key);
        MessageDto result = request0(rpcContent, rpcCmd, timeout);
        log.info("cmd request used time: {} ms", System.currentTimeMillis() - startTime);
        return result;
    }

    private MessageDto request0(RpcContent rpcContent, RpcCmd rpcCmd, long timeout) throws Exception {
        log.info("get channel, key:{}", rpcCmd.getKey());
        //获取远程channel 通道
        Channel channel = rpcLoadBalancel.getRemoteChannel();
        channel.writeAndFlush(rpcCmd);
        log.info("await response key : {}", rpcCmd.getKey());
        //阻塞结果
        if (timeout < 0) {
            //一直阻塞
            rpcContent.await();
        } else {
            rpcContent.await(timeout);
        }
        MessageDto messageDto = rpcContent.getRes();
        return messageDto;
    }

}
