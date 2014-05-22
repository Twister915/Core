/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.cogzmc.chat.channels.commands;

import net.cogzmc.chat.ChatManager;
import net.cogzmc.chat.channels.Channel;
import net.communitycraft.core.modular.command.*;
import net.communitycraft.core.player.CPlayer;

/**
 * Commands to manage a player's channel
 * including listing and switching between
 * channels.
 *
 * <p>
 * Latest Change: Rewrite for Bukkit
 * <p>
 *
 * @author Jake0oo0
 * @since 1/18/2014
 */
@CommandMeta(
        aliases = {"ch", "chan"}
)
public final class ChannelCommand extends ModuleCommand {

    public ChannelCommand() {
        super("channel");
    }

    @Override
    protected void handleCommand(CPlayer sender, String[] args) throws CommandException {
        if (args.length != 1) throw new ArgumentRequirementException("You must specify a channel to join.");

        Channel channel = ChatManager.getInstance().getChannelManager().getChannelByName(args[0].toLowerCase());
        if (channel == null) {
            throw new CommandException("The channel " + args[0] + " does not exist!");
        }

        if (channel.hasPermission() && !sender.hasPermission(channel.getPermission())) {
            throw new PermissionException("You need the permission " + channel.getPermission() + " to use this channel.");
        }

        ChatManager.getInstance().getChannelManager().setChannel(sender, channel);
        sender.sendMessage(ChatManager.getInstance().getFormat("switched", false, new String[]{"<channel>", channel.getName()}));
    }
}
