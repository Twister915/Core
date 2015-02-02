package net.cogzmc.core.chat.channels;

import net.cogzmc.core.Core;
import net.cogzmc.core.chat.CoreChat;
import net.cogzmc.core.network.NetCommandHandler;
import net.cogzmc.core.network.NetworkServer;
import net.cogzmc.core.player.COfflinePlayer;

import java.util.UUID;

public class ChannelNetCommandHandler implements NetCommandHandler<ChatNetCommand> {
    @Override
    public void handleNetCommand(NetworkServer sender, ChatNetCommand netCommand) {
        COfflinePlayer offlinePlayerByUUID = Core.getOfflinePlayerByUUID(UUID.fromString(netCommand.getSenderUUID()));
        if (offlinePlayerByUUID == null) return;
        Channel channelByName = CoreChat.getInstance().getChannelManager().getChannelByName(netCommand.getChannel());
        try {
            ChatterListener.sendMessage(offlinePlayerByUUID, netCommand.getMessage(), channelByName);
        } catch (ChannelException e) {
            Core.getInstance().getLogger().severe("Unable to handle cross server message from " + sender.getName() + " on channel " + netCommand.getChannel());
        }
    }
}
