package net.cogzmc.core.chat.command;

import net.cogzmc.core.chat.CoreChat;
import net.cogzmc.core.chat.channels.Channel;
import net.cogzmc.core.chat.channels.ChannelException;
import net.cogzmc.core.chat.channels.IChannelManager;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CPlayer;

class LeaveCommand extends ModuleCommand {
    private final Channel channel;

    LeaveCommand(Channel channel) {
        super("leave");
        this.channel = channel;
    }

    @Override
    protected void handleCommand(CPlayer commandSender, String[] args) throws CommandException {
        CoreChat coreChat = CoreChat.getInstance();
        IChannelManager channelManager = coreChat.getChannelManager();
        try {
            channelManager.removePlayerAsListener(commandSender, channel);
        } catch (ChannelException e) {
            commandSender.sendMessage(
                    coreChat.getFormat("cannot-join-channel",
                            new String[]{"<channel>", channel.getName()},
                            new String[]{"<error>", e.getMessage()})
            );
            return;
        }
        String leaveMessage = coreChat.getFormat("leave-announce", new String[]{"<player>", commandSender.getDisplayName()}, new String[]{"<channel>", channel.getName()});
        commandSender.sendMessage(leaveMessage);
        for (CPlayer cPlayer : channelManager.getParticipants(channel)) {
            cPlayer.sendMessage(leaveMessage);
        }
    }
}
