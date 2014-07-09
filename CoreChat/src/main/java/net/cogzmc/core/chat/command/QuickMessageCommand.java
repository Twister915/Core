package net.cogzmc.core.chat.command;

import com.google.common.base.Joiner;
import net.cogzmc.core.chat.CoreChat;
import net.cogzmc.core.chat.channels.Channel;
import net.cogzmc.core.chat.channels.ChannelException;
import net.cogzmc.core.chat.channels.ChatterListener;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CPlayer;

public class QuickMessageCommand extends ModuleCommand {
    private final Channel channel;

    public QuickMessageCommand(String name, Channel channel) {
        super(name);
        this.channel = channel;
    }

    @Override
    protected void handleCommand(CPlayer commandSender, String[] args) throws CommandException {
        String message = Joiner.on(" ").join(args);
        try {
            ChatterListener.sendMessageOnChannel(commandSender, message, channel);
        } catch (ChannelException e) {
            commandSender.sendMessage(
                    CoreChat.getInstance().getFormat("cannot-join-channel",
                            new String[]{"<channel>", channel.getName()},
                            new String[]{"<error>", e.getMessage()})
            );
        }
    }
}
