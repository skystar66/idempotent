package com.tbex.idmpotent.server.core.process;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.msg.enums.ResponseCode;
import com.tbex.idmpotent.server.core.IdpChecker;
import com.tbex.idmpotent.server.server.MessageCreator;
import com.tbex.idmpotent.server.token.TokenProvider;
import com.tbex.idmpotent.server.utils.TopicConstants;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static com.tbex.idmpotent.netty.msg.MessageConstants.STATE_OK;
import static com.tbex.idmpotent.server.utils.TopicConstants.USER_NAME;
import static com.tbex.idmpotent.server.utils.TopicConstants.USER_PWD;

/**
 * 退出登录
 *
 */
@Component
@Slf4j
public class LoginOutProcess implements IdpChecker {

    @Override
    public void process(Channel channel, RpcCmd rpcCmd) {

        handleLogout(channel, rpcCmd);
    }

    /**
     * 用户退出登录
     */
    public void handleLogout(Channel channel, RpcCmd rpcCmd) {
        //校验token
        if (!rpcCmd.getToken().equals(TokenProvider.get())) {
            channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ResponseCode.NOT_LOGIN));
            return;
        }
        //移除token
        TokenProvider.remove();
        channel.writeAndFlush((MessageCreator.okResponse(rpcCmd, STATE_OK)));
    }
}
