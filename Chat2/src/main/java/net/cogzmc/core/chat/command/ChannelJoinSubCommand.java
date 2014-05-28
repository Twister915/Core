package net.cogzmc.core.chat.command;

import net.cogzmc.core.chat.CoreChat;
import net.cogzmc.core.chat.channels.Channel;
import net.cogzmc.core.chat.channels.ChannelException;
import net.cogzmc.core.chat.channels.IChannelManager;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CPlayer;

final class ChannelJoinSubCommand extends ModuleCommand {
    private final Channel channel;

    protected ChannelJoinSubCommand(Channel channel) {
        super(channel.getName().toLowerCase(), new LeaveCommand(channel), new QuickMessageCommand("qm", channel));
        this.channel = channel;
    }

    @Override
    protected void handleCommand(CPlayer commandSender, String[] args) throws CommandException {
        IChannelManager channelManager = CoreChat.getInstance().getChannelManager();
        try {
            channelManager.makePlayerParticipant(commandSender, channel);
        } catch (ChannelException e) {
            commandSender.sendMessage(
                    CoreChat.getInstance().getFormat("cannot-join-channel",
                            new String[]{"<channel>", channel.getName()},
                            new String[]{"<error>", e.getMessage()})
            );
            return;
        }
        commandSender.sendMessage(CoreChat.getInstance().getFormat("joined-channel", new String[]{"<channel>", channel.getName()}));
    }
}
