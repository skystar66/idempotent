package com.tbex.idmpotent.server.server;

import com.alibaba.fastjson.JSON;
import com.tbex.idmpotent.netty.dto.ManagerProperties;
import com.tbex.idmpotent.netty.node.NodeBuilder;
import com.tbex.idmpotent.netty.node.NodeInfo;
import com.tbex.idmpotent.netty.server.init.RpcServerInitializer;
import com.tbex.idmpotent.server.config.IdmpotentServerConfig;
import com.tbex.idmpotent.server.zookeeper.ZkHelp;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IdmpotentServer implements Runnable {


    IdmpotentServerConfig rpcConfig;

    RpcServerInitializer rpcServerInitializer;

    public IdmpotentServer(RpcServerInitializer rpcServerInitializer,IdmpotentServerConfig rpcConfig) {
        this.rpcConfig = rpcConfig;
        this.rpcServerInitializer = rpcServerInitializer;
    }

    @Override
    public void run() {
        // 2. 初始化RPC Server
        ManagerProperties managerProperties = new ManagerProperties();
        managerProperties.setCheckTime(rpcConfig.getHeartTime());
        managerProperties.setRpcPort(rpcConfig.getPort());
        managerProperties.setRpcHost(rpcConfig.getHost());
        rpcServerInitializer.init(managerProperties);
        //注册zookeeper
        log.info("###### 是否注册zookeeper 长连接服务节点 isReg : 【{}】"
                ,rpcConfig.isZkSwitch()?"是":"否");
        if (rpcConfig.isZkSwitch()) {
            NodeInfo nodeInfo =NodeBuilder.buildNode(rpcConfig.getHost(),rpcConfig.getPort());
            /**注册zk服务节点*/
            ZkHelp.getInstance().regInCluster(rpcConfig.getZkPath(), JSON.toJSONString(nodeInfo));
        }
    }
}
