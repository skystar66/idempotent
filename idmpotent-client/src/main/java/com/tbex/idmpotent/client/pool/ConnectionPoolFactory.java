package com.tbex.idmpotent.client.pool;

import com.google.common.cache.LoadingCache;
import com.tbex.idmpotent.client.config.IdmpomtentClientConfig;
import com.tbex.idmpotent.client.weight.ServerAdrWeight;
import com.tbex.idmpotent.client.weight.ServerNode;
import com.tbex.idmpotent.netty.client.init.RpcClientInitializer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ConnectionPoolFactory {


    @Autowired
    IdmpomtentClientConfig idmpomtentClientConfig;

    @Autowired
    RpcClientInitializer rpcClientInitializer;


    /**
     * 共享连接 nodeId ----> channels
     */
    public static final Map<ServerNode, List<Channel>> sharedConnectPool = new ConcurrentHashMap<>();


    /**
     * 添加链接
     */
    public void addConnect(LoadingCache<ServerNode, ServerAdrWeight> serverCacheMap) {
        if (serverCacheMap == null) {
            log.warn("Server 端服务列表为空，请注册Server服务器！");
            return;
        }
        for (int i = 0; i < idmpomtentClientConfig.getPoolServerSize(); i++) {

            serverCacheMap.asMap().entrySet().stream().forEach(serverNodeServerAdrWeightEntry -> {
                if (serverNodeServerAdrWeightEntry.getKey().getAvailable() == 1) {
                    /**可用*/
                    /**是否存在当前节点channel*/
                    sharedConnectPool.keySet().stream().forEach(serverNode -> {
                        if (serverNode.getNodeId().equals(serverNodeServerAdrWeightEntry.getKey().getNodeId())) {
                            return;
                        }
                    });
                    ServerAdrWeight serverAdrWeight = serverNodeServerAdrWeightEntry.getValue();
                    //开始连接server端
                    Channel channel = rpcClientInitializer.init(serverAdrWeight.getIp(), serverAdrWeight.getPort(), true);
                    if (sharedConnectPool.keySet().contains(serverAdrWeight.getNodeId())) {
                        sharedConnectPool.get(serverNodeServerAdrWeightEntry.getKey()).add(channel);
                    } else {
                        sharedConnectPool.put(serverNodeServerAdrWeightEntry.getKey(), Arrays.asList(channel));
                    }
                } else {
                    /**不可用*/
                    sharedConnectPool.keySet().stream().forEach(serverNode -> {
                        if (serverNode.getNodeId().equals(serverNodeServerAdrWeightEntry.getKey().getNodeId())) {
                            /**设置为不可用*/
                            serverNodeServerAdrWeightEntry.getKey().setAvailable(0);
                        }
                    });
                }
            });


        }
    }


    /**
     * 掉线链接，删除掉
     */
    public void removeConnect(Channel channel) {
        if (!sharedConnectPool.values().contains(channel)) {
            return;
        }
        sharedConnectPool.values().remove(channel);
    }


    /**
     * 根据幂等id 获取可用的指定服务列表
     */
    public List<Channel> getChannelsByIdpId(String idpId) {
        String nodeID = idpId.substring(0, 2);
        return sharedConnectPool.get(nodeID);
    }


//    /**
//     * 根据权重获取server 端服务
//     */
//
//    public ServerAdrWeight getServerRoute(int weight, LoadingCache<String, ServerAdrWeight> serverCacheMap) {
//        int random = RandomUtils.nextInt(0, weight);
//        int sum = 0;
//
//        for (Map.Entry<String, ServerAdrWeight> stringServerAdrWeightEntry : serverCacheMap.asMap().entrySet()) {
//            sum += stringServerAdrWeightEntry.getValue().getWeight();
//            if (sum > 0 && sum >= random) {
//                return stringServerAdrWeightEntry.getValue();
//            }
//        }
//        return null;
//    }

}
