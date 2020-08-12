package com.tbex.idmpotent.netty.msg.enums;


import java.util.Arrays;

/**
 * 事件
 *
 * @author zhulei
 * @date 2017/9/19 17:34
 */

public enum EventType {


    /**
     * 心跳事件
     */
    heartCheck,

    //
    /**
     * 生成全局唯一id请求
     */
    CREATE_ID,
    /**
     * 校验
     */
    EXECUTING,
    /**
     * 登录
     */
    LOGIN,
    /**
     * 登出
     */
    LOGOUT,
    /**
     * 业务处理成功
     */
    BUSSINESS_SUCCESS,
    /**
     * 程序 exception
     */
    BUSSINESS_FAIL,
    /**
     * 业务异常RuntimeException
     */
    BUSSINESS_RUNTIMEEXCEPTION_FAIL,;

    public static EventType of(String type) {
        return Arrays.stream(EventType.values())
                .filter(eventType -> eventType.name().equalsIgnoreCase(type))
                .findFirst()
//            .orElseThrow(() ->new InvalidEventException("event "+ type +" not exist."));
                .orElse(null);
    }


}
