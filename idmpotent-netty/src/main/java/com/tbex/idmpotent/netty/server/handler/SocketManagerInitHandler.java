package com.tbex.idmpotent.netty.server.handler;

import com.tbex.idmpotent.netty.msg.MessageConstants;
import com.tbex.idmpotent.netty.msg.dto.MessageDto;
import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.msg.manager.SocketChannelManager;
import com.tbex.idmpotent.netty.util.SnowflakeIdWorker;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Description:
 * Company: api
 * Date: 2018/12/10
 *
 * @author xuliang
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class SocketManagerInitHandler extends ChannelInboundHandlerAdapter {


    private RpcCmd heartCmd;

    @Autowired
    SocketChannelManager socketChannelManager;


    /**
     * 构造心跳消息
     */

    public SocketManagerInitHandler() {
        MessageDto messageDto = new MessageDto();
//        messageDto.setCmd(MessageConstants.ACTION_HEART_CHECK);
        heartCmd = new RpcCmd();
        heartCmd.setMsg(messageDto);
        heartCmd.setKey(MessageConstants.ACTION_HEART_CHECK + SnowflakeIdWorker.getInstance().nextId());
        heartCmd.setEvent(MessageConstants.ACTION_HEART_CHECK);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("stocketManager add channel  remote address : {}", ctx.channel().remoteAddress().toString());
        socketChannelManager.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("stocketManager remove channel address : {}", ctx.channel().remoteAddress().toString());
        socketChannelManager.removeChannel(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //心跳配置
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.writeAndFlush(heartCmd);
            }
        }
    }


}
