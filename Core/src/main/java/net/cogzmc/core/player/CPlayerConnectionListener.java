package net.cogzmc.core.player;

import java.net.InetAddress;

/**
 * Implement this to hook into the {@link net.cogzmc.core.player.CPlayerManager}'s join and leave notifications to modify behavior.
 *
 * You must register using the {@link net.cogzmc.core.player.CPlayerManager#registerCPlayerConnectionListener(CPlayerConnectionListener)} method provided.
 *
 * @since 1.0
 * @author Joey
 * @see net.cogzmc.core.player.CPlayerManager#registerCPlayerConnectionListener(CPlayerConnectionListener)
 * @see net.cogzmc.core.player.CPlayerManager#unregisterCPlayerConnectionListener(CPlayerConnectionListener)
 */
public interface CPlayerConnectionListener {
    /**
     * Called by the {@link net.cogzmc.core.player.CPlayerManager} to notify you that
     * a player has logged into the server on the {@link java.net.InetAddress} specified in the {@code address}
     * param.
     * @param player The player who has logged in.
     * @param address The IP address they are logging in through.
     * @throws CPlayerJoinException Throw this exception during login to cancel it for any reason. This will kick the
     * player and discard the {@link net.cogzmc.core.player.CPlayer} instance for them.
     */
    void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException;

    /**
     * Called by the {@link net.cogzmc.core.player.CPlayerManager} when a player disconnects from the server.
     * @param player The player who is disconnecting from the server.
     */
    void onPlayerDisconnect(CPlayer player);
}
