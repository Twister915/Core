package net.cogzmc.core.chat;

import lombok.Getter;
import net.cogzmc.core.chat.channels.Channel;
import net.cogzmc.core.chat.channels.ConfigurationChannelManager;
import net.cogzmc.core.chat.channels.IChannelManager;
import net.cogzmc.core.modular.ModularPlugin;

public final class CoreChat extends ModularPlugin {
    @Getter private static CoreChat instance;
    @Getter private IChannelManager channelManager;

    public final static String COLOR_CHAT_PERMISSION = "core.chat.color";

    public static String getChannelJoinPermission(Channel channel) {
        return "core.chat.join" + channel.getName();
    }

    public static String getChannelListenPermission(Channel channel) {
        return "core.chat.listen" + channel.getName();
    }

    public static String getChannelLeavePermission(Channel channel) {
        return "core.chat.leave" + channel.getName();
    }


    public static String getChannelUnlistenPermission(Channel channel) {
        return "core.chat.unlisten" + channel.getName();
    }

    @Override
    protected void onModuleEnable() {
        instance = this;
        this.channelManager = new ConfigurationChannelManager();

    }
}
