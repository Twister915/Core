package net.cogzmc.core.chat;

import lombok.Getter;
import net.cogzmc.core.chat.channels.ChannelManager;
import net.cogzmc.core.chat.channels.IChannelManager;
import net.cogzmc.core.modular.ModularPlugin;

public final class CoreChat extends ModularPlugin {
    @Getter private IChannelManager channelManager;

    @Override
    protected void onModuleEnable() {
        this.channelManager = new ChannelManager();

    }
}
