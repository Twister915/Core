package net.cogzmc.core.chat.channels;

import java.util.List;

public interface ChannelSource {
    List<Channel> getNewChannels() throws ChannelException;
}
