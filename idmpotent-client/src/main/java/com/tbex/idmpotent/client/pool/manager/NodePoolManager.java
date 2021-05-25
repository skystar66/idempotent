package com.tbex.idmpotent.client.pool.manager;

import com.alibaba.fastjson.JSON;
import com.tbex.idmpotent.client.cluster.ClusterCenter;
import com.tbex.idmpotent.client.consistentHash.HashCircle;
import com.tbex.idmpotent.client.pool.ConnectionPoolFactory;
import com.tbex.idmpotent.client.pool.RpcClient;
import com.tbex.idmpotent.client.pool.connect.ConnectionCache;
import com.tbex.idmpotent.client.pool.weight.RpcLoadBalance;
import com.tbex.idmpotent.client.utils.RPCConstants;
import com.tbex.idmpotent.netty.node.NodeBuilder;
import com.tbex.idmpotent.netty.node.NodeInfo;
import com.tbex.idmpotent.netty.zookeeper.ZkHelp;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author xuliang
 * @date 2019年3月18日 下午3:16:41
 * <p>
 * 链接zookeeper,建立连接池
 */
public class NodePoolManager {


    // 内部静态类方式
    private static class InstanceHolder {
        private static NodePoolManager instance = new NodePoolManager();
    }

    public static NodePoolManager getInstance() {
        return NodePoolManager.InstanceHolder.instance;
    }


    private static final Logger logger = LoggerFactory.getLogger(NodePoolManager.class);

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private ZkHelp zkHelp = ZkHelp.getInstance();


    /**
     * 初始化连接池
     */
    public void initNodePool() {

        /**获取节点列表*/
        List<String> nodeDatas = zkHelp.getChildren(RPCConstants.DEFAULT_IDP_SERVER);
        /**添加到hash circle环中*/
        HashCircle.getInstance().init(RPCConstants.DEFAULT_IDP_SERVER, nodeDatas.stream().collect(Collectors.joining(",")));
        List<NodeInfo> nodeInfos = new ArrayList<>();
        for (String nodeIp : nodeDatas) {
            try {
                /**获取当前节点数据*/
                String nodeData = zkHelp.getValue(RPCConstants.DEFAULT_IDP_SERVER +
                        "/" + nodeIp);

                NodeInfo nodeInfo = JSON.parseObject(nodeData, NodeInfo.class);
                nodeInfos.add(nodeInfo);
                /**监控当前服务节点的变化*/
                ClusterCenter.getInstance().listenerServerRpcConfig(nodeIp);
            } catch (Exception e) {
                logger.error("onNodeDataChange.parseObject", e);
            }
        }
        /**初始化连接池及权重值变化*/
        initPoolAndWeight(nodeInfos);
    }


    /**
     * 节点变更通知
     */
    public void onNodeChange(List<NodeInfo> nodeDatas) {
        try {
            lock.writeLock().lock();
            logger.info("onNodeDataChange->" + nodeDatas.size() + "=" + JSON.toJSONString(nodeDatas));
            /**初始化连接并赋予权重值*/
            initPoolAndWeight(nodeDatas);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 初始化连接并赋予权重值
     */
    public void initPoolAndWeight(List<NodeInfo> nodeDatas) {
        for (NodeInfo nodeInfo : nodeDatas) {
            /**step1: 建立连接*/
            ConnectionPoolFactory.getInstance().zkSyncRpcServer(nodeInfo);
            /**step2: 添加服务节点信息*/
            RpcLoadBalance.getInstance().addNode(nodeInfo);
        }
        /**step3: 初始化服务节点权重*/
        RpcLoadBalance.getInstance().initWeight();
    }


    /**
     * 根据选择服务器,支持权重
     */
    public RpcClient chooseRpcClient() {
        try {
            //todo 内存级别锁，不会影响效率,IO 不建议这样做
            lock.readLock().lock();
            String channelKey = RpcLoadBalance.getInstance().chooseNodeChannel();
            if (StringUtils.isEmpty(channelKey)) {
                logger.info(">>>>>>> channel 不存在，请检查服务是否发生异常！！！");
                throw new RuntimeException(" channel 不存在，请检查调用服务是否发生异常！！！");
            }
            logger.info(">>>>>>> current choose server node key :{} ", channelKey);
            return ConnectionCache.get(channelKey);

        } finally {
            lock.readLock().unlock();
        }

    }

    /**
     * 根据选择服务器,支持取模
     * 根据幂等ID 进行取模
     */
    public RpcClient chooseRpcClient(String node) {
        try {
            //todo 内存级别锁，不会影响效率,IO 不建议这样做
            lock.readLock().lock();
            String channelKey = RpcLoadBalance.getInstance().getChannelKey(node);
            if (StringUtils.isEmpty(channelKey)) {
                logger.info(">>>>>>> channel 不存在，请检查服务是否发生异常！！！");
                throw new RuntimeException(" channel 不存在，请检查调用服务是否发生异常！！！");
            }
            logger.info(">>>>>>> current choose server node key :{} ", channelKey);
            return ConnectionCache.get(channelKey);

        } finally {
            lock.readLock().unlock();
        }

    }
}
