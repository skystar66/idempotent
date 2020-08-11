package com.tbex.idmpotent.client.loadbalance;


import io.netty.channel.Channel;

/**
 * @author xuliang
 */
public interface RpcLoadBalance {


    /**
     * 获取一个远程标识关键字
     *
     * @return 远程key
     * @throws RpcException 远程调用请求异常
     */
    Channel getRemoteChannel() throws Exception;


}
