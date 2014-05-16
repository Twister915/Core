package net.communitycraft.core.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * The CPlayerManager can be used to access information about players or iterate through all online players in an enhanced for loop
 * <pre>
 *     for ({@link net.communitycraft.core.player.CPlayer} player : playerManagerInstance) {
 *
 *     }
 * </pre>
 *
 * the code above will work and allow you to iterate through all online players.
 *
 * You can also get offline players by username, uuid, or a list of UUIDs.
 *
 * You can get any online player by name, UUID, or {@link org.bukkit.entity.Player} representation.
 *
 * You should never call {@link #playerLoggedIn(org.bukkit.entity.Player, java.net.InetAddress)} or {@link #playerLoggedOut(org.bukkit.entity.Player)} unless you are a listener for the {@link net.communitycraft.core.player.CPlayerManager}.
 * It is unsafe to call these methods unless you are aware of the effect as it can break the server easily.
 * @author Joey
 */
public interface CPlayerManager extends Iterable<CPlayer> {
    /**
     * Gets an offline player by searching through the database for anyone with a current-name as specified.
     * @param username The username you want to search for.
     * @return Any matching {@link net.communitycraft.core.player.COfflinePlayer} object or {@code null} if none is found.
     */
    List<COfflinePlayer> getOfflinePlayerByName(String username);

    /**
     * Gets the offline player using their UUID to identify them.
     * @param uuid The {@link java.util.UUID} representing the UUID of the player.
     * @return An offline player with this UUID or {@code null} if it could not be found.
     */
    COfflinePlayer getOfflinePlayerByUUID(UUID uuid);

    /**
     * Gets {@link net.communitycraft.core.player.COfflinePlayer}s that match the {@link java.util.List} of {@link java.util.UUID}s that you passed.
     * @param uuids
     * @return
     */
    List<COfflinePlayer> getOfflinePlayersByUUIDS(List<UUID> uuids);
    COfflinePlayer getCOfflinePlayerForOfflinePlayer(OfflinePlayer player);
    Collection<CPlayer> getOnlinePlayers();
    CPlayer getCPlayerForPlayer(Player player);
    CPlayer getOnlineCPlayerForUUID(UUID uuid);
    CPlayer getOnlineCPlayerForName(String name);

    void savePlayerData(COfflinePlayer player) throws DatabaseConnectException;

    void playerLoggedIn(Player player, InetAddress address);
    void playerLoggedOut(Player player);
    CDatabase getDatabase();
    CPermissionsManager getPermissionsManager();

    void onDisable();
}
