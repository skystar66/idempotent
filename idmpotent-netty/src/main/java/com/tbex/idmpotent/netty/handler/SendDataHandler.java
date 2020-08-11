package com.tbex.idmpotent.netty.handler;


import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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
public class SendDataHandler extends MessageToMessageEncoder<RpcCmd> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcCmd cmd, List<Object> out) throws Exception {
        log.info("send->{}", cmd);
        out.add(cmd);
    }
}
