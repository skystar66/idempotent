package com.tbex.idmpotent.netty.node;

import org.springframework.util.StringUtils;

public class NodeBuilder {


    public static NodeInfo buildNode(String ip, int port) {

        //初始化节点信息类
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setIp(ip);
        nodeInfo.setPort(port);

        nodeInfo.setWeight(Byte.parseByte("20"));

        nodeInfo.setCoreThread(Runtime.getRuntime().availableProcessors() * 2);
        nodeInfo.setRpcPoolSize(3);
        nodeInfo.setRetrySize(3);
        return nodeInfo;
    }

}
