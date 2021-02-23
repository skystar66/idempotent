//package com.tbex.idmpotent.client.client;
//
//import com.tbex.idmpotent.client.config.IdmpomtentClientConfig;
//import com.tbex.idmpotent.netty.client.init.RpcClientInitializer;
//import com.tbex.idmpotent.netty.dto.ManagerProperties;
//
//import java.util.Arrays;
//
//public class IdmpotentClient implements Runnable {
//
//
//    IdmpomtentClientConfig rpcConfig;
//
//    RpcClientInitializer rpcClientInitializer;
//
//
//    public IdmpotentClient(IdmpomtentClientConfig rpcConfig,
//                           RpcClientInitializer rpcClientInitializer) {
//        this.rpcConfig = rpcConfig;
//        this.rpcClientInitializer = rpcClientInitializer;
//    }
//
//    @Override
//    public void run() {
//        // 2. 初始化RPC Server
//        ManagerProperties managerProperties = new ManagerProperties();
//        managerProperties.setRpcPort(rpcConfig.getPort());
//        managerProperties.setRpcHost(rpcConfig.getHost());
//        rpcClientInitializer.init(Arrays.asList(managerProperties), true);
//    }
//}
