package com.tbex.idmpotent.client.client;


import com.tbex.idmpotent.client.enums.RpcResponseState;
import com.tbex.idmpotent.netty.msg.dto.MessageDto;
import com.tbex.idmpotent.netty.msg.dto.RpcCmd;

/**
 * Description: 客户端api
 * Company: CodingApi
 * Date: 2020/2/10
 *
 * @author xuliang
 */
public abstract class RpcClient {




    /**
     * 发送指令不需要返回数据，需要知道返回状态
     *
     * @param rpcCmd 指令内容
     * @return 指令状态
     * @throws RpcException 远程调用请求异常
     */

    public abstract RpcResponseState send(RpcCmd rpcCmd) throws Exception;


    /**
     * 发送指令不需要返回数据，需要知道返回的状态
     *
     * @param remoteKey 远程标识关键字
     * @param msg       指令内容
     * @return 指令状态
     * @throws RpcException 远程调用请求异常
     */
    public abstract RpcResponseState send(String remoteKey, MessageDto msg) throws Exception;



    /**
     * 发送请求并获取响应
     *
     * @param messageDto 指令内容
     * @return 响应指令数据
     * @throws RpcException 远程调用请求异常
     */
    public abstract MessageDto request(RpcCmd rpcCmd) throws Exception;


    /**
     * 发送请求并获取响应
     *
     * @param remoteKey 远程标识关键字
     * @param msg       请求内容
     * @param timeout   超时时间
     * @return 响应消息
     * @throws RpcException 远程调用请求异常
     */
    public abstract MessageDto request(RpcCmd rpcCmd, long timeout) throws Exception;


}
