package net.cogzmc.core.chat.channels;

import net.cogzmc.core.chat.CoreChat;

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
