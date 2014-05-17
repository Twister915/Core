package net.communitycraft.core.network;

import net.communitycraft.core.player.COfflinePlayer;
import net.communitycraft.core.player.CPlayer;

import java.util.Date;
import java.util.List;

/**
 * This interface is used to represent a single server on the network that this server is connected to.
 *
 * Any implementation of this code should return values for all methods in this interface, and throw no exceptions.
 */
public interface NetworkServer {
    /**
     * Gets the name of the server
     * @return {@link java.lang.String} that represents the name of the server.
     */
    String getName();

    /**
     * Gets the number of players online on the {@link net.communitycraft.core.network.NetworkServer}.
     * @return The number of players online as an {@link java.lang.Integer}
     */
    Integer getOnlineCount();

    /**
     * Gets a list of {@link net.communitycraft.core.player.COfflinePlayer}s that represent the players currently logged into this server.
     * @return A list of {@link net.communitycraft.core.player.COfflinePlayer}s
     */
    List<COfflinePlayer> getPlayers();

    /**
     * Transfers a player to this server.
     * @param player The {@link net.communitycraft.core.player.CPlayer} to transfer to this server.
     */
    void sendPlayerToServer(CPlayer player);

    /**
     * Gets the last time this server's information was updated.
     * @return A {@link java.util.Date} object representing the last time this server was pinged.
     */
    Date getLastPing();
}
