package com.tbex.idmpotent.server.core.process;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.msg.enums.ResponseCode;
import com.tbex.idmpotent.server.core.IdpChecker;
import com.tbex.idmpotent.server.server.MessageCreator;
import com.tbex.idmpotent.server.token.TokenProvider;
import com.tbex.idmpotent.server.utils.MySeqIdGen;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.tbex.idmpotent.netty.msg.MessageConstants.STATE_OK;

/**
 * 创建幂等id
 */
@Component
@Slf4j
public class CreateIdProcess implements IdpChecker {

    @Override
    public void process(Channel channel, RpcCmd rpcCmd) {
        createId(channel, rpcCmd);
    }

    /**
     * 创建唯一id
     */
    public void createId(Channel channel, RpcCmd rpcCmd) {
        String id = MySeqIdGen.getId();
        //校验token
        if (!rpcCmd.getToken().equals(TokenProvider.get())) {
            channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ResponseCode.NOT_LOGIN));
            return;
        }
        rpcCmd.getMsg().setIdempotentId(id);
        channel.writeAndFlush((MessageCreator.okResponse(rpcCmd, STATE_OK)));
    }


}
