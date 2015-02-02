package net.cogzmc.core.chat.command;

import net.cogzmc.core.chat.CoreChat;
import net.cogzmc.core.chat.channels.Channel;
import net.cogzmc.core.modular.command.CommandMeta;
import net.cogzmc.core.modular.command.CommandPermission;
import net.cogzmc.core.modular.command.ModuleCommand;

import java.util.ArrayList;
import java.util.List;

@CommandMeta(aliases = {"ch", "chan"})
@CommandPermission("core.chat.channels")
public final class ChannelCommand extends ModuleCommand {
    public ChannelCommand() {
        super("channel", generateSubCommands());
    }

    private static ModuleCommand[] generateSubCommands() {
        List<ChannelJoinSubCommand> channelJoinSubCommandList = new ArrayList<>();
        for (Channel channel : CoreChat.getInstance().getChannelManager().getChannels()) {
            channelJoinSubCommandList.add(new ChannelJoinSubCommand(channel));
        }
        return channelJoinSubCommandList.toArray(new ModuleCommand[channelJoinSubCommandList.size()]);
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
