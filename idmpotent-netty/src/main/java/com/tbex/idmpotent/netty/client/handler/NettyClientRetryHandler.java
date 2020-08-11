package com.tbex.idmpotent.netty.client.handler;

import com.tbex.idmpotent.netty.msg.MessageConstants;
import com.tbex.idmpotent.netty.msg.dto.MessageDto;
import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.msg.manager.SocketChannelManager;
import com.tbex.idmpotent.netty.util.SnowflakeIdWorker;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.SocketAddress;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/21
 *
 * @author xuliang
 * @desc:重试handler
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class NettyClientRetryHandler extends ChannelInboundHandlerAdapter {


    private RpcCmd heartCmd;

    @Autowired
    NettyRetryConnect retryConnect;

    @Autowired
    SocketChannelManager socketChannelManager;

    /**
     * 构建心跳信息
     */
    public NettyClientRetryHandler() {
        MessageDto messageDto = new MessageDto();
        messageDto.setCmd(MessageConstants.ACTION_HEART_CHECK);
        heartCmd = new RpcCmd();
        heartCmd.setMsg(messageDto);
        heartCmd.setKey(MessageConstants.ACTION_HEART_CHECK
                + SnowflakeIdWorker.getInstance().nextId());
        heartCmd.setEvent(MessageConstants.ACTION_HEART_CHECK);

//        this.clientInitCallBack = clientInitCallBack;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
//        keepSize = NettyContext.currentParam(List.class).size();

        //连接成功后，开始登陆 回调


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        socketChannelManager.removeChannel(ctx.channel());
        log.error("socketAddress:{} ", socketAddress);
        retryConnect.reConnect(socketAddress);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("NettyClientRetryHandler - exception . ", cause);
        socketChannelManager.removeChannel(ctx.channel());
        if (cause instanceof ConnectException) {
            Thread.sleep(1000 * 15);
            log.error("try connect tx-manager:{} ", ctx.channel().remoteAddress());
            retryConnect.reConnect(ctx.channel().remoteAddress());
        }
        //发送数据包检测是否断开连接.
        ctx.writeAndFlush(heartCmd);

    }
}
