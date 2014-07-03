package net.cogzmc.core.player;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

public interface CPlayerRepository {
    /**
     * Gets an offline player by searching through the database for anyone with a current-name as specified.
     * @param username The username you want to search for.
     * @return Any matching {@link net.cogzmc.core.player.COfflinePlayer} object or {@code null} if none is found.
     */
    List<COfflinePlayer> getOfflinePlayerByName(String username);

    /**
     * Gets the offline player using their UUID to identify them.
     * @param uuid The {@link java.util.UUID} representing the UUID of the player.
     * @return An offline player with this UUID or {@code null} if it could not be found.
     */
    COfflinePlayer getOfflinePlayerByUUID(UUID uuid);

    /**
     * Gets {@link net.cogzmc.core.player.COfflinePlayer}s that match the {@link java.util.List} of {@link java.util.UUID}s that you passed.
     * @param uuids The {@link java.util.UUID}s that you want to find the {@link net.cogzmc.core.player.COfflinePlayer}s for.
     * @return A {@link java.util.List} of {@link net.cogzmc.core.player.COfflinePlayer}
     */
    List<COfflinePlayer> getOfflinePlayersByUUIDS(List<UUID> uuids);

    /**
     * Gets all players whom have connected via this IP address.
     * @param address The {@link java.net.InetAddress} to test.
     * @return A {@link java.util.List} of players.
     */
    List<COfflinePlayer> getOfflinePlayersForIP(InetAddress address);

    /**
     * Saves a player's data into the database, the exact process of this depends very much on the implementation of this class that you're using.
     * @param player The {@link net.cogzmc.core.player.COfflinePlayer} that you wish to save into the database.
     * @throws DatabaseConnectException If there is a failure in saving the database.
     */
    void savePlayerData(COfflinePlayer player) throws DatabaseConnectException;

    /**
     * <b>THIS METHOD IS POTENTIALLY DESTRUCTIVE. USE AT YOUR OWN RISK</b>
     *
     * This will delete all records of the player from the database.
     * @param player The {@link net.cogzmc.core.player.COfflinePlayer} to delete from the database.
     */
    void deletePlayerRecords(COfflinePlayer player) throws IllegalArgumentException;
}
