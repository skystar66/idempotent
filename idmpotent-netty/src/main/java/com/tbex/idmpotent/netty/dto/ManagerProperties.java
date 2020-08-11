package com.tbex.idmpotent.netty.dto;

import lombok.Data;

@Data
public class ManagerProperties {


    private String rpcHost;

    /**
     * 端口
     */
    private int rpcPort;

    /**
     * 心态检测时间(ms)
     */
    private long checkTime;


}
