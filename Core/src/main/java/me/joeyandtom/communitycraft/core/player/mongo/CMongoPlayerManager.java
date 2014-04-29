package me.joeyandtom.communitycraft.core.player.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import lombok.Getter;
import me.joeyandtom.communitycraft.core.Core;
import me.joeyandtom.communitycraft.core.player.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

import static me.joeyandtom.communitycraft.core.player.mongo.COfflineMongoPlayer.getValueFrom;

public final class CMongoPlayerManager implements CPlayerManager {
    @Getter private CMongoDatabase database;

    private Map<String, CPlayer> onlinePlayerMap = new HashMap<>();

    public CMongoPlayerManager(CMongoDatabase database) {
        this.database = database;
    }

    @Override
    public COfflinePlayerIterator getOfflinePlayerByName(String username) {
        DBCursor dbObjects = database.getCollection("users").find(new BasicDBObject("usernames", username));
        List<COfflinePlayer> offlinePlayers = new ArrayList<>();
        for (DBObject dbObject : dbObjects) {
            offlinePlayers.add(new COfflineMongoPlayer(UUID.fromString(getValueFrom(dbObject, "uuid", String.class)), dbObject, this));
        }
        return new COfflinePlayerIterator(offlinePlayers);
    }

    @Override
    public COfflineMongoPlayer getOfflinePlayerByUUID(UUID uuid) {
        DBObject player = getPlayerDocumentFor(uuid);
        return new COfflineMongoPlayer(uuid, player, this);
    }

    DBObject getPlayerDocumentFor(UUID uuid) {
        DBCollection users = database.getCollection("users");
        return users.findOne(new BasicDBObject("uuid", uuid.toString()));
    }

    @Override
    public CMongoPlayer getCPlayerForPlayer(Player player) {
        return (CMongoPlayer) this.onlinePlayerMap.get(player.getName());
    }

    @Override
    public COfflineMongoPlayer getCOfflinePlayerForOfflinePlayer(OfflinePlayer player) {
        return getOfflinePlayerByUUID(player.getUniqueId());
    }

    @Override
    public Collection<CPlayer> getOnlinePlayers() {
        return this.onlinePlayerMap.values();
    }

    @Override
    public void savePlayerData(COfflinePlayer player) throws DatabaseConnectException {
        player.saveIntoDatabase();
    }

    @Override
    public void playerLoggedIn(Player player) {
        CMongoPlayer cMongoPlayer = new CMongoPlayer(player, getOfflinePlayerByUUID(player.getUniqueId()), this);
        this.onlinePlayerMap.put(player.getName(), cMongoPlayer);
    }

    @Override
    public void playerLoggedOut(Player player) {
        CMongoPlayer cPlayerForPlayer = getCPlayerForPlayer(player);
        cPlayerForPlayer.onLeave();
        try {
            cPlayerForPlayer.saveIntoDatabase();
        } catch (DatabaseConnectException e) {
            Core.getInstance().getLogger().severe("Could not save player into the database " + e.getMessage() + " - " + cPlayerForPlayer.getName());
        }
        this.onlinePlayerMap.remove(player.getName());
    }

    @Override
    public void onDisable() {
        for (CPlayer onlinePlayer : getOnlinePlayers()) {
            try {
                onlinePlayer.saveIntoDatabase();
            } catch (DatabaseConnectException e) {
                Core.getInstance().getLogger().severe("Could not save player into the database " + e.getMessage() + " - " + onlinePlayer.getName());
            }
        }
        this.database.disconnect();
    }
}
