package com.tbex.idmpotent.server.core.process;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
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
 * 登录
 *
 */
@Component
@Slf4j
public class LoginProcess implements IdpChecker {

    @Override
    public void process(Channel channel, RpcCmd rpcCmd) {
        handleLogin(channel, rpcCmd);
    }

    /**
     * 用户登录
     */
    public void handleLogin(Channel channel, RpcCmd rpcCmd) {
        String token = TokenProvider.get();
        if (StringUtils.isEmpty(token)) {
            String userNameAndPwd = rpcCmd.getMsg().loadBean(String.class);
            String[] params = userNameAndPwd.split(":");

            String userName = params[0];
            String pwd = params[1];
            //登陆成功
            if (userName.equals(USER_NAME) && pwd.equals(TopicConstants.USER_PWD)) {
                //生成token
                token = UUID.randomUUID().toString();
                TokenProvider.put(token);
            }
            rpcCmd.setToken(token);
            channel.writeAndFlush(MessageCreator.okResponse(rpcCmd, STATE_OK));
            log.info("user login: userName:{} pwd :{} token : {}", userName, pwd, token);
            return;
        }
        rpcCmd.setToken(token);
        log.info("user login: userName:{} pwd :{} token : {}", USER_NAME, USER_PWD, token);
        channel.writeAndFlush(MessageCreator.okResponse(rpcCmd, STATE_OK));
    }
}
