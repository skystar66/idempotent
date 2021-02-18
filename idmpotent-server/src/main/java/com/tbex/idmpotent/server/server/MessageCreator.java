package com.tbex.idmpotent.server.server;


import com.alibaba.druid.wall.violation.ErrorCode;
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
        rpcCmd.getMsg().setData(responseCode.getDesc());
        return rpcCmd;
    }


    /**
     * 服务器错误
     *
     * @param action action
     * @return MessageDto
     */
//    public static MessageDto serverException(String action) {
//        MessageDto messageDto = new MessageDto();
//        messageDto.setCmd(action);
//        messageDto.setState(MessageConstants.STATE_EXCEPTION);
//        return messageDto;
//    }


}
