package net.communitycraft.core.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CPlayerManager {
    /**
     * Gets an offline player by searching through the database for anyone with a current-name as specified.
     * @param username The username you want to search for.
     * @return Any matching {@link net.communitycraft.core.player.COfflinePlayer} object or {@code null} if none is found.
     */
    List<COfflinePlayer> getOfflinePlayerByName(String username);
    COfflinePlayer getOfflinePlayerByUUID(UUID uuid);
    List<COfflinePlayer> getOfflinePlayersByUUIDS(List<UUID> uuids);
    COfflinePlayer getCOfflinePlayerForOfflinePlayer(OfflinePlayer player);
    Collection<CPlayer> getOnlinePlayers();
    CPlayer getCPlayerForPlayer(Player player);
    CPlayer getOnlineCPlayerForUUID(UUID uuid);
    CPlayer getOnlineCPlayerForName(String name);

    void savePlayerData(COfflinePlayer player) throws DatabaseConnectException;

    void playerLoggedIn(Player player);
    void playerLoggedOut(Player player);
    CDatabase getDatabase();

    void onDisable();
}
