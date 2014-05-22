package net.cogzmc.chat.channels.commands;

import net.cogzmc.chat.ChatManager;
import net.cogzmc.chat.channels.Channel;
import net.communitycraft.core.modular.command.ArgumentRequirementException;
import net.communitycraft.core.modular.command.CommandException;
import net.communitycraft.core.modular.command.ModuleCommand;
import org.bukkit.command.CommandSender;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/21/2014
 */
public class ChannelsCommand extends ModuleCommand {
    public ChannelsCommand() {
        super("channels");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length != 0) throw new ArgumentRequirementException("No arguments are required.");

        sender.sendMessage(ChatManager.getInstance().getFormat("channels", false));
        for (Channel channel : ChatManager.getInstance().getChannelManager().getChannels()) {
            if (channel.hasPermission() && !sender.hasPermission(channel.getPermission())) continue;
            sender.sendMessage(ChatManager.getInstance().getFormat("channel", false, new String[]{"<channel>", channel.getName()}));
        }
    }
}
