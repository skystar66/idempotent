package com.tbex.idmpotent.server.mq;


import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.server.consumer.RpcMsgConsumer;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public final class MQProvider {
    private static final Map<Integer, MessageQueue<RpcCmd>> fromRPCMsgQueueMap = new HashMap<>();


    static {
        for (int i = 0; i < RpcMsgConsumer.threadCnt; i++) {//cpu*2个队列对应cpu*2个消费线程
            fromRPCMsgQueueMap.put(i, new DefaultMQ<>());
        }
    }

    /**
     * 得到与index相匹配的队列
     *
     * @param index
     * @return
     */
    public static MessageQueue<RpcCmd> getFromRPCMsgQueueByIndex(int index) {
        return fromRPCMsgQueueMap.get(index);
    }

    /**
     * 得到与key相匹配的队列
     *
     * @param key
     * @return
     */
    public static MessageQueue<RpcCmd> getFromRPCMsgQueueByKey(long key) {
        Long index = key % RpcMsgConsumer.threadCnt;
        return fromRPCMsgQueueMap.get(index.intValue());
    }

    /**
     * 得到随机的队列
     *
     * @return
     */
    public static MessageQueue<RpcCmd> getFromRPCMsgQueueByRandom() {
        return fromRPCMsgQueueMap.get(RandomUtils.nextInt(0, RpcMsgConsumer.threadCnt));
    }

    /**
     * push 消息
     *
     * @param rpcCmd
     */
    public static void push(RpcCmd rpcCmd) {
        if (null != rpcCmd) {
//            if (msgWrapper.getType() == RpcMsgTargetType.RPC_MSG_TARGET_TYPE_UID) {//发给uid的 同一uid发给相同队列，保证有序

            //保证有序
            getFromRPCMsgQueueByKey(Long.valueOf(rpcCmd.getMsg().loadBean(String.class))).push(rpcCmd, Duration.ofMillis(100));
//            } else {//发给随机的队列，考虑发给最少消息的队列
//                getFromRPCMsgQueueByRandom().push(msgWrapper, Duration.ofMillis(100));
//            }

        }
    }

    public static String getFromRPCMsgQueueSize() {
        StringBuilder builder = new StringBuilder();
        int totalSize = 0;
        for (Map.Entry<Integer, MessageQueue<RpcCmd>> entry : fromRPCMsgQueueMap.entrySet()) {
            int size = entry.getValue().size();
            builder.append("index:").append(entry.getKey()).append("-").append("size:").append(size).append("; ");
            totalSize += size;
        }
        builder.append("totalSize:").append(totalSize);
        return builder.toString();
    }

}
