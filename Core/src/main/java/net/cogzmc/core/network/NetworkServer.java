package net.cogzmc.core.network;

import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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
     * Gets the number of players online on the {@link net.cogzmc.core.network.NetworkServer}.
     * @return The number of players online as an {@link java.lang.Integer}
     */
    Integer getOnlineCount();

    /**
     * Get the maximum players permitted on the server as per the server.properties
     * @return The maximum players allowed on the server.
     */
    Integer getMaximumPlayers();

    /**
     * Gets a list of {@link net.cogzmc.core.player.COfflinePlayer}s that represent the players currently logged into this server.
     * @return A list of {@link net.cogzmc.core.player.COfflinePlayer}s
     */
    List<UUID> getPlayers();

    /**
     * Transfers a player to this server.
     * @param player The {@link net.cogzmc.core.player.CPlayer} to transfer to this server.
     */
    void sendPlayerToServer(CPlayer player);

    /**
     * Gets the last time this server's information was updated.
     * @return A {@link java.util.Date} object representing the last time this server was pinged.
     */
    Date getLastPing();

    /**
     * Sends a {@link net.cogzmc.core.network.NetCommand} to any listeners on the target {@link net.cogzmc.core.network.NetworkServer}.
     * @param command The {@link net.cogzmc.core.network.NetCommand} for the target server to receive.
     */
    void sendNetCommand(NetCommand command);
}
