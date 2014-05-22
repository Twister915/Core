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

import lombok.Data;
import lombok.ToString;
import net.cogzmc.chat.channels.base.BaseChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Latest Change: Rewrite for Bukkit
 * <p>
 *
 * @author Jake
 * @since 1/16/2014
 */
@Data
@ToString(exclude = {"members", "format"})
public final class Channel implements BaseChannel {
    private String name; //Name of the channel
    private String format; //Format of the channel
    private String permission; //Permission required to see chat in this channel
    private boolean main; //Whether or not this is the default channel
    private boolean crossServer; //Whether or not messages in this channel are sent across the network
    private boolean filtered; //Whether or not this channel is filtered
    private List<Player> members; //A list of members in this channel

    public Channel(String name, String format, String permission) {
        this.name = name;
        this.format = format;
        this.permission = permission;
        this.members = new ArrayList<>();
    }

    @Override
    public boolean hasPermission() {
        return permission != null && !permission.equals("");
    }

    @Override
    public boolean isDefault() {
        return this.main;
    }

    @Override
    public void setDefault(boolean main) {
        this.main = main;
    }

    @Override
    public void sendMessage(String message, Player sender) {
        for (Player receiver : Bukkit.getOnlinePlayers()) {
            if (!receiver.isValid()) continue;
            if (this.hasPermission()) {
                if (receiver.hasPermission(getPermission())) {
                    receiver.sendMessage(message);
                }
            } else {
                receiver.sendMessage(message);
            }
        }
    }

    /**
     * Adds a member to the channel
     *
     * @param player player to add to he channel
     */
    public void addMember(Player player) {
        this.members.add(player);
    }

    /**
     * Removes a member from the {@link net.cogzmc.chat.channels.Channel}
     *
     * @param player {@link org.bukkit.entity.Player} to remove from the {@link net.cogzmc.chat.channels.Channel}
     */
    public void removeMember(Player player) {
        this.members.remove(player);
    }

    /**
     * Checks if this {@link net.cogzmc.chat.channels.Channel} instance has the {@link org.bukkit.entity.Player} as a member.
     *
     * @param player player to check for
     * @return whether or not the {@link org.bukkit.entity.Player} is a member
     */
    public boolean hasMember(Player player) {
        return this.members.contains(player);
    }
}
