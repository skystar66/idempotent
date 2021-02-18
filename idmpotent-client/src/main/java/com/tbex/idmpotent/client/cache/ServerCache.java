package com.tbex.idmpotent.client.cache;

import com.google.common.cache.LoadingCache;
import com.tbex.idmpotent.client.kit.ZKit;
import com.tbex.idmpotent.client.weight.ServerAdrWeight;
import com.tbex.idmpotent.client.weight.ServerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Function: 服务器节点缓存
 *
 * @author xuliang
 * Date: 2018/8/19 01:31
 * @since JDK 1.8
 */
@Component
public class ServerCache {

    private static Logger logger = LoggerFactory.getLogger(ServerCache.class);

    @Autowired
    private LoadingCache<ServerNode, ServerAdrWeight> cache;

    @Autowired
    ConnectionPoolFactory connectionPoolFactory;

    @Autowired
    private ZKit zkUtil;

    public void addCache(ServerNode serverNode, ServerAdrWeight serverAddress) {
        cache.put(serverNode, serverAddress);
    }


    /**
     * 更新所有缓存/先删除 再新增
     *
     * @param currentChildren
     */
    public void updateCache(List<String> currentChildren) {
        cache.invalidateAll();
        for (String currentChild : currentChildren) {
            // currentChildren=nodeId-127.0.0.1:11212:available 0/1
            String key = "";
            String serverAddress = "";
            if (currentChild.split("-").length == 2) {
                key = currentChild.split("-")[1];
                serverAddress = currentChild.split("-")[2];
            } else {
                key = currentChild;
            }
            /**构建server 权重*/
            ServerAdrWeight serverAdrWeight = ServerAdrWeight.builder().ip(serverAddress.split(":")[0])
                    .port(Integer.parseInt(serverAddress.split(":")[1]))
                    .weight(Integer.parseInt(serverAddress.split(":")[2]))
                    .nodeId(key)
                    .build();
            /**构建node 节点*/
            ServerNode serverNode = ServerNode.builder().nodeId(key)
                    .available(Integer.parseInt(serverAddress.split(":")[2]))
                    .build();

            addCache(serverNode, serverAdrWeight);
        }

        /**创建连接池*/
        connectionPoolFactory.addConnect(cache);

    }


//    /**
//     * 获取所有的服务列表
//     *
//     * @return
//     */
//    public List<ServerAdrWeight> getServerList() {
//
//        List<ServerAdrWeight> list = new ArrayList<>();
//
//        if (cache.size() == 0) {
//            List<String> allNode = zkUtil.getAllNode();
//            for (String node : allNode) {
//                String key = node.split("-")[1];
//                String serverAddress = node.split("-")[2];
//
//                /**构建server 权重*/
//                ServerAdrWeight serverAdrWeight = ServerAdrWeight.builder().ip(serverAddress.split(":")[0])
//                        .port(Integer.parseInt(serverAddress.split(":")[1]))
//                        .weight(Integer.parseInt(serverAddress.split(":")[2]))
//                        .build();
//
//                addCache(key, serverAdrWeight);
//            }
//        }
//        for (Map.Entry<String, ServerAdrWeight> entry : cache.asMap().entrySet()) {
//            list.add(entry.getKey());
//        }
//        return list;
//
//    }

//    /**
//     * rebuild cache list
//     */
//    public void rebuildCacheList() {
//        updateCache(getServerList());
//    }
}
