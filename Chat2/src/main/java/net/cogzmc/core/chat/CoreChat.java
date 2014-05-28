package net.cogzmc.core.chat;

import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.chat.channels.*;
import net.cogzmc.core.chat.channels.yaml.ConfigurationChannelManager;
import net.cogzmc.core.chat.command.ChannelCommand;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;
import net.cogzmc.core.player.CPlayerConnectionListener;

@ModuleMeta(
        name = "Core Chat",
        description = "Manages chat and channels, along with providing a rich API for hooking into the chat system."
)
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
    protected void onModuleEnable() throws Exception {
        instance = this;
        this.channelManager = new ConfigurationChannelManager(); //THROWS CAUGHT
        getPlayerManager().registerCPlayerConnectionListener((CPlayerConnectionListener) this.channelManager);
        registerListener(new ChatterListener(this.channelManager));
        if (Core.getNetworkManager() != null) Core.getNetworkManager().registerNetCommandHandler(new ChannelNetCommandHandler(), ChatNetCommand.class);
        registerCommand(new ChannelCommand());
    }
}
