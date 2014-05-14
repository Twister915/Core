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
    String getName();

    Integer getOnlineCount();
    List<COfflinePlayer> getPlayers();
    void sendPlayerToServer(CPlayer player);

    Date getLastPing();
}
