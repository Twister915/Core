package me.joeyandtom.communitycraft.core.player.mongo;

import lombok.Getter;
import me.joeyandtom.communitycraft.core.player.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public final class CMongoPlayerManager implements CPlayerManager {
    @Getter private List<CMongoPlayer> onlinePlayers;
    @Getter private CMongoDatabase database;

    public CMongoPlayerManager(CMongoDatabase database) {
        this.database = database;
    }

    @Override
    public COfflinePlayerIterator getOfflinePlayerByName(String username) {
        return null;
    }

    @Override
    public CPlayer getCPlayerForPlayer(Player player) {
        return null;
    }

    @Override
    public COfflinePlayer getCOfflinePlayerForOfflinePlayer(OfflinePlayer player) {
        return null;
    }

    @Override
    public void savePlayerData(COfflinePlayer player) {

    }

    @Override
    public void playerLoggedIn(Player player) {

    }

    @Override
    public void playerLoggedOut(Player player) {

    }

    @Override
    public void onDisable() {
        for (CMongoPlayer onlinePlayer : onlinePlayers) {
            onlinePlayer.saveIntoDatabase();
        }
        this.database.disconnect();
    }
}
