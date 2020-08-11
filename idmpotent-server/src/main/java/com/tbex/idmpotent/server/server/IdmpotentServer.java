package com.tbex.idmpotent.server.server;

import com.tbex.idmpotent.netty.dto.ManagerProperties;
import com.tbex.idmpotent.netty.server.init.RpcServerInitializer;
import com.tbex.idmpotent.server.config.IdmpotentServerConfig;

public class IdmpotentServer implements Runnable {


    IdmpotentServerConfig rpcConfig;

    RpcServerInitializer rpcServerInitializer;

    public IdmpotentServer(RpcServerInitializer rpcServerInitializer,IdmpotentServerConfig rpcConfig) {
        this.rpcConfig = rpcConfig;
        this.rpcServerInitializer = rpcServerInitializer;
    }


    @Override
    public void run() {

//        // 1. 配置
//        if (rpcConfig.getWaitTime() <= 5) {
//            rpcConfig.setWaitTime(1000);
//        }
//        if (rpcConfig.getAttrDelayTime() < 0) {
//            //网络延迟时间 8s
//            rpcConfig.setAttrDelayTime(txManagerConfig.getDtxTime());
//        }

        // 2. 初始化RPC Server
        ManagerProperties managerProperties = new ManagerProperties();
        managerProperties.setCheckTime(rpcConfig.getHeartTime());
        managerProperties.setRpcPort(rpcConfig.getPort());
        managerProperties.setRpcHost(rpcConfig.getHost());
        rpcServerInitializer.init(managerProperties);
    }
}
