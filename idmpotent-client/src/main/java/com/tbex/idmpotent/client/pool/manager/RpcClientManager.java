package com.tbex.idmpotent.client.pool.manager;

import com.tbex.idmpotent.client.mq.MQProvider;
import com.tbex.idmpotent.client.pool.RpcClient;
import com.tbex.idmpotent.client.pool.connect.ConnectionCache;
import com.tbex.idmpotent.client.pool.connect.NodePoolCache;
import com.tbex.idmpotent.client.utils.RPCConstants;
import com.tbex.idmpotent.client.utils.SpringUtil;
import com.tbex.idmpotent.netty.client.init.RpcClientInitializer;
import com.tbex.idmpotent.netty.node.NodeInfo;
import com.tbex.idmpotent.netty.util.AddressUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;


/**
 * 连接池客户端管理
 *
 * @author xl
 * @date 2020-11-23
 */
public class RpcClientManager {

    private static Logger log = LoggerFactory.getLogger(RpcClientManager.class);

    public RpcClientManager() {
        if (rpcClientInitializer == null) {
            rpcClientInitializer = SpringUtil.getBean(RpcClientInitializer.class);
        }
    }
    private static class InstanceHolder {
        public static final RpcClientManager instance = new RpcClientManager();
    }
    public static RpcClientManager getInstance() {
        return InstanceHolder.instance;
    }


    RpcClientInitializer rpcClientInitializer;

    /**
     * 连接
     */
    public void connect(NodeInfo nodeInfo, int index) {

        String rpcServer = nodeInfo.getIp();
        int rpcPort = nodeInfo.getPort();
        boolean isConnected = false;
        int rpcRetryTimes = nodeInfo.getRetrySize();
        int i = 0;
        final String localIp = AddressUtils.getInnetIp();
        while (!isConnected) {
            String key = rpcServer + RPCConstants.SEQ + rpcPort + RPCConstants.SEQ + index;
            i++;
            if (i > rpcRetryTimes) {

                log.info("###### 连接失败，key:{}  到达重试次数上线 retryCount:{}  添加服务监控队列中...", key, i, rpcRetryTimes);
                /**添加监控队列*/
                MQProvider.getRetryConnectQueue().push(nodeInfo, Duration.ofMillis(1000));
                break;
            }
            log.info("###### 开始对 {} 进行第 {}/{} 次连接...", key, i, rpcRetryTimes);
            try {
                RpcClient client0 = ConnectionCache.get(key);
                log.info("###### 开始重新连接IM...    key={},    imServerIp={},	 localIp={},    client0={},    clientMap.get(key))={},   clientMap.size()={}", key, rpcServer, localIp, client0, ConnectionCache.get(key), ConnectionCache.rpcPoolSize());
                if (client0 == null) {
                    synchronized (key.intern()) {
                        RpcClient client = new RpcClient(nodeInfo, index,key,rpcClientInitializer);   //服务端IP， 端口， 连接池索引
                        if (client.connection()) {
                            NodePoolCache.addActionRpcSrv(nodeInfo.getIp(),key,client);
                            isConnected = true;
                            log.info("@@@@RPC Server 连接成功！key={},     imServerIp={},	 localIp={},    clientMap.get(key)={},   clientMap.size()={}", key, rpcServer, localIp, ConnectionCache.get(key), ConnectionCache.rpcPoolSize());
                            }
                        }
                } else {
                    log.info("map中 {}   连接已存在,停止连接  client0={} !!!!!!!!!!!!!!", key, client0);
                    break;
                }


            } catch (Exception e) {
//                ConnectionCache.remove(key);
                NodePoolCache.removeActionRpcSrv(nodeInfo.getIp(),key);
                log.error("重连失败! 继续尝试...  key={}, e.toString()={}", key, e.toString());
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                //nothing to do
            }
        }
    }
}
