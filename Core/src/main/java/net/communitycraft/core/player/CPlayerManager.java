package net.communitycraft.core.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public interface CPlayerManager {
    Collection<COfflinePlayer> getOfflinePlayerByName(String username);
    COfflinePlayer getOfflinePlayerByUUID(UUID uuid);
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
