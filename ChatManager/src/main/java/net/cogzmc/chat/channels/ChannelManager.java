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

package net.cogzmc.chat.channels;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.cogzmc.chat.ChatManager;
import net.cogzmc.chat.filter.Filter;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Manages creation and registration of
 * channels. Also provides implementation
 * for sending messages on channels.
 *
 * <p>
 * Latest Change: Player display names
 * <p>
 *
 * @author Jake
 * @since 1/16/2014
 */
public final class ChannelManager {
    /**
     * Whether or not channels are enabled
     */
    @Getter
    private boolean enabled = false;

    /**
     * A list of currently registered channels.
     */
    @Getter
    private final List<Channel> channels = new ArrayList<>();

    @Getter public Channel defaultChannel;

    /**
     * A map of players to their respective channels
     */
    private final Map<CPlayer, Channel> playerChannels = new HashMap<>();

    public static final char COLOR_CHAR = '\u0026';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");

    public ChannelManager() {
        enabled = ChatManager.getInstance().getChannelsConfig().getConfig().getBoolean("channels.enabled");
    }

    /**
     * Registers all channels based on data stored in a config file.
     */
    public void registerChannels() {
        FileConfiguration config = ChatManager.getInstance().getChannelsConfig().getConfig();
        for (String chanName : config.getStringList("channels.list")) {
            String format = config.getString("formatting." + chanName + ".format");
            String permission = config.getString("formatting." + chanName + ".permission");
            boolean main = config.getBoolean("formatting." + chanName + ".default");
            boolean crossServer = config.getBoolean("formatting." + chanName + ".cross-server");
            boolean filter = config.getBoolean("formatting." + chanName + ".filter");
            Channel channel = new Channel(chanName, format, permission);
            channel.setPrimary(main);
            if (main) {
                this.defaultChannel = channel;
            }
            channel.setCrossServer(crossServer);
            channel.setFiltered(filter);
            registerChannel(channel);
        }
    }

    /**
     * Unregisters all registered channels
     */
    public void unregisterChannels() {
        for (Channel channel : channels) {
            unregisterChannel(channel);
        }
    }

    /**
     * Registers a channel
     *
     * @param channel channel to register
     */
    private void registerChannel(Channel channel) {
        ChatManager.getInstance().getLogger().info("Registered channel " + channel.getName() + ": " + channel.toString());
        channels.add(channel);
    }

    /**
     * Unregisters a channel. Sets all the
     * channel's current members to the default
     * channel.
     *
     * @param channel channel to unregister
     */
    private void unregisterChannel(Channel channel) {
        if (channels.contains(channel)) {
            for (CPlayer player : channel.getMembers()) {
                setChannel(player, getDefaultChannel());
            }
            channels.remove(channel);
        }
    }

    /**
     * Returns whether or not a channel is registered
     *
     * @param name name of the channel to check
     * @return whether or not the channel is registered
     */
    private boolean isChannelRegistered(String name) {
        return getChannelByName(name) != null;
    }

    /**
     * Returns a channel based on the search by name
     *
     * @param name name of the channel to search for
     * @return channel found based on the name parameter
     */
    public Channel getChannelByName(String name) {
        Preconditions.checkNotNull(name, "Name can not be null");
        for (Channel channel : channels) {
            if (channel.getName().toLowerCase().equals(name)) return channel;
        }
        return null;
    }

    /**
     * Returns the current channel for the player
     *
     * @param player the player to lookup
     * @return the channel that the player is currently on
     */
    public Channel getCurrentChannel(CPlayer player) {
        return getChannel(player);
    }

    /**
     * Handles the filtering of a message if necessary.
     * The finalized message is then passed on to the {@link Channel}
     * instance to be sent to the channel's listeners.
     *
     * @param sender  sender of the message as a {@link org.bukkit.entity.Player}
     * @param message message sent
     * @return the channel that the message was sent to
     */
    public Channel sendMessage(CPlayer sender, String message) {
        Channel channel = getCurrentChannel(sender);
        if (channel == null) {
            setChannel(sender, getDefaultChannel());
            channel = getCurrentChannel(sender);
        }
        if (channel.isFiltered()) {
            Filter.FilterData filterData = Filter.filter(message, sender);
            if (filterData.isCancelled()) {
                return channel;
            }
            message = filterData.getMessage();
        }
        String finalMessage = formatMessage(message, sender);
        channel.sendMessage(finalMessage, sender);
        Bukkit.getConsoleSender().sendMessage(finalMessage);
        return channel;
    }

    private String formatMessage(String message, CPlayer player) {
        String chanFormat = getCurrentChannel(player).getFormat();
        CGroup playerGroup = player.getPrimaryGroup();
        MessageFormat formatter = new MessageFormat(chanFormat);
        String senderName = player.getName();
        String senderDisplay = player.getDisplayName();
        String cleanMessage = STRIP_COLOR_PATTERN.matcher(message).replaceAll("");
        String prefix = player.getChatPrefix() != null ? player.getChatPrefix() : playerGroup.getChatPrefix();
        String suffix = player.getChatSuffix() != null ? player.getChatSuffix() : playerGroup.getChatSuffix();
        String resetColor = ChatColor.RESET + "";
        ChatColor rawColor = player.getChatColor() != null ? player.getChatColor() : playerGroup.getChatColor();
        String chatColor = rawColor != null ? "\u0026" + rawColor.getChar() : "";
        Object[] args = {senderName, senderDisplay, message, cleanMessage, prefix, suffix, chatColor, resetColor};
        return ChatColor.translateAlternateColorCodes('&', formatter.format(args));
    }

    /**
     * Sets a players channel
     *
     * @param player  player to set the channel of
     * @param channel channel to set the player to
     */
    public void setChannel(CPlayer player, Channel channel) {
        if (this.playerChannels.containsKey(player)) {
            this.playerChannels.get(player).removeMember(player);
        }
        channel.addMember(player);
    }


    /**
     * Removes the channel from a player
     *
     * @param player player to remove the channel from
     */
    public void removeChannel(CPlayer player) {
        this.playerChannels.get(player).removeMember(player);
        this.playerChannels.remove(player);
    }

    /**
     * Gets a {@link org.bukkit.entity.Player}'s channel
     *
     * @param player player to get the channel of
     * @return the channel of the player parameter
     */
    public Channel getChannel(CPlayer player) {
        return this.playerChannels.get(player);
    }
}
