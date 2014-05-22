package net.communitycraft.core.player;

import java.net.InetAddress;

/**
 * Implement this to hook into the {@link net.communitycraft.core.player.CPlayerManager}'s join and leave notifications to modify behavior.
 *
 * You must register using the {@link net.communitycraft.core.player.CPlayerManager#registerCPlayerConnectionListener(CPlayerConnectionListener)} method provided.
 */
public interface CPlayerConnectionListener {
    void onPlayerJoin(CPlayer player, InetAddress address) throws CPlayerJoinException;
    void onPlayerDisconnect(CPlayer player);
}
