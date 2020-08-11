package com.tbex.idmpotent.client.client;


import com.tbex.idmpotent.client.client.token.TokenProvider;
import com.tbex.idmpotent.netty.msg.MessageConstants;
import com.tbex.idmpotent.netty.msg.dto.MessageDto;
import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.msg.enums.EventType;
import com.tbex.idmpotent.netty.msg.enums.ResponseCode;
import com.tbex.idmpotent.netty.util.SnowflakeIdWorker;

/**
 * 消息创建器
 *
 * @author xuliang
 */
public class MessageCreator {


    /**
     * 正常响应
     *
     * @param action  action
     * @param message message
     * @return MessageDto
     */
    public static RpcCmd okResponse(RpcCmd message, int action) {
        message.getMsg().setState(action);
        return message;
    }

    /**
     * 失败响应
     *
     * @param action  action
     * @param message message
     * @return MessageDto
     */
    public static RpcCmd failResponse(RpcCmd message, int action) {
        message.getMsg().setState(action);
        return message;
    }


    /**
     * 失败响应
     *
     * @param action  action
     * @param message message
     * @return MessageDto
     */
    public static RpcCmd bussinesError(RpcCmd rpcCmd, ResponseCode responseCode) {
        rpcCmd.getMsg().setState(Integer.parseInt(responseCode.getCode()));
        return rpcCmd;
    }


    /**
     * 服务器错误
     *
     * @param action action
     * @return MessageDto
     */
    public static MessageDto serverException(String action) {
        MessageDto messageDto = new MessageDto();
        messageDto.setCmd(action);
        messageDto.setState(MessageConstants.STATE_EXCEPTION);
        return messageDto;
    }


    /**
     * 构建登录请求
     *
     * @param action action
     * @return MessageDto
     */
    public static RpcCmd serverLogin(String userName, String pwd) {
        RpcCmd rpcCmd = new RpcCmd();
        rpcCmd.setKey(EventType.LOGIN.name() +
                SnowflakeIdWorker.getInstance().nextId());
        MessageDto messageDto = new MessageDto();
        messageDto.setData(userName + ":" + pwd);
        messageDto.setState(MessageConstants.STATE_OK);
        rpcCmd.setMsg(messageDto);
        rpcCmd.setEvent(EventType.LOGIN.name());
        return rpcCmd;
    }

    /**
     * 构建校验幂等请求
     *
     * @param action action
     * @return MessageDto
     */
    public static RpcCmd serverValidate(String traceId) {
        RpcCmd rpcCmd = new RpcCmd();
        rpcCmd.setKey(EventType.EXECUTING.name() +
                SnowflakeIdWorker.getInstance().nextId());
        MessageDto messageDto = new MessageDto();
        messageDto.setData(traceId);
        messageDto.setState(MessageConstants.STATE_OK);
        rpcCmd.setMsg(messageDto);
        rpcCmd.setEvent(EventType.EXECUTING.name());
        rpcCmd.setToken(TokenProvider.get());
        return rpcCmd;
    }


    /**
     * 构建业务处理成功后请求
     *
     * @param action action
     * @return MessageDto
     */
    public static RpcCmd serverBussinessSuccess(String traceId) {
        RpcCmd rpcCmd = new RpcCmd();
        rpcCmd.setKey(EventType.BUSSINESS_SUCCESS.name() +
                SnowflakeIdWorker.getInstance().nextId());
        MessageDto messageDto = new MessageDto();
        messageDto.setData(traceId);
        messageDto.setState(MessageConstants.STATE_OK);
        rpcCmd.setMsg(messageDto);
        rpcCmd.setEvent(EventType.BUSSINESS_SUCCESS.name());
        rpcCmd.setToken(TokenProvider.get());
        return rpcCmd;
    }


    /**
     * 构建业务处理出现exception后请求
     *
     * @param action action
     * @return MessageDto
     */
    public static RpcCmd serverBussinessException(String traceId) {
        RpcCmd rpcCmd = new RpcCmd();
        rpcCmd.setKey(EventType.BUSSINESS_FAIL.name() +
                SnowflakeIdWorker.getInstance().nextId());
        MessageDto messageDto = new MessageDto();
        messageDto.setData(traceId);
        messageDto.setState(MessageConstants.STATE_OK);
        rpcCmd.setMsg(messageDto);
        rpcCmd.setEvent(EventType.BUSSINESS_FAIL.name());
        rpcCmd.setToken(TokenProvider.get());
        return rpcCmd;
    }

    /**
     * 构建业务处理出现Runtimeexception后请求
     *
     * @param action action
     * @return MessageDto
     */
    public static RpcCmd serverBussinessRuntimeException(String traceId) {
        RpcCmd rpcCmd = new RpcCmd();
        rpcCmd.setKey(EventType.BUSSINESS_RUNTIMEEXCEPTION_FAIL.name() +
                SnowflakeIdWorker.getInstance().nextId());
        MessageDto messageDto = new MessageDto();
        messageDto.setData(traceId);
        messageDto.setState(MessageConstants.STATE_OK);
        rpcCmd.setMsg(messageDto);
        rpcCmd.setEvent(EventType.BUSSINESS_RUNTIMEEXCEPTION_FAIL.name());
        rpcCmd.setToken(TokenProvider.get());
        return rpcCmd;
    }


}
