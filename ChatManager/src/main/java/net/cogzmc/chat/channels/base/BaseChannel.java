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

package net.cogzmc.chat.channels.base;

import net.cogzmc.core.player.CPlayer;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Base implementation of a channel.
 *
 * <p>
 * Latest Change: Rewrite for Bukkit
 * <p>
 *
 * @author Jake
 * @since 1/16/2014
 */
public interface BaseChannel {
    /**
     * Name of the channel
     *
     * @return channel name
     */
    public String getName();

    /**
     * Format of the channel. Uses
     * {@link java.text.MessageFormat} to
     * format the message sent.
     *
     * @return the channel format
     */
    public String getFormat();

    /**
     * Sets the channel format
     *
     * @param format format to set the channel to
     */
    public void setFormat(String format);

    /**
     * Gets the permission required to see messages in this channel
     *
     * @return channel permission
     */
    public String getPermission();

    /**
     * Whether or not the channel has a permission. Checks if the permission is null or is empty.
     *
     * @return whether or not a permission exists
     */
    public boolean hasPermission();

    /**
     * Returns whether or not the channel is the default one
     *
     * @return whether or not the channel is the default one
     */
    public boolean isDefault();

    /**
     * Sets whether or not the channel is the default one
     *
     * @param main whether or not the channel is the default one
     */
    public void setDefault(boolean main);

    /**
     * Returns whether or not the channel sends messages across servers
     *
     * @return whether or not the channel sends cross server messages
     */
    public boolean isCrossServer();

    /**
     * Sets whether or not the channel is cross server
     *
     * @param crossServer whether or not the channel is cross server
     */
    public void setCrossServer(boolean crossServer);

    public boolean isFiltered();

    public void setFiltered(boolean filtered);

    /**
     * Sends a message on this channel to all of the player's with the necessary listening permission
     *
     * @param message message to send
     * @param sender  {@link org.bukkit.entity.Player} instance of the sender
     */
    public void sendMessage(String message, CPlayer sender);

    /**
     * {@link java.util.List} of Bukkit {@link org.bukkit.entity.Player} that are members of this channel.
     *
     * @return a list of {@link org.bukkit.entity.Player} who are members in this channel
     */
    public List<Player> getMembers();
}
