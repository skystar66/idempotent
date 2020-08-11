package com.tbex.idmpotent.netty.msg.enums;


import java.util.Arrays;

/**
 * 事件
 *
 * @author zhulei
 * @date 2017/9/19 17:34
 */

public enum BussinessType {


    /**
     * USDT
     */
    USDT,
    MIX,;

    public static BussinessType of(String type) {
        return Arrays.stream(BussinessType.values())
                .filter(eventType -> eventType.name().equalsIgnoreCase(type))
                .findFirst()
//            .orElseThrow(() ->new InvalidEventException("event "+ type +" not exist."));
                .orElse(null);
    }


}
