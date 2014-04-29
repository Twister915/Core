package me.joeyandtom.communitycraft.core.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public interface CPlayerManager {
    COfflinePlayerIterator getOfflinePlayerByName(String username);
    List<CPlayer> getOnlinePlayers();
    CPlayer getCPlayerForPlayer(Player player);
    COfflinePlayer getCOfflinePlayerForOfflinePlayer(OfflinePlayer player);

    void savePlayerData(COfflinePlayer player) throws DatabaseConnectException;

    void playerLoggedIn(Player player);
    void playerLoggedOut(Player player);
    CDatabase getDatabase();

    void onDisable();
}
