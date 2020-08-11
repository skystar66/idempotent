package com.tbex.idmpotent.netty.msg.dto;


import com.tbex.idmpotent.netty.msg.MessageConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author xuliang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class MessageDto implements Serializable {


    /**
     * 请求动作
     */
    private String cmd;


    /**
     * 请求参数
     */
    private Serializable data;

    /**
     * 请求状态
     */
    private int state = MessageConstants.STATE_REQUEST;


    public <T> T loadBean(Class<T> tClass) {
        return (T) data;
    }
}
