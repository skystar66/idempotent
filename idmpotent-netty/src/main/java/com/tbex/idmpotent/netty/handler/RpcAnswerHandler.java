package com.tbex.idmpotent.netty.handler;


import com.tbex.idmpotent.netty.msg.RpcAnswer;
import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/10
 *
 * @author xuliang
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class RpcAnswerHandler extends SimpleChannelInboundHandler<RpcCmd> {


    @Autowired
    RpcAnswer rpcAnswer;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcCmd rpcCmd) throws Exception {
        String remoteKey = channelHandlerContext.channel().remoteAddress().toString();
        rpcCmd.setRemoteKey(remoteKey);
        rpcAnswer.callback(rpcCmd);
    }
}
