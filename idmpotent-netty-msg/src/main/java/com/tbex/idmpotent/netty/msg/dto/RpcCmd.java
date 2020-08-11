package com.tbex.idmpotent.netty.msg.dto;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

//@Data
public class RpcCmd implements Serializable {

    /**
     * 请求唯一标识
     */

    private String key;

    /**
     * 远程标识关键字 netty 默认为 指定host
     * 默认分配 64457 个端口，形式为：ip:ranDomProt
     * todo remoteKey ??
     */
    private String remoteKey;


    /**
     * eventEnum
     */
    private String event;

    /**
     * 业务类型
     */
    private String type;

    /**
     * 请求的消息内容体
     */
    private MessageDto msg;

    /**
     * token
     */

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRemoteKey() {
        return remoteKey;
    }

    public void setRemoteKey(String remoteKey) {
        this.remoteKey = remoteKey;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public MessageDto getMsg() {
        return msg;
    }

    public void setMsg(MessageDto msg) {
        this.msg = msg;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
