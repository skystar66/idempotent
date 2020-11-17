package com.tbex.idmpotent.server.server.validate;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.msg.enums.ResponseCode;
import com.tbex.idmpotent.server.core.store.FastStorage;
import com.tbex.idmpotent.server.server.MessageCreator;
import com.tbex.idmpotent.server.utils.MD5;
import com.tbex.idmpotent.server.utils.RedisConstants;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonValidate {


    @Autowired
    FastStorage fastStorage;


    /**
     * 公共校验：校验发放上的参数 MD5
     * 防止重复提交！
     */

    public void checkCommonValidate(Channel channel, RpcCmd rpcCmd) {
        String key = MD5.crypt(rpcCmd.toString());
        if (fastStorage.exist(key)) {
            channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ResponseCode.ID_DUPLICATE));
            return;
        }

        fastStorage.setex(key, RedisConstants.default_request_time_out, rpcCmd.toString());
    }


}
