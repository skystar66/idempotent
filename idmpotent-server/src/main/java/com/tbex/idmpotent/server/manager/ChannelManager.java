package com.tbex.idmpotent.server.manager;

import com.tbex.idmpotent.netty.msg.manager.SocketChannelManager;
import com.tbex.idmpotent.server.server.channel.NettyChannelManager;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

@Component
public class ChannelManager implements SocketChannelManager {

    @Override
    public void addChannel(Channel channel) {
        NettyChannelManager.getInstance().addChannel(channel);
    }

    @Override
    public void removeChannel(Channel channel) {
        NettyChannelManager.getInstance().removeChannel(channel);
    }

    @Override
    public void getChannel(String channelId) {
        NettyChannelManager.getInstance().getChannel(channelId);
    }
}
