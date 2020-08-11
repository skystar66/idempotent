package com.tbex.idmpotent.netty.content;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RpcCmdContext {


    private int cacheSize = 1024;
    private long waitTime = 1;

    private static RpcCmdContext context = null;

    private Map<String, RpcContent> map;


    private List<RpcContent> cacheList;


    private LinkedList<RpcContent> freeList;

    public static RpcCmdContext getInstance() {
        if (context == null) {
            synchronized (RpcCmdContext.class) {
                if (context == null) {
                    context = new RpcCmdContext();
                }
            }
        }
        return context;
    }

    private RpcCmdContext() {
        map = new ConcurrentHashMap<>();
        cacheList = new CopyOnWriteArrayList<>();
        freeList = new LinkedList<>();
    }

    /**
     * 并发可能会出现脏读
     *
     * @param key key
     * @return hasKey
     */
    public synchronized boolean hasKey(String key) {
        return map.containsKey(key);
    }

    /**
     * 并发可能会出重复添加
     *
     * @param key key
     * @return RpcContent
     */
    public synchronized RpcContent addKey(String key) {
        RpcContent rpcContent = createRpcContent();
        map.put(key, rpcContent);
        return rpcContent;
    }

    private RpcContent createRpcContent() {
        if (cacheList.size() < cacheSize) {
            RpcContent rpcContent = new RpcContent(getWaitTime());
            rpcContent.init();
            cacheList.add(rpcContent);
            return rpcContent;
        } else {
            return findRpcContent();
        }


    }


    /**
     * 空闲队列处理
     *
     * @return RpcContent
     */
    private RpcContent findRpcContent() {
        synchronized (freeList) {
            //获取第一个元素
            RpcContent rpcContent = freeList.getFirst();
            if (!rpcContent.isUsed()) {
                rpcContent.init();
                freeList.remove(rpcContent);
                return rpcContent;
            }
        }
        RpcContent rpcContent = new RpcContent(getWaitTime());
        rpcContent.init();
        return rpcContent;
    }


    public RpcContent getKey(String key) {
        RpcContent rpcContent = map.get(key);
        clearKey(key);
        return rpcContent;
    }

    private void clearKey(String key) {
        RpcContent rpcContent = map.get(key);
        if (cacheList.contains(rpcContent)) {
            synchronized (freeList) {
                freeList.add(rpcContent);
            }
        }
        map.remove(key);
    }


    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }
}
