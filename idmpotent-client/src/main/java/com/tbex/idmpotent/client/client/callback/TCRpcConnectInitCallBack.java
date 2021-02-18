package com.tbex.idmpotent.client.client.callback;

import com.tbex.idmpotent.client.client.MessageCreator;
import com.tbex.idmpotent.client.client.ReqRpcClient;
import com.tbex.idmpotent.client.client.channel.NettyChannelManager;
import com.tbex.idmpotent.client.client.token.TokenProvider;
import com.tbex.idmpotent.client.utils.Constants;
import com.tbex.idmpotent.netty.msg.MessageConstants;
import com.tbex.idmpotent.netty.msg.dto.MessageDto;
import com.tbex.idmpotent.netty.msg.listener.ClientInitCallBack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Description:
 * Company: xuliang
 * Date: 2018/12/29
 *
 * @author xuliang
 * @desc：客户端连接成功回调服务端类
 * @see
 */
@Component
@Slf4j
public class TCRpcConnectInitCallBack implements ClientInitCallBack {

    @Autowired
    ReqRpcClient reqRpcClient;


    @Override
    public void connected(String remoteKey) {
        new Thread(() -> {
            try {
                log.info("Send login message to Login Idmpotent Server[{}]", remoteKey);

                MessageDto messageDtoLogin = reqRpcClient.request(MessageCreator.serverLogin(Constants.USER_NAME,
                        Constants.USER_PWD));
                log.info("登录返回消息：{}", messageDtoLogin);
                if (messageDtoLogin.getState() != MessageConstants.STATE_OK) {
                    //登录失败，关闭channel 通道
                    NettyChannelManager.getInstance().getChannel(remoteKey).close();
                } else {
                    //处理成功,获取token
                    String token = messageDtoLogin.loadBean(String.class);
                    TokenProvider.put(token);
                }
                return;
            } catch (Exception ex) {
                log.error("Idmpotent Server[{}] exception. connect fail!，error {}", remoteKey, ex);
            }
        }).start();
    }

    @Override
    public void connectFail(String remoteKey) {

    }
}
