package com.tbex.idmpotent.netty.msg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 从客户端接收到的消息
 *
 * @author zhulei
 * @date 2017/9/18 20:24
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class MessageBO implements Serializable {
    private long id;
    private long fromId;
    private String fromName;
    private String topicName;
    private long userId;
    private long createDate;
    private Object payload;
}



