package com.tbex.idmpotent.server.server;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.msg.enums.EventType;
import com.tbex.idmpotent.netty.msg.enums.ResponseCode;
import com.tbex.idmpotent.server.server.channel.NettyChannelManager;
import com.tbex.idmpotent.server.server.manager.IdmpotentManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;


@Component
@Slf4j
public class IdmpotentServerHandler {

    IdmpotentManager idmpotentManager;

    public IdmpotentServerHandler(IdmpotentManager idmpotentManager) {
        this.idmpotentManager = idmpotentManager;
    }

    public void callback(RpcCmd rpcCmd) {
        try {
            //获取channel
            Channel channel = NettyChannelManager.getInstance().getChannel(rpcCmd.getRemoteKey());
            //校验channel 是否为空
            if (channel == null) {
                log.info("channel 已失效！");
                return;
            }
            //校验消息是否为空
            if (Objects.isNull(rpcCmd)) {
                channel.writeAndFlush(ResponseCode.PARAM_NULL);
                log.info("msg is null");
                return;
            }
            //校验token,登录除外
            if (StringUtils.isEmpty(rpcCmd.getToken())
                    && !rpcCmd.getEvent().equals(EventType.LOGIN.name())) {
                channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ResponseCode.NOT_LOGIN));
                return;
            }

            /**新版本*/
            idmpotentManager.execute(channel,rpcCmd);

            /**旧版*/
//            //具体业务处理
//            EventType eventType = EventType.of(rpcCmd.getEvent());
//            switch (eventType) {
//                /**登录*/
//                case LOGIN:
//                    idmpotentManager.handleLogin(channel, rpcCmd);
//                    break;
//                /**生成id*/
//                case CREATE_ID:
//                    idmpotentManager.createId(channel, rpcCmd);
//                    break;
//                /**幂等校验*/
//                case EXECUTING:
//                    idmpotentManager.executing(channel, rpcCmd);
//                    break;
//                /**处理成功*/
//                case BUSSINESS_SUCCESS:
//                    idmpotentManager.success(channel, rpcCmd);
//                    break;
//                /**程序异常，exception*/
//                case BUSSINESS_FAIL:
//                    idmpotentManager.executing(channel, rpcCmd);
//                    break;
//                /**业务处理失败，RuntimeException*/
//                case BUSSINESS_RUNTIMEEXCEPTION_FAIL:
//                    idmpotentManager.bussinesException(channel, rpcCmd);
//
//                    break;
//                /**退出登录*/
//                case LOGOUT:
//                    idmpotentManager.handleLogout(channel, rpcCmd);
//                    break;
//                default:
//                    break;
//            }
        } catch (Exception ex) {
            log.error("执行幂等服务 发生错误, errMsg:{}", ex);
        }
    }
}
