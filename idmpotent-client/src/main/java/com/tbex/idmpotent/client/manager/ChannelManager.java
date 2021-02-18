package com.tbex.idmpotent.client.manager;

import com.tbex.idmpotent.client.client.channel.NettyChannelManager;
import com.tbex.idmpotent.netty.msg.manager.SocketChannelManager;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChannelManager implements SocketChannelManager {

    @Autowired
    ConnectionPoolFactory connectionPoolFactory;

    @Override
    public void addChannel(Channel channel) {
        NettyChannelManager.getInstance().addChannel(channel);
    }

    @Override
    public void removeChannel(Channel channel) {
        NettyChannelManager.getInstance().removeChannel(channel);
        connectionPoolFactory.removeConnect(channel);
    }

    @Override
    public void getChannel(String channelId) {
        NettyChannelManager.getInstance().getChannel(channelId);
    }

    @Override
    public boolean contains(String channelId) {
        return NettyChannelManager.getInstance().contains(channelId);
    }
}
