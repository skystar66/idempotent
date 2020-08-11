package com.tbex.idmpotent.netty.msg.manager;

import io.netty.channel.Channel;

public interface SocketChannelManager {

    /**
     * 添加channel
     */

    public void addChannel(Channel channel);


    /**
     * 移除channel
     */

    public void removeChannel(Channel channel);

    /**
     * 获取channel
     */
    public void getChannel(String channelId);

    default public boolean contains(String channelId) {
        return false;
    }

}
