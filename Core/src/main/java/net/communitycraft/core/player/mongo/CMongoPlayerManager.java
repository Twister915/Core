package net.communitycraft.core.player.mongo;

import com.mongodb.*;
import lombok.Getter;
import net.communitycraft.core.Core;
import net.communitycraft.core.player.*;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

import static net.communitycraft.core.player.mongo.COfflineMongoPlayer.getValueFrom;

public final class CMongoPlayerManager implements CPlayerManager {
    @Getter private CMongoDatabase database;

    private Map<String, CPlayer> onlinePlayerMap = new HashMap<>();

    public CMongoPlayerManager(CMongoDatabase database) {
        this.database = database;
        Core.getInstance().registerListener(new CPlayerManagerListener(this));
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), new CPlayerManagerSaveTask(this), 1200, 1200);
    }

    @Override
    public Collection<COfflinePlayer> getOfflinePlayerByName(String username) {
        DBCursor dbObjects = database.getCollection("users").find(new BasicDBObject("usernames", username));
        List<COfflinePlayer> offlinePlayers = new ArrayList<>();
        for (DBObject dbObject : dbObjects) {
            offlinePlayers.add(new COfflineMongoPlayer(UUID.fromString(getValueFrom(dbObject, "uuid", String.class)), dbObject, this));
        }
        return offlinePlayers;
    }

    @Override
    public COfflineMongoPlayer getOfflinePlayerByUUID(UUID uuid) {
        return new COfflineMongoPlayer(uuid, getPlayerDocumentFor(uuid), this);
    }

    DBObject getPlayerDocumentFor(UUID uuid) {
        return database.getCollection("users").findOne(new BasicDBObject("uuid", uuid.toString()));
    }

    @Override
    public CMongoPlayer getCPlayerForPlayer(Player player) {
        if (player == null) return null;
        return (CMongoPlayer) this.onlinePlayerMap.get(player.getName());
    }

    @Override
    public CPlayer getOnlineCPlayerForUUID(UUID uuid) {
        return getCPlayerForPlayer(Bukkit.getPlayer(uuid));
    }

    @Override
    public CPlayer getOnlineCPlayerForName(String name) {
        return this.onlinePlayerMap.get(name);
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
        COfflineMongoPlayer player1 = (COfflineMongoPlayer) player;
        if (player1 instanceof CMongoPlayer) ((CMongoPlayer)player1).updateForSaving();
        DBObject objectForPlayer = player1.getObjectForPlayer();
        this.database.getCollection("users").save(objectForPlayer);
        player1.setObjectId(getValueFrom(objectForPlayer, "_id", ObjectId.class));
    }

    @Override
    public void playerLoggedIn(Player player) {
        CMongoPlayer cMongoPlayer = new CMongoPlayer(player, getOfflinePlayerByUUID(player.getUniqueId()), this);
        try {
            cMongoPlayer.onJoin();
        } catch (DatabaseConnectException | MongoException e) {
            Core.getInstance().getLogger().severe("Could not save player into the database " + e.getMessage() + " - " + player.getName());
        }
        this.onlinePlayerMap.put(player.getName(), cMongoPlayer);
    }

    @Override
    public void playerLoggedOut(Player player) {
        CMongoPlayer cPlayerForPlayer = getCPlayerForPlayer(player);
        cPlayerForPlayer.updateForSaving();
        try {
            cPlayerForPlayer.saveIntoDatabase();
        } catch (DatabaseConnectException | MongoException e) {
            Core.getInstance().getLogger().severe("Could not save player into the database " + e.getMessage() + " - " + cPlayerForPlayer.getName());
        }
        this.onlinePlayerMap.remove(player.getName());
    }

    @Override
    public void onDisable() {
        for (CPlayer onlinePlayer : getOnlinePlayers()) {
            try {
                onlinePlayer.saveIntoDatabase();
            } catch (DatabaseConnectException | MongoException e) {
                Core.getInstance().getLogger().severe("Could not save player into the database " + e.getMessage() + " - " + onlinePlayer.getName());
            }
        }
        this.database.disconnect();
    }
}
