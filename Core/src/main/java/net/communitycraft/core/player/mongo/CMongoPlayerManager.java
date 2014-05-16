package net.communitycraft.core.player.mongo;

import com.mongodb.*;
import lombok.Getter;
import net.communitycraft.core.Core;
import net.communitycraft.core.player.*;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.util.*;

import static net.communitycraft.core.player.mongo.MongoUtils.getValueFrom;

public final class CMongoPlayerManager implements CPlayerManager {
    @Getter private CMongoDatabase database;
    @Getter private CMongoPermissionsManager permissionsManager;

    private Map<String, CPlayer> onlinePlayerMap = new HashMap<>();

    public CMongoPlayerManager(CMongoDatabase database) throws DatabaseConnectException {
        this.database = database;
        database.connect();
        this.permissionsManager = new CMongoPermissionsManager(database, this);
        Core.getInstance().registerListener(new CPlayerManagerListener(this));
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), new CPlayerManagerSaveTask(this), 1200, 1200);
        DBCollection users = database.getCollection(MongoKey.USERS_COLLETION.toString());
        if (users.count() == 0) { //Looks like a new collection to me
            //Need to setup the index
            users.createIndex(new BasicDBObject(MongoKey.UUID_KEY.toString(), 1));
        }
    }

    @Override
    public List<COfflinePlayer> getOfflinePlayerByName(String username) {
        CPlayer onlinePlayer;
        if ((onlinePlayer = getOnlineCPlayerForName(username)) != null) {
            //Hmm... is there an easier way to store a single player in an array list with an implicit generic type of it's super interface?
            ArrayList<COfflinePlayer> cOfflinePlayers = new ArrayList<>();
            cOfflinePlayers.add(onlinePlayer);
            return cOfflinePlayers;
        }
        DBCursor dbObjects = database.getCollection(MongoKey.USERS_COLLETION.toString()).find(new BasicDBObject(MongoKey.USERNAMES_KEY.toString(), username));
        List<COfflinePlayer> offlinePlayers = new ArrayList<>();
        for (DBObject dbObject : dbObjects) {
            offlinePlayers.add(new COfflineMongoPlayer(UUID.fromString(getValueFrom(dbObject, MongoKey.UUID_KEY.toString(), String.class)), dbObject, this));
        }
        return offlinePlayers;
    }

    @Override
    public COfflineMongoPlayer getOfflinePlayerByUUID(UUID uuid) {
        DBObject playerDocumentFor = getPlayerDocumentFor(uuid);
        return new COfflineMongoPlayer(uuid, playerDocumentFor, this);
    }

    @Override
    public List<COfflinePlayer> getOfflinePlayersByUUIDS(List<UUID> uuids) {
        List<COfflinePlayer> offlinePlayers = new ArrayList<>();
        for (UUID uuid : uuids) {
            DBObject playerDocumentFor = getPlayerDocumentFor(uuid);
            if (playerDocumentFor == null) continue;
            offlinePlayers.add(new COfflineMongoPlayer(uuid, playerDocumentFor, this));
        }
        return offlinePlayers;
    }

    public COfflinePlayer getOfflinePlayerByObjectId(ObjectId id) {
        DBObject one = database.getCollection(MongoKey.USERS_COLLETION.toString()).findOne(new BasicDBObject(MongoKey.ID_KEY.toString(), id));
        if (one == null) return null;
        UUID uuid = UUID.fromString(getValueFrom(one, MongoKey.UUID_KEY, String.class));
        return new COfflineMongoPlayer(uuid, one, this);
    }

    DBObject getPlayerDocumentFor(UUID uuid) {
        return database.getCollection(MongoKey.USERS_COLLETION.toString()).findOne(new BasicDBObject(MongoKey.UUID_KEY.toString(), uuid.toString()));
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
        this.database.getCollection(MongoKey.USERS_COLLETION.toString()).save(objectForPlayer);
        player1.setObjectId(getValueFrom(objectForPlayer, MongoKey.ID_KEY, ObjectId.class));
    }

    @Override
    public void playerLoggedIn(Player player, InetAddress address) {
        CMongoPlayer cMongoPlayer = new CMongoPlayer(player, getOfflinePlayerByUUID(player.getUniqueId()), this);
        try {
            cMongoPlayer.onJoin(address);
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

    @Override
    public Iterator<CPlayer> iterator() {
        return this.onlinePlayerMap.values().iterator();
    }
}
