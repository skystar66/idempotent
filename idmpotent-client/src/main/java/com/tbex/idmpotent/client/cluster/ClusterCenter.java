package com.tbex.idmpotent.client.cluster;

import com.alibaba.fastjson.JSON;
import com.github.zkclient.IZkChildListener;
import com.github.zkclient.IZkDataListener;
import com.tbex.idmpotent.client.consistentHash.HashCircle;
import com.tbex.idmpotent.client.pool.manager.NodePoolManager;
import com.tbex.idmpotent.client.utils.RPCConstants;
import com.tbex.idmpotent.netty.node.NodeInfo;
import com.tbex.idmpotent.netty.zookeeper.ZkHelp;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Zk Cluster管理
 *
 * @author xl
 * @version 2020年11月20日
 */
public class ClusterCenter {

    private Logger log = LoggerFactory.getLogger(ClusterCenter.class);

    private static ZkHelp zkHelp = ZkHelp.getInstance();

    // 内部静态类方式
    private static class InstanceHolder {
        private static ClusterCenter instance = new ClusterCenter();
    }

    public static ClusterCenter getInstance() {
        return InstanceHolder.instance;
    }


    public ClusterCenter() {

    }

    public List<String> serverRpcList = null;
    public String rpcPoolSize = null;


    /**
     * Server RPC连接
     */
    public void listenerServerRpc() {
        serverRpcList = zkHelp.getChildren(RPCConstants.DEFAULT_IDP_SERVER);
        log.info("serverRpcList:{}", serverRpcList);
        IZkChildListener listener = new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
                // 监听到子节点变化 更新cluster
                log.info("----->>>>> Starting handle children change " + parentPath + "/" + currentChildren + " size=" + currentChildren.size());
                serverRpcList = currentChildren;
                List<NodeInfo> nodeInfos = new ArrayList<>();
                for (String node : currentChildren) {
                    try {
                        String nodeData = zkHelp.getValue(RPCConstants.DEFAULT_IDP_SERVER +
                                "/" + node);
                        if (StringUtils.isEmpty(nodeData)) {
                            return;
                        }
                        NodeInfo nodeInfo = JSON.parseObject(nodeData, NodeInfo.class);
                        if (nodeInfo != null) nodeInfos.add(nodeInfo);

                    } catch (Exception e) {
                        log.error("onNodeDataChange.parseObject", e);
                    }
                }
                /**添加到hash circle环中*/
                HashCircle.getInstance().init(RPCConstants.DEFAULT_IDP_SERVER, currentChildren.stream().collect(Collectors.joining(",")));
                NodePoolManager.getInstance().onNodeChange(nodeInfos);
            }
        };
        // 监控节点变更
        zkHelp.subscribeChildChanges(RPCConstants.DEFAULT_IDP_SERVER
                , listener);
    }


    /**
     * 监听serverRpc 配置信息
     */
    public void listenerServerRpcConfig(String ip) {

        String listenerPath = RPCConstants.DEFAULT_IDP_SERVER + "/" + ip;
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, byte[] data) throws Exception {
                // 监听到子节点变化 更新cluster
                log.info("----->>>>> Starting handle data change " + "/" + dataPath + " data=" + new String(data));
                String dataStr = new String(data);
                List<NodeInfo> nodeInfos = new ArrayList<>();
                try {
                    NodeInfo nodeInfo = JSON.parseObject(dataStr, NodeInfo.class);
                    if (nodeInfo != null) nodeInfos.add(nodeInfo);
                } catch (Exception e) {
                    log.error("onNodeDataChange.parseObject", e);
                }

                NodePoolManager.getInstance().onNodeChange(nodeInfos);
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {

            }
        };

        // 监控节点变更
        zkHelp.subscribeDataChanges(listenerPath
                , listener);
    }
}