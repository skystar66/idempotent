package com.tbex.idmpotent.server.core;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import io.netty.channel.Channel;

/**
 * 幂等服务业务逻辑处理
 */
public interface IdpChecker {


    /**
     * excuting
     */
    public void process(Channel channel, RpcCmd rpcCmd);

}
