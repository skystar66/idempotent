package com.tbex.idmpotent.client.client.channel;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyChannelManager {

    private static NettyChannelManager manager = null;

    private Map<String, Channel> channelMap;

    private ChannelGroup channelGroup;


    public NettyChannelManager() {
        channelMap = new ConcurrentHashMap<>();
        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    }

    public static NettyChannelManager getInstance() {
        if (manager == null) {
            synchronized (NettyChannelManager.class) {
                if (manager == null) {
                    manager = new NettyChannelManager();
                }
            }
        }
        return manager;
    }

    public void addChannel(Channel channel) {
        channelGroup.add(channel);

    }

    public void removeChannel(Channel channel) {
        channelGroup.remove(channel);

    }

    public Channel getChannel(String channelId) {
        return getChannel2(channelId);
    }


    public boolean contains(String channelId) {
        return getChannel2(channelId) != null;
    }


    public int currentSize() {
        return channelGroup.size();
    }


    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }


    /**
     * 根据key 获取channel通道
     */
    public Channel getChannel2(String key) {
        for (Channel channel : channelGroup) {
            if (channel.remoteAddress().toString().equals(key)) {
                return channel;
            }
        }
        return null;
    }


}
