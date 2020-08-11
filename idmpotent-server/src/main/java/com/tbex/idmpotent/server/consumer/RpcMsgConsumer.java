package com.tbex.idmpotent.server.consumer;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.server.mq.MQProvider;
import com.tbex.idmpotent.server.mq.MessageQueue;
import com.tbex.idmpotent.server.server.IdmpotentServerHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @ClassName: RpcMsgConsumer
 * @Description: todo rpc消息服务处理器
 * @Author: xuliang
 * @Date: 2020/4/26 下午1:05
 * @Version: 1.0
 */
@Slf4j
public class RpcMsgConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RpcMsgConsumer.class);

    public static final int threadCnt = Runtime.getRuntime().availableProcessors() * 2;

    private final IdmpotentServerHandler rpcMsgHandler;
    private ExecutorService msgSenderExecutor;

    public RpcMsgConsumer(IdmpotentServerHandler rpcMsgHandler) {
        this.rpcMsgHandler = rpcMsgHandler;
    }

    public void start() {

        msgSenderExecutor = Executors.newFixedThreadPool(threadCnt);

        for (int i = 0; i < threadCnt; i++) {
            msgSenderExecutor.execute(new MQMsgConsumerWorker(i));
        }
    }

    public void stop() {
        msgSenderExecutor.shutdown();
    }

    private void consumeNewMsg(RpcCmd msgWrapper) {
        logger.info("处理消息 consumer msg : {}", msgWrapper);
        rpcMsgHandler.callback(msgWrapper);
    }


    private class MQMsgConsumerWorker implements Runnable {
        private final Duration timeout = Duration.ofMillis(100);
        private final MessageQueue<RpcCmd> fromRPCMsgQueue;

        public MQMsgConsumerWorker(int index) {
            this.fromRPCMsgQueue = MQProvider.getFromRPCMsgQueueByIndex(index);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (null != fromRPCMsgQueue) {
                        RpcCmd msg = fromRPCMsgQueue.pop(timeout);
                        consumeNewMsg(msg);
                    }
                } catch (Exception ignore) {
                    logger.warn("fromRPCMsgQueue.pop", ignore);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }
}
