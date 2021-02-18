package com.tbex.idmpotent.netty.node;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Arrays;

/**
 * @author xl
 * @date 2019年3月15日 下午1:38:25
 * <p>
 * 节点信息
 */
public class NodeInfo {

    private String ip;// 内网ip/外网IP
    private int port;
    private int rpcPoolSize;
    private int rpcServerIndex;

    private int retrySize;//重试次数


    private String zkRpcPath;

    private String zip;// 压缩 snappy gzip
    private int coreThread = Runtime.getRuntime().availableProcessors() * 2;//这个决定工作线程数量和链接的tcp数量
    private byte weight = 1;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getRpcPoolSize() {
        return rpcPoolSize;
    }

    public void setRpcPoolSize(int rpcPoolSize) {
        this.rpcPoolSize = rpcPoolSize;
    }

    public int getRpcServerIndex() {
        return rpcServerIndex;
    }

    public void setRpcServerIndex(int rpcServerIndex) {
        this.rpcServerIndex = rpcServerIndex;
    }

    public int getRetrySize() {
        return retrySize;
    }

    public void setRetrySize(int retrySize) {
        this.retrySize = retrySize;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public int getCoreThread() {
        return coreThread;
    }

    public void setCoreThread(int coreThread) {
        this.coreThread = coreThread;
    }

    public byte getWeight() {
        return weight;
    }

    public void setWeight(byte weight) {
        this.weight = weight;
    }

    public String getZkRpcPath() {
        return zkRpcPath;
    }

    public void setZkRpcPath(String zkRpcPath) {
        this.zkRpcPath = zkRpcPath;
    }
}
