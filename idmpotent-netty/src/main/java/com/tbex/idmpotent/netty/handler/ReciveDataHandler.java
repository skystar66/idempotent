package com.tbex.idmpotent.netty.handler;


import com.tbex.idmpotent.netty.content.RpcCmdContext;
import com.tbex.idmpotent.netty.content.RpcContent;
import com.tbex.idmpotent.netty.enums.NettyType;
import com.tbex.idmpotent.netty.msg.MessageConstants;
import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.util.NettyContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/10
 *
 * @author ujued
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class ReciveDataHandler extends SimpleChannelInboundHandler<RpcCmd> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcCmd cmd) throws Exception {
        log.info("cmd->{}", cmd);
        //心态数据包直接响应
        if (cmd.getEvent() != null &&
                MessageConstants.ACTION_HEART_CHECK.equals(cmd.getEvent())) {
            if (NettyContext.currentType().equals(NettyType.client)) {
                //设置值
                ctx.writeAndFlush(cmd);
                return;
            }
            return;
        }

        if (!StringUtils.isEmpty(cmd.getKey())) {
            RpcContent rpcContent = RpcCmdContext.getInstance().getKey(cmd.getKey());
            if (rpcContent != null) {
                log.info("got response message[Netty Handler]");
                rpcContent.setRes(cmd.getMsg());
                rpcContent.signal();
                return;
            } else {
                ctx.fireChannelRead(cmd);
                return;
            }
        }
        // 通知执行下一个InboundHandler
        ctx.fireChannelRead(cmd);
    }
}
