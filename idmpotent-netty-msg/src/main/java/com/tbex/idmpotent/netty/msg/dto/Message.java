package com.tbex.idmpotent.netty.msg.dto;


import lombok.Data;

/**
 * @author: leon (pengli@58coin.com)
 * @createDate: 2018/4/7
 * @company: (C) Copyright 58BTC 2018
 * @since: JDK 1.8
 * @description:
 */
@Data
public class Message {


    private String event = "data";
    private String userName;
    private String topicName;
    private long messageId;
    private String payload;

    private int readFlag;
    private long createDate;







}
